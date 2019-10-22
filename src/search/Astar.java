/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import java.util.*;

/**
 *
 * @author steven
 */
public class Astar {

    private PriorityQueue<AstarNode> frontier;
    private Set<State> closed;
    private int nodesExpanded;
    
    public Astar(State initialState) {
        frontier = new PriorityQueue();
        closed = new HashSet();
        frontier.add(new AstarNode(initialState, null, null));
    }

    public int getNodesExpanded(){
        return nodesExpanded;
    }
    
    public Node findPathToGoal() {
        while (!frontier.isEmpty()) {
            Node n = frontier.poll();            
            if (!closed.contains(n.getState())) {
                nodesExpanded++;
                closed.add(n.getState());
                if (n.getState().isGoal()) {
                    /*
                 * We have found the goal. From the representation of the node
                 * we can easily retrieve the path that has led us from the
                 * initial state to this solution
                     */
                    return n;
                } else {
                    List<Action> actions = n.getState().getLegalActions();
                    for (Action action : actions) {
                        State child = n.getState().doAction(action);
                        /* 
                     * Cycle checking: to avoid going in circles, we check if we 
                     * have already encountered this state on the path from the initial
                     * state to the current state
                         */
                        //if (!n.alreadyExplored(child)) {
                            frontier.add(new AstarNode(child, n, action));
                        //}
                    }
                }
            }
        }

        /*
         * We have explored the entire tree, but found no goal state. This 
         * means that the problem does not have a solution.
         */
        return null;
    }

}
