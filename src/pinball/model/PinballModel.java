/**
 *
 */
package pinball.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import pinball.constants.FileConstants;
import pinball.helper.HighscoreComparator;

/**
 * Responsible for handling the database of scores, the world and the state of
 * the game.
 */
public class PinballModel extends AbstractTableModel implements
PinballModelInterface, FileConstants {
  private ArrayList<Object[]> highscoreData;
  private int currentHighscore;
  private String currentPlayer;
  private String[] columnNames = { "Name", "Score" };
  private File db;
  private boolean running;
  private ScoreObserver scoreObserver;
  private LiveObserver liveObserver;
  private String currentMessage = "";
  private int currentLives = 0;

  @Override
  public boolean isRunning() {
	return running;
  }

  @Override
  public void setRunning(boolean running) {
	this.running = running;
  }

  public PinballModel() {
	highscoreData = new ArrayList<Object[]>();
  }

  @Override
  public void setPlayerName(String name) {
	currentPlayer = name;
  }

  @Override
  public void clearCurrentPlayerData() {
	currentHighscore = 0;
	scoreChanged();
	currentPlayer = "";
	currentLives = 0;
	liveChanged();
  }

  @Override
  public void saveHighscore() {
	addHighscore();
	fireTableDataChanged();
	writeDatabaseFile();
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
   * Adds the score of the current player to the high score list. After that the
   * list is sorted in descending order.
   */
  public void addHighscore() {
	Object[] current = { currentPlayer, currentHighscore };
	boolean entryAlreadyThere = false;
	for (Object[] entry : highscoreData) {
	  if (entry[0].equals(currentPlayer)) {
		entryAlreadyThere = true;
		entry[1] = currentHighscore;
	  }
	}
	if (!entryAlreadyThere) {
	  highscoreData.add(current);
	}
	sortHighscoreList();
  }

  /**
   * Used to add a highscore object to the internal database.
   *
   * @param data
   *          The object consisting of the player name and his score
   */
  private void addHighscore(Object[] data) {
	highscoreData.add(data);
	sortHighscoreList();
  }

  @Override
  public void initDatabase() {
	try {
	  db = new File(DB_FILE);
	  if (db.exists() && !db.isDirectory()) {
		readDatabaseFile();
	  }
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }

  /**
   * Reads from the database and stores it in an interal representation.
   */
  private void readDatabaseFile() {
	try {
	  String line;
	  BufferedReader rd = new BufferedReader(new FileReader(db));
	  while ((line = rd.readLine()) != null) {
		addHighscore(parseLine(line));
	  }
	  rd.close();
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }

  /**
   * Writes the internal representation of the database to a determined file.
   */
  private void writeDatabaseFile() {
	try {
	  PrintWriter writer = new PrintWriter(new FileWriter(db));
	  for (Object[] data : highscoreData) {
		writer.println(data[0] + ":" + data[1]);
	  }
	  writer.close();
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }

  /**
   * Parses a line in the database file.
   *
   * @return An array representation of an entry in the database
   */
  private Object[] parseLine(String line) {
	Object[] data = new Object[2];
	data[0] = line.substring(0, line.indexOf(':'));
	data[1] = Integer.valueOf(line.substring(line.indexOf(':') + 1));
	return data;
  }

  @Override
  public void addPoints(int points) {
	currentHighscore += points;
	notifyScoreObserver();
  }

  /**
   * Used to notify score observer
   */
  public void notifyScoreObserver() {
	scoreObserver.updateScore(currentHighscore);
  }

  /**
   * Called when score changes
   */
  public void scoreChanged() {
	notifyScoreObserver();
  }

  /**
   * Called when live changes
   */
  public void liveChanged() {
	notifyLiveObserver();
  }

  /**
   * Used to notify live observer
   */
  public void notifyLiveObserver() {
	liveObserver.updateLives("Lives " + getLives());
  }

  @Override
  public void addObserver(ScoreObserver scoreObserver) {
	this.scoreObserver = scoreObserver;
  }

  @Override
  public void addObserver(LiveObserver liveObserver) {
	this.liveObserver = liveObserver;
  }

  @Override
  public int getLives() {
	return currentLives;
  }

  @Override
  public void setLives(int i) {
	currentLives = i;
	liveChanged();
  }
}
