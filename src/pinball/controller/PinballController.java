package pinball.controller;

import javax.swing.JOptionPane;

import pinball.model.LiveObserver;
import pinball.model.PinballModelInterface;
import pinball.model.ScoreObserver;
import pinball.view.PinballView;

/*
 * XXX Implement game interaction
 */
public class PinballController implements PinballControllerInterface {
  // References
  PinballModelInterface model;
  PinballView view;

  public PinballController(PinballModelInterface model) {
	this.model = model;
	view = new PinballView(this, model);
	view.createView();
	model.addObserver((ScoreObserver) view);
	model.addObserver((LiveObserver) view);
	model.initDatabase();
  }

  @Override
  public void startGame(String playerName) {
	model.setPlayerName(playerName);
	model.setRunning(true);
	model.setLives(3);
  }

  @Override
  public boolean isRunning() {
	return model.isRunning();
  }

  @Override
  public void stopGame(int responseSaveHighscore) {
	if (responseSaveHighscore == JOptionPane.YES_OPTION) {
	  model.saveHighscore();
	}
	model.setRunning(false);
	model.clearCurrentPlayerData();
  }

  @Override
  public void addPoints(int points) {
	model.addPoints(points);
  }

  @Override
  public void decreaseLives() {
	if (model.getLives() == 1) {
	  view.setMessage("Game Over");
	  wait100Ms(5);
	  stopGame(view.getSaveHighscoreDialog());
	  view.showMenu();
	} else {
	  int currentLives = model.getLives();
	  model.setLives(--currentLives);
	  wait100Ms(5);
	  view.createNewBall();
	}
  }

  public void wait100Ms(int numberOfTimes) {
	try {
	  Thread.sleep(100 * numberOfTimes);
	} catch (InterruptedException ex) {
	  Thread.currentThread().interrupt();
	}
  }
}
