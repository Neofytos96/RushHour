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
    boolean[][] nonOccupiedPositions;
    List<Car> cars; // target car is always the first one

    int methodCounter;

    int nrRows;
    int nrCols;

    GameState updateState;


    public GameState(String fileName) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        nrRows = Integer.parseInt(in.readLine().split("\\s")[0]);
        nrCols = Integer.parseInt(in.readLine().split("\\s")[0]);
        String s = in.readLine();
//        System.out.println("this is s: "+ s);
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
                    System.out.print(".");
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
        List<PositionOccupied> neighbours = new ArrayList();
        List<Action> possibleMoves = new ArrayList();

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


        for (int pos = 0; pos < neighbours.size(); pos++) {
            for (int car = 0; car < cars.size(); car++) {
                for (int occPos = 0; occPos < cars.get(car).getOccupyingPositions().size(); occPos++) {
                    Position occupiedPos = cars.get(car).getOccupyingPositions().get(occPos);
                    if (neighbours.get(pos).getCol() ==
                            occupiedPos.getCol()
                            && neighbours.get(pos).getRow() ==
                            occupiedPos.getRow()
                            && neighbours.get(pos).getDirection().equals("down")
                            && cars.get(car).isVertical()) {
//                        System.out.println("Up: " + cars.indexOf(cars.get(car)));
//                        cars.get(car).setDirectionToMove("up");
//                        movingCars.add(cars.get(car));
                        MoveUp moveUp = new MoveUp(1, cars.indexOf(cars.get(car)));
                        possibleMoves.add(moveUp);

//                    found cars that are next to non occupied positions
                    } else if (neighbours.get(pos).getCol() ==
                            occupiedPos.getCol()
                            && neighbours.get(pos).getRow() ==
                            occupiedPos.getRow()
                            && neighbours.get(pos).getDirection().equals("up")
                            && cars.get(car).isVertical()) {
//                        System.out.println("Down: " + cars.indexOf(cars.get(car)));
//                        cars.get(car).setDirectionToMove("down");
//                        movingCars.add(cars.get(car));
                        MoveDown moveDown = new MoveDown(1, cars.indexOf(cars.get(car)));
                        possibleMoves.add(moveDown);

//                    found cars that are next to non occupied positions
                    } else if (neighbours.get(pos).getCol()
                            == occupiedPos.getCol()
                            && neighbours.get(pos).getRow()
                            == occupiedPos.getRow()
                            && neighbours.get(pos).getDirection().equals("right")
                            && !cars.get(car).isVertical()) {

//                        System.out.println("Left: " + cars.indexOf(cars.get(car)));
//                        cars.get(car).setDirectionToMove("left");
//                        movingCars.add(cars.get(car));
                        MoveLeft moveLeft = new MoveLeft(1, cars.indexOf(cars.get(car)));
                        possibleMoves.add(moveLeft);

                    } else if (neighbours.get(pos).getCol()
                            == occupiedPos.getCol()
                            && neighbours.get(pos).getRow()
                            == occupiedPos.getRow()
                            && neighbours.get(pos).getDirection().equals("left")
                            && !cars.get(car).isVertical()) {

//                        System.out.println("Right: " + cars.indexOf(cars.get(car)));
//                        cars.get(car).setDirectionToMove("right");
//                        movingCars.add(cars.get(car));
                        MoveRight moveRight = new MoveRight(1, cars.indexOf(cars.get(car)));
                        possibleMoves.add(moveRight);
                    }
                }
            }
        }
System.out.println("Possible Moves: "+possibleMoves);
        System.out.println("Initial State: ");
        printState();
        return possibleMoves;

    }

    public boolean isLegal(Action action) {

// in order to define if a move is legal or not
// we have to know all the occupied positions by other cars
// and not allow moves outside the box
// check the orientation first of the car and decided if move can be made

        return true;
    }

    public State doAction(Action action) {

        System.out.println("Action given: " + action + " the car " + action.getCarIndex());
        GameState gameState = new  GameState( new GameState (nrRows, nrCols, cars));


        if (action.getDirection().equals("right")) {
            gameState.cars.get(action.getCarIndex()).setCol(cars.get(action.getCarIndex()).getCol()+ action.getSteps());

        } else if (action.getDirection().equals("left")) {
            gameState.cars.get(action.getCarIndex()).setCol(cars.get(action.getCarIndex()).getCol()- action.getSteps());

        } else if (action.getDirection().equals("up")) {
            gameState.cars.get(action.getCarIndex()).setRow(cars.get(action.getCarIndex()).getRow()- action.getSteps());


        } else if (action.getDirection().equals("down") ) {
            gameState.cars.get(action.getCarIndex()).setRow(cars.get(action.getCarIndex()).getRow()+ action.getSteps());
        }

        gameState.printState();
        return gameState;
    }


    public int getEstimatedDistanceToGoal() {
//        get the column of the car and the total columns
        int distance = 0;
        int carColumn = cars.get(0).getCol() ;
        distance = nrCols - carColumn + cars.get(0).getLength();

        return distance;


//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
