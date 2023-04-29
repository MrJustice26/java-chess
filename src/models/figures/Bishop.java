package models.figures;

import models.Cell;
import models.Colors;

public class Bishop extends Figure {
    public Bishop(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.BISHOP);
        String imageName = color == Colors.BLACK ? "black" : "white";
        imageName = imageName.concat("-bishop.png");
        this.setImageName(imageName);
    }

    public boolean canMove(Cell target){
        if(!super.canMove(target)) return false;
        if(!this.getCell().isEmptyByDiagonal(target)) return false;

        return true;
    }
}
