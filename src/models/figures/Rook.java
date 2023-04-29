package models.figures;

import models.Cell;
import models.Colors;

public class Rook extends Figure {
    public Rook(Colors color, Cell cell) {
        super(color, cell);
        this.setFigureName(FigureNames.ROOK);
    }
}
