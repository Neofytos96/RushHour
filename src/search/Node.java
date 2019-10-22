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
 * A Node is essentially a state from the problem domain, but with some extra
 * bookkeeping. In particular, this class allows us to retrieve that path from
 * the initial state to the current state, which is needed to obtain the actual 
 * solution to the problem once a goal state is found, and for pruning (cycle checking).
 */
public class Node{

    protected State st;   // state corresponding to this node
    protected Node previousNode; // parent of this node in the search tree
    protected Action lastAction; // action that was taken to get from the previousNode to this node
    protected int depth;
    protected int cost;
    
    public Node(State st, Node previousNode, Action lastAction) {        
        this.st = st;
        this.previousNode = previousNode;
        this.lastAction=lastAction;
        if(previousNode==null)
            depth=0;
        else
            depth = previousNode.depth+1;
        if(previousNode==null)
            cost=0;
        else
            cost = previousNode.getCost() + lastAction.getCost();
    }

    public State getState() {
        return st;
    }

    public int getDepth(){
        return depth;
    }
    
    public List<State> getPath() {
        List<State> res;
        if (previousNode != null)
            res = previousNode.getPath();
        else
            res = new ArrayList();
        res.add(st);
        return res;
    }        
    
    public List<Action> getActions() {
        List<Action> res;
        if (previousNode != null){
            res = previousNode.getActions();
            res.add(lastAction);
        }
        else
            res = new ArrayList();       
        return res;
    }        
    
    public int getCost(){
        return cost;
    }
    
    
    // Check if st0 has already been considered on the path from the initial state to the current state
    public boolean alreadyExplored(State st0){
        if(previousNode!=null && previousNode.alreadyExplored(st0)) //st0 correspond to one of the ancestors of the current state in the search tree
            return true;
        else if(st.equals(st0)) //st0 is the current state
            return true;
        else // st0 has not yet been considered on this branch of the search tree
            return false;
    }
        
}
