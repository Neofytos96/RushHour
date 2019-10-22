/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rushhour;

import java.util.List;
import search.Action;
import search.Astar;
import search.Node;
import search.State;

/**
 *
 * @author steven
 */
public class RushHour {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        GameState gs = new GameState(args[0]);
        Astar as = new Astar(gs);
        long startTime = System.currentTimeMillis();
        Node goal = as.findPathToGoal();
        long endTime = System.currentTimeMillis();
        if(goal!=null)
            System.out.println(args[0] + " " + (endTime - startTime) + " " + as.getNodesExpanded() + " " + goal.getCost());
        else
            System.out.println("No solution was found.");
    }

}
