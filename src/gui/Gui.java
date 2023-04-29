package gui;

import models.Board;
import models.Cell;
import models.Colors;
import models.figures.Figure;

import javax.swing.*;
import java.awt.*;

public class Gui {

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
                button.addActionListener(e -> actionButtonScript(button));
                button.setBackground((i + j) % 2 == 0 ? Color.white : Color.gray);
                panel.add(button);
            }
        }

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

    private void actionButtonScript(JButton button){
        Cell cell = (Cell) button.getClientProperty("cell");

        switch(moveState){
            case NOT_SELECTED:
                if(cell.isEmpty()) return;

                button.setBackground(Color.darkGray);
                moveState = MoveStates.SELECTED;
                selectedButtonInstance = button;
                break;
            case SELECTED:
                Cell selectedCell = (Cell) selectedButtonInstance.getClientProperty("cell");
                Color selectedCellColor = selectedCell.getFigure().getColor() == Colors.BLACK ? Color.gray : Color.white;
                if(selectedCell == cell){
                    System.out.println("Same piece clicked");
                    System.out.println(selectedCellColor);
                    selectedButtonInstance.setBackground(selectedCellColor);
                    break;
                }
                if(selectedCell.getFigure().canMove(cell)){
                    System.out.println("Piece can go to");
                    moveState = MoveStates.NOT_SELECTED;
                    selectedButtonInstance.setBackground(Color.black);
                } else {
                    System.out.println("Piece can't go");
                    moveState = MoveStates.NOT_SELECTED;
                }


                break;
            default:
                System.out.println("ERROR: Undefined case in moveState!!!");
        }
    }
}
