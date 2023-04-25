package figures;

public class Figure {
    private FigureColor color = FigureColor.UNKNOWN;
    private String type = "";
    private boolean isJumpingOver = false;
    private boolean hasUniversalMoves = false;

    public Figure(String figureType, boolean isJumpingOver, boolean hasUniversalMoves){
        this.type = figureType;
        this.isJumpingOver = isJumpingOver;
        this.hasUniversalMoves = hasUniversalMoves;
    }

    public void setColor(FigureColor color){
        this.color = color;
    }

    public FigureColor getColor(){
        return this.color;
    }

}
