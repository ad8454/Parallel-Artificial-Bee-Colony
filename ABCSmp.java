import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.util.Instance;
import edu.rit.pj2.vbl.DoubleVbl;

import java.util.Random;

/**
 * This program is a parallel implementation of the Artificial
 * Bee Colony (ABC) algorithm for solving the Vehicle Routing 
 * Problem (VRP).
 *
 * It prints out the best found path for each vehicle.
 *
 * Usage: java pj2 cores=<cores> ABCSmp "RandomGraph(<nodes>,<range>,<seed>)" <V> <S>
 *
 * @author Ajinkya Dhaigude
 * @author Sameer Raghuram
 */
public class ABCSmp extends Task {

	// Initialize global variables
    int totEmployedBees;
    int MAX_EPOCH=500;
    int totVehicles;
    Graph graph;
    Random rand;
    int lb;
    int ub;
    Solution[] employedBees;
    Solution[] onlookerBees;
    SolutionVbl bestDiscarded;
    SolutionVbl bestSolution;
    DoubleVbl totWeight;

	/**
	 * Main program for ABCSmp.
	 */
    public void main(String args[]) {

        if (args.length < 3) {
            usage(0);
        }

        // Get Graph instance and read command line arguments
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
        
        // Intialize employed and onlooker bees to be equal in number
        rand = new Random();
        employedBees = new Solution[totEmployedBees];
        onlookerBees = new Solution[totEmployedBees];

        totWeight = new DoubleVbl.Sum(0);
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

        // Initialize instance for storing global best solution
        bestDiscarded = new SolutionVbl.Max(new Solution(allNodes, totVehicles, 0));
        bestDiscarded.item.genRandomSolution(rand);
        bestSolution = new SolutionVbl.Max(new Solution(allNodes, totVehicles, 0));
        bestSolution.item.genRandomSolution(rand);


        int range = totEmployedBees/cores();


        for(int round = 0; round < 10; round++){

            int epoch = 0;

            // Generate initial solutions
            for (int i = 0; i < totEmployedBees; i++) {
                employedBees[i] = new Solution(allNodes, totVehicles, i);
                employedBees[i].genRandomSolution(rand);
                onlookerBees[i] = new Solution(allNodes, totVehicles, i);
                onlookerBees[i].genRandomSolution(rand);
            }

            while (epoch++ < MAX_EPOCH) {

                parallelFor(lb, ub).exec(new Loop() {
                    Random thrRand;
                    SolutionVbl thrBestDiscarded;

                    public void start(){

                        thrBestDiscarded = threadLocal(bestDiscarded);
                        thrRand = new Random();
                    }

                    @Override
                    public void run(int j) throws Exception {

                        int offset = j * range;
                        double totWeight = 0;

                        // Employed phase
                        for(int i=offset; i < range; i++){
                            Solution localSolution = employedBees[i];
                            totWeight += localSolution.exploitSolution(thrRand);

                        }

                        // Onlooker bee phase
                        for(int i=offset; i < range; i++){
                            // The roulette wheel selection
                            double probab = totWeight * thrRand.nextDouble();

                            Solution onlookerSoln = onlookerBees[i];
                            boolean picked = false;

                            for(int k=offset; k<range; k++){
                                Solution soln = employedBees[k];
                                probab -= soln.getFitness();
                                if(probab <= 0){
                                    onlookerSoln.copy(soln);
                                    picked = true;
                                    break;
                                }
                            }
                            if(! picked) // in case of round off error, assign last solution
                                onlookerSoln.copy(employedBees[offset + range-1]);

                            onlookerSoln.exploitSolution(rand);

                            // Set solution to employedBees array if better
                            if(employedBees[onlookerSoln.id].compareTo(onlookerSoln)>0){
                                employedBees[onlookerSoln.id] = onlookerSoln;
                            }
                        }

                        // Discard any exhausted solutions
                        for(int i=offset; i<range; i++){
                            if(employedBees[i].isExhausted()){
                                // See if our discarded is the best one yet (to be discarded)
                                if(thrBestDiscarded.item.compareTo(employedBees[i])>0){
                                    thrBestDiscarded.item.copy(employedBees[i]);
                                }
                                employedBees[i].genRandomSolution(rand);
                            }
                        }
                    }
                });

            }
            
			// Replace bestDiscarded solution if a better one is found
            for(Solution soln:employedBees){
                if(bestDiscarded.item.compareTo(soln)>0){
                    bestDiscarded.item.copy(soln);
                }
            }

            if(bestDiscarded.item.computeDistance() < bestSolution.item.computeDistance() || (bestSolution.item.
                    computeDistance() == Double.POSITIVE_INFINITY && bestDiscarded.item.computeDistance() !=
                    Double.POSITIVE_INFINITY))
                bestSolution.item.copy(bestDiscarded.item);

        }

		// Print final result
        System.out.println(bestSolution.item);

    }

    private static void usage(int err){
        switch(err){
			case 0: System.err.println ("Inavlid number of arguments. Usage is: java pj2 cores=<cores>" +
										"ABCSmp \"RandomGraph(<nodes>,<range>,<seed>)\" <V> <S>");
				break;
			case 1: System.err.println ("Inavlid argument type. Types should be: <cores>(int), "+
					"RandomGraph<ctor>(string), <nodes>(int), <range>(int), <seed>(int), <V>(int), <S>(int)");
				break;
        }
        throw new IllegalArgumentException();
    }
}
