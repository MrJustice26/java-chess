package gui;

import models.Board;
import models.Cell;
import models.Colors;
import models.figures.Figure;

import javax.swing.*;
import java.awt.*;

public class Gui {

    Color greenColor = new Color(116, 217, 38);
    Color focusedCellColor = new Color(100, 40, 90);
    Color movedFromCellColor = new Color(27, 200, 34);
    Color movedToCellColor = new Color(16, 240, 50);


    Board board;
    String assetsFolderPath = "assets/";
    enum MoveStates {
        NOT_SELECTED,
        SELECTED,
    }

    MoveStates moveState = MoveStates.NOT_SELECTED;
    Colors currentPlayerColor = Colors.WHITE;

    Label labelInstance = new Label();


    JButton selectedButtonInstance;
    public Gui(Board board){
        this.initGUI(board);
    }

    private void initGUI(Board board){
        this.board = board;
        JFrame frame = new JFrame("Chess Board");
        frame.setSize(8*75, 8*75);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(8, 8));
        panel.setBackground(Color.white);

        this.renderButtons(panel);

        JPanel colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(50, 50));

        colorPanel.add(this.labelInstance);
        this.updateLabelText();


        frame.add(colorPanel);
        frame.add(panel);
        frame.setVisible(true);
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
                Color cellColor = (cell.getX() + cell.getY()) % 2 == 0 ? Color.white : Color.gray;
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

                button.setBackground(this.focusedCellColor);

                this.setMoveState(MoveStates.SELECTED);
                selectedButtonInstance = button;

                boolean[][] availableMoves = cell.getAvailableMoves();
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
                break;
            case SELECTED:
                Cell selectedCell = (Cell) selectedButtonInstance.getClientProperty("cell");
                if(selectedCell == cell){
                    this.drawDefaultBackgroundButtonsColor(panel);
                    this.fillBackgroundForCellsWhereFigureWasMoved(panel, selectedCell, cell);
                    this.setMoveState(MoveStates.NOT_SELECTED);
                    break;
                }
                if(selectedCell.getFigure().canMove(cell)){
                    this.setMoveState(MoveStates.NOT_SELECTED);
                    selectedCell.moveFigure(cell);
                    this.runNextGameTick(panel);
                    cell.getFigure().markAsUpdatedMoveHistoryState();
                    fillBackgroundForCellsWhereFigureWasMoved(panel, selectedCell, cell);
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
        System.out.println("Current player move: " + this.getCurrentPlayerColor().toString());
    }

    public void updateLabelText(){
        String currentPlayerColor = this.getCurrentPlayerColor().toString();
        this.labelInstance.setText("Current player move: " + currentPlayerColor);
    }

    public void runNextGameTick(JPanel panel){
        this.reRenderButtons(panel);
        this.changeCurrentPlayerColor();
        this.updateLabelText();
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                Cell cell = (Cell) button.getClientProperty("cell");
                if(cell.isEmpty()) continue;

                Figure cellFigure = cell.getFigure();
                cellFigure.changeReceivedMoveHistoryState();
                if(cellFigure.isMovedFromStart() && cellFigure.isMovedPreviousRound()){
                    cellFigure.changeMovedState();

                }
            }
        }
        this.board.getMoves();
    }

    public void fillBackgroundForCellsWhereFigureWasMoved(JPanel panel,Cell cellFrom, Cell cellTo){
        Component buttonFrom = panel.getComponent(cellFrom.getY()*8 + cellFrom.getX());
        buttonFrom.setBackground(movedFromCellColor);

        Component buttonTo = panel.getComponent(cellTo.getY()*8 + cellTo.getX());
        buttonTo.setBackground(movedToCellColor);
    }
}
