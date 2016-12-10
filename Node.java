/**
 * Node represents a location in our graph.
 * Customers as well as the depot are nodes.
 * The id of a depot is 0.
 *
 * @author  Ajinkya Dhaigude
 * @author  Sameer Raghuram
 */
public class Node {
	public int id;
	public int x;
	public int y;

	/**
	 * String representation of a node in the graph.
	 *
	 * @return String	Node String repr
     */
	public String toString(){
		return id+"("+x+", "+y+")  ";
	}

	/**
	 * Checks if this is an instance of a depot.
	 *
	 * @return boolean
     */
	public boolean isDepot(){
		return this.id == 0;
	}
}
