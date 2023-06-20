package gui;

import game.GameManager;
import game.GameManagerCommands;
import models.Board;
import models.Cell;
import models.Colors;
import models.figures.*;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class Gui {

    Color greenColor = new Color(116, 217, 38);
    Color focusedCellColor = new Color(110, 229, 220);
    Color movedFromCellColor = new Color(155, 24, 112);
    Color movedToCellColor = new Color(213, 59, 159);



    Board board;
    GameManager gameManager;
    String assetsFolderPath = "assets/";


    JPanel chessPanel;
    JTextPane currentPlayerTextPane;
    JTextPane estimatedTimeTextPane;




    public HistoryPane historyTextPane;




    public Gui(Board board, GameManager gameManager){
        this.initGUI(board, gameManager);
    }

    private void initGUI(Board board, GameManager gameManager){
        this.board = board;
        this.gameManager = gameManager;
        JFrame mainFrame = new JFrame("Chess Board");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        JPanel chessPanel = new JPanel(new GridLayout(8, 8));
        chessPanel.setPreferredSize(new Dimension(8 * 75, 8 * 75));
        chessPanel.setBackground(Color.white);


        JPanel additionalPanel = new JPanel();
        additionalPanel.setPreferredSize(new Dimension(240, 8 * 75));
        additionalPanel.setBackground(Color.lightGray);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(chessPanel, BorderLayout.EAST);
        contentPanel.add(additionalPanel, BorderLayout.WEST);

        JTextPane currentPlayerTextPane = new JTextPane();
        currentPlayerTextPane.setPreferredSize(new Dimension(240, 30));
        currentPlayerTextPane.setEditable(false);
        currentPlayerTextPane.setBackground(Color.lightGray);
        currentPlayerTextPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,5));

        StyledDocument document = currentPlayerTextPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        document.setParagraphAttributes(0, document.getLength(), style, false);

        this.currentPlayerTextPane = currentPlayerTextPane;
        this.currentPlayerTextPane.setText("Current player move: WHITE");

        this.historyTextPane = new HistoryPane();

        JTextPane estimatedTimeTextPane = new JTextPane();
        estimatedTimeTextPane.setPreferredSize(new Dimension(240, 100));
        estimatedTimeTextPane.setEditable(false);
        estimatedTimeTextPane.setBackground(Color.lightGray);
        estimatedTimeTextPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,5));

        this.estimatedTimeTextPane = estimatedTimeTextPane;


        additionalPanel.add(currentPlayerTextPane);
        additionalPanel.add(this.historyTextPane.getHistoryTextPaneInstance());
        additionalPanel.add(this.estimatedTimeTextPane);

        this.chessPanel = chessPanel;

        this.initChessButtons();
        mainFrame.add(contentPanel);
        mainFrame.setVisible(true);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);


    }

    private void applyButtonStyles(JButton buttonInstance){
        buttonInstance.setHorizontalTextPosition(JButton.CENTER);
        buttonInstance.setVerticalTextPosition(JButton.CENTER);
        buttonInstance.setFocusPainted(false);
        buttonInstance.setBorderPainted(false);
        buttonInstance.setRolloverEnabled(false);
    }

    private void initChessButtons(){
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                JButton button = new JButton();
                this.applyButtonStyles(button);
                int buttonPosition = y * 8 + x;
                button.addActionListener(e -> this.actionButtonScript(buttonPosition));
                this.chessPanel.add(button);
            }
        }
        this.drawButtonsIcon();
        this.drawDefaultBackgroundButtonsColor();
    }

    public void reRenderButtons(){
        this.drawButtonsIcon();
        this.drawDefaultBackgroundButtonsColor();
        this.chessPanel.revalidate();
        this.chessPanel.repaint();
    }

    private void drawButtonsIcon(){
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                Component component = this.chessPanel.getComponent(y*8 + x);
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    Cell cellData = this.board.getCell(x, y);
                    if(cellData.isEmpty()) {
                        button.setIcon(null);
                        continue;
                    }

                    Figure cellFigure = cellData.getFigure();
                    String pathToFigureImage = this.assetsFolderPath + cellFigure.getImageName();
                    ImageIcon icon = new ImageIcon(pathToFigureImage);
                    button.setIcon(icon);
                }
            }
        }
    }

    public void drawDefaultBackgroundButtonsColor(){
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                Component component = this.chessPanel.getComponent(y*8 + x);
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    Cell cellData = this.board.getCell(x, y);

                    Color cellColor = cellData.getColor() == Colors.BLACK ? Color.gray : Color.white;
                    button.setBackground(cellColor);
                    button.setBorderPainted(false);

                }
            }
        }
    }
    private void actionButtonScript(int buttonPosition){
        Component component = this.chessPanel.getComponent(buttonPosition);
        if (!(component instanceof JButton)) {
            return;
        }
        JButton button = (JButton) component;
        int y = buttonPosition / 8;
        int x = buttonPosition % 8;
        Cell cellData = this.board.getCell(x, y);
        GameManagerCommands receivedCommand = this.gameManager.handlePieceButtonClick(cellData);

        switch(receivedCommand){
            case FILL_SELECTED_BUTTON_BACKGROUND_WITH_FOCUSED_COLOR:
                button.setBackground(this.focusedCellColor);
                break;

            case NO_ACTIONS_TO_PERFORM:
                break;

            default:
                break;
        }
    }



    public void updateTextPane(){
        String currentPlayerColor = this.gameManager.getCurrentPlayerColor().toString();
        this.currentPlayerTextPane.setText("Current player move: " + currentPlayerColor);
    }

    public void paintAvailableCellMoves(boolean[][] availableMoves){
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                if(availableMoves[y][x]){
                    Component component = this.chessPanel.getComponent(y * 8 + x);
                    if (component instanceof JButton) {
                        JButton iteratedButton = (JButton) component;
                        Cell iteratedCell = this.board.getCell(x, y);
                        if(iteratedCell.isEmpty()){
                            iteratedButton.setBackground(this.greenColor);
                        } else {
                            iteratedButton.setBackground(Color.red);
                        }
                        iteratedButton.setBorderPainted(true);
                    }
                }
            }
        }
    }

    public void fillBackgroundRecentMoveCells(){

        Cell[] recentMoveRecord = this.board.getRecentMoveRecord();
        Cell cellFrom = recentMoveRecord[0];
        Cell cellTo = recentMoveRecord[1];

        if(cellFrom == null || cellTo == null) return;

        Component buttonFrom = this.chessPanel.getComponent(cellFrom.getY()*8 + cellFrom.getX());
        buttonFrom.setBackground(movedFromCellColor);

        Component buttonTo = this.chessPanel.getComponent(cellTo.getY()*8 + cellTo.getX());
        buttonTo.setBackground(movedToCellColor);
    }

    public void paintCheckedKings(){

        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                Component component = this.chessPanel.getComponent(y*8 + x);
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    Cell cell = this.board.getCell(x, y);
                    if(cell.isEmpty()) continue;

                    Figure cellFigure = cell.getFigure();
                    if(cellFigure.getName() != FigureNames.KING) continue;

                    King kingFigure = (King) cellFigure;
                    if(kingFigure.getCheckedStatus()){
                        button.setBackground(Color.red);
                    }
                }
            }
        }

    }



    public void printEstimatedTime(int[] PLAYERS_ESTIMATED_TIME){
        String whitePlayerText = String.format("White player: %d seconds\n", PLAYERS_ESTIMATED_TIME[0]);
        String blackPlayerText = String.format("Black player: %d seconds", PLAYERS_ESTIMATED_TIME[1]);
        this.estimatedTimeTextPane.setText(whitePlayerText.concat(blackPlayerText));
    }

    public int showOptionDialog(String textContent){
        Object[] options = {"Tak", "Nie"};

        return JOptionPane.showOptionDialog(null,
                textContent,
                "Potwierdzenie",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    public int showChoosePawnDialog(){
        int selectedCorrectFigure;
         while(true){
            String selectedFigureStr = JOptionPane.showInputDialog(null, "Choose the chess piece:\n1 - Queen\n2 - Horse\n3 - Rook\n4 - Bishop");
             try {
                 if(selectedFigureStr == null){
                     continue;
                 }
                 int selectedFigure = Integer.parseInt(selectedFigureStr);

                 if(selectedFigure > 0 && selectedFigure <= 4){
                     selectedCorrectFigure = selectedFigure;
                     break;
                 }
             }  catch (NumberFormatException ignored){}

         }
         return selectedCorrectFigure;
    }

}
