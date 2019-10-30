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
    List<Car> carsHorizontal;
    List<Car> carsVertical;
    int nrRows;
    int nrCols;

    private static MoveUp move_up = new MoveUp();
    private static MoveDown move_down = new MoveDown();
    private static MoveRight move_right = new MoveRight();
    private static MoveLeft move_left = new MoveLeft();

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
        System.out.println(occupiedPositions[0][0]);
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

    public List<Position> getNonOccupiedPositions() {
        List<Position> nonOccupiedPositions = new ArrayList();

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
//                    nonOccupiedPositions[i][j] = true;
//                    find the neighbour points of the non occupied positions

                    nonOccupiedPositions.add(new Position(i, j));
//                    System.out.print(i + "," + j + "   ");
                }
            }
        }
//        System.out.println("Non occupied: " + nonOccupiedPositions);
//        printState();
        return nonOccupiedPositions;
    }

    //finds the neighbouring points of the empty positions in the grid
    public List<Car> getMovingCars() {
        List<Position> nonOccupiedPositions = new ArrayList();
        List<PositionOccupied> neighbours = new ArrayList();
        List<Car> movingCars = new ArrayList();



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
//                    nonOccupiedPositions[i][j] = true;
//                    find the neighbour points of the non occupied positions
                    if (i > 0) {
                        neighbours.add(new PositionOccupied(i - 1, j, "up"));
                    }
                    if (i < nrRows - 1) {
                        neighbours.add(new PositionOccupied(i + 1, j, "down"));
                    }
                    if (j > 0) {
//                        System.out.println("First: "+j);
                        neighbours.add(new PositionOccupied(i, j - 1, "left"));
                    }
                    if (j < nrCols - 1) {
//                        System.out.println("Second: "+j);

                        neighbours.add(new PositionOccupied(i, j + 1, "right"));
                    }
//                    if (j > 0 && j < nrCols) {
//                        neighbours.add(new Position(i, j + 1));
//                        neighbours.add(new Position(i, j - 1));
//
//                    }
                    nonOccupiedPositions.add(new Position(i, j));
//                    System.out.print(i + "," + j + "   ");
                }
            }
        }
//        System.out.println("Non occupied: " + nonOccupiedPositions);
//        printState();

        for (int pos = 0; pos < neighbours.size(); pos++) {
            for (int car = 0; car < cars.size(); car++) {
                for (int occPos = 0; occPos < cars.get(car).getOccupyingPositions().size(); occPos++) {
                    if (neighbours.get(pos).getCol() ==
                            cars.get(car).getOccupyingPositions().get(occPos).getCol()
                            && neighbours.get(pos).getRow() ==
                            cars.get(car).getOccupyingPositions().get(occPos).getRow()
                            && neighbours.get(pos).getDirection().equals("down")
                            && cars.get(car).isVertical()) {
//                        System.out.println("Up: " + cars.indexOf(cars.get(car)));
                        cars.get(car).setDirectionToMove("up");
                        movingCars.add(cars.get(car));


//                    found cars that are next to non occupied positions
                    }
                    else if (neighbours.get(pos).getCol() ==
                            cars.get(car).getOccupyingPositions().get(occPos).getCol()
                            && neighbours.get(pos).getRow() ==
                            cars.get(car).getOccupyingPositions().get(occPos).getRow()
                            && neighbours.get(pos).getDirection().equals("up")
                            && cars.get(car).isVertical()) {
//                        System.out.println("Down: " + cars.indexOf(cars.get(car)));
                        cars.get(car).setDirectionToMove("down");
                        movingCars.add(cars.get(car));


//                    found cars that are next to non occupied positions
                    }
                    else if (neighbours.get(pos).getCol()
                            == cars.get(car).getOccupyingPositions().get(occPos).getCol()
                            && neighbours.get(pos).getRow()
                            == cars.get(car).getOccupyingPositions().get(occPos).getRow()
                            && neighbours.get(pos).getDirection().equals("right")
                            && !cars.get(car).isVertical()) {

//                        System.out.println("Left: " + cars.indexOf(cars.get(car)));
                        cars.get(car).setDirectionToMove("left");
                        movingCars.add(cars.get(car));

                    }else if (neighbours.get(pos).getCol()
                            == cars.get(car).getOccupyingPositions().get(occPos).getCol()
                            && neighbours.get(pos).getRow()
                            == cars.get(car).getOccupyingPositions().get(occPos).getRow()
                            && neighbours.get(pos).getDirection().equals("left")
                            && !cars.get(car).isVertical()) {

//                        System.out.println("Right: " + cars.indexOf(cars.get(car)));
                        cars.get(car).setDirectionToMove("right");
                        movingCars.add(cars.get(car));

                    }
                }
            }
        }
        return movingCars;
    }



    public List<Action> getLegalActions() {
//        find which cars have blanks and can move to the blank
//      based on the game state i retrieve the possible car that can move

        ArrayList<Action> possibleMoves = new ArrayList();
        List<Car> movingCars = getMovingCars();
        for (int i=0; i<movingCars.size(); i++){
            System.out.println(cars.indexOf(movingCars.get(i))+" can go "+ movingCars.get(i).getDirectionToMove());
            if (movingCars.get(i).getDirectionToMove().equals("right")){
                possibleMoves.add(move_right);
            }else if (movingCars.get(i).getDirectionToMove().equals("left")){
                possibleMoves.add(move_left);
            }else if (movingCars.get(i).getDirectionToMove().equals("up")){
                possibleMoves.add(move_up);
            }else if (movingCars.get(i).getDirectionToMove().equals("down")){
                possibleMoves.add(move_down);
            }
        }
        printState();

//
//        if (isLegal(move_up))
//            res.add(move_up);
//        if (isLegal(move_down))
//            res.add(move_down);
//        if (isLegal(move_right))
//            res.add(move_right);
//        if (isLegal(move_left))
//            res.add(move_left);
//
//
        return possibleMoves;

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isLegal(Action action) {

// in order to define if a move is legal or not
// we have to know all the occupied positions by other cars
// and not allow moves outside the box
// check the orientation first of the car and decided if move can be made


//        if (action instanceof MoveUp)
//            check the above row
//            return true;
//        else if (action instanceof MoveDown)
//                check the row below
//            return true;
//        else if (action instanceof MoveRight)
//                check the column + 1
//            return true;
//        else if (action instanceof MoveLeft)
//                check the column - 1
//            return true;
//        else

            return true;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public State doAction(Action action) {
        GameState res = new GameState(nrRows, nrCols, cars);
//        if(action instanceof MoveRight)


//        else if(action instanceof MoveRight)
//            res.moveBlank(rowBlank,colBlank+1);
//        else if(action instanceof MoveUp)
//            res.moveBlank(rowBlank-1,colBlank);
//        else if(action instanceof MoveDown)
//            res.moveBlank(rowBlank+1,colBlank);
//        else
//            return null;
//        return res;
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public int getEstimatedDistanceToGoal() {
//        get the column of the car and the total columns
        int distance = 0;
        int carColumn = cars.get(0).getCol();
        distance = nrCols - carColumn;

        return distance;


//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
