package models.figures;

import models.Cell;
import models.Colors;

public class Queen extends Figure {
    public Queen(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.QUEEN);
        String imageName = color == Colors.BLACK ? "black" : "white";
        imageName = imageName.concat("-queen.png");
        this.setImageName(imageName);
    }

    public boolean canMove(Cell target){
        if(!super.canMove(target)) return false;
        if(this.getCell().isEmptyByVertical(target)) return true;
        if(this.getCell().isEmptyByHorizontal(target)) return true;
        return this.getCell().isEmptyByDiagonal(target);
    }
}
