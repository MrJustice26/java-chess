package models.figures;

import models.Cell;
import models.Colors;

public class Pawn extends Figure {
    public Pawn(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.PAWN);
    }
}