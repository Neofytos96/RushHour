/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package search;

import rushhour.Car;

import java.util.List;

/**
 *
 * @author steven
 */
public interface Action {
    
    public int getCost();
    public String toString();
    public int getCarIndex();
    public int getSteps();
    public String getDirection();
}
