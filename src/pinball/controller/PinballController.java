package pinball.controller;

import pinball.model.PinballModelInterface;
import pinball.view.PinballView;

/*
 * XXX Implement game interaction
 * XXX Validate Player name
 */
public class PinballController implements PinballControllerInterface {
    // References
    PinballModelInterface model;
    PinballView view;

    public PinballController(PinballModelInterface model) {
	this.model = model;
	this.view = new PinballView(this, model);
	view.createView();
    }

    @Override
    public void startGame(String name) {
	model.startGame(name);
    }

    @Override
    public void stopGame() {
	model.stopGame();
    }

}
