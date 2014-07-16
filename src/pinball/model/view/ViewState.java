package pinball.model.view;

import java.util.Observable;

import pinball.view.GameView;

/**
 * Defines which view is active.
 */
public class ViewState extends Observable {

    private Screen view;
    private GameView gameView;

    public ViewState() {
	setViewState(Screen.MENU);
    }

    /**
     * @return the view
     */
    public Screen getViewState() {
	return view;
    }

    /**
     * @param view
     *            the view to set
     */
    public void setViewState(Screen view) {
	this.view = view;
	setChanged();
	notifyObservers();
    }

}
