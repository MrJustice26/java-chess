package models.figures;

import models.Cell;
import models.Colors;

public class Queen extends Figure {
    public Queen(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.QUEEN);
    }
}
