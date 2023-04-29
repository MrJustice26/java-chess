package models.figures;

import models.Cell;
import models.Colors;

public class Horse extends Figure {
    public Horse(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.HORSE);
    }
}
