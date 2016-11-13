import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.util.Instance;
import edu.rit.pj2.vbl.DoubleVbl;

import java.util.Random;

/**
 * Created by Sameer on 11/12/2016.
 */
public class ABCSmp extends Task{

    int totEmployedBees = 100;		//TODO: find way to determine
    int MAX_EPOCH = 10000;
    int totVehicles = 4;
    Graph graph = null;
    Random rand;
    int lb=0;
    int ub = totEmployedBees;
    SolutionVbl employedBees[] = new SolutionVbl[totEmployedBees];
    SolutionVbl onlookerBees[] = new SolutionVbl[totEmployedBees];
    SolutionVbl bestDiscarded;
    DoubleVbl totWeight;

    public void main(String args[]){

        if (args.length != 1) {
            usage(0);
        }

        // Get Graph instance
        try{
            graph = (Graph) Instance.newInstance (args[0]);
        } catch(Exception e){
            usage(1);
        }
        bestDiscarded = new SolutionVbl.Max();
        totWeight = new DoubleVbl.Sum(0);
        rand = new Random();

        // Get total vertices
        int totNodes = graph.getNodes();
        Node allNodes[] = new Node[totNodes];

        // Get all nodes in graph
        for(int i=0; i<totNodes; i++){
            allNodes[i] = new Node();
            graph.nextVertex(allNodes[i]);
        }

        // Generate initial solutions
        for(int i=0; i<totEmployedBees; i++){
            //employedBees[i] = new Bee(totNodes, totVehicles);
            employedBees[i].item = new Solution(allNodes, totVehicles, i);
            employedBees[i].item.genRandomSolution(rand);
            //System.out.println(employedBees[i]+"\n");
            onlookerBees[i].item = new Solution();
        }

        int epoch = 0;

        while(epoch++ < MAX_EPOCH){
            // Initiate a parallelFor loop across all cores.
            // Each core will handle a different solution

            parallelFor(lb, ub).exec(new Loop() {

                SolutionVbl thrBestDiscarded;
                Random thrRand;
                DoubleVbl thrTotWeight;

                public void start(){
                    thrTotWeight = threadLocal(totWeight);
                    thrBestDiscarded = threadLocal(bestDiscarded);
                    thrRand = new Random();
                }

                @Override
                public void run(int i) throws Exception {
                    double totWeight = 0;
                    //Random rand = new Random();
                    SolutionVbl localSolution = employedBees[i];
                    thrTotWeight.item += localSolution.item.exploitSolution(thrRand);
                }
            });

            // Now start roulette wheel selection for onlooker bees
            parallelFor(lb, ub).exec(new Loop() {
                @Override
                public void run(int i) throws Exception {

                }
            });
        }
    }
}
