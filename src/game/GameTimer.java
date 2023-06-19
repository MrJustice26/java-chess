package game;

import javax.swing.Timer;

public class GameTimer {

    GameManager gameManagerInstance;
    Timer timerInstance;

    int performedMoveBonus;

    int INITIAL_TIME;

    int[] playersEstimatedTimeAmount = {-1, -1};

    int selectedPlayerIdx = 0;


    public GameTimer(GameManager gameManager, int initialTime, int performedMoveBonus){
        this.gameManagerInstance = gameManager;

        this.INITIAL_TIME = initialTime;

        this.playersEstimatedTimeAmount[0] = this.INITIAL_TIME;
        this.playersEstimatedTimeAmount[1] = this.INITIAL_TIME;

        this.gameManagerInstance.updateEstimatedTimePanel(this.playersEstimatedTimeAmount);

        this.performedMoveBonus = performedMoveBonus;

        this.timerInstance = new javax.swing.Timer(1000, e -> this.timerAction());
        this.timerInstance.start();
    }

    public void changePlayerTimer(){
        this.selectedPlayerIdx = (this.selectedPlayerIdx + 1) % 2;
    }

    public void timerAction(){
        this.playersEstimatedTimeAmount[this.selectedPlayerIdx] -= 1;

        this.gameManagerInstance.updateEstimatedTimePanel(this.playersEstimatedTimeAmount);

        if(this.playersEstimatedTimeAmount[this.selectedPlayerIdx] == 0){
            this.timerInstance.stop();
            this.gameManagerInstance.setGameOver();
        }

    }

    public void giveAdditionalSeconds(int playerIdx){
        this.playersEstimatedTimeAmount[playerIdx] += this.performedMoveBonus;
    }

    public void resetTimer(){
        this.playersEstimatedTimeAmount[0] = this.INITIAL_TIME;
        this.playersEstimatedTimeAmount[1] = this.INITIAL_TIME;

        this.selectedPlayerIdx = 0;
        this.timerInstance.start();
    }

    public void stopTimer(){
        this.timerInstance.stop();
    }


}
