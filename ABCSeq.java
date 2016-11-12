import java.util.ArrayList;
import java.util.Random;

import edu.rit.pj2.Task;
import edu.rit.util.Instance;

/**
 * This program is a sequential implementation of the brute 
 * force approach to finding solutions for the general 
 * Diophantine equation given by: x^n + y^n = z^n + c.
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
	int totEmployedBees = 10;		//TODO: find way to determine
	int MAX_EPOCH = 50;
	int totVehicles = 5;
    Graph graph = null;
    Random rand;
	Solution employedBees[] = new Solution[totEmployedBees];
	Solution onlookerBees[] = new Solution[totEmployedBees];
	Solution bestDiscarded = new Solution();
	
	/**
	 * Main program for DioEqnSeq.
	 */
	public void main(final String[] args){
		
		if (args.length != 1)
			usage(0);
        
        // Get Graph instance
        try{
        	graph = (Graph) Instance.newInstance (args[0]);
        } catch(Exception e){
            usage(1);
        }
        
        rand = new Random();
        
        // Get total vertices
        int totNodes = graph.getNodes();
        Node allNodes[] = new Node[totNodes];

		// Get all nodes in graph
		for(int i=0; i<totNodes; i++){
			graph.nextVertex(allNodes[i]);
		}

		// Generate initial solutions
        for(int i=0; i<totEmployedBees; i++){
        	//employedBees[i] = new Bee(totNodes, totVehicles);
        	employedBees[i] = new Solution(allNodes, totVehicles, i);
        	employedBees[i].genRandomSolution(rand);
        	onlookerBees[i] = new Solution();
        }
        
        int epoch = 0;
        
		while(epoch++ < MAX_EPOCH){
			double totWeight = 0;
			
			for(int i=0; i < totEmployedBees; i++){
				Solution localSolution = employedBees[i];
				
				double oldFitness = localSolution.computeFitness();
				
				int idx1 = rand.nextInt(localSolution.getSize());
				int idx2 = rand.nextInt(localSolution.getSize());
				localSolution.swap(idx1, idx2);
				double newFitness = localSolution.computeFitness();
				if(oldFitness > newFitness) {
					// Increment the number of trials to indicate exhaustion of
					// a food source
					localSolution.incTrial();

					localSolution.swap(idx1, idx2); //revert
					newFitness = oldFitness;		//revert to old fitness
				}
				// reset the number of trials of solution to indicate improvement
				if(!(oldFitness == newFitness)){
					localSolution.setTrial(0);
				}
				localSolution.setFitness(newFitness);
				totWeight += newFitness;
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
						soln.getDeepCopy(onlookerSoln);
						picked = true;
						break;
					}
				}
				if(! picked) // in case of round off error, assign last solution
					employedBees[totEmployedBees-1].getDeepCopy(onlookerSoln);
					//onlookerSoln.setLocalSolution(employedBees[totEmployedBees - 1].getLocalSolution());
				
				
				double oldFitness = onlookerSoln.computeFitness();
				int idx1 = rand.nextInt(onlookerSoln.getSize());
				int idx2 = rand.nextInt(onlookerSoln.getSize());
				onlookerSoln.swap(idx1, idx2);
				double newFitness = onlookerSoln.computeFitness();
				if(oldFitness > newFitness) {
					onlookerSoln.incTrial();
					onlookerSoln.swap(idx1, idx2); //revert
					newFitness = oldFitness;
				}
				if(!(oldFitness == newFitness)){
					onlookerSoln.setTrial(0);
				}
				onlookerSoln.setFitness(newFitness);
				totWeight += newFitness;

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
						bestDiscarded = employedBees[i];
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
        
	}
    
	/**
	 * Print error description to console and gracefully exit.
	 *
	 * @param  err  Error identifier
	 */
	private static void usage(int err){
		switch(err){
			case 0: System.err.println ("Inavlid number of arguments. Usage is: java pj2 DioEqnSeq <n> <c> <lb> <ub>");
					break;
			case 1: System.err.println ("Inavlid argument type. Types should be: <n>(int) <c>(long) <lb>(long) <ub>(long)");
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
