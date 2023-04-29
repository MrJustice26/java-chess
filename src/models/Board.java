package models;

import models.figures.King;
import models.figures.Queen;
import models.figures.Bishop;
import models.figures.Horse;
import models.figures.Rook;
import models.figures.Pawn;

public class Board {
    Cell[][] cells = new Cell[8][8];

    public void initCells(){
        for(int i = 0; i < 8; i++){
            Cell[] row = new Cell[8];
            for(int j = 0; j < 8; j++){
                Colors cellColor = (i+j) % 2 == 0 ? Colors.WHITE : Colors.BLACK;
                row[j] = new Cell(this, j, i, cellColor, null);
            }
            this.cells[i] = row;
        }

    }

    public Cell getCell(int x, int y){
        return this.cells[y][x];
    }

    public void addFigures(){
        this.addKings();
        this.addQueens();
        this.addBishops();
        this.addHorses();
        this.addRooks();
        this.addPawns();
    }

    private void addKings(){
        new King(Colors.BLACK, this.getCell(4,0));
        new King(Colors.WHITE, this.getCell(4, 7));
    }

    private void addQueens() {
        new Queen(Colors.BLACK, this.getCell(3, 0));
        new Queen(Colors.WHITE, this.getCell(3, 7));
    }

    private void addBishops() {
        new Bishop(Colors.BLACK, this.getCell(2, 0));
        new Bishop(Colors.BLACK, this.getCell(5, 0));
        new Bishop(Colors.WHITE, this.getCell(2, 7));
        new Bishop(Colors.WHITE, this.getCell(5, 7));
    }

    private void addHorses() {
        new Horse(Colors.BLACK, this.getCell(1, 0));
        new Horse(Colors.BLACK, this.getCell(6, 0));
        new Horse(Colors.WHITE, this.getCell(1, 7));
        new Horse(Colors.WHITE, this.getCell(6, 7));
    }

    private void addRooks() {
        new Rook(Colors.BLACK, this.getCell(0, 0));
        new Rook(Colors.BLACK, this.getCell(7, 0));
        new Rook(Colors.WHITE, this.getCell(0, 7));
        new Rook(Colors.WHITE, this.getCell(7, 7));
    }

    private void addPawns(){
        for(int i = 0; i < 8; i++){
            new Pawn(Colors.BLACK, this.getCell(i, 1));
            new Pawn(Colors.WHITE, this.getCell(i, 6));
        }
    };

    public void getBoard(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++ ){
                System.out.printf("[%s]", getCell(j, i).getFigure());
            }
            System.out.println("");
        }
    }

}
