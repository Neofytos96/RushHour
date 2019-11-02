package rushhour;

import search.Action;

import java.util.List;

public class MoveDown implements Action {

    private int steps;
    private int carIndex;

    public String getDirection() {
        return "down";
    }

    private String direction;

    public MoveDown(int steps, int carIndex){
        this.steps = steps;
        this.carIndex = carIndex;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setCarIndex(int carIndex) {
        this.carIndex = carIndex;
    }

    public int getSteps() {

        return steps;
    }

    public int getCarIndex() {
        return carIndex;
    }

    public int getCost() {
        return 1;
    }

    public String toString() {
        return "(" + carIndex + "," + "down" + ","+ steps+ ")";

    }

}
