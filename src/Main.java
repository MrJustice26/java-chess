import gui.Gui;
import models.Board;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        board.initCells();
        board.addFigures();
        new Gui(board);
    }
}