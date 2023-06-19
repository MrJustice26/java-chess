package game;

import models.Board;
import models.Cell;
import models.Colors;

import java.util.Random;

public class GameEnemy {

    private final Board boardInstance;

    private final Colors enemyColor;

    public GameEnemy(Board board, Colors enemyColor){
        this.boardInstance = board;
        this.enemyColor = enemyColor;

    }

    private Cell[] getCellsWithEnemyColorFigures(){
        Cell[] collectedCells = new Cell[20];

        int collectedCellsIdx = 0;

        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                Cell iteratedCell = this.boardInstance.getCell(x, y);

                if(iteratedCell.isEmpty()) continue;

                if(iteratedCell.getFigure().getColor() != this.enemyColor) continue;

                collectedCells[collectedCellsIdx++] = iteratedCell;
            }
        }
        return collectedCells;
    }

    private Cell getFromCell(Cell[] availableCells){
        Random rand = new Random();

        int randomAvailableCellsIdx = rand.nextInt(16);

        do {
            if (availableCells[randomAvailableCellsIdx] != null && availableCells[randomAvailableCellsIdx].hasAtLeasMove()) {
                break;
            }
            randomAvailableCellsIdx = rand.nextInt(16);
        } while (true);

        return availableCells[randomAvailableCellsIdx];
    }

    private Cell getTargetCell(Cell fromCell){
        Random rand = new Random();

        boolean[][] availableMoves = fromCell.getAvailableMoves();

        int[] collectedAvailableMovesIdx = new int[64];
        int collectedAvailableMovesArrIdx = 0;

        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                if(!availableMoves[y][x]) continue;

                collectedAvailableMovesIdx[collectedAvailableMovesArrIdx++] = y*8 + x;
            }
        }

        int selectedMoveAsTargetIdx = rand.nextInt(collectedAvailableMovesArrIdx);

        int cellNumber = collectedAvailableMovesIdx[selectedMoveAsTargetIdx];
        int cellY = (int) Math.floor(cellNumber / 8);
        int cellX = cellNumber % 8;

        return this.boardInstance.getCell(cellX, cellY);
    }

    public void performEnemyMove(){

        Cell[] availableCellsToSelect = this.getCellsWithEnemyColorFigures();

        Cell fromCell = getFromCell(availableCellsToSelect);
        Cell toCell = getTargetCell(fromCell);


        fromCell.moveFigure(toCell);
    }
}
