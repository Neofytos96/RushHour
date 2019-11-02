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
    public List<Action> getLegalActions() {
//        find which cars have blanks and can move to the blank
//      based on the game state i retrieve the possible car that can move
        List<Action> possibleMoves = new ArrayList();

        List<PositionOccupied> neighbours = new ArrayList();
        int[][] state = new int[nrRows][nrCols];
        for (int i = 0; i < cars.size(); i++) {
            List<Position> l = cars.get(i).getOccupyingPositions();
            for (Position pos : l) {
                state[pos.getRow()][pos.getCol()] = i + 1;
            }
        }
        getNeighbours(neighbours, state);


        for (int pos = 0; pos < neighbours.size(); pos++) {
            for (int car = 0; car < cars.size(); car++) {
                for (int occPos = 0; occPos < cars.get(car).getOccupyingPositions().size(); occPos++) {
                    Position occupiedPos = cars.get(car).getOccupyingPositions().get(occPos);
                    getPossibleMoves(possibleMoves, neighbours, state, pos, car, occupiedPos);
                }
            }
        }
        return possibleMoves;

    }

    private void getPossibleMoves(List<Action> possibleMoves, List<PositionOccupied> neighbours, int[][] state, int pos, int car, Position occupiedPos) {
        if (neighbours.get(pos).getCol() ==
                occupiedPos.getCol()
                && neighbours.get(pos).getRow() ==
                occupiedPos.getRow()
                && neighbours.get(pos).getDirection().equals("down")
                && cars.get(car).isVertical()) {
            for (int steps = 1; steps <= nrRows - (nrRows - neighbours.get(pos).getRow()); steps++) {
                if (state[neighbours.get(pos).getRow() - steps][occupiedPos.getCol()] == 0) {
//                                System.out.println("Car to move more than one : " + cars.indexOf(cars.get(car)));
                    MoveUp moveUp = new MoveUp(steps, cars.indexOf(cars.get(car)));
                    possibleMoves.add(moveUp);
//                                System.out.println("In loop: " + possibleMoves);
                } else break;
            }


//                    found cars that are next to non occupied positions
        } else if (neighbours.get(pos).getCol() ==
                occupiedPos.getCol()
                && neighbours.get(pos).getRow() ==
                occupiedPos.getRow()
                && neighbours.get(pos).getDirection().equals("up")
                && cars.get(car).isVertical()) {
            for (int steps = 1; steps < nrRows - (neighbours.get(pos).getRow()); steps++) {
                if (state[neighbours.get(pos).getRow() + steps][occupiedPos.getCol()] == 0) {
//                                System.out.println("Car to move more than one : " + cars.indexOf(cars.get(car)));
                    MoveDown moveDown = new MoveDown(steps, cars.indexOf(cars.get(car)));
                    possibleMoves.add(moveDown);
//                                System.out.println("In loop: " + possibleMoves);
                } else break;
            }

//                    found cars that are next to non occupied positions
        } else if (neighbours.get(pos).getCol()
                == occupiedPos.getCol()
                && neighbours.get(pos).getRow()
                == occupiedPos.getRow()
                && neighbours.get(pos).getDirection().equals("right")
                && !cars.get(car).isVertical()) {

            for (int steps = 1; steps <= nrCols - (nrCols - neighbours.get(pos).getCol()); steps++) {
                if (state[neighbours.get(pos).getRow()][occupiedPos.getCol() - steps] == 0) {
//                                System.out.println("Car to move more than one : " + cars.indexOf(cars.get(car)));
                    MoveLeft moveLeft = new MoveLeft(steps, cars.indexOf(cars.get(car)));
                    possibleMoves.add(moveLeft);
//                                System.out.println("In loop: " + possibleMoves);
                } else break;
            }

        } else if (neighbours.get(pos).getCol()
                == occupiedPos.getCol()
                && neighbours.get(pos).getRow()
                == occupiedPos.getRow()
                && neighbours.get(pos).getDirection().equals("left")
                && !cars.get(car).isVertical()) {

            for (int steps = 1; steps < nrCols - neighbours.get(pos).getCol(); steps++) {
                if (state[neighbours.get(pos).getRow()][occupiedPos.getCol() + steps] == 0) {
//                                System.out.println("Car to move more than one : " + cars.indexOf(cars.get(car)));
                    MoveRight moveRight = new MoveRight(steps, cars.indexOf(cars.get(car)));
                    possibleMoves.add(moveRight);
//                                System.out.println("In loop: " + possibleMoves);
                } else break;
            }
        }
    }

    private void getNeighbours(List<PositionOccupied> neighbours, int[][] state) {
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

//        System.out.println("Action given: " + action + " the car " + action.getCarIndex());
        GameState gameState = new GameState(new GameState(nrRows, nrCols, cars));


        if (action.getDirection().equals("right")) {
            moveRight(action, gameState);

        } else if (action.getDirection().equals("left")) {
            moveLeft(action, gameState);

        } else if (action.getDirection().equals("up")) {
            gameState.cars.get(action.getCarIndex()).setRow(cars.get(action.getCarIndex()).getRow() - action.getSteps());


        } else if (action.getDirection().equals("down")) {
            moveDown(action, gameState);
        }

//        gameState.printState();
        return gameState;
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

        int hybrid_heuristic = distance + blockingCars.size() + blockingSquared.size();

        return hybrid_heuristic;

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
            if (cars.get(carIndex).getRow() > 0) {
                if (state[cars.get(carIndex).getRow() - 1][cars.get(carIndex).getCol()] != 0) {
                    blockingSquared.add("blocked");
                }

            }
            if (cars.get(carIndex).getRow() < nrRows - 1) {
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
