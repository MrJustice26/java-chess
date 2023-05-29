package models;

import models.figures.*;

public class Board {
    Cell[][] cells = new Cell[8][8];

    King whiteKingRef;
    King blackKingRef;

    Figure[][] figuresMakingCheckForKings = new Figure[2][16];

    Cell[] recentMove = new Cell[2];

    private boolean isGameOver = false;

    public Board(){
        this.initTable();
    }

    public void initTable(){
        this.initCells();
        this.addFigures();
    }

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
        if(x >= 8 || y >= 8 || x < 0 || y < 0){
            return null;
        } else {
            return this.cells[y][x];
        }
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
        this.blackKingRef = new King(Colors.BLACK, this.getCell(4,0));
        this.whiteKingRef = new King(Colors.WHITE, this.getCell(4, 7));
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
    }

    public void updateRecentMoveRecord(Cell from, Cell target){
        if(from == null || target == null) {
            System.out.println("ERROR: From cell or target cell is/are null in updateRecentMoveRecord!");
            return;
        }
        this.recentMove[0] = from;
        this.recentMove[1] = target;
    }

    public Cell[] getRecentMoveRecord(){
        return this.recentMove;
    }

    public boolean isKingChecked(Colors kingColor){
        Cell kingCheckCandidateCell = kingColor == Colors.WHITE ? this.whiteKingRef.getCell() : this.blackKingRef.getCell();

        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){

                Cell receivedCellFromBoard = this.getCell(x, y);
                if(receivedCellFromBoard.isEmpty()) continue;

                if(receivedCellFromBoard.getFigure().getColor() == kingColor) continue;

                if(receivedCellFromBoard.getFigure().canMove(kingCheckCandidateCell)){
                    return true;
                }
            }
        }
        return false;
    }

    public void checkAndUpdateKingsCheckedStatus(){

        boolean isBlackKingChecked = this.isKingChecked(Colors.BLACK);
        if(isBlackKingChecked){
            this.findCheckingFigures(Colors.BLACK);
        }
        if(isBlackKingChecked && this.blackKingRef.getCheckedStatus()){
            System.out.println("Player WHITE won!");
            this.setGameOverStatus(true);
        }
        this.blackKingRef.setCheckedStatus(isBlackKingChecked);

        boolean isWhiteKingChecked = this.isKingChecked(Colors.WHITE);
        if(isWhiteKingChecked){
            this.findCheckingFigures(Colors.WHITE);
        }
        if(isWhiteKingChecked && this.whiteKingRef.getCheckedStatus()){
            System.out.println("Player BLACK won!");
            this.setGameOverStatus(true);
        }
        this.whiteKingRef.setCheckedStatus(isWhiteKingChecked);

    }

    public void findCheckingFigures(Colors kingColor){
        Cell kingCheckCandidateCell = kingColor == Colors.WHITE ? this.whiteKingRef.getCell() : this.blackKingRef.getCell();

        int figuresMakingCheckForKingsRow = kingColor == Colors.WHITE ? 0 : 1;

        int checkingFiguresCounter = 0;
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){

                Cell receivedCellFromBoard = this.getCell(x, y);
                if(receivedCellFromBoard.isEmpty()) continue;

                if(receivedCellFromBoard.getFigure().getColor() == kingColor) continue;

                if(receivedCellFromBoard.getFigure().canMove(kingCheckCandidateCell)){
                    this.figuresMakingCheckForKings[figuresMakingCheckForKingsRow][checkingFiguresCounter] = receivedCellFromBoard.getFigure();
                    checkingFiguresCounter++;
                }
            }
        }
    }


    public boolean getGameOverStatus(){
        return this.isGameOver;
    }

    public void setGameOverStatus(boolean bool){
        this.isGameOver = bool;
    }

    public boolean canPreventCheckMate(Colors chessColor){
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){

                Cell receivedCellFromBoard = this.getCell(x, y);
                if(receivedCellFromBoard.isEmpty()) continue;

                if(receivedCellFromBoard.getFigure().getColor() != chessColor) continue;

                Figure alliedFigure = receivedCellFromBoard.getFigure();
                if(alliedFigure.getCell().hasAtLeasMove()) return true;
            }
        }
        return false;
    }



}
