/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package search;

import java.util.*;
        
/**
 *
 * @author steven
 * 
 *  A State represents a state of the problem domain. It can be a goal state or 
 *  a normal state, it has associated actions, and there may be heuristic 
 *  information that allows us to estimate the distance from this state to a 
 *  goal state, i.e. the estimated cost to reach the goal, starting from this 
 *  state. Note that for A* to be optimal, these estimation must be such that it
 *  never overestimates the true cost.
 * 
 */
public interface State {
    
    public List<Action> getLegalActions();
    public boolean isGoal();
    public void printState();
    public boolean isLegal(Action action);
    public State doAction(Action action);
    public int getEstimatedDistanceToGoal();
    public boolean equals(Object obj);
}
