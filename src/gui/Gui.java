package gui;

import models.Board;
import models.Cell;
import models.figures.Figure;

import javax.swing.*;
import java.awt.*;

public class Gui {

    Color greenColor = new Color(116, 217, 38);

    Board board;
    String assetsFolderPath = "assets/";
    enum MoveStates {
        NOT_SELECTED,
        SELECTED,
    }
    MoveStates moveState = MoveStates.NOT_SELECTED;

    JButton selectedButtonInstance;
    public Gui(Board board){
        this.board = board;
        JFrame frame = new JFrame("Chess Board");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(8, 8));
        panel.setBackground(Color.white);

        this.renderButtons(panel);

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

        switch(moveState){
            case NOT_SELECTED:
                if(cell.isEmpty()) return;

                button.setBackground(Color.darkGray);
                moveState = MoveStates.SELECTED;
                selectedButtonInstance = button;

                boolean[][] availableMoves = cell.getAvailableMoves();
                for(int y = 0; y < 8; y++){
                    for(int x = 0; x < 8; x++){
                        if(availableMoves[y][x]){
                            Component component = panel.getComponent(y * 8 + x);
                            if (component instanceof JButton) {
                                JButton iteratedButton = (JButton) component;
                                iteratedButton.setBackground(this.greenColor);
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
                    moveState = MoveStates.NOT_SELECTED;
                    break;
                }
                if(selectedCell.getFigure().canMove(cell)){
                    moveState = MoveStates.NOT_SELECTED;
                    selectedButtonInstance.setBackground(Color.black);
                    selectedCell.moveFigure(cell);
                    reRenderButtons(panel);
                }


                break;
            default:
                System.out.println("ERROR: Undefined case in moveState!!!");
        }
    }
}
