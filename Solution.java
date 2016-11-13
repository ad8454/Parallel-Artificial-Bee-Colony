import java.util.Random;

/**
 * This class provides three long reduction
 * variables to be shared by multiple threads
 * running in parallel.
 *
 * @author  Ajinkya Dhaigude
 * @author  Sameer Raghuram
 */
 
class Solution implements Cloneable, Comparable<Solution>{

    // shared variables
	private Node[] allNodes;
	private int EXPLOITATION_LIMIT = 10;
	private Node route[];	// Solution path eg {0->1->2->3->0->4->5->6->0->7->8}
	private int totalNodes;	// Total nodes
	private double fitness = -1;
    private int TRIAL_LIMIT = 100;
    private int trial = 0;
    public int id;


	public Solution(){}

	/**
	 * Constructor to initialize shared variables.
	 *
	 * @param  allNodes
	 * @param  totVehicles
	 * @param  id
	 */
	public Solution(Node allNodes[], int totVehicles, int id){
        this.id = id;
		this.allNodes = allNodes;
		totalNodes = allNodes.length;
		route = new Node[totalNodes + totVehicles];
		for(int i=0; i<route.length; i++){
			if(i < totalNodes)
				route[i] = allNodes[i];
			else
				route[i] = allNodes[0]; 	// depot
		}
	}


	public double computeFitness(){

		double distance = computeDistance();
		return 1/distance;
	}

	public double computeDistance(){
		double distance = 0;
		for(int i=0; i<route.length-1; i++){
			distance += getDistance(route[i], route[i+1]);
		}
		return distance;
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

	public Node[] getRoute(){
		return this.route;
	}

	public void setRoute(Node[] route){
		this.route = route;
	}

    public void setTrial(int trial){
		this.trial = trial;
    }

	public void incTrial(int num){
		this.trial += num;
    }

    public int getTrial(){
		return this.trial;
    }

	public boolean isExhausted(){
		return this.trial>this.TRIAL_LIMIT;
	}

	public int getSize(){
		return route.length;
	}

	public int getTotNodes(){
		return totalNodes;
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
		//this.fitness = computeFitness();
	}

	public double exploitSolution(Random rand){
		for(int i = 0; i<EXPLOITATION_LIMIT; i++){
			double oldFitness = computeFitness();
			int idx1 = rand.nextInt(route.length - 2) + 1;
			int idx2 = rand.nextInt(route.length - 2) + 1;
			swap(idx1, idx2);
			double newFitness = computeFitness();
			if(oldFitness > newFitness) {
				// Increment the number of trials to indicate exhaustion of
				// a food source
				incTrial(1);
				swap(idx1, idx2); //revert
				newFitness = oldFitness;		//revert to old fitness
			}
			// reset the number of trials of solution to indicate improvement
			if(!(oldFitness == newFitness)){
				setTrial(0);
			}
			setFitness(newFitness);
		}
		return this.fitness;
	}

	/**
	 * Method to deep copy shared variables.
	 *
	 *
	 */
	public Node[] getLocalSolution(){
		return route;
	}

	public void setLocalSolution(Node route[]){
		this.route = route;
	}

	public void getDeepCopy(Solution copy){
		copy.setFitness(this.fitness);
		copy.setRoute(this.route);
		copy.setTrial(this.trial);
		copy.id = this.id;
		copy.totalNodes = this.totalNodes;
	}

	@Override
	public int compareTo(Solution other) {
		// This soltion is better
		if(this.fitness > other.fitness){
			return -1;
		}
		// The other solution is better
		else if(this.fitness < other.fitness){
			return 1;
		}
		else{
			return 0;
		}
	}

	public String toString(){
		String toReturn1 = "Fitness: "+computeDistance()+"\nRoute:";
		String toReturn2="";
		for(Node n:route){
			toReturn2 += n.toString();
		}
		return  toReturn1 + toReturn2;
	}
	
	/**
	 * Implement method to clone object
	 */
	public Object clone()
	{
		try{
			Solution soln = (Solution) super.clone();
			soln.copy(this);
			return soln;
		}
		catch(CloneNotSupportedException e){
			throw new RuntimeException("Bad code");
		}
	}
	
	/**
	 * Method to deep copy shared variables.
	 *
	 * @param  soln  Solution instance
	 */
	public Solution copy(Solution soln){
		this.setFitness(soln.getFitness());
		this.setRoute(soln.getRoute());
		this.setTrial(soln.getTrial());
		this.id = soln.id;
		this.totalNodes = soln.totalNodes;
		return this;
	}
}