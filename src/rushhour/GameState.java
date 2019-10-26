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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import search.Action;
import search.State;

/**
 * @author steven
 */
public class GameState implements search.State {

    boolean[][] occupiedPositions;
    boolean[][] nonOccupiedPositions;
    List<Car> cars; // target car is always the first one    
    int nrRows;
    int nrCols;

    List<Integer> colCars;
    List<Integer> rowCars;


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

    public List<Position> getNonOccupyingPositions() {
        List<Position> nonOccupiedPositions = new ArrayList();
        List<Position> neighbours = new ArrayList();

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
                    if (i > 0) {
                        neighbours.add(new Position(i - 1, j));
                    }
                    if (i < nrRows -1) {
                        neighbours.add(new Position(i + 1, j));
                    }
                    if (j > 0) {
//                        System.out.println("First: "+j);
                        neighbours.add(new Position(i , j-1));
                    }
                    if (j < nrCols -1) {
//                        System.out.println("Second: "+j);

                        neighbours.add(new Position(i , j+1));
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
        System.out.println("Neighbours" + neighbours);
//        printState();
        return nonOccupiedPositions;
    }

    public List<Action> getLegalActions() {
//        find which cars have blanks and can move to the blank
        GameState gs = new GameState(nrRows, nrCols, cars);
        for (int i = 0; i < cars.size(); i++) {
//            System.out.println(cars.get(i).getOccupyingPositions());
        }
        System.out.println("Non occupied: " + getNonOccupyingPositions());
        for (int j = 0; j < getNonOccupyingPositions().size(); j++) {
            for (int i = 0; i < cars.size(); i++) {
//                System.out.println("Car occupied: "+ cars.get(i).getOccupyingPositions());
//                System.out.println("Non occupied: "+ getNonOccupyingPositions().get(j));


//                System.out.println(getNonOccupyingPositions().contains(cars.get(i).getOccupyingPositions().get(0)));
            }
        }


        ArrayList<Action> res = new ArrayList();
        if (isLegal(move_up))
            res.add(move_up);
        if (isLegal(move_down))
            res.add(move_down);
        if (isLegal(move_right))
            res.add(move_right);
        if (isLegal(move_left))
            res.add(move_left);
        return res;

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isLegal(Action action) {

// in order to define if a move is legal or not
// we have to know all the occupied positions by other cars
// and not allow moves outside the box
// check the orientation first of the car and decided if move can be made
        for (int i = 0; i < cars.size(); i++) {
//            System.out.println("Car:"+ i + "has "+cars.get(i).getOccupyingPositions());
        }

//printState();

        if (action instanceof MoveUp)
//            check the above row
            return true;
        else if (action instanceof MoveDown)
//                check the row below
            return true;
        else if (action instanceof MoveRight)
//                check the column + 1
            return true;
        else if (action instanceof MoveLeft)
//                check the column - 1
            return true;
        else

            return false;
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
