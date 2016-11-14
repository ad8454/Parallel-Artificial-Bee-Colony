import edu.rit.pj2.Vbl;

/**
 * This class contains an instance of the Solution class such 
 * that reduction variables can be shared by multiple threads
 * running in parallel.
 *
 * @author  Ajinkya Dhaigude
 * @author Sameer Raghuram
 */

public class SolutionVbl implements Vbl{
	public Solution item;
	
	/**
	 * Default empty constructor.
	 *
	 */
	public SolutionVbl(){
	}
	
	/**
	 * Constructor to initialize Solution instance.
	 *
	 */
	public SolutionVbl(Solution item){
		this.item = item;
	}

	/**
	 * Set the shared variables to the given object's shared variables.
	 * Performs deep copy.
	 *
	 * @param  vbl	Object containing shared variables
	 */
	public void set(Vbl vbl){
		this.item.copy(((SolutionVbl)vbl).item);
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
			SolutionVbl vbl = (SolutionVbl) super.clone();
			if (this.item != null)
				vbl.item = (Solution) this.item.clone();
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
	public static class Max extends SolutionVbl{
		
		/**
		 * Constructor to initialize shared variables.
		 *
		 * @param  item   Instance of Solution
		 */
		public Max(Solution item){
			super(item);
		}

        /*public Max(){
            super();
            this.item = new Solution();
        }*/
		
		/**
		 * Reduce the given shared variable into this shared variable using the
		 * compareTo implementation. The result is stored in this shared 
		 * variable.
		 *
		 * @param  vbl  Shared variable.
		 */
		public void reduce(Vbl vbl){
			SolutionVbl otherSolnVbl = (SolutionVbl)vbl;
			if(item.compareTo(otherSolnVbl.item) > 0)
				super.set(vbl);
		}
	}
}