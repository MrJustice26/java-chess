package models.figures;
import models.Cell;
import models.Colors;

public class Figure {
    private Colors color;
    private Cell cell;
    private FigureNames name;

    private String imageName;

    public Figure(Colors color, Cell cell){
        this.color = color;
        this.cell = cell;
        this.cell.setFigure(this);
        this.name = FigureNames.FIGURE;
    }

    public Colors getColor(){
        return this.color;
    }

    public FigureNames getName(){
        return this.name;
    }

    public void setFigureName(FigureNames name){
        this.name = name;
    }

    public void setImageName(String imageName){ this.imageName = imageName; }

    public String getImageName() { return this.imageName; }

    public void setCell(Cell cell){
        this.cell = cell;
    }

    public Cell getCell(){
        return this.cell;
    }

    public boolean canMove(Cell target) {
        if(target.isEmpty()){
            return true;
        }
        if(target.getFigure().getColor() == this.getColor()) {
            return false;
        }
        if(target.getFigure().getName() == FigureNames.KING){
            return false;
        }
        return true;
    }

}
