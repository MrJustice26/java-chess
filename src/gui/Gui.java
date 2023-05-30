package gui;

import models.Board;
import models.Cell;
import models.Colors;
import models.figures.Figure;
import models.figures.FigureNames;
import models.figures.King;

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

    JPanel chessPanel;
    JTextPane currentPlayerTextPane;

    JTextPane estimatedTimeTextPane;

    // WHITE, BLACK;
    int[] playerTimeAmount = {90, 90};
    HistoryPane historyTextPane;

    Timer timerInstance;

    JButton selectedButtonInstance;
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
        contentPanel.add(chessPanel, BorderLayout.WEST);
        contentPanel.add(additionalPanel, BorderLayout.CENTER);

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

    private void deleteButtons(JPanel panel){
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                panel.remove(button);
            }
        }
    }
    private void renderButtons(JPanel panel){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = this.board.getCell(j, i);
                JButton button = new JButton();
                if(!cell.isEmpty()){
                    Figure cellFigure = cell.getFigure();
                    String pathToFigureImage = this.assetsFolderPath + cellFigure.getImageName();
                    ImageIcon icon = new ImageIcon(pathToFigureImage);
                    button.setIcon(icon);
                }
                this.applyButtonOptions(button);
                button.putClientProperty("cell", cell);
                button.addActionListener(e -> this.actionButtonScript(button, panel));
                panel.add(button);
            }
        }
        this.drawDefaultBackgroundButtonsColor(panel);
    }

    private void reRenderButtons(JPanel panel){
        this.deleteButtons(panel);
        this.renderButtons(panel);
        panel.revalidate();
        panel.repaint();
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
                this.selectPieceAndShowAvailableMoves(button, cell, panel);

                this.paintCheckedKings();

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
                    this.selectPieceAndShowAvailableMoves(button, cell, panel);

                    this.paintCheckedKings();

                    break;
                }

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

    public void setCurrentPlayerColor(Colors currentPlayerColor){
        this.currentPlayerColor = currentPlayerColor;
    }

    public void changeCurrentPlayerColor(){
        Colors oppositeCurrentColor = this.getCurrentPlayerColor() == Colors.BLACK ? Colors.WHITE : Colors.BLACK;
        this.setCurrentPlayerColor(oppositeCurrentColor);
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
        this.board.checkIfPawnShouldBeChanged();
        this.board.checkAndUpdateKingsCheckedStatus();
        this.reRenderButtons(panel);
        this.changeCurrentPlayerColor();
        this.updateTextPane();
        this.updatePaintedFiguresHistoryState();
        this.historyTextPane.printHistory(this.board);

        if(!this.board.canPreventCheckMate(this.currentPlayerColor)){
            // TODO IMPLEMENT RELAUNCH GAME
            System.out.printf("PLAYER %s WINS!\n", this.currentPlayerColor == Colors.BLACK ? Colors.WHITE.toString() : Colors.BLACK.toString());
            System.out.println("CALL RERUN FN");

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
            // TODO IMPLEMENT RELAUNCH GAME
            System.out.printf("Time is up. PLAYER %s WINS!\n", this.currentPlayerColor == Colors.BLACK ? Colors.WHITE.toString() : Colors.BLACK.toString());
            System.out.println("CALL RERUN FN");
            this.timerInstance.stop();
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



}
