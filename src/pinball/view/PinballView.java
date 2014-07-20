package pinball.view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableModel;

import pinball.constants.ImageConstants;
import pinball.constants.LayoutConstants;
import pinball.constants.TextConstants;
import pinball.controller.PinballControllerInterface;
import pinball.helper.ViewHelper;
import pinball.model.PinballModelInterface;

/**
 * Responsible for the appearance of the application.
 */
public class PinballView extends JFrame implements LayoutConstants,
ImageConstants, TextConstants {

    // Menu panel items
    private JLabel logo;
    private JButton play;
    private JButton highscore;
    private JButton credits;
    private JLabel menu_big_ball;

    // Credits screen items
    private JLabel credits_text;
    private JButton credits_to_menu;

    // Highscore screen items
    private JTable highscore_scores;
    private JScrollPane scrollPane;
    private JButton highscore_to_menu;
    private JLabel highscore_logo;

    // Game panel items
    private Canvas canvas;
    private JButton game_to_menu;

    // Panels
    private JPanel menuPanel;
    private JPanel creditsPanel;
    private JPanel highscorePanel;
    private JPanel gamePanel;

    // Main panel
    private JPanel cards;

    // References
    PinballControllerInterface controller;
    PinballModelInterface model;

    public PinballView(PinballControllerInterface controller,
	    PinballModelInterface model) {
	this.controller = controller;
	this.model = model;
    }

    /**
     * Sets up the view including the menu screen, highscore screen and credits
     * screen
     */
    public void createView() {
	// Set native look and feel
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException | InstantiationException
		| IllegalAccessException | UnsupportedLookAndFeelException e) {
	    e.printStackTrace();
	}
	// Set window size
	setSize(new Dimension((int) (APP_WIDTH * SCALING_FACTOR),
		(int) (APP_HEIGHT * SCALING_FACTOR)));
	// Set name
	setTitle("Pinball");
	// Exit window on close
	setDefaultCloseOperation(EXIT_ON_CLOSE);

	// Instantiate menu panel items
	logo = new JLabel(ViewHelper.createImageIcon(LOGO));
	logo.setPreferredSize(ViewHelper.imageSize(logo.getIcon()));
	play = new JButton(ViewHelper.createImageIcon(PLAY_BUTTON));
	play.setPreferredSize(ViewHelper.imageSize(play.getIcon()));
	play.addActionListener(e -> {
	    CardLayout cl = (CardLayout) (cards.getLayout());
	    cl.show(cards, GAME);
	    controller.startGame((String) (JOptionPane.showInputDialog(this,
		    "Please enter your player name!", "Player Name",
		    DO_NOTHING_ON_CLOSE,
		    ViewHelper.createImageIcon(DIALOG_BALL), null, null)));
	});
	credits = new JButton(ViewHelper.createImageIcon(CREDITS_BUTTON));
	credits.setPreferredSize(ViewHelper.imageSize(credits.getIcon()));
	credits.addActionListener(e -> {
	    CardLayout cl = (CardLayout) (cards.getLayout());
	    cl.show(cards, CREDITS);
	});
	menu_big_ball = new JLabel(ViewHelper.createImageIcon(MENU_BIG_BALL));
	menu_big_ball.setPreferredSize(ViewHelper.imageSize(menu_big_ball
		.getIcon()));
	highscore = new JButton(ViewHelper.createImageIcon(HIGHSCORE_BUTTON));
	highscore.setPreferredSize(ViewHelper.imageSize(highscore.getIcon()));
	highscore.addActionListener(e -> {
	    CardLayout cl = (CardLayout) (cards.getLayout());
	    cl.show(cards, HIGHSCORE);
	});

	// Populate menu panel
	menuPanel = new JPanel(new FlowLayout());
	menuPanel.add(ViewHelper.verticalSpace(LOGO_TO_TOP_DISPLACEMENT));
	menuPanel.add(logo);
	menuPanel.add(ViewHelper.verticalSpace(BUTTON_TO_LOGO_DISPLACEMENT));
	menuPanel.add(play);
	menuPanel.add(ViewHelper.verticalSpace(BUTTON_DISPLACEMENT));
	menuPanel.add(highscore);
	menuPanel.add(ViewHelper.verticalSpace(BUTTON_DISPLACEMENT));
	menuPanel.add(credits);
	menuPanel.add(ViewHelper.verticalSpace(BUTTON_TO_BALL_DISPLACEMENT));
	menuPanel.add(ViewHelper.horizontalSpace(RIGHT_TO_BALL_DISPLACEMENT,
		ViewHelper.imageSize(MENU_BIG_BALL).getHeight()));
	menuPanel.add(menu_big_ball);

	// Instantiate credits panel items
	credits_to_menu = createBackButton(credits_to_menu);
	credits_text = new JLabel(CREDITS_TEXT);
	ViewHelper.setBounds(credits_text, RETURN_BUTTON_TO_TEXT_DISPLACEMENT
		+ RETURN_BUTTON_TO_LEFT_DISPLACEMENT
		+ (int) ViewHelper.imageSize(credits_to_menu.getIcon())
		.getWidth(), RETURN_BUTTON_TO_TOP_DISPLACEMENT
		+ credits_to_menu.getHeight() / 2);

	// Populate credits panel;
	creditsPanel = new JPanel(null);
	creditsPanel.add(credits_to_menu);
	creditsPanel.add(credits_text);

	// Instantiate highscore panel items
	highscore_to_menu = createBackButton(highscore_to_menu);
	highscore_scores = new JTable((TableModel) model);
	scrollPane = new JScrollPane(highscore_scores);
	ViewHelper
	.setBounds(
		scrollPane,
		(APP_WIDTH / 2) - (TABLE_WIDTH / 2),
		(RETURN_BUTTON_TO_TOP_DISPLACEMENT
			+ highscore_to_menu.getHeight() + RETURN_BUTTON_TO_TABLE_DISPLACEMENT),
			TABLE_WIDTH, TABLE_HEIGHT);
	highscore_logo = new JLabel(ViewHelper.createImageIcon(HIGHSCORE_LOGO));
	ViewHelper.setBounds(highscore_logo,
		(int) ((APP_WIDTH / 2) - (ViewHelper.imageSize(HIGHSCORE_LOGO)
			.getWidth() / 2)), RETURN_BUTTON_TO_TOP_DISPLACEMENT
			+ RETURN_BUTTON_TO_HIGHSCORE_LOGO_DISPLACEMENT);

	// Populate highscore panel
	highscorePanel = new JPanel(null);
	highscorePanel.add(highscore_to_menu);
	highscorePanel.add(scrollPane);
	highscorePanel.add(highscore_logo);

	// Instantiate game panel items
	game_to_menu = createBackButton(game_to_menu);
	game_to_menu.addActionListener(e -> {
	    controller.stopGame();
	});

	// Populate game panel
	gamePanel = new JPanel(null);
	gamePanel.add(game_to_menu);

	// Set up card layout
	cards = new JPanel(new CardLayout());
	cards.add(menuPanel, MENU);
	cards.add(creditsPanel, CREDITS);
	cards.add(highscorePanel, HIGHSCORE);
	cards.add(gamePanel, GAME);
	add(cards, BorderLayout.CENTER);

	// Set visible
	setVisible(true);
	// Don't allow resize
	setResizable(false);
    }

    /**
     * Creates a back button in the upper left corner and adds a listener to it.
     *
     * @param button
     * @return
     */
    private JButton createBackButton(JButton button) {
	button = new JButton(ViewHelper.createImageIcon(BACK_BUTTON));
	button.setBorder(BorderFactory.createEmptyBorder());
	ViewHelper.setBounds(button, RETURN_BUTTON_TO_LEFT_DISPLACEMENT,
		RETURN_BUTTON_TO_TOP_DISPLACEMENT);
	button.addActionListener(new BackButtonListener());
	return button;
    }

    /**
     * When button is pressed, the view is changed to MENU
     */
    public class BackButtonListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    CardLayout cl = (CardLayout) (cards.getLayout());
	    cl.show(cards, MENU);
	}

    }
}
