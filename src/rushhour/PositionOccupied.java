/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rushhour;

/**
 *
 * @author steven
 */
public class PositionOccupied{

    private int row;
    private int col;
    private String direction;

    public PositionOccupied(int row, int col, String direction){
        this.row=row;
        this.col=col;
        this.direction= direction;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public String getDirection() {return direction; }

    public String toString(){
        return "(" +row+","+col+ ","+ direction+")";
    }

}
