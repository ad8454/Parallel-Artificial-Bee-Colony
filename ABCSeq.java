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
 */
 
public class ABCSeq extends Task{
	
	// command line arguments and bounds for search
	int totEmployedBees = 10;		//TODO: find way to determine
	int MAX_EPOCH = 50;
	int totVehicles = 5;
    Graph graph = null;
    Random rand;		//TODO: switch to pj2 maybe
	Solution employedBees[] = new Solution[totEmployedBees];
	Solution onlookerBees[] = new Solution[totEmployedBees];
	
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
        
        for(int i=0; i<totEmployedBees; i++){
        	//employedBees[i] = new Bee(totNodes, totVehicles);
        	employedBees[i] = new Solution(allNodes, totVehicles);
        	employedBees[i].genRandomSolution(rand);
        	onlookerBees[i] = new Solution(allNodes, totVehicles);
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
					localSolution.swap(idx1, idx2); //revert
					newFitness = oldFitness;
				}
				localSolution.setFitness(newFitness);
				totWeight += newFitness;
			}
			

			for(int i=0; i < totEmployedBees; i++){
				double probab = totWeight * rand.nextDouble();
				Solution pickedSoln = null;
				for(Solution soln: employedBees){
					probab -= soln.getFitness();
					if(probab <= 0){
						pickedSoln = soln;
						break;
					}
				}
				if(pickedSoln == null)
					pickedSoln = employedBees[totEmployedBees - 1];
				
				
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
