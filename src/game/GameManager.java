package game;

import models.Board;
import gui.Gui;
import models.Cell;
import models.Colors;
import models.figures.Figure;


public class GameManager {

    // MAIN INSTANCES
    Board boardInstance;
    Gui guiInstance;
    GameTimer timerInstance;

    GameEnemy gameEnemyInstance;

    // CHESS OPTIONS
    int TIME_AMOUNT = 90;
    int PERFORMED_MOVE_BONUS = 4;

    boolean IS_ENEMY_ROBOT_ENABLED = true;


    Cell selectedCell;

    enum MoveStates {
        NOT_SELECTED,
        SELECTED,
    }

    MoveStates moveState = MoveStates.NOT_SELECTED;
    Colors currentPlayerColor = Colors.WHITE;
    Colors oppositePlayerColor = this.getOppositePlayerColor();

    Colors enemyRobotColor = this.getOppositePlayerColor();

    boolean isGameOver = false;

    public GameManager(){
        this.boardInstance = new Board();
        this.guiInstance = new Gui(this.boardInstance, this);
        this.timerInstance = new GameTimer(this, this.TIME_AMOUNT, this.PERFORMED_MOVE_BONUS);
        this.gameEnemyInstance = new GameEnemy(this.boardInstance, this.enemyRobotColor);
    }

    public Colors getCurrentPlayerColor(){
        return this.currentPlayerColor;
    }

    public void setCurrentPlayerColor(Colors color){
        this.currentPlayerColor = color;
        this.oppositePlayerColor = this.getOppositePlayerColor();
        this.timerInstance.changePlayerTimer();
    }

    public Colors getOppositePlayerColor() {
        return this.currentPlayerColor == Colors.BLACK ? Colors.WHITE : Colors.BLACK;
    }

    public void changeCurrentPlayerColor(){
        this.setCurrentPlayerColor(this.getOppositePlayerColor());
    }

    public void performPawnChangeFigure(){
        Cell cellInWhichFigureShouldBeChanged = this.boardInstance.checkIfPawnShouldBeChanged();
        if(cellInWhichFigureShouldBeChanged == null){
            return;
        }

        if(cellInWhichFigureShouldBeChanged.isEmpty()){
            return;
        }

        if(this.IS_ENEMY_ROBOT_ENABLED && cellInWhichFigureShouldBeChanged.getFigure().getColor() == this.enemyRobotColor){

            int randomlySelectedFigureIdx = this.gameEnemyInstance.selectRandomlyAnIdxOfFigure();

            this.boardInstance.promotePawnFigure(cellInWhichFigureShouldBeChanged, randomlySelectedFigureIdx);

        } else {
            int selectedFigure = this.guiInstance.showChoosePawnDialog();
            this.boardInstance.promotePawnFigure(cellInWhichFigureShouldBeChanged, selectedFigure);
        }


    }

    public void giveAdditionalSecondsToPlayer(Colors playerColor){
        int playerTimeAmountIdx = playerColor == Colors.WHITE ? 0 : 1;

        this.timerInstance.giveAdditionalSeconds(playerTimeAmountIdx);
    }

    public void runNextGameTick(){

        this.performPawnChangeFigure();
        this.boardInstance.checkAndUpdateKingsCheckedStatus();
        this.guiInstance.reRenderButtons();
        this.giveAdditionalSecondsToPlayer(this.getCurrentPlayerColor());
        this.changeCurrentPlayerColor();
        this.guiInstance.updateTextPane();
        this.updateFiguresHistoryState();
        this.guiInstance.historyTextPane.printHistory(this.boardInstance.getDoneMoves());

        this.guiInstance.fillBackgroundRecentMoveCells();
        this.guiInstance.paintCheckedKings();

        if(!this.boardInstance.canPreventCheckMate(this.getCurrentPlayerColor())){
            if(this.boardInstance.isKingChecked(this.getCurrentPlayerColor())){
                this.setGameOver(GameOverStates.SOMEONE_WON);
            } else {
                this.setGameOver(GameOverStates.DRAW);
            }
        }

        if(this.IS_ENEMY_ROBOT_ENABLED && this.getCurrentPlayerColor() == this.enemyRobotColor){
            this.performRobotMove();
            this.runNextGameTick();
        }
    }

    public void updateFiguresHistoryState(){
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                Cell receivedCellFromBoard = this.boardInstance.getCell(x, y);

                if(receivedCellFromBoard.isEmpty()) continue;

                Figure cellFigure = receivedCellFromBoard.getFigure();
                cellFigure.updateLastPerformedMoveAge();
                if(cellFigure.isMovedFromStart() && cellFigure.isMovedPreviousRound()){
                    cellFigure.changeMovedState();

                }
            }
        }
    }


    public MoveStates getMoveState(){
        return this.moveState;
    }

    public void setMoveState(MoveStates desiredMoveState){
        this.moveState = desiredMoveState;
    }

    public GameManagerCommands handlePieceButtonClick(Cell cellData){

        if (this.isGameOver || this.getCurrentPlayerColor() == this.enemyRobotColor) {
            return GameManagerCommands.NO_ACTIONS_TO_PERFORM;
        }

        // Do some logic and send command to gui what it should to perform
        switch(this.getMoveState()){
            case NOT_SELECTED:

                if(cellData.isEmpty()) break;
                if(this.getCurrentPlayerColor() != cellData.getFigure().getColor()) break;


                this.guiInstance.fillBackgroundRecentMoveCells();
                this.guiInstance.paintCheckedKings();


                this.selectPieceAndShowAvailableMoves(cellData);

                return GameManagerCommands.FILL_SELECTED_BUTTON_BACKGROUND_WITH_FOCUSED_COLOR;


            case SELECTED:

                // Case if user clicked itself cell, then unselect.
                if(this.selectedCell == cellData){
                    this.guiInstance.drawDefaultBackgroundButtonsColor();
                    this.setMoveState(MoveStates.NOT_SELECTED);

                    this.guiInstance.fillBackgroundRecentMoveCells();
                    this.guiInstance.paintCheckedKings();
                    break;
                }


                // Case if user clicked on cellData where is standing the piece of the same color (switch selected piece)
                if(!cellData.isEmpty() && cellData.getFigure().getColor() == this.selectedCell.getFigure().getColor()){
                    this.guiInstance.drawDefaultBackgroundButtonsColor();

                    this.guiInstance.fillBackgroundRecentMoveCells();

                    this.guiInstance.paintCheckedKings();

                    this.selectPieceAndShowAvailableMoves(cellData);

                    return GameManagerCommands.FILL_SELECTED_BUTTON_BACKGROUND_WITH_FOCUSED_COLOR;


                }




                // Check if piece will perform check for his king
                if(this.selectedCell.getFigure().canMove(cellData) && this.selectedCell.isMoveSafe(cellData)){
                    this.selectedCell.moveFigure(cellData);

                    this.setMoveState(MoveStates.NOT_SELECTED);

                    this.runNextGameTick();
                    cellData.getFigure().markAsUpdatedMoveHistoryState();



                    break;
                }

                break;

            default:
                System.out.println("ERROR: Undefined case in moveState!!!");
        }

        return GameManagerCommands.NO_ACTIONS_TO_PERFORM;
    }

    public void selectPieceAndShowAvailableMoves(Cell cellData){
        this.setMoveState(MoveStates.SELECTED);
        this.selectedCell = cellData;

        boolean[][] availableMoves = cellData.getAvailableMoves();
        this.guiInstance.paintAvailableCellMoves(availableMoves);
    }





    public void setGameOver(GameOverStates gameOverState){
        this.isGameOver = true;
        this.timerInstance.stopTimer();

        String dialogContent;
        if(gameOverState == GameOverStates.SOMEONE_WON){
            Colors winner = this.getOppositePlayerColor();
            dialogContent = String.format("Player %s wins!", winner);
        } else {
            dialogContent = "DRAW. No one's win.";
        }

        dialogContent = dialogContent.concat("\nDo you want to rerun the game?");

        // Yes - 0, No - 1
        boolean hasSelectedYesOption = !(this.guiInstance.showOptionDialog(dialogContent) == 1);

        if(!hasSelectedYesOption){
            return;
        }

        this.reRunGame();
    }

    public void reRunGame(){
        this.boardInstance.resetBoard();
        this.guiInstance.historyTextPane.printHistory(this.boardInstance.getDoneMoves());
        this.guiInstance.reRenderButtons();
        this.setCurrentPlayerColor(Colors.WHITE);
        this.timerInstance.resetTimer();
        this.isGameOver = false;
    }



    public void updateEstimatedTimePanel(int[] playersEstimatedTime){
        this.guiInstance.printEstimatedTime(playersEstimatedTime);
    }

    public void performRobotMove(){
        this.gameEnemyInstance.performEnemyMove();
    }


}
