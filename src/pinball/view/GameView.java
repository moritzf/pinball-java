package pinball.view;

import java.util.Observable;
import java.util.Observer;

import pinball.model.view.Screen;
import pinball.model.view.ViewState;

public class GameView implements Observer {
    private Screen activeView;
    private ViewState viewState;

    public GameView(ViewState viewState) {
	this.viewState = viewState;
	this.activeView = viewState.getViewState();
    }

    @Override
    public void update(Observable o, Object arg) {
	if (o == viewState)
	    activeView = viewState.getViewState();
    }

    public Screen getActiveView() {
	return activeView;
    }
}
