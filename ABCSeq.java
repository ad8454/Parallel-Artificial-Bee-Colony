import java.util.ArrayList;
import java.util.Random;

import edu.rit.pj2.Task;
import edu.rit.util.Instance;
import edu.rit.util.Searching;

/**
 * This program is a sequential implementation of
 * the artificial bee colony algorithm to solve the
 * artificial bee colony algorithm.
 *
 * Usage: java pj2 DioEqnSeq <n> <c> <lb> <ub>
 *
 * It then prints out the number of unique solutions found
 * followed by the minimum solution followed by the maximum
 * solution.
 *
 * @author  Ajinkya Dhaigude
 * @author  Sameer Raghuram
 */
 
public class ABCSeq extends Task{
	// command line arguments and bounds for search
	int totEmployedBees;		//TODO: find way to determine
	int MAX_EPOCH;
	int totVehicles;
    Graph graph = null;
    Random rand;
	Solution employedBees[];
	Solution onlookerBees[];
	Solution bestDiscarded = new Solution();
	
	/**
	 * Main program for DioEqnSeq.
	 */
	public void main(final String[] args){

		if (args.length != 3) {
			usage(0);
		}

        // Get Graph instance
        try{
        	graph = (Graph) Instance.newInstance (args[0]);
			totVehicles = Integer.parseInt(args[1]);
			totEmployedBees = Integer.parseInt(args[2]);
			MAX_EPOCH = 500;

        } catch(Exception e){
            usage(1);
        }
        
        rand = new Random();
        employedBees = new Solution[totEmployedBees];
		onlookerBees = new Solution[totEmployedBees];

        // Get total vertices
        int totNodes = graph.getNodes();
        Node allNodes[] = new Node[totNodes];

		// Get all nodes in graph
		for(int i=0; i<totNodes; i++){
			allNodes[i] = new Node();
			graph.nextVertex(allNodes[i]);
		}

		// Generate initial solutions
        for(int i=0; i<totEmployedBees; i++){
        	//employedBees[i] = new Bee(totNodes, totVehicles);
        	employedBees[i] = new Solution(allNodes, totVehicles, i);
        	employedBees[i].genRandomSolution(rand);
			//System.out.println(employedBees[i]+"\n");
			onlookerBees[i] = new Solution(allNodes, totVehicles, i);
        }
        
        int epoch = 0;

		while(epoch++ < MAX_EPOCH){

			double totWeight = 0;
			
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
				for(Solution soln: employedBees){
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
		}
		// Final reduction to get best solution
		for(int i=0; i<employedBees.length; i++){
			if(bestDiscarded.compareTo(employedBees[i])>0){
				bestDiscarded = employedBees[i];
			}
		}

		System.out.println(bestDiscarded);

	}
    
	/**
	 * Print error description to console and gracefully exit.
	 *
	 * @param  err  Error identifier
	 */
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
	
	/**
	 * Specify number of cores to use.
	 */
	protected static int coresRequired(){
		return 1;
	}
}
