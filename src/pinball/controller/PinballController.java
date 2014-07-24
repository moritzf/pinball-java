package pinball.controller;

import javax.swing.JOptionPane;

import pinball.model.PinballModelInterface;
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
	model.initDatabase();
  }

  @Override
  public void startGame(String playerName) {
	model.setPlayerName(playerName);
	model.setRunning(true);
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

}
