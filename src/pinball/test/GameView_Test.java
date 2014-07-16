package pinball.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import pinball.model.view.Screen;
import pinball.model.view.ViewState;
import pinball.view.GameView;

public class GameView_Test {

    GameView gameView;
    ViewState viewState;

    @Before
    public void setUp() throws Exception {
	viewState = new ViewState();
	gameView = new GameView(viewState);
	viewState.addObserver(gameView);
    }

    @Test
    public void ActiveViewInit() {
	assertEquals(Screen.MENU, gameView.getActiveView());
    }

    @Test
    public void ActiveViewCorrectlySetAfterChangeInViewState() {
	viewState.setViewState(Screen.GAME);
	assertEquals(Screen.GAME, gameView.getActiveView());
    }
}
