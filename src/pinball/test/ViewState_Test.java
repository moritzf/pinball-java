package pinball.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import pinball.model.view.Screen;
import pinball.model.view.ViewState;
import pinball.view.GameView;

public class ViewState_Test {

    ViewState viewState;
    GameView gameView;

    @Before
    public void setUp() {
	viewState = new ViewState();
    }

    @Test
    public void InitialViewIsMenu() {
	assertEquals(Screen.valueOf("MENU"), viewState.getViewState());
    }

}
