package models.figures;

import models.Cell;
import models.Colors;

public class Rook extends Figure {
    public Rook(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.ROOK);
        String imageName = color == Colors.BLACK ? "black" : "white";
        imageName = imageName.concat("-rook.png");
        this.setImageName(imageName);
    }

    public boolean canMove(Cell target){
        if(!super.canMove(target)){
            return false;
        }
        Cell cell = this.getCell();
        if(cell.isEmptyByVertical(target)){
            return true;
        }
        return cell.isEmptyByHorizontal(target);
    }
}
