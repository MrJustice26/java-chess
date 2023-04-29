package models.figures;

import models.Cell;
import models.Colors;

public class Bishop extends Figure {
    public Bishop(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.BISHOP);
    }
}
