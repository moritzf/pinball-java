package pinball.view;

import javax.swing.JButton;
import javax.swing.JPanel;

import pinball.controller.MenuButtonListener;
import pinball.model.view.ViewState;

public class MenuScreen extends JPanel {
    private JButton play;
    private JButton highscore;
    private JButton credits;
    private ViewState viewState;

    public MenuScreen(ViewState viewState) {
	this.viewState = viewState;
	play = new JButton("Play");
	highscore = new JButton("Highscore");
	credits = new JButton("Credits");
	play.addActionListener(new MenuButtonListener(viewState));
	credits.addActionListener(new MenuButtonListener(viewState));
	highscore.addActionListener(new MenuButtonListener(viewState));
	this.add(play);
	this.add(credits);
	this.add(highscore);
    }

}
