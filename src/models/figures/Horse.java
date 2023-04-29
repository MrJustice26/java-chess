package models.figures;

import models.Cell;
import models.Colors;

public class Horse extends Figure {
    public Horse(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.HORSE);
        String imageName = color == Colors.BLACK ? "black" : "white";
        imageName = imageName.concat("-horse.png");
        this.setImageName(imageName);
    }

    public boolean canMove(Cell target){
        if(!super.canMove(target)) return false;

        Cell cell = this.getCell();
        int absX = Math.abs(cell.getX() - target.getX());
        int absY = Math.abs(cell.getY() - target.getY());
        return (absX == 2 && absY == 1) || (absX == 1 && absY == 2);
    }
}
