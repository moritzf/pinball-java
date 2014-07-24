package pinball.driver;

import pinball.controller.PinballController;
import pinball.controller.PinballControllerInterface;
import pinball.model.PinballModel;
import pinball.model.PinballModelInterface;

/**
 * Provides the starting point for the game and contains the game engine.
 */
public class GameEntryPoint {
  public static void main(String[] args) {
	PinballModelInterface model = new PinballModel();
	PinballControllerInterface controller = new PinballController(model);
  }
}