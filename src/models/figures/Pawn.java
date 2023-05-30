package models.figures;

import models.Cell;
import models.Colors;

public class Pawn extends Figure {
    public Pawn(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.PAWN);
        String imageName = color == Colors.BLACK ? "black" : "white";
        imageName = imageName.concat("-pawn.png");
        this.setImageName(imageName);
    }

    public boolean canMove(Cell target){
        if(!super.canMove(target)) return false;


        Cell cell = this.getCell();
        int diffX = cell.getX() - target.getX();
        int diffY = cell.getY() - target.getY();

        int absX = Math.abs(diffX);
        int absY = Math.abs(diffY);

        // Common case if pawn tries to move too far
        if(absY > 2) return false;

        // Disable ability to move somewhere by X;
        if(absY == 0 && absX > 0) return false;

        // Common case if in front of pawn figure exists
        if(absY == 1 && absX == 0 && !target.isEmpty()) return false;

        // Case if pawn in left/right side of pawn moved by 2 cells
        if(absX == 1 && absY == 1){
            Cell leftCell = cell.getRelativeLeftCellByX();
            Cell rightCell = cell.getRelativeRightCellByX();
            if(diffX == 1 && !leftCell.isEmpty() && leftCell.getFigure().getMovedState() == MovedStates.MOVED_FROM_START && leftCell.getFigure().getName() == FigureNames.PAWN || diffX == -1 && !rightCell.isEmpty() && rightCell.getFigure().getMovedState() == MovedStates.MOVED_FROM_START && rightCell.getFigure().getName() == FigureNames.PAWN){
                if(diffY == 1 && cell.getFigure().getColor() == Colors.WHITE){
                    return true;
                }
                if(diffY == -1 && cell.getFigure().getColor() == Colors.BLACK){
                    return true;
                }
            }
        }

        // Common case if figure is on diagonal
        if(absY == 1 && absX == 1 && target.isEmpty()) return false;

        // Common case if figure tries to far in X axis
        if(absX > 1) return false;

        if(absX == 1 && absY == 2) return false;
        if(this.getHasMoved() && absY == 2) return false;

        if(absX == 1 && target.isEmpty()) return false;

        if(absY == 2 && !this.getCell().isEmptyByVertical(target) || absY == 2 && !target.isEmpty()) return false;


        // Black pawn case;
        if(this.getColor() == Colors.BLACK){
            // Case if tries to move to opposite Y direction
            if(diffY >= 0) return false;
        }

        // White pawn case
        if(this.getColor() == Colors.WHITE){
            // Case if tries to move to opposite Y direction
            return diffY >= 0;
        }


        return true;
    }


}