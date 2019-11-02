package rushhour;

import search.Action;

import java.util.List;

public class MoveLeft implements Action {
    public int steps;
    public int carIndex;
    private String direction;

    public String getDirection() {
        return "left";
    }

    public MoveLeft(int steps, int carIndex){
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

    public String toString(){

        return "(" + carIndex + "," + "left" + ","+ steps+ ")";

    }


}
