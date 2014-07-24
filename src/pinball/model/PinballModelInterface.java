/**
 *
 */
package pinball.model;

/**
 * Interface of the Pinball model class.
 */
public interface PinballModelInterface {
  /**
   * Responsible for creating a score database in an external file. If the file
   * exists, the existing database is used. If the file does not exist, a new
   * one is created.
   */
  public void initDatabase();

  /**
   * Sets the player name.
   */
  public void setPlayerName(String name);

  /**
   * Saves the current highscore and player name to the database
   */
  void saveHighscore();

  /**
   * Deletes the current player and sets the highscore to 0.
   */
  public void clearCurrentPlayerData();

  /**
   * Sets whether the game is running or not.
   */
  public void setRunning(boolean status);

  /**
   * Gets the game status
   */
  public boolean isRunning();

}
