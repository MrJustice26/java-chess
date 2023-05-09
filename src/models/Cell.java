package models;

import models.figures.Figure;

public class Cell {
    private final int x;
    private final int y;
    private final Colors color;

    private Figure figure;
    private final Board board;


    public Cell(Board board, int x, int y, Colors color, Figure figure){
        this.board = board;
        this.x = x;
        this.y = y;
        this.color = color;
        this.figure = figure;
    }

    public int getX(){ return this.x; }
    public int getY(){ return this.y; }

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

        int minY = Math.min(this.y, target.y);
        int maxY = Math.max(this.y, target.y);
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

        int minX = Math.min(this.x, target.x);
        int maxX = Math.max(this.x, target.x);
        for (int x = minX + 1; x < maxX; x++) {
            if(!this.board.getCell(x, this.y).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmptyByDiagonal(Cell target){

        int differenceX = Math.abs(this.x - target.x);
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

    public void moveFigure(Cell target){
        this.board.updateRecentMoveRecord(this, target);

        Figure figure = this.getFigure();

        target.setFigure(figure);

        figure.changeMovedState();
        figure.setCell(target);
        figure.setIsMoved(true);

        this.setFigure(null);
    }

    public Cell getRelativeLeftCellByX(){
        return this.board.getCell(this.x - 1, this.y);
    }

    public Cell getRelativeRightCellByX(){
        return this.board.getCell(this.x + 1, this.y);
    }

    public boolean[][] getAvailableMoves(){
        boolean[][] availableMoves = new boolean[8][8];
        if(this.getFigure() == null){
            System.out.println("Figure is null!");
            return availableMoves;
        }
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                if(this.getFigure().canMove(this.board.getCell(x, y))){
                    availableMoves[y][x] = true;
                }
            }
        }

        return availableMoves;
    }
}
