package models;

import models.figures.Figure;

public class Cell {
    private final int x;
    private final int y;
    private final Colors color;

    private Figure figure;
    private Board board;
    private boolean available;


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
}
