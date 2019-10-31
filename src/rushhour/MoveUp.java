package rushhour;

import search.Action;
import search.State;

public class MoveUp implements Action {

    public int getCarIndex() {
        return carIndex;
    }

    public void setCarIndex(int carIndex) {
        this.carIndex = carIndex;
    }

    private int carIndex;

    public int getCost() {
        return 1;
    }

    public String toString(){
        return "move up";
    }

}
