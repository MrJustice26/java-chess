package models;

import models.figures.Figure;

public class Cell {
    private final int x;
    private final int y;
    private final Colors color;

    private Figure figure;
    private Board board;
    private boolean isAvailable;


    public Cell(Board board, int x, int y, Colors color, Figure figure){
        this.board = board;
        this.x = x;
        this.y = y;
        this.color = color;
        this.figure = figure;
    }

    public void setFigure(Figure figure){
        this.figure = figure;
    }

    public boolean isEmpty(){
        return this.figure == null;
    }

    public Figure getFigure(){
        return this.figure;
    }

    public boolean moveFigure(Cell target){
        if(!this.figure.canMove(target)) {
            System.out.println("Figure can't be moved to the target position");
            return false;
        };
        target.setFigure(this.figure);
        this.setFigure(null);
        this.getFigure().setCell(target);
        return true;

    }
}
