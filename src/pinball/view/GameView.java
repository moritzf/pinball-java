package pinball.view;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import pinball.model.view.Screen;
import pinball.model.view.ViewConstants;
import pinball.model.view.ViewState;

public class GameView extends JFrame implements Observer, ViewConstants {
    private Screen activeView;
    private ViewState viewState;

    public GameView(ViewState viewState) {
	this.viewState = viewState;
	this.activeView = viewState.getViewState();
	createAndShowGUI();
	display();
    }

    private void createAndShowGUI() {
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setTitle("Pinball");
	this.setSize(APP_WIDTH, APP_HEIGHT);
	this.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
	if (o == viewState) {
	    activeView = viewState.getViewState();
	    display();
	}
    }

    public Screen getActiveView() {
	return activeView;
    }

    public void display() {
	if (activeView == Screen.valueOf("MENU")) {
	    this.setLayout(new BorderLayout());
	    this.add(new MenuScreen(viewState), BorderLayout.CENTER);
	} else if (activeView == Screen.valueOf("GAME"))
	    this.add(new GameScreen(viewState));
	else if (activeView == Screen.valueOf("HIGHSCORE"))
	    this.add(new HighscoreScreen(viewState));
	else if (activeView == Screen.valueOf("CREDITS"))
	    this.add(new CreditsScreen(viewState));
    }
}
