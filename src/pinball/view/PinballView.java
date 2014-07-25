package pinball.view;

import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

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

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import pinball.constants.ConversionConstants;
import pinball.constants.ImageConstants;
import pinball.constants.LayoutConstants;
import pinball.constants.TextConstants;
import pinball.controller.PinballControllerInterface;
import pinball.helper.ViewHelper;
import pinball.model.PinballModelInterface;

/**
 * Responsible for the appearance of the application.
 */
/*
 * XXX Implement pause menu
 * XXX Implement game scoring
 * XXX Implement game messages
 * XXX Notify user that high score was saved
 * XXX Create game bodies
 */
// XXX Implement game
public class PinballView extends JFrame implements LayoutConstants,
ImageConstants, TextConstants, ConversionConstants {

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
  private JLabel placeholder;
  private World world;
  JLabel button_transparent;

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

  // Game loop
  private long last;
  private Thread thread;

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
	  // controller.startGame((String) (JOptionPane.showInputDialog(this,
	  // "Please enter your player name!", "Player Name", DO_NOTHING_ON_CLOSE,
	  // ViewHelper.createImageIcon(DIALOG_BALL), null, null)));
	  controller.startGame("Test"); // DEBUG
	  last = System.nanoTime();
	  canvas.setIgnoreRepaint(true);
	  canvas.createBufferStrategy(2);
	  CardLayout cl = (CardLayout) (cards.getLayout());
	  cl.show(cards, GAME);
	  initializeWorld();
	  Thread thread = new Thread() {
		@Override
		public void run() {
		  while (controller.isRunning()) {
			gameLoop();
		  }
		}
	  };
	  thread.setDaemon(true);
	  thread.start();
	});
	credits = new JButton(ViewHelper.createImageIcon(CREDITS_BUTTON));
	credits.setPreferredSize(ViewHelper.imageSize(credits.getIcon()));
	credits.addActionListener(e -> {
	  CardLayout cl = (CardLayout) (cards.getLayout());
	  cl.show(cards, CREDITS);
	});
	menu_big_ball = new JLabel(ViewHelper.createImageIcon(MENU_BIG_BALL));
	menu_big_ball
	.setPreferredSize(ViewHelper.imageSize(menu_big_ball.getIcon()));
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
	ViewHelper.setBounds(credits_text,
		RETURN_BUTTON_TO_TEXT_DISPLACEMENT + RETURN_BUTTON_TO_LEFT_DISPLACEMENT
		+ (int) ViewHelper.imageSize(credits_to_menu.getIcon()).getWidth(),
		RETURN_BUTTON_TO_TOP_DISPLACEMENT + credits_to_menu.getHeight() / 2);

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
		(RETURN_BUTTON_TO_TOP_DISPLACEMENT + highscore_to_menu.getHeight() + RETURN_BUTTON_TO_TABLE_DISPLACEMENT),
		TABLE_WIDTH, TABLE_HEIGHT);
	highscore_logo = new JLabel(ViewHelper.createImageIcon(HIGHSCORE_LOGO));
	ViewHelper.setBounds(highscore_logo, (int) ((APP_WIDTH / 2) - (ViewHelper
		.imageSize(HIGHSCORE_LOGO).getWidth() / 2)),
		RETURN_BUTTON_TO_TOP_DISPLACEMENT
		+ RETURN_BUTTON_TO_HIGHSCORE_LOGO_DISPLACEMENT);

	// Populate highscore panel
	highscorePanel = new JPanel(null);
	highscorePanel.add(highscore_to_menu);
	highscorePanel.add(scrollPane);
	highscorePanel.add(highscore_logo);

	// Instantiate game panel items
	game_to_menu = createBackButton(game_to_menu);
	ViewHelper.setBounds(game_to_menu, RETURN_BUTTON_TO_LEFT_DISPLACEMENT,
		RETURN_BUTTON_TO_TOP_GAME_SCREEN);
	game_to_menu.addActionListener(e -> {
	  controller.stopGame(JOptionPane.showConfirmDialog(this,
		  "Do you want to save your \nhighscore.", "Highscore",
		  DO_NOTHING_ON_CLOSE, JOptionPane.PLAIN_MESSAGE,
		  ViewHelper.createImageIcon(DIALOG_BALL)));
	});
	placeholder =
		new JLabel(ViewHelper.createImageIcon(GAME_SCREEN_BACKGROUND));
	ViewHelper.setBounds(placeholder, 0, 0, 800, 1280);
	canvas = new Canvas();
	ViewHelper.setBounds(canvas, 0, 0, 800, 1280);
	button_transparent = new JLabel();
	button_transparent.setBounds(
		(int) (RETURN_BUTTON_TO_LEFT_DISPLACEMENT * SCALING_FACTOR),
		(int) (RETURN_BUTTON_TO_TOP_GAME_SCREEN * SCALING_FACTOR), 30, 30);

	// Populate game panel
	gamePanel = new JPanel(null);
	gamePanel.add(game_to_menu);
	gamePanel.add(canvas);
	gamePanel.add(placeholder);
	gamePanel.add(button_transparent);

	// Set up card layout
	cards = new JPanel(new CardLayout());
	cards.add(menuPanel, MENU);
	cards.add(creditsPanel, CREDITS);
	cards.add(highscorePanel, HIGHSCORE);
	cards.add(gamePanel, GAME);
	add(cards);

	// Set visible
	setVisible(true);
	// Don't allow resize
	setResizable(false);

	// Set window size
	setSize(new Dimension((int) (APP_WIDTH * SCALING_FACTOR)
		+ getInsets().right + getInsets().left,
		(int) (APP_HEIGHT * SCALING_FACTOR) + getInsets().top
		+ getInsets().bottom));
  }

  /**
   * Contains the game loop which redraws the game screen continuously.
   */
  protected void gameLoop() {
	Graphics2D g = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
	// before we render everything im going to flip the y axis and move the
	// origin to the center (instead of it being in the top left corner)
	render(g);
	// dispose of the graphics object
	g.dispose();

	// blit/flip the buffer
	BufferStrategy strategy = canvas.getBufferStrategy();
	if (!strategy.contentsLost()) {
	  strategy.show();
	}

	// Sync the display on some systems.
	// (on Linux, this fixes event queue problems)
	Toolkit.getDefaultToolkit().sync();

	// update the World

	// get the current time
	long time = System.nanoTime();
	// get the elapsed time from the last iteration
	long diff = time - last;
	// set the last time
	last = time;
	// convert from nanoseconds to seconds
	double elapsedTime = diff / NANO_TO_BASE;
	// update the world with the elapsed time
	world.update(elapsedTime);
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
  private class BackButtonListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	  CardLayout cl = (CardLayout) (cards.getLayout());
	  cl.show(cards, MENU);
	}

  }

  public static class GameObject extends Body {
	/** The color of the object */
	protected Color color;

	/**
	 * Default constructor.
	 */
	public GameObject() {
	  // randomly generate the color
	  color =
		  new Color((float) Math.random() * 0.5f + 0.5f,
			  (float) Math.random() * 0.5f + 0.5f,
			  (float) Math.random() * 0.5f + 0.5f);
	}

	/**
	 * Draws the body.
	 * <p>
	 * Only coded for polygons and circles.
	 *
	 * @param g
	 *          the graphics object to render to
	 */
	public void render(Graphics2D g) {
	  // save the original transform
	  AffineTransform ot = g.getTransform();

	  // transform the coordinate system from world coordinates to local
	  // coordinate
	  AffineTransform lt = new AffineTransform();
	  lt.translate(transform.getTranslationX() * SCALE,
		  transform.getTranslationY() * SCALE);
	  lt.rotate(transform.getRotation());

	  // apply the transform
	  g.transform(lt);

	  // loop over all the body fixtures for this body
	  for (BodyFixture fixture : fixtures) {
		// get the shape on the fixture
		Convex convex = fixture.getShape();
		Graphics2DRenderer.render(g, convex, SCALE, color);
	  }

	  // set the original transform
	  g.setTransform(ot);
	}
  }

  /**
   * Creates game objects and adds them to the world.
   * <p>
   * Basically the same shapes from the Shapes test in the TestBed.
   */
  protected void initializeWorld() {
	// create the world
	world = new World();

	// create all your bodies/joints

	// create the floor
	Rectangle floorRect = new Rectangle(15.0, 1.0);
	GameObject floor = new GameObject();
	floor.addFixture(new BodyFixture(floorRect));
	floor.setMass(Mass.Type.INFINITE);
	// move the floor down a bit
	floor.translate(0.0, -8.0);
	world.addBody(floor);

	// create a circle
	Circle cirShape = new Circle(0.5);
	GameObject circle = new GameObject();
	circle.addFixture(cirShape);
	circle.setMass();
	circle.translate(2.0, 2.0);
	// test adding some force
	circle.applyForce(new Vector2(-100.0, 0.0));
	// set some linear damping to simulate rolling friction
	circle.setLinearDamping(0.05);
	world.addBody(circle);

	// try a rectangle
	Rectangle rectShape = new Rectangle(1.0, 1.0);
	GameObject rectangle = new GameObject();
	rectangle.addFixture(rectShape);
	rectangle.setMass();
	rectangle.translate(0.0, 2.0);
	rectangle.getLinearVelocity().set(-5.0, 0.0);
	world.addBody(rectangle);

	// try a polygon with lots of vertices
	Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
	GameObject polygon = new GameObject();
	polygon.addFixture(polyShape);
	polygon.setMass();
	polygon.translate(-2.5, 2.0);
	// set the angular velocity
	polygon.setAngularVelocity(Math.toRadians(-20.0));
	world.addBody(polygon);

	// try a compound object
	Circle c1 = new Circle(0.5);
	BodyFixture c1Fixture = new BodyFixture(c1);
	c1Fixture.setDensity(0.5);
	Circle c2 = new Circle(0.5);
	BodyFixture c2Fixture = new BodyFixture(c2);
	c2Fixture.setDensity(0.5);
	Rectangle rm = new Rectangle(2.0, 1.0);
	// translate the circles in local coordinates
	c1.translate(-1.0, 0.0);
	c2.translate(1.0, 0.0);
	GameObject capsule = new GameObject();
	capsule.addFixture(c1Fixture);
	capsule.addFixture(c2Fixture);
	capsule.addFixture(rm);
	capsule.setMass();
	capsule.translate(0.0, 4.0);
	world.addBody(capsule);

	GameObject issTri = new GameObject();
	issTri.addFixture(Geometry.createIsoscelesTriangle(1.0, 3.0));
	issTri.setMass();
	issTri.translate(2.0, 3.0);
	world.addBody(issTri);

	GameObject equTri = new GameObject();
	equTri.addFixture(Geometry.createEquilateralTriangle(2.0));
	equTri.setMass();
	equTri.translate(3.0, 3.0);
	world.addBody(equTri);

	GameObject rightTri = new GameObject();
	rightTri.addFixture(Geometry.createRightTriangle(2.0, 1.0));
	rightTri.setMass();
	rightTri.translate(4.0, 3.0);
	world.addBody(rightTri);

	GameObject cap1 = new GameObject();
	cap1.addFixture(new Capsule(1.0, 0.5));
	cap1.setMass();
	cap1.translate(-3.0, 3.0);
	world.addBody(cap1);
	GameObject leftWall = new GameObject();
	leftWall.addFixture(new Rectangle(0.1, 12.8));
	leftWall.translate(0.0, -6.4);
	world.addBody(leftWall);
  }

  /**
   * Renders the example.
   *
   * @param g
   *          the graphics object to render to
   */
  protected void render(Graphics2D g) {
	// lets draw over everything with a white background
	g.drawImage(ViewHelper.getImageSuppressExceptions(GAME_SCREEN_BACKGROUND),
		0, 0, (int) (APP_WIDTH * SCALING_FACTOR),
		(int) (APP_HEIGHT * SCALING_FACTOR), null);
	g.setColor(new Color(0f, 0f, 0f, 0f));
	g.fillRect(0, 0, (int) (APP_WIDTH * SCALING_FACTOR),
		(int) (APP_HEIGHT * SCALING_FACTOR));

	// lets move the view up some
	// g.translate(0.0, -1.0 * SCALE);

	AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
	g.transform(yFlip);
	// draw all the objects in the world
	for (int i = 0; i < world.getBodyCount(); i++) {
	  // get the object
	  GameObject go = (GameObject) world.getBody(i);
	  // draw the object
	  go.render(g);
	}
  }
}
