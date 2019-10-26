package rushhour;

import search.Action;
import search.State;

public class MoveLeft implements Action {
    public int getCost() {
        return 1;
    }

    public String toString(){
        return "move left";
    }

}
