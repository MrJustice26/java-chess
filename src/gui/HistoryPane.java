package gui;

import javax.swing.*;
import java.awt.*;

public class HistoryPane {

    JTextPane historyTextPaneInstance;

    public HistoryPane(){
        this.historyTextPaneInstance = new JTextPane();
        this.historyTextPaneInstance.setPreferredSize(new Dimension(240, 8*75 - 30 - 100));
        this.historyTextPaneInstance.setEditable(false);
        this.historyTextPaneInstance.setBackground(Color.white);
        this.historyTextPaneInstance.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createStrokeBorder(new BasicStroke()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    public JTextPane getHistoryTextPaneInstance() {
        return historyTextPaneInstance;
    }

    public void printHistory(String[] doneMoves){
        String result = "";
        for(int doneMoveIdx = 0; doneMoveIdx < doneMoves.length; doneMoveIdx++){
            if(doneMoves[doneMoveIdx] == null) break;

            result = result.concat(String.format("%d. %s\t", doneMoveIdx + 1, doneMoves[doneMoveIdx]));
        }
        this.historyTextPaneInstance.setText(result);
    }
}
