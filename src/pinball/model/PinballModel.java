/**
 *
 */
package pinball.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import pinball.helper.HighscoreComparator;

/**
 *
 */
public class PinballModel extends AbstractTableModel implements
	PinballModelInterface {
    private ArrayList<Object[]> highscoreData;
    private int currentHighscore;
    private String currentPlayer;
    private String[] columnNames = { "Name", "Score" };

    public PinballModel() {
	highscoreData = new ArrayList<Object[]>();
	currentHighscore = 500;
	currentPlayer = "Moritz";
	addHighscore();
	sortHighscoreList();
    }

    @Override
    public void startGame(String name) {
	currentPlayer = name;
	currentHighscore = 0;
    }

    @Override
    public void stopGame() {
	addHighscore();
    }

    @Override
    public int getRowCount() {
	return highscoreData.size();
    }

    @Override
    public int getColumnCount() {
	return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
	return highscoreData.get(rowIndex)[columnIndex];
    }

    @Override
    public String getColumnName(int col) {
	return columnNames[col];
    }

    /*
     * Sorts the high score list in descending order by score.
     */
    private void sortHighscoreList() {
	highscoreData.sort(new HighscoreComparator());
    }

    /**
     * Adds the score of the current player to the high score list.
     */
    public void addHighscore() {
	Object[] data = { currentPlayer, currentHighscore };
	highscoreData.add(data);
	sortHighscoreList();
    }
}
