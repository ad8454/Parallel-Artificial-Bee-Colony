import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.pj2.vbl.IntVbl;
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
    SolutionArrayVbl employedBees = new SolutionArrayVbl.Max(new Solution[totEmployedBees]);
    SolutionArrayVbl onlookerBees = new SolutionArrayVbl.Max(new Solution[totEmployedBees]);
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
            employedBees.item[i] = new Solution(allNodes, totVehicles, i);
            employedBees.item[i].genRandomSolution(rand);
            //System.out.println(employedBees[i]+"\n");
            onlookerBees.item[i] = new Solution(allNodes, totVehicles, i);
        }

        IntVbl epoch = new IntVbl();

        while(epoch.item++ < MAX_EPOCH){
            // Initiate a parallelFor loop across all cores.
            // Each core will handle a different solution

            parallelFor(lb, ub-1).exec(new Loop() {
                IntVbl thrEpoch;
                SolutionVbl thrBestDiscarded;
                Random thrRand;
                DoubleVbl thrTotWeight;

                public void start(){
                    thrEpoch = threadLocal(epoch);
                    thrTotWeight = threadLocal(totWeight);
                    thrBestDiscarded = threadLocal(bestDiscarded);
                    thrRand = new Random();
                }

                @Override
                public void run(int i) throws Exception {
                    double totWeight = 0;
                    //Random rand = new Random();
                    Solution localSolution = employedBees.item[i];
                    //System.out.println("happened at epoch: "+thrEpoch.item);
                    thrTotWeight.item += localSolution.exploitSolution(thrRand);
                }
            });

            // Now start roulette wheel selection for onlooker bees
            parallelFor(lb, ub-1).exec(new Loop() {

                SolutionArrayVbl thrEmployedBees;
                SolutionArrayVbl thrOnlookerBees;
                SolutionVbl thrBestDiscarded;
                DoubleVbl thrTotWeight;
                Random thrRand;

                public void start(){
                    thrEmployedBees = threadLocal(employedBees);
                    thrOnlookerBees = threadLocal(onlookerBees);
                    thrBestDiscarded = threadLocal(bestDiscarded);
                    thrTotWeight = threadLocal(totWeight);
                    thrRand = new Random();
                }
                @Override
                public void run(int i) throws Exception {
                    double probab = thrTotWeight.item * thrRand.nextDouble();
                    Solution onlookerSoln = thrOnlookerBees.item[i];
                    boolean picked = false;
                    for(Solution soln: thrEmployedBees.item){
                        probab -= soln.getFitness();
                        if(probab <= 0){
                            soln.getDeepCopy(onlookerSoln);
                            picked = true;
                            break;
                        }
                    }
                    if(! picked) // in case of round off error, assign last solution
                        thrEmployedBees.item[totEmployedBees-1].getDeepCopy(onlookerSoln);
                    //onlookerSoln.setLocalSolution(employedBees[totEmployedBees - 1].getLocalSolution());

                    onlookerSoln.exploitSolution(rand);
                    if(thrEmployedBees.item[onlookerSoln.id].compareTo(onlookerSoln)>0){
                        thrEmployedBees.item[onlookerSoln.id] = onlookerSoln;
                    }

                    for(int j = 0; j  < thrEmployedBees.item.length; j++){

                        if(thrEmployedBees.item[j].isExhausted()){
                            if(thrBestDiscarded.item.compareTo(thrEmployedBees.item[j])>0){
                                bestDiscarded.item = thrEmployedBees.item[j];
                            }
                            thrEmployedBees.item[j].genRandomSolution(thrRand);
                        }
                    }
                }
            });

        }

        // Final reduction to get best solution
        for(int i=0; i<employedBees.item.length; i++){
            if(bestDiscarded.item.compareTo(employedBees.item[i])>0){
                bestDiscarded.item = employedBees.item[i];
            }
        }

        System.out.println(bestDiscarded.item);

    }

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
}
