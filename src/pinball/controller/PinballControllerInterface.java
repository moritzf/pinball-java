/**
 *
 */
package pinball.controller;

/**
 * Defines the interface of the controller of the application.
 */
public interface PinballControllerInterface {
  /**
   * Called when the game ends. Clears the highscore and possibly saves it.
   *
   * @param responseSaveHighscore
   *          User response from a YES/NO dialog as to whether the highscore
   *          should be saved or not.
   */
  public void stopGame(int responseSaveHighscore);

  /**
   * Called when the game starts. Sets up game start.
   *
   * @param playerName
   *          The name of the player
   */
  public void startGame(String playerName);

  /**
   * Returns whether the game is running or not.
   */
  public boolean isRunning();

  /**
   * Adds points to the current score
   */
  public void addPoints(int points);

  public void decreaseLives();
}
