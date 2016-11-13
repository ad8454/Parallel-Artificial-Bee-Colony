import edu.rit.pj2.Vbl;

/**
 * This class contains an instance of the Solution class such 
 * that reduction variables can be shared by multiple threads
 * running in parallel.
 *
 * @author  Ajinkya Dhaigude
 * @author Sameer Raghuram
 */

public class SolutionArrayVbl implements Vbl{
	public Solution item[];
	
	/**
	 * Default empty constructor.
	 *
	 */
	public SolutionArrayVbl(){
	}
	
	/**
	 * Constructor to initialize Solution instance.
	 *
	 */
	public SolutionArrayVbl(Solution item[]){
		this.item = item;
	}

	/**
	 * Set the shared variables to the given object's shared variables.
	 * Performs deep copy.
	 *
	 * @param  vbl	Object containing shared variables
	 */
	public void set(Vbl vbl){
		for(int i=0; i<item.length; i++)
			this.item[i].copy(((SolutionArrayVbl)vbl).item[i]);
	}
	
	/**
	 * Implement method from inherited Vbl class.
	 *
	 * @param  vbl  Shared variable
	 */
	public void reduce(Vbl vbl){
	}
	
	/**
	 * Implement method to clone object
	 */
	public Object clone(){
		try{
			SolutionArrayVbl vbl = (SolutionArrayVbl) super.clone();
			if (this.item != null){
				for(int i=0; i< item.length; i++){
					vbl.item[i] = (Solution) this.item[i].clone();
				}
			}
			return vbl;
		}
		catch(CloneNotSupportedException e){
			throw new RuntimeException("Bad code");
		}
	}
	
	/**
	 * Class SolnVbl.Max provides three long reduction variables, with
	 * maximum as the reduction operation.
	 */
	public static class Max extends SolutionArrayVbl{
		
		/**
		 * Constructor to initialize shared variables.
		 *
		 * @param  item   Instance of Solution
		 */
		public Max(Solution[] item){
			super(item);
		}
		
		/**
		 * Reduce the given shared variable into this shared variable using the
		 * compareTo implementation. The result is stored in this shared 
		 * variable.
		 *
		 * @param  vbl  Shared variable.
		 */
		public void reduce(Vbl vbl){
			SolutionArrayVbl otherSolnVbl = (SolutionArrayVbl)vbl;
			for(int i=0; i< item.length; i++){
				int comparisionResult = item[i].compareTo(otherSolnVbl.item[i]);
				if(comparisionResult < 0)
					otherSolnVbl.item[i].copy(item[i]);
				else if(comparisionResult == 0){
					int oldTrial = otherSolnVbl.item[i].getTrial();
					otherSolnVbl.item[i].copy(item[i]);
					otherSolnVbl.item[i].incTrial(oldTrial);
				}
			}
			super.set(otherSolnVbl);
		}
	}
}