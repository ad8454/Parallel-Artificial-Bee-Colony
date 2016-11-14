import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.pj2.vbl.IntVbl;
import edu.rit.util.Instance;
import edu.rit.pj2.vbl.DoubleVbl;

import java.util.Random;

/**
 * Created by Sameer on 11/12/2016.
 */
public class ABCSmp extends Task {

    int totEmployedBees;        //TODO: find way to determine
    int MAX_EPOCH=1000;
    int totVehicles;
    Graph graph;
    Random rand;
    int lb;
    int ub;
    Solution[] employedBees;
    Solution[] onlookerBees;
    SolutionVbl bestDiscarded;
    DoubleVbl totWeight;

    public void main(String args[]) {

        // Usage Statement
        if (args.length != 1) {
            usage(0);
        }

        // Get Graph instance
        try {
            graph = (Graph) Instance.newInstance(args[0]);
        } catch (Exception e) {
            usage(1);
        }

        totWeight = new DoubleVbl.Sum(0);
        rand = new Random();
        lb = 0;
        ub = cores() - 1;

        // Get total vertices
        int totNodes = graph.getNodes();
        Node allNodes[] = new Node[totNodes];
        totEmployedBees = 1000;
        totVehicles = 4;
        employedBees = new Solution[totEmployedBees];
        onlookerBees = new Solution[totEmployedBees];

        // Get all nodes in graph
        for (int i = 0; i < totNodes; i++) {
            allNodes[i] = new Node();
            graph.nextVertex(allNodes[i]);
        }

        bestDiscarded = new SolutionVbl.Max(new Solution(allNodes, totVehicles, 0));


        // Generate initial solutions
        for (int i = 0; i < totEmployedBees; i++) {
            //employedBees[i] = new Bee(totNodes, totVehicles);
            employedBees[i] = new Solution(allNodes, totVehicles, i);
            employedBees[i].genRandomSolution(rand);
            //System.out.println(employedBees[i]+"\n");
            onlookerBees[i] = new Solution(allNodes, totVehicles, i);
            onlookerBees[i].genRandomSolution(rand);
        }
        int range = totEmployedBees/cores();
        int epoch = 0;

        while (epoch++ < MAX_EPOCH) {

            parallelFor(lb, ub).exec(new Loop() {

                Random thrRand;
                Solution[] thrEmployedBees;
                SolutionVbl thrBestDiscarded;

                public void start(){

                    thrBestDiscarded = threadLocal(bestDiscarded);
                    thrRand = new Random();
                }

                @Override
                public void run(int j) throws Exception {

                    int offset = j * range;
                    double totWeight = 0;

                    for(int i=offset; i < range; i++){
                        Solution localSolution = employedBees[i];

                        totWeight += localSolution.exploitSolution(rand);
                    }
                    // Onlooker bee phase
                    for(int i=offset; i < range; i++){
                        // The roulette wheel selection
                        double probab = totWeight * rand.nextDouble();

                        Solution onlookerSoln = onlookerBees[i];
                        boolean picked = false;
                        for(int k=offset; k<range; k++){
                            Solution soln = employedBees[k];
                            probab -= soln.getFitness();
                            if(probab <= 0){
                                soln.getDeepCopy(onlookerSoln);
                                picked = true;
                                break;
                            }
                        }
                        if(! picked) // in case of round off error, assign last solution
                            employedBees[offset + range-1].getDeepCopy(onlookerSoln);

                        totWeight+= onlookerSoln.exploitSolution(rand);

                        // Set solution to employedbees array if better
                        if(employedBees[onlookerSoln.id].compareTo(onlookerSoln)>0){
                            employedBees[onlookerSoln.id] = onlookerSoln;
                        }
                    }
                    // Discard any exhausted solutions
                    for(int i=offset; i<range; i++){
                        if(employedBees[i].isExhausted()){
                            // See if our discarded is the best one yet (to be discarded)
                            if(bestDiscarded.item.compareTo(employedBees[i])>0){
                                bestDiscarded.item = employedBees[i];
                            }
                            employedBees[i].genRandomSolution(rand);
                        }
                    }

                }
            });

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
