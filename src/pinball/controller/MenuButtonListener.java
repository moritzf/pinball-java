/**
 *
 */
package pinball.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pinball.model.view.Screen;
import pinball.model.view.ViewState;

/**
 *
 */
public class MenuButtonListener implements ActionListener {

    private ViewState viewState;

    /**
     * @param viewState
     */
    public MenuButtonListener(ViewState viewState) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand() == "Play") {
	    System.out.println("Play");
	    viewState.setViewState(Screen.GAME);
	} else if (e.getActionCommand() == "Highscore") {
	    viewState.setViewState(Screen.HIGHSCORE);
	} else if (e.getActionCommand() == "Credits") {
	    viewState.setViewState(Screen.CREDITS);
	}
    }
}
