package models.figures;
import models.Cell;
import models.Colors;

public class Figure {
    private Colors color;
    private Cell cell;
    private FigureNames name;

    private String imageName;

    // TODO Remove it and use MovedStates
    private boolean hasMoved;

    enum MovedStates {
        NOT_MOVED,
        MOVED_FROM_START,
        MOVED_FROM_NOT_START
    }

    enum ReceivedMoveHistoryStates {
        THIS_ROUND,
        PREVIOUS_ROUND,
        OLDER_THAN_PREVIOUS_ROUND
    }

    MovedStates movedState = MovedStates.NOT_MOVED;

    ReceivedMoveHistoryStates receivedMoveHistoryState = ReceivedMoveHistoryStates.THIS_ROUND;

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
        return (target.getFigure().getName() != FigureNames.KING);
    }

    public boolean getHasMoved(){ return this.hasMoved;}
    public void setIsMoved(boolean moveState){
        this.hasMoved = moveState;
    }

    public MovedStates getMovedState(){
        return this.movedState;
    }

    public void setMovedState(MovedStates movedState){
        this.movedState = movedState;
    }

    public void changeMovedState(){
        switch(this.getMovedState()){
            case NOT_MOVED:
                this.setMovedState(MovedStates.MOVED_FROM_START);
                break;
            case MOVED_FROM_START:
                this.setMovedState(MovedStates.MOVED_FROM_NOT_START);
                break;
        }
    }

    public void changeReceivedMoveHistoryState(){
        switch(this.receivedMoveHistoryState){
            case THIS_ROUND:
                this.receivedMoveHistoryState = ReceivedMoveHistoryStates.PREVIOUS_ROUND;
                break;
            case PREVIOUS_ROUND:
                this.receivedMoveHistoryState = ReceivedMoveHistoryStates.OLDER_THAN_PREVIOUS_ROUND;
                break;
            case OLDER_THAN_PREVIOUS_ROUND:
                break;
            default:
                System.out.println("Undefined receivedMoveHistoryState!!!");
                break;
        }

    }

    public void markAsUpdatedMoveHistoryState(){
        this.receivedMoveHistoryState = ReceivedMoveHistoryStates.THIS_ROUND;
    }

    public boolean isMovedPreviousRound(){
        return this.receivedMoveHistoryState == ReceivedMoveHistoryStates.PREVIOUS_ROUND;
    }

    public boolean isMovedFromStart(){
        return this.getMovedState() == MovedStates.MOVED_FROM_START;
    }


}
