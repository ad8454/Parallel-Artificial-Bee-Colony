import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.pj2.vbl.IntVbl;
import edu.rit.util.Instance;
import edu.rit.pj2.vbl.DoubleVbl;

import java.util.Random;

/**
 * Created by Sameer on 11/12/2016.
 */
public class ABCSeq extends Task {

	int totEmployedBees;        //TODO: find way to determine
	int MAX_EPOCH=500;
	int totVehicles;
	Graph graph;
	Random rand;
	int lb;
	int ub;
	Solution[] employedBees;
	Solution[] onlookerBees;
	Solution bestDiscarded;
	Solution bestSolution;
	double totWeight;

	public void main(String args[]) {

		if (args.length < 3) {
			usage(0);
		}

		// Get Graph instance
		try{
			graph = (Graph) Instance.newInstance (args[0]);
			totVehicles = Integer.parseInt(args[1]);
			totEmployedBees = Integer.parseInt(args[2]);
			if(args.length>3){
				MAX_EPOCH = Integer.parseInt(args[3]);
			}

		} catch(Exception e){
			usage(1);
		}

		rand = new Random();
		employedBees = new Solution[totEmployedBees];
		onlookerBees = new Solution[totEmployedBees];

		totWeight = 0.0;
		rand = new Random();
		lb = 0;
		ub = cores() - 1;

		// Get total vertices
		int totNodes = graph.getNodes();
		Node allNodes[] = new Node[totNodes];

		// Get all nodes in graph
		for (int i = 0; i < totNodes; i++) {
			allNodes[i] = new Node();
			graph.nextVertex(allNodes[i]);
		}

		bestDiscarded = (new Solution(allNodes, totVehicles, 0));
		bestDiscarded.genRandomSolution(rand);
		bestSolution = new Solution(allNodes, totVehicles, 0);
		bestSolution.genRandomSolution(rand);


		int range = totEmployedBees/cores();


		for(int round = 0; round < 10; round++){

			int epoch = 0;

			// Generate initial solutions
			for (int i = 0; i < totEmployedBees; i++) {
				//employedBees[i] = new Bee(totNodes, totVehicles);
				employedBees[i] = new Solution(allNodes, totVehicles, i);
				employedBees[i].genRandomSolution(rand);
				//System.out.println(employedBees[i]+"\n");
				onlookerBees[i] = new Solution(allNodes, totVehicles, i);
				onlookerBees[i].genRandomSolution(rand);
			}

			while (epoch++ < MAX_EPOCH) {



				double totWeight = 0;

				// Employed phase
				for(int i=0; i < totEmployedBees; i++){
					Solution localSolution = employedBees[i];
					totWeight += localSolution.exploitSolution(rand);

				}

				// Onlooker bee phase
				for(int i=0; i < totEmployedBees; i++){
					// The roulette wheel selection
					double probab = totWeight * rand.nextDouble();

					Solution onlookerSoln = onlookerBees[i];
					boolean picked = false;

					for(int k=0; k<totEmployedBees; k++){
						Solution soln = employedBees[k];
						probab -= soln.getFitness();
						if(probab <= 0){
							onlookerSoln.copy(soln);
							picked = true;
							break;
						}
					}
					if(! picked) // in case of round off error, assign last solution
						onlookerSoln.copy(employedBees[totEmployedBees-1]);

					onlookerSoln.exploitSolution(rand);

					// Set solution to employedbees array if better
					if(employedBees[onlookerSoln.id].compareTo(onlookerSoln)>0){
						employedBees[onlookerSoln.id] = onlookerSoln;
					}
				}

				// Discard any exhausted solutions
				for(int i=0; i<employedBees.length; i++){
					if(employedBees[i].isExhausted()){
						// See if our discarded is the best one yet (to be discarded)
						if(bestDiscarded.compareTo(employedBees[i])>0){
							bestDiscarded.copy(employedBees[i]);
						}
						employedBees[i].genRandomSolution(rand);
					}
				}

				// Final reduction to get best solution
				for(int i=0; i<employedBees.length; i++){
					if(bestDiscarded.compareTo(employedBees[i])>0){
						bestDiscarded = employedBees[i];
					}
				}



			}

			for(Solution soln:employedBees){
				if(bestDiscarded.compareTo(soln)>0){
					bestDiscarded.copy(soln);
				}
			}

			if(bestDiscarded.computeDistance() < bestSolution.computeDistance() || (bestSolution.
					computeDistance() == Double.POSITIVE_INFINITY && bestDiscarded.computeDistance() !=
					Double.POSITIVE_INFINITY))
				bestSolution.copy(bestDiscarded);

		}

		System.out.println(bestSolution);

	}

	private static void usage(int err){
		switch(err){
			case 0: System.err.println ("Inavlid number of arguments. Usage is: java pj2 ABCSeq RandomGraph<ctor> " +
					"<totalvehicles> <numberofbees>");
				break;
			case 1: System.err.println ("Inavlid argument type. Types should be: RandomGraph<ctor>(string) <total" +
					"vehicles> (int) <numberofbees>(int)");
				break;
			case 2: System.err.println ("Inavlid argument. <n> should be at least equal to 2");
				break;
			case 3: System.err.println ("Inavlid argument. <ub> should not be less than <lb>");
		}
		throw new IllegalArgumentException();
	}
}
