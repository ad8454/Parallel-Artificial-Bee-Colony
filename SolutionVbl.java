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
	public Solution soln;
	
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
	public SolutionVbl(Solution soln){
		this.soln = soln;
	}

	/**
	 * Set the shared variables to the given object's shared variables.
	 * Performs deep copy.
	 *
	 * @param  vbl	Object containing shared variables
	 */
	public void set(Vbl vbl){
		this.soln.copy(((SolutionVbl)vbl).soln);
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
			if (this.soln != null)
				vbl.soln = (Solution) this.soln.clone();
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
		 * @param  soln   Instance of Solution
		 */
		public Max(Solution soln){
			super(soln);
		}
		
		/**
		 * Reduce the given shared variable into this shared variable using the
		 * compareTo implementation. The result is stored in this shared 
		 * variable.
		 *
		 * @param  vbl  Shared variable.
		 */
		public void reduce(Vbl vbl){
			SolutionVbl otherSolnVbl = (SolutionVbl)vbl;
			if(soln.compareTo(otherSolnVbl.soln) > 1)
				super.set(vbl);
		}
	}
}