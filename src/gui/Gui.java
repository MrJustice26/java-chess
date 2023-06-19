package gui;

import models.Board;
import models.Cell;
import models.Colors;
import models.figures.*;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class Gui {

    // TODO Refactor it later
    Color greenColor = new Color(116, 217, 38);
    Color focusedCellColor = new Color(110, 229, 220);
    Color movedFromCellColor = new Color(155, 24, 112);
    Color movedToCellColor = new Color(213, 59, 159);



    Board board;
    String assetsFolderPath = "assets/";
    enum MoveStates {
        NOT_SELECTED,
        SELECTED,
    }

    MoveStates moveState = MoveStates.NOT_SELECTED;
    Colors currentPlayerColor = Colors.WHITE;
    Colors oppositePlayerColor = Colors.BLACK;

    JPanel chessPanel;
    JTextPane currentPlayerTextPane;

    JTextPane estimatedTimeTextPane;

    int TIME_AMOUNT = 90;

    int PERFORMED_MOVE_BONUS = 4;

    // WHITE, BLACK;
    int[] playerTimeAmount = {this.TIME_AMOUNT, this.TIME_AMOUNT};

    HistoryPane historyTextPane;

    Timer timerInstance;

    JButton selectedButtonInstance;

    boolean isGameOver = false;

    public Gui(Board board){
        this.initGUI(board);
    }

    private void initGUI(Board board){
        this.board = board;
        JFrame mainFrame = new JFrame("Chess Board");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        JPanel chessPanel = new JPanel(new GridLayout(8, 8));
        chessPanel.setPreferredSize(new Dimension(8 * 75, 8 * 75));
        chessPanel.setBackground(Color.white);


        JPanel additionalPanel = new JPanel();
        additionalPanel.setPreferredSize(new Dimension(240, 8 * 75));
        additionalPanel.setBackground(Color.lightGray);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(chessPanel, BorderLayout.EAST);
        contentPanel.add(additionalPanel, BorderLayout.WEST);

        JTextPane currentPlayerTextPane = new JTextPane();
        currentPlayerTextPane.setPreferredSize(new Dimension(240, 30));
        currentPlayerTextPane.setEditable(false);
        currentPlayerTextPane.setBackground(Color.lightGray);
        currentPlayerTextPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,5));

        StyledDocument document = currentPlayerTextPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        document.setParagraphAttributes(0, document.getLength(), style, false);

        this.currentPlayerTextPane = currentPlayerTextPane;
        this.currentPlayerTextPane.setText("Current player move: WHITE");

        this.historyTextPane = new HistoryPane();

        JTextPane estimatedTimeTextPane = new JTextPane();
        estimatedTimeTextPane.setPreferredSize(new Dimension(240, 100));
        estimatedTimeTextPane.setEditable(false);
        estimatedTimeTextPane.setBackground(Color.lightGray);
        estimatedTimeTextPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,5));

        this.estimatedTimeTextPane = estimatedTimeTextPane;
        this.printEstimatedTime();

        this.timerInstance = new Timer(1000, e -> this.timerAction());
        this.timerInstance.start();

        additionalPanel.add(currentPlayerTextPane);
        additionalPanel.add(this.historyTextPane.getHistoryTextPaneInstance());
        additionalPanel.add(this.estimatedTimeTextPane);

        this.renderButtons(chessPanel);
        mainFrame.add(contentPanel);
        mainFrame.setVisible(true);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);

        this.chessPanel = chessPanel;

    }

    private void applyButtonOptions(JButton buttonInstance){
        buttonInstance.setHorizontalTextPosition(JButton.CENTER);
        buttonInstance.setVerticalTextPosition(JButton.CENTER);
        buttonInstance.setFocusPainted(false);
        buttonInstance.setBorderPainted(false);
        buttonInstance.setRolloverEnabled(false);
    }

    private void renderButtons(JPanel panel){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = this.board.getCell(j, i);
                JButton button = new JButton();
                this.applyButtonOptions(button);
                button.putClientProperty("cell", cell);
                button.addActionListener(e -> this.actionButtonScript(button, panel));
                panel.add(button);
            }
        }
        this.drawButtonsIcon(panel);
        this.drawDefaultBackgroundButtonsColor(panel);
    }

    private void reRenderButtons(JPanel panel){
        this.drawButtonsIcon(panel);
        this.drawDefaultBackgroundButtonsColor(panel);
        panel.revalidate();
        panel.repaint();
    }

    private void drawButtonsIcon(JPanel panel){
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                Cell cellData = (Cell) button.getClientProperty("cell");
                if(cellData.isEmpty()) {
                    button.setIcon(null);
                    continue;
                }

                Figure cellFigure = cellData.getFigure();
                String pathToFigureImage = this.assetsFolderPath + cellFigure.getImageName();
                ImageIcon icon = new ImageIcon(pathToFigureImage);
                button.setIcon(icon);
            }
        }
    }

    private void drawDefaultBackgroundButtonsColor(JPanel panel){
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                Cell cell = (Cell) button.getClientProperty("cell");
                Color cellColor = cell.getColor() == Colors.BLACK ? Color.gray : Color.white;
                button.setBackground(cellColor);
                button.setBorderPainted(false);
            }
        }
    }
    private void actionButtonScript(JButton button, JPanel panel){
        Cell cell = (Cell) button.getClientProperty("cell");



        switch(this.getMoveState()){
            case NOT_SELECTED:

                if(cell.isEmpty()) return;
                if(this.getCurrentPlayerColor() != cell.getFigure().getColor()) return;


                this.fillBackgroundRecentMoveCells(panel);
                this.paintCheckedKings();

                this.selectPieceAndShowAvailableMoves(button, cell, panel);


                break;

            case SELECTED:
                Cell selectedCell = (Cell) this.selectedButtonInstance.getClientProperty("cell");

                // Case if user clicked itself cell, then unselect.
                if(selectedCell == cell){
                    this.drawDefaultBackgroundButtonsColor(panel);
                    this.setMoveState(MoveStates.NOT_SELECTED);

                    this.fillBackgroundRecentMoveCells(panel);
                    this.paintCheckedKings();
                    break;
                }


                // Case if user clicked on cell where is standing the piece of the same color (switch selected piece)
                if(!cell.isEmpty() && cell.getFigure().getColor() == selectedCell.getFigure().getColor()){
                    this.drawDefaultBackgroundButtonsColor(panel);

                    this.fillBackgroundRecentMoveCells(panel);

                    this.paintCheckedKings();
                    this.selectPieceAndShowAvailableMoves(button, cell, panel);

                    break;
                }

                if (this.isGameOver) return;


                // Check if piece will perform check for his king
                if(selectedCell.getFigure().canMove(cell) && selectedCell.isMoveSafe(cell)){
                    selectedCell.moveFigure(cell);

                    this.setMoveState(MoveStates.NOT_SELECTED);

                    this.runNextGameTick(panel);
                    cell.getFigure().markAsUpdatedMoveHistoryState();

                    this.fillBackgroundRecentMoveCells(panel);
                    this.paintCheckedKings();

                    break;
                }

                break;

            default:
                System.out.println("ERROR: Undefined case in moveState!!!");
        }
    }

    public MoveStates getMoveState(){
        return this.moveState;
    }

    public void setMoveState(MoveStates moveState){
        this.moveState = moveState;
    }

    public Colors getCurrentPlayerColor(){
        return this.currentPlayerColor;
    }

    public void setCurrentPlayerColor(Colors color){
        this.currentPlayerColor = color;
    }

    public Colors getOppositePlayerColor() {
        return this.oppositePlayerColor;
    }

    public void setOppositePlayerColor(Colors color){
        this.oppositePlayerColor = color;
    }

    public void changeCurrentPlayerColor(){
        Colors oppositeColor = this.getOppositePlayerColor();
        Colors currentColor = this.getCurrentPlayerColor();
        this.setOppositePlayerColor(currentColor);
        this.setCurrentPlayerColor(oppositeColor);
    }

    public void updateTextPane(){
        String currentPlayerColor = this.getCurrentPlayerColor().toString();
        this.currentPlayerTextPane.setText("Current player move: " + currentPlayerColor);
    }

    public void paintAvailableCellMoves(JPanel panel, boolean[][] availableMoves){
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                if(availableMoves[y][x]){
                    Component component = panel.getComponent(y * 8 + x);
                    if (component instanceof JButton) {
                        JButton iteratedButton = (JButton) component;
                        Cell iteratedCell = (Cell) iteratedButton.getClientProperty("cell");
                        if(iteratedCell.isEmpty()){
                            iteratedButton.setBackground(this.greenColor);
                        } else {
                            iteratedButton.setBackground(Color.red);
                        }
                        iteratedButton.setBorderPainted(true);
                    }
                }
            }
        }
    }

    public void selectPieceAndShowAvailableMoves(JButton button, Cell cell,JPanel panel){
        button.setBackground(this.focusedCellColor);

        this.setMoveState(MoveStates.SELECTED);
        this.selectedButtonInstance = button;

        boolean[][] availableMoves = cell.getAvailableMoves();
        this.paintAvailableCellMoves(panel, availableMoves);
    }

    public void runNextGameTick(JPanel panel){
        this.performPawnChangeFigure();
        this.board.checkAndUpdateKingsCheckedStatus();
        this.reRenderButtons(panel);
        this.giveAdditionalSeconds(this.currentPlayerColor);
        this.changeCurrentPlayerColor();
        this.updateTextPane();
        this.updatePaintedFiguresHistoryState();
        this.historyTextPane.printHistory(this.board);

        if(!this.board.canPreventCheckMate(this.currentPlayerColor)){
            this.setGameOver();
        }
    }

    public void updatePaintedFiguresHistoryState(){
        for (Component component : this.chessPanel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                Cell cell = (Cell) button.getClientProperty("cell");
                if(cell.isEmpty()) continue;

                Figure cellFigure = cell.getFigure();
                cellFigure.updateLastPerformedMoveAge();
                if(cellFigure.isMovedFromStart() && cellFigure.isMovedPreviousRound()){
                    cellFigure.changeMovedState();

                }
            }
        }
    }

    public void fillBackgroundRecentMoveCells(JPanel panel){

        Cell[] recentMoveRecord = this.board.getRecentMoveRecord();
        Cell cellFrom = recentMoveRecord[0];
        Cell cellTo = recentMoveRecord[1];

        if(cellFrom == null || cellTo == null) return;

        Component buttonFrom = panel.getComponent(cellFrom.getY()*8 + cellFrom.getX());
        buttonFrom.setBackground(movedFromCellColor);

        Component buttonTo = panel.getComponent(cellTo.getY()*8 + cellTo.getX());
        buttonTo.setBackground(movedToCellColor);
    }

    public void paintCheckedKings(){
        for (Component component : this.chessPanel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                Cell cell = (Cell) button.getClientProperty("cell");
                if(cell.isEmpty()) continue;

                Figure cellFigure = cell.getFigure();
                if(cellFigure.getName() != FigureNames.KING) continue;

                King kingFigure = (King) cellFigure;
                if(kingFigure.getCheckedStatus()){
                    button.setBackground(Color.red);
                }
            }
        }
    }

    public void timerAction(){
        int estimatedTimeTextPaneIdx = this.currentPlayerColor == Colors.WHITE ? 0 : 1;
        if(this.playerTimeAmount[estimatedTimeTextPaneIdx] == 0){
            this.setGameOver();
            return;

        }
        this.playerTimeAmount[estimatedTimeTextPaneIdx] = this.playerTimeAmount[estimatedTimeTextPaneIdx] - 1;

        this.printEstimatedTime();
    }

    public void printEstimatedTime(){
        String whitePlayerText = String.format("White player: %d seconds\n", this.playerTimeAmount[0]);
        String blackPlayerText = String.format("Black player: %d seconds", this.playerTimeAmount[1]);
        this.estimatedTimeTextPane.setText(whitePlayerText.concat(blackPlayerText));
    }


    public void resetTimer(){
        this.playerTimeAmount[0] = this.TIME_AMOUNT;
        this.playerTimeAmount[1] = this.TIME_AMOUNT;
        this.timerInstance.start();
    }

    public void setGameOver(){
        this.isGameOver = true;
        this.timerInstance.stop();

        this.showResultDialog();
    }

    public void rerunGame(){
        this.board.resetBoard();
        this.historyTextPane.printHistory(this.board);
        this.reRenderButtons(this.chessPanel);
        this.setCurrentPlayerColor(Colors.WHITE);
        this.resetTimer();
        this.isGameOver = false;
    }

    public void performPawnChangeFigure(){
        Cell cellInWhichFigureShouldBeChanged = this.board.checkIfPawnShouldBeChanged();
        if(cellInWhichFigureShouldBeChanged == null){
            return;
        }

        if(cellInWhichFigureShouldBeChanged.isEmpty()){
            return;
        }

        int selectedFigure = this.showChoosePawnDialog();

        this.board.promotePawnFigure(cellInWhichFigureShouldBeChanged, selectedFigure);

    }

    public void showResultDialog(){
        Object[] options = {"Tak", "Nie"};

        String winner = this.getOppositePlayerColor().toString();
        int answer = JOptionPane.showOptionDialog(null,
                String.format("Player %s wins!\nDo you want to rerun the game?", winner),
                "Potwierdzenie",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if(answer == 0) {
            this.rerunGame();
        }
    }

    public int showChoosePawnDialog(){
        int selectedCorrectFigure;
         while(true){
            String selectedFigureStr = JOptionPane.showInputDialog(null, "Choose the chess piece:\n1 - Queen\n2 - Bishop\n3 - Horse\n4 - Rook");
             try {
                 if(selectedFigureStr == null){
                     continue;
                 }
                 int selectedFigure = Integer.parseInt(selectedFigureStr);

                 if(selectedFigure > 0 && selectedFigure <= 4){
                     selectedCorrectFigure = selectedFigure;
                     break;
                 }
             }  catch (NumberFormatException e){
                 System.out.println("Catch error at NumberFormatException " + e);
             }

         }
         return selectedCorrectFigure;
    }

    // GameManager
    public void giveAdditionalSeconds(Colors playerColorWhichShouldReceiveAdditionalSeconds){

        int playerTimeAmountIdx = playerColorWhichShouldReceiveAdditionalSeconds == Colors.WHITE ? 0 : 1;

        this.playerTimeAmount[playerTimeAmountIdx] += this.PERFORMED_MOVE_BONUS;
    }


}
