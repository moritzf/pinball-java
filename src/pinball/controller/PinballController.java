package pinball.controller;

import pinball.model.PinballModel;
import pinball.model.PinballModelInterface;
import pinball.view.PinballView;

public class PinballController implements PinballControllerInterface {
    // References
    PinballModelInterface model;
    PinballView view;

    public PinballController(PinballModelInterface model) {
	this.model = new PinballModel();
	this.view = new PinballView(this, model);
	view.createView();
    }

    @Override
    public void startGame(String name) {
	model.startGame(name);
	System.out.println("Game started.");
    }

    @Override
    public void stopGame() {
	model.stopGame();
	System.out.println("Game stopped");
    }

}
