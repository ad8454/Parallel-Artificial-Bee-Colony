import java.util.Random;

/**
 * This class provides three long reduction
 * variables to be shared by multiple threads
 * running in parallel.
 *
 * @author  Ajinkya Dhaigude
 */
 
class Solution implements Cloneable{

    // shared variables
	private Node route[];	// Solution path eg {0->1->2->3->0->4->5->6->0->7->8}
	private int totNodes;	// Total nodes
	private double fitness = 0;
    private int TRIAL_LIMIT = 100;
    private int trial = 0;
    public int id;


	public Solution(){}

	/**
	 * Constructor to initialize shared variables.
	 *
	 * @param  x  Initial value for x
	 * @param  y  Initial value for y
	 * @param  z  Initial value for z
	 */
	public Solution(Node allNodes[], int totVehicles, int id){
        this.id = id;
		totNodes = allNodes.length;
		route = new Node[totNodes + totVehicles];
		for(int i=0; i<route.length; i++){
			if(i < totNodes)
				route[i] = allNodes[i];
			else
				route[i] = allNodes[0]; 	// depot
		}
	}

	public double computeFitness(){
		double distance = 0;
		for(int i=0; i<route.length-1; i++){
			distance += getDistance(route[i], route[i+1]);
		}
		return 1/distance;
	}

	public double getDistance(Node n1, Node n2){
		int yDiff = (n2.y - n1.y);
		int xDiff = (n2.x - n1.x);
		return Math.sqrt((yDiff * yDiff) + (xDiff * xDiff));
	}

	public void setFitness(double fitness){
		this.fitness = fitness;
	}

	public double getFitness(){
		return fitness;
	}

    public void setTrial(int trial){
        this.trial = trial;
    }
    public void incTrial(){
     this.trial++;

    }
    public int getTrial(){
        return this.trial;
    }


	public int getSize(){
		return route.length;
	}

	public int getTotNodes(){
		return totNodes;
	}

	public void setIndex(int index, Node node){
		route[index] = node;
	}

	public void swap(int idx1, int idx2){
		Node temp = route[idx1];
		route[idx1] = route[idx2];
		route[idx2] = temp;
	}

	void genRandomSolution(Random rand){
		for(int i=1; i< route.length - 1; i++){
			int idx2 = rand.nextInt(route.length - 2) + 1;
			swap(i, idx2);
		}
	}

	/**
	 * Method to deep copy shared variables.
	 *
	 * @param  soln  Solution instance
	 */
	public Node[] getLocalSolution(){
		return route;
	}

	public void setLocalSolution(Node route[]){
		this.route = route;
	}
}