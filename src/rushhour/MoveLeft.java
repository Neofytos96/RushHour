package rushhour;

import search.Action;
import search.State;

public class MoveLeft implements Action {
    private int carIndex;
    public int getCarIndex() {
        return carIndex;
    }

    public void setCarIndex(int carIndex) {
        this.carIndex = carIndex;
    }
    public int getCost() {
        return 1;
    }

    public String toString(){
        return "move left";
    }

}
