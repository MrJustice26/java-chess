package models.figures;

import models.Cell;
import models.Colors;

public class King extends Figure {

    private boolean isChecked = false;

    public King(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.KING);
        String imageName = color == Colors.BLACK ? "black" : "white";
        imageName = imageName.concat("-king.png");
        this.setImageName(imageName);
    }

    public boolean canMove(Cell target){
        if(!super.canMove(target)) return false;

        Cell cell = this.getCell();
        int absX = Math.abs(cell.getX() - target.getX());
        int absY = Math.abs(cell.getY() - target.getY());

        if(absX > 1 || absY > 1) return false;

        return true;
    }

    public boolean getCheckedStatus(){
        return this.isChecked;
    }

    public void setCheckedStatus(boolean checkedStatus){
        this.isChecked = checkedStatus;
    }
}
