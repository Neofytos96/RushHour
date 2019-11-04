/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rushhour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import search.Action;
import search.State;

/**
 * @author steven
 */
public class GameState implements search.State {

    boolean[][] occupiedPositions;
    List<Car> cars; // target car is always the first one

    int nrRows;
    int nrCols;

    public GameState(String fileName) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        nrRows = Integer.parseInt(in.readLine().split("\\s")[0]);
        nrCols = Integer.parseInt(in.readLine().split("\\s")[0]);
        String s = in.readLine();
        cars = new ArrayList();

        while (s != null) {
            cars.add(new Car(s));
            s = in.readLine();

        }
        initOccupied();
    }

    public GameState(int nrRows, int nrCols, List<Car> cars) {
        this.nrRows = nrRows;
        this.nrCols = nrCols;
        this.cars = cars;
        initOccupied();
    }

    public GameState(GameState gs) {
        nrRows = gs.nrRows;
        nrCols = gs.nrCols;
        occupiedPositions = new boolean[nrRows][nrCols];
        for (int i = 0; i < nrRows; i++) {
            for (int j = 0; j < nrCols; j++) {
                occupiedPositions[i][j] = gs.occupiedPositions[i][j];
            }
        }
        cars = new ArrayList();
        for (Car c : gs.cars) {
            cars.add(new Car(c));
        }
    }


    public void printState() {
        int[][] state = new int[nrRows][nrCols];

        for (int i = 0; i < cars.size(); i++) {
            List<Position> l = cars.get(i).getOccupyingPositions();
            for (Position pos : l) {
                state[pos.getRow()][pos.getCol()] = i + 1;
            }
        }

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] == 0) {
                    System.out.print("[.]");
                } else {
                    System.out.print("[" + (state[i][j] - 1) + "]");
                }
            }
            System.out.println();
        }
    }

    private void initOccupied() {
        occupiedPositions = new boolean[nrRows][nrCols];
        for (Car c : cars) {
            List<Position> l = c.getOccupyingPositions();
            for (Position pos : l) {
                occupiedPositions[pos.getRow()][pos.getCol()] = true;
            }
        }
    }

    public boolean isGoal() {
        Car goalCar = cars.get(0);
        return goalCar.getCol() + goalCar.getLength() == nrCols;
    }

    public boolean equals(Object o) {
        if (!(o instanceof GameState)) {
            return false;
        } else {
            GameState gs = (GameState) o;
            return nrRows == gs.nrRows && nrCols == gs.nrCols && cars.equals(gs.cars); // note that we don't need to check equality of occupiedPositions since that follows from the equality of cars
        }
    }

    public int hashCode() {
        return cars.hashCode();
    }

    public void printToFile(String fn) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(fn));
            out.println(nrRows);
            out.println(nrCols);
            for (Car c : cars) {
                out.println(c.getRow() + " " + c.getCol() + " " + c.getLength() + " " + (c.isVertical() ? "V" : "H"));
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //finds the neighbouring points of the empty positions in the grid
    public List<Action> getLegalActions() {//      based on the game state retrieve the possible car that can move
        List<Action> possibleMoves = new ArrayList();
        List<PositionOccupied> neighbours = new ArrayList();
        int[][] state = getStateMap();

//        find the neighbouring points of the blank spots in the map
        getNeighbours(neighbours, state);

//check to which cars those neighbouring points overlap and hence find which cars can move
        for (int pos = 0; pos < neighbours.size(); pos++) {
            for (int car = 0; car < cars.size(); car++) {
                for (int occPos = 0; occPos < cars.get(car).getOccupyingPositions().size(); occPos++) {
                    Position occupiedPos = cars.get(car).getOccupyingPositions().get(occPos);
                    PositionOccupied neighbourPos = neighbours.get(pos);
                    getPossibleMoves(possibleMoves, neighbours, state, pos, car, occupiedPos, neighbourPos);
                }
            }
        }
//        System.out.println(possibleMoves);
//        System.out.println("Initial State");
//        printState();
        return possibleMoves;

    }

    private int[][] getStateMap() {
        int[][] state = new int[nrRows][nrCols];
        for (int i = 0; i < cars.size(); i++) {
            List<Position> l = cars.get(i).getOccupyingPositions();
            for (Position pos : l) {
                state[pos.getRow()][pos.getCol()] = i + 1;
            }
        }
        return state;
    }

    private void getPossibleMoves(List<Action> possibleMoves, List<PositionOccupied> neighbours, int[][] state, int pos, int car, Position occupiedPos, PositionOccupied neighbourPos) {
        getMoveUps(possibleMoves, state, car, occupiedPos, neighbourPos);
        getMoveDowns(possibleMoves, state, car, occupiedPos, neighbourPos);
        getMoveLefts(possibleMoves, state, car, occupiedPos, neighbourPos);
        getMoveRights(possibleMoves, state, car, occupiedPos, neighbourPos);
    }

    private void getMoveRights(List<Action> possibleMoves, int[][] state, int car, Position occupiedPos, PositionOccupied neighbourPos) {
        if (neighbourPos.getCol() == occupiedPos.getCol()
                && neighbourPos.getRow() == occupiedPos.getRow()
                && neighbourPos.getDirection().equals("left")
                && !cars.get(car).isVertical()) {
//            check if there are more than one steps to be made in the particular direction
            for (int steps = 1; steps < nrCols - neighbourPos.getCol(); steps++) {
                if (state[neighbourPos.getRow()][occupiedPos.getCol() + steps] == 0) {
                    MoveRight moveRight = new MoveRight(steps, cars.indexOf(cars.get(car)));
                    possibleMoves.add(moveRight);
                } else break;
            }
        }
    }

    private void getMoveLefts(List<Action> possibleMoves, int[][] state, int car, Position occupiedPos, PositionOccupied neighbourPos) {
        if (neighbourPos.getCol() == occupiedPos.getCol()
                && neighbourPos.getRow() == occupiedPos.getRow()
                && neighbourPos.getDirection().equals("right")
                && !cars.get(car).isVertical()) {
//            check if there are more than one steps to be made in the particular direction
            for (int steps = 1; steps <= nrCols - (nrCols - neighbourPos.getCol()); steps++) {
                if (state[neighbourPos.getRow()][occupiedPos.getCol() - steps] == 0) {
                    MoveLeft moveLeft = new MoveLeft(steps, cars.indexOf(cars.get(car)));
                    possibleMoves.add(moveLeft);
                } else break;
            }

        }
    }

    private void getMoveDowns(List<Action> possibleMoves, int[][] state, int car, Position occupiedPos, PositionOccupied neighbourPos) {
        if (neighbourPos.getCol() == occupiedPos.getCol()
                && neighbourPos.getRow() == occupiedPos.getRow()
                && neighbourPos.getDirection().equals("up")
                && cars.get(car).isVertical()) {
//            check if there are more than one steps to be made in the particular direction
            for (int steps = 1; steps < nrRows - (neighbourPos.getRow()); steps++) {
                if (state[neighbourPos.getRow() + steps][occupiedPos.getCol()] == 0) {
                    MoveDown moveDown = new MoveDown(steps, cars.indexOf(cars.get(car)));
                    possibleMoves.add(moveDown);
                } else break;
            }
        }
    }

    private void getMoveUps(List<Action> possibleMoves, int[][] state, int car, Position occupiedPos, PositionOccupied neighbourPos) {
        if (neighbourPos.getCol() == occupiedPos.getCol()
                && neighbourPos.getRow() == occupiedPos.getRow()
                && neighbourPos.getDirection().equals("down")
                && cars.get(car).isVertical()) {
            //            check if there are more than one steps to be made in the particular direction
            for (int steps = 1; steps <= nrRows - (nrRows - neighbourPos.getRow()); steps++) {
                if (state[neighbourPos.getRow() - steps][occupiedPos.getCol()] == 0) {
                    MoveUp moveUp = new MoveUp(steps, cars.indexOf(cars.get(car)));
                    possibleMoves.add(moveUp);
                } else break;
            }


        }
    }

    private void getNeighbours(List<PositionOccupied> neighbours, int[][] state) {
//using a similar method to the getState method in order to find the
//points that are next to each empty point in the map
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] == 0) {
//                    find the neighbour points of the non occupied positions
                    if (i > 0) {
                        neighbours.add(new PositionOccupied(i - 1, j, "up"));
                    }
                    if (i < nrRows - 1) {
                        neighbours.add(new PositionOccupied(i + 1, j, "down"));
                    }
                    if (j > 0) {
                        neighbours.add(new PositionOccupied(i, j - 1, "left"));
                    }
                    if (j < nrCols - 1) {

                        neighbours.add(new PositionOccupied(i, j + 1, "right"));
                    }
                }
            }
        }
    }

    public boolean isLegal(Action action) {

// in order to define if a move is legal or not
// we have to know all the occupied positions by other cars
// and not allow moves outside the box
// check the orientation first of the car and decided if move can be made

        return true;
    }

    public State doAction(Action action) {

        GameState gameState = new GameState(new GameState(nrRows, nrCols, cars));
//        System.out.println(action);

        if (action.getDirection().equals("right")) {
            moveRight(action, gameState);

        } else if (action.getDirection().equals("left")) {
            moveLeft(action, gameState);

        } else if (action.getDirection().equals("up")) {
            moveUp(action, gameState);


        } else if (action.getDirection().equals("down")) {
            moveDown(action, gameState);
        }
//        System.out.println("After Move");
//        gameState.printState();
        return gameState;
    }

    private void moveUp(Action action, GameState gameState) {
        gameState.cars.get(action.getCarIndex()).setRow(cars.get(action.getCarIndex()).getRow() - action.getSteps());
    }

    private void moveDown(Action action, GameState gameState) {
        gameState.cars.get(action.getCarIndex()).setRow(cars.get(action.getCarIndex()).getRow() + action.getSteps());
    }

    private void moveLeft(Action action, GameState gameState) {
        gameState.cars.get(action.getCarIndex()).setCol(cars.get(action.getCarIndex()).getCol() - action.getSteps());
    }

    private void moveRight(Action action, GameState gameState) {
        gameState.cars.get(action.getCarIndex()).setCol(cars.get(action.getCarIndex()).getCol() + action.getSteps());
    }


    public int getEstimatedDistanceToGoal() {
        int distance = getDistanceToLastColumn();

        int[][] state = getState();
        List<Integer> blockingCars = getBlockingCars(state[cars.get(0).getRow()]);

        List<String> blockingSquared = getBlockersOfBlockingCars(state, blockingCars);
//*5 *2 /2
//        System.out.println("Distance: "+ distance/(nrCols-cars.get(0).getLength()));
//        System.out.println("Blocking Cars: "+ blockingCars.size() );
//        System.out.println("Blocking of blocking: "+ blockingSquared.size());
//        System.out.println(blockingCars.size()+blockingSquared.size());
//        Add one to the cost if there are 1 or more cars blocking the way
        int blockingCarsSize = 0;
        if (blockingCars.size()>0){
            blockingCarsSize = blockingCars.size()+1;
        }
        return ( blockingCarsSize + blockingSquared.size());
//        return 0;

    }

    private int getDistanceToLastColumn() {
        int distance = 0;
        int carColumn = cars.get(0).getCol();
        distance = nrCols - carColumn + cars.get(0).getLength();
        return distance;
    }

    private int[][] getState() {
        int[][] state = new int[nrRows][nrCols];

        for (int i = 0; i < cars.size(); i++) {
            List<Position> l = cars.get(i).getOccupyingPositions();
            for (Position pos : l) {
                state[pos.getRow()][pos.getCol()] = i;
            }

        }
        return state;
    }

    private List<String> getBlockersOfBlockingCars(int[][] state, List<Integer> blockingCars) {
        List<String> blockingSquared = new ArrayList();


        for (int carIndex = 0; carIndex < blockingCars.size(); carIndex++) {
           boolean found = false;
            if (cars.get(carIndex).getRow() > 0) {
                if (state[cars.get(carIndex).getRow() - 1][cars.get(carIndex).getCol()] != 0) {
                    found = true;
                }

            }
            if (cars.get(carIndex).getRow() < nrRows - 1 && found) {
                if (state[cars.get(carIndex).getRow() + 1][cars.get(carIndex).getCol()] != 0) {
                    blockingSquared.add("blocked");
                }
            }

        }
        return blockingSquared;
    }

    private List<Integer> getBlockingCars(int[] ints) {
        List<Integer> blockingCars = new ArrayList();

        for (int nextCol = 1; nextCol <= nrCols - cars.get(0).getCol() - cars.get(0).getLength(); nextCol++) {
            int nextCar = ints[cars.get(0).getCol() + cars.get(0).getLength() - 1 + nextCol];
            if (nextCar != 0) {
                blockingCars.add(nextCar);
            }
        }

        return blockingCars;
    }

}
