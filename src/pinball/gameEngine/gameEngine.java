package pinball.gameEngine;

import pinball.model.view.ViewState;
import pinball.view.GameView;

public class gameEngine {
    public static void main(String[] args) {
	ViewState viewState = new ViewState();
	GameView gameView = new GameView(viewState);
	viewState.addObserver(gameView);
    }
}
