import edu.rit.util.Random;
import java.util.NoSuchElementException;

/**
 * Class RandomGraph encapsulates a random undirected graph generated using the
 * Gilbert procedure. Each possible edge appears in the graph with probability
 * E/choose(V,2).
 *
 * @author  Alan Kaminsky
 * @version 01-Sep-2016
 */
public class RandomGraph implements Graph{

	private int totNodes;          // Number of vertices
	private int E;          // Number of edges
	private int toGenerate; // Number of edges still to generate
	private int range;
	private Random prng;    // Pseudorandom number generator

	/**
	 * Construct a new random graph.
	 *
	 * @param  totNodes     Number of vertices, V &ge; 2.
	 * @param  E     Number of edges, 0 &le; E &le; choose(V,2).
	 * @param  seed  Random seed.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if V or E is out of range.
	 */
	public RandomGraph (int totNodes, int range, long seed){
		// Verify preconditions.
		this.totNodes = totNodes;
		this.E = totNodes * (totNodes - 1) /2 ;
		this.range = range;
		if (totNodes < 2)
			throw new IllegalArgumentException (String.format
				("RandomGraph(): V = %d illegal", totNodes));

		// Initialize fields.
		toGenerate = E;
		prng = new Random (seed);
	}

	/**
	 * Returns the number of vertices in this graph, V.
	 */
	public int getNodes(){
		return totNodes;
	}

	/**
	 * Returns the number of edges in this graph, E.
	 */
	public int getEdges(){
		return E;
	}

	/**
	 * Obtain the next edge in this graph. This method must be called
	 * repeatedly, E times, to obtain all the edges. Each time this method is
	 * called, it stores, in the v1 and v2 fields of object e, the vertices
	 * connected by the next edge. Each vertex is in the range 0 .. V-1.
	 *
	 * @param  edge  Edge object in which to store the vertices.
	 *
	 * @exception  NoSuchElementException
	 *     (unchecked exception) Thrown if this method is called more than E
	 *     times.
	 */
	public void nextVertex (Node node){
		// Verify preconditions.
		if (toGenerate == 0)
			throw new NoSuchElementException();
		// TODO : might coincide
		node.id = -- toGenerate;
		node.x = prng.nextInt(range);
		node.y = prng.nextInt(range);
	}
}
