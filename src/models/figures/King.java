package models.figures;

import models.Cell;
import models.Colors;

public class King extends Figure {
    public King(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.KING);
    }
}
