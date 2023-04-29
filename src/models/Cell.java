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

    public boolean isEmptyByVertical(Cell target){
        if (this.x != target.x) {
            return false;
        }

        int minY = this.y < target.y ? this.y : target.y;
        int maxY = this.y > target.y ? this.y : target.y;
        for (int y = minY + 1; y < maxY; y++) {
            if(!this.board.getCell(this.x, y).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmptyByHorizontal(Cell target){
        if (this.y != target.y) {
            return false;
        }

        int minX = this.x < target.x ? this.x : target.x;
        int maxX = this.x > target.x ? this.x : target.x;
        for (int x = minX + 1; x < maxX; x++) {
            if(!this.board.getCell(x, this.y).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmptyByDiagonal(Cell target){

        int differenceX = Math.abs(this.x - target.y);
        int differenceY = Math.abs(this.y - target.y);
        if(differenceX != differenceY){
            return false;
        }

        int dx = this.x < target.x ? 1 : -1;
        int dy = this.y < target.y ? 1 : -1;

        for(int j = 1; j < differenceX; j++){
            if(!this.board.getCell(this.x + j*dx, this.y + j*dy).isEmpty()) return false;
        }
        return true;
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
