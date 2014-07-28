package pinball.view;

import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

import pinball.constants.ConversionConstants;
import pinball.constants.ImageConstants;
import pinball.constants.LayoutConstants;
import pinball.constants.PhysicsConstants;
import pinball.constants.TextConstants;
import pinball.controller.PinballControllerInterface;
import pinball.helper.ViewHelper;
import pinball.model.LiveObserver;
import pinball.model.PinballModelInterface;
import pinball.model.ScoreObserver;

/**
 * Responsible for the appearance of the application.
 */
/*
 * TODO Implement game messages
 * TODO keep track of balls and supply new one when ball out of bounds
 * TODO make sure that button are only pressed in game
 * TODO ball listener
 */
public class PinballView extends JFrame implements LayoutConstants,
ImageConstants, TextConstants, ConversionConstants, PhysicsConstants,
	ScoreObserver, LiveObserver {

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
  private JLabel game_messages;
  private JLabel game_score;

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

  // Game objects
  private GameObject right_pedal;
  public GameObject ball;
  private GameObject left_pedal;
  private RevoluteJoint j1;
  private RevoluteJoint j2;
  public GameObject circBumper1;
  public GameObject circBumper2;
  public GameObject circBumper3;
  public GameObject circBumper4;
  public GameObject circBumper5;
  public GameObject circBumper6;

  private MyCollisionAdapter collisionAdapter;
  private MyOutOfBoundsAdapter outOfBoundsAdapter;

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
	  controller.startGame((String) (JOptionPane.showInputDialog(this,
		  "Please enter your player name!", "Player Name", DO_NOTHING_ON_CLOSE,
		  ViewHelper.createImageIcon(DIALOG_BALL), null, null)));
	  last = System.nanoTime();
	  canvas.setIgnoreRepaint(true);
	  canvas.createBufferStrategy(2);
	  CardLayout cl = (CardLayout) (cards.getLayout());
	  cl.show(cards, GAME);
	  gamePanel.requestFocusInWindow();
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
	  controller.stopGame(getSaveHighscoreDialog());
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
	game_messages = new JLabel("");
	game_messages.setFont(new Font("Impact", Font.PLAIN, 30));
	ViewHelper.setBounds(game_messages, 40, 463, 150, 45);
	game_score = new JLabel(new Integer(0).toString());
	ViewHelper.setBounds(game_score, 627, 463, 150, 45);
	game_score.setFont(new Font("Impact", Font.PLAIN, 35));

	// Populate game panel
	gamePanel = new JPanel(null);
	gamePanel.add(game_to_menu);
	gamePanel.add(game_messages);
	gamePanel.add(game_score);
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
	gamePanel.addKeyListener(new KeyAdapter() {
	  @Override
	  public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
		  if (left_pedal.getAccumulatedForce().y == 0) {
			left_pedal.applyForce(new Force(0, PEDAL_FORCE));
		  } else {
			left_pedal.clearForce();
		  }
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
		  if (right_pedal.getAccumulatedForce().y < PEDAL_FORCE) {
			right_pedal.applyForce(new Force(0, PEDAL_FORCE));
		  } else {
			right_pedal.clearForce();
		  }

		}
	  }

	  @Override
	  public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
		  left_pedal.clearAccumulatedForce();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
		  right_pedal.clearAccumulatedForce();
		}
	  }
	});
	cards.setFocusable(true);

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
  public void initializeWorld() {
	// create the world
	world = new World();
	outOfBoundsAdapter = new MyOutOfBoundsAdapter(this, controller);
	collisionAdapter = new MyCollisionAdapter(this, controller);
	world.setBounds(new AxisAlignedBounds(16, 24.8));
	world.addListener(outOfBoundsAdapter);
	world.addListener(collisionAdapter);

	// Static bodies

	// Left wall

	Rectangle r1 =
		new Rectangle((2.03 * SCALING_FACTOR), (1.2 * SCALING_FACTOR));
	r1.translate((2.03 * SCALING_FACTOR) / 2,
		-((4.28 * SCALING_FACTOR) + ((1.2 * SCALING_FACTOR) / 2)));

	Vector2[] v1 =
		{ new Vector2(0.0, 5.47 * SCALING_FACTOR),
			new Vector2(2.03 * SCALING_FACTOR, 5.47 * SCALING_FACTOR),
			new Vector2(1.31 * SCALING_FACTOR, 6.28 * SCALING_FACTOR),
			new Vector2(0.0, 6.28 * SCALING_FACTOR) };
	Polygon p1 = new Polygon(v1);
	p1 = Geometry.flipAlongTheXAxis(p1);
	p1.translate(0, -(11.7 * SCALING_FACTOR));

	Rectangle r2 = new Rectangle(1.31 * SCALING_FACTOR, 0.98 * SCALING_FACTOR);
	GameObject w4 = new GameObject();
	w4.addFixture(new BodyFixture(r2));
	w4.setMass(Mass.Type.INFINITE);
	r2.translate((1.31 * SCALING_FACTOR) / 2,
		-((6.28 * SCALING_FACTOR) + ((0.98 * SCALING_FACTOR) / 2)));
	// world.addBody(w4);

	Rectangle r3 = new Rectangle(0.59 * SCALING_FACTOR, 2.81 * SCALING_FACTOR);
	r3.translate((0.59 * SCALING_FACTOR) / 2,
		-((8.05 * SCALING_FACTOR) + ((2.81 * SCALING_FACTOR) / 2)));

	Vector2[] v3 =
		{ new Vector2(0.0, 10.87 * SCALING_FACTOR),
			new Vector2(0.59 * SCALING_FACTOR, 10.87 * SCALING_FACTOR),
			new Vector2(3.0 * SCALING_FACTOR, 12.8 * SCALING_FACTOR),
			new Vector2(0.0, 12.8 * SCALING_FACTOR) };
	Polygon p3 = new Polygon(v3);
	p3 = Geometry.flipAlongTheXAxis(p3);
	p3.translate(0, -24.1 * SCALING_FACTOR);

	GameObject leftWall = new GameObject();
	leftWall.addFixture(r2);
	leftWall.addFixture(r3);
	leftWall.addFixture(p1);
	leftWall.addFixture(p3);
	leftWall.addFixture(r1);
	leftWall.setMass(Mass.Type.INFINITE);
	world.addBody(leftWall);

	// Right wall

	Rectangle r4 =
		new Rectangle((2.03 * SCALING_FACTOR), (1.2 * SCALING_FACTOR));
	r4.translate((8.0 * SCALING_FACTOR) - (2.03 * SCALING_FACTOR) / 2,
		-((4.28 * SCALING_FACTOR) + ((1.2 * SCALING_FACTOR) / 2)));

	Vector2[] v4 =
		{ new Vector2(0.0, 5.47 * SCALING_FACTOR),
			new Vector2(2.03 * SCALING_FACTOR, 5.47 * SCALING_FACTOR),
			new Vector2(1.31 * SCALING_FACTOR, 6.28 * SCALING_FACTOR),
			new Vector2(0.0, 6.28 * SCALING_FACTOR) };
	Polygon p4 = new Polygon(v4);
	p4 = Geometry.flipAlongTheXAxis(p4);
	p4 = Geometry.flipAlongTheYAxis(p4);
	p4.translate((8.0 * SCALING_FACTOR) - 1.7 * SCALING_FACTOR,
		-(11.7 * SCALING_FACTOR));

	Rectangle r5 = new Rectangle(1.31 * SCALING_FACTOR, 0.98 * SCALING_FACTOR);
	GameObject w11 = new GameObject();
	w11.addFixture(new BodyFixture(r5));
	w11.setMass(Mass.Type.INFINITE);
	r5.translate((8.0 * SCALING_FACTOR) - (1.31 * SCALING_FACTOR) / 2,
		-((6.28 * SCALING_FACTOR) + ((0.98 * SCALING_FACTOR) / 2)));

	Rectangle r6 = new Rectangle(0.59 * SCALING_FACTOR, 2.81 * SCALING_FACTOR);
	GameObject w13 = new GameObject();
	w13.addFixture(new BodyFixture(r6));
	w13.setMass(Mass.Type.INFINITE);
	r6.translate((8.0 * SCALING_FACTOR) - (0.59 * SCALING_FACTOR) / 2,
		-((8.05 * SCALING_FACTOR) + ((2.81 * SCALING_FACTOR) / 2)));

	Vector2[] v6 =
		{ new Vector2(0.0, 10.87 * SCALING_FACTOR),
			new Vector2(0.59 * SCALING_FACTOR, 10.87 * SCALING_FACTOR),
			new Vector2(3.0 * SCALING_FACTOR, 12.8 * SCALING_FACTOR),
			new Vector2(0.0, 12.8 * SCALING_FACTOR) };
	Polygon p6 = new Polygon(v6);
	p6 = Geometry.flipAlongTheXAxis(p6);
	p6 = Geometry.flipAlongTheYAxis(p6);
	p6.translate((8.0 * SCALING_FACTOR) - 2.07 * SCALING_FACTOR, -24.1
		* SCALING_FACTOR);

	GameObject rightWall = new GameObject();
	rightWall.setMass(Mass.Type.INFINITE);
	rightWall.addFixture(r4);
	rightWall.addFixture(r5);
	rightWall.addFixture(r6);
	rightWall.addFixture(p4);
	rightWall.addFixture(p6);
	world.addBody(rightWall);

	// Entry point ball

	Capsule cp1 = new Capsule(0.24 * SCALING_FACTOR, 1.66 * SCALING_FACTOR);
	GameObject e1 = new GameObject();
	e1.addFixture(new BodyFixture(cp1));
	e1.setMass(Mass.Type.INFINITE);
	e1.translate(1.5 * SCALING_FACTOR, -2.57 * SCALING_FACTOR);
	e1.rotate(Math.toRadians(25));
	world.addBody(e1);

	GameObject e2 = new GameObject();
	e2.addFixture(new BodyFixture(cp1));
	e2.setMass(Mass.Type.INFINITE);
	e2.translate(0.82 * SCALING_FACTOR, -2.30 * SCALING_FACTOR);
	e2.rotate(Math.toRadians(25));
	world.addBody(e2);

	Rectangle top_wall_rect =
		new Rectangle(8.0 * SCALING_FACTOR, 0.001 * SCALING_FACTOR);
	top_wall_rect.translate(4.0 * SCALING_FACTOR, 0.0);
	GameObject top_wall = new GameObject();
	top_wall.setMass(Mass.Type.INFINITE);
	top_wall.addFixture(top_wall_rect);
	world.addBody(top_wall);

	// Dynamic bodies

	Vector2[] v2 =
		{ new Vector2(0.0, 7.26 * SCALING_FACTOR),
			new Vector2(1.31 * SCALING_FACTOR, 7.26 * SCALING_FACTOR),
			new Vector2(0.59 * SCALING_FACTOR, 8.05 * SCALING_FACTOR),
			new Vector2(0.0, 8.05 * SCALING_FACTOR) };
	Polygon p2 = new Polygon(v2);
	p2 = Geometry.flipAlongTheXAxis(p2);
	GameObject leftWallBumper = new GameObject();
	leftWallBumper.addFixture(new BodyFixture(p2));
	leftWallBumper.setMass(Mass.Type.INFINITE);
	p2.translate(0, -15.2 * SCALING_FACTOR);
	world.addBody(leftWallBumper);

	Vector2[] v5 =
		{ new Vector2(0.0, 7.26 * SCALING_FACTOR),
			new Vector2(1.31 * SCALING_FACTOR, 7.26 * SCALING_FACTOR),
			new Vector2(0.59 * SCALING_FACTOR, 8.05 * SCALING_FACTOR),
			new Vector2(0.0, 8.05 * SCALING_FACTOR) };
	Polygon p5 = new Polygon(v5);
	p5 = Geometry.flipAlongTheXAxis(p5);
	p5 = Geometry.flipAlongTheYAxis(p5);
	GameObject rightWallBumper = new GameObject();
	rightWallBumper.addFixture(new BodyFixture(p5));
	rightWallBumper.setMass(Mass.Type.INFINITE);
	p5.translate((8.0 * SCALING_FACTOR) - 1.0 * SCALING_FACTOR, -15.2
		* SCALING_FACTOR);
	world.addBody(rightWallBumper);

	Circle c1 = new Circle(0.23 * SCALING_FACTOR);

	circBumper1 = new GameObject();
	circBumper1.addFixture(new BodyFixture(c1));
	circBumper1.translate(4.0 * SCALING_FACTOR, -2.19 * SCALING_FACTOR);
	circBumper1.setMass(Mass.Type.INFINITE);
	world.addBody(circBumper1);
	circBumper2 = new GameObject();
	circBumper2.addFixture(c1);
	circBumper2.translate(4.67 * SCALING_FACTOR, -1.16 * SCALING_FACTOR);
	circBumper1.setMass(Mass.Type.INFINITE);
	world.addBody(circBumper2);
	circBumper3 = new GameObject();
	circBumper3.addFixture(c1);
	circBumper3.translate(5.37 * SCALING_FACTOR, -2.19 * SCALING_FACTOR);
	circBumper3.setMass(Mass.Type.INFINITE);
	world.addBody(circBumper3);
	circBumper4 = new GameObject();
	circBumper4.addFixture(c1);
	circBumper4.setMass(Mass.Type.INFINITE);
	circBumper4.translate(2.64 * SCALING_FACTOR, -7.76 * SCALING_FACTOR);
	world.addBody(circBumper4);
	circBumper5 = new GameObject();
	circBumper5.addFixture(c1);
	circBumper5.setMass(Mass.Type.INFINITE);
	circBumper5.translate(1.97 * SCALING_FACTOR, -8.79 * SCALING_FACTOR);
	world.addBody(circBumper5);
	circBumper6 = new GameObject();
	circBumper6.setMass(Mass.Type.INFINITE);
	circBumper6.addFixture(c1);
	circBumper6.translate(3.35 * SCALING_FACTOR, -8.79 * SCALING_FACTOR);
	world.addBody(circBumper6);

	Triangle t1 =
		Geometry.createRightTriangle(2.03 * SCALING_FACTOR,
			4.28 * SCALING_FACTOR);
	GameObject w1 = new GameObject();
	t1.translate((2.03 * SCALING_FACTOR) / 3,
		-(((2 * (4.28 * SCALING_FACTOR)) / 3)));
	w1.addFixture(t1);
	world.addBody(w1);

	Triangle t2 =
		Geometry.createRightTriangle(2.03 * SCALING_FACTOR,
			4.28 * SCALING_FACTOR, true);
	t2.translate((8.0 * SCALING_FACTOR) - (2.03 * SCALING_FACTOR) / 3,
		-(((2 * (4.28 * SCALING_FACTOR)) / 3)));
	GameObject w2 = new GameObject();
	w2.addFixture(t2);
	world.addBody(w2);

	createNewBall();

	Triangle t3 =
		Geometry.createIsoscelesTriangle(0.31 * SCALING_FACTOR,
			1.21 * SCALING_FACTOR);
	t3.translate(-0.01, 0.03);
	HalfEllipse el1 =
		Geometry
			.createHalfEllipse(0.31 * SCALING_FACTOR, 0.31 * SCALING_FACTOR);
	el1.translate(0.01, 0.205);
	el1.rotate(Math.toRadians(90));
	t3.rotate(Math.toRadians(-90));

	left_pedal = new GameObject();
	left_pedal.addFixture(new BodyFixture(t3));
	left_pedal.addFixture(new BodyFixture(el1));
	left_pedal.setMass();
	left_pedal.translate((3) * SCALING_FACTOR, -12.52 * SCALING_FACTOR);
	world.addBody(left_pedal);

	Triangle t4 =
		Geometry.createIsoscelesTriangle(0.31 * SCALING_FACTOR,
			1.21 * SCALING_FACTOR);
	t4.translate(0.01, 0.0);
	HalfEllipse el2 =
		Geometry
			.createHalfEllipse(0.31 * SCALING_FACTOR, 0.31 * SCALING_FACTOR);
	el2.translate(-0.01, 0.2);
	el2.rotate(Math.toRadians(90));
	t4.rotate(Math.toRadians(-90));
	t4.rotate(Math.toRadians(180));
	t4.translate(1.2, 0);
	el2.rotate(Math.toRadians(180));
	el2.translate(1.25, 0.003);

	right_pedal = new GameObject();
	right_pedal.addFixture(t4);
	right_pedal.addFixture(el2);
	right_pedal.setMass();
	right_pedal.translate(3 * SCALING_FACTOR, -12.52 * SCALING_FACTOR);
	world.addBody(right_pedal);

	j1 =
		new RevoluteJoint(left_pedal, leftWall, new Vector2((8.0 - 5.47)
			* SCALING_FACTOR, -12.52 * SCALING_FACTOR));
	j1.setLimitEnabled(true);
	j1.setReferenceAngle(Math.toRadians(0));
	j1.setLimits(Math.toRadians(-7.2), Math.toRadians(50));
	world.addJoint(j1);
	j2 =
		new RevoluteJoint(rightWall, right_pedal, new Vector2(
			(5.47) * SCALING_FACTOR, -12.52 * SCALING_FACTOR));
	j2.setLimitEnabled(true);
	j2.setReferenceAngle(Math.toRadians(0));
	j2.setLimits(Math.toRadians(-7.2), Math.toRadians(50));
	world.addJoint(j2);

  }

  /**
   * Creates a new game ball.
   */
  public void createNewBall() {
	Circle c2 = new Circle(0.12 * SCALING_FACTOR);
	ball = new GameObject();
	ball.addFixture(c2);
	ball.setMass();
	ball.setGravityScale(1.0);
	ball.getLinearVelocity().set(0.0, -6.0);
	ball.setAngularVelocity(Math.toRadians(-20.0));
	ball.translate(1.84 * SCALING_FACTOR, -1.4 * SCALING_FACTOR);
	world.addBody(ball);
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

  @Override
  public void updateScore(int score) {
	game_score.setText(new Integer(score).toString());
  }

  @Override
  public void updateLives(String message) {
	game_messages.setText(message);
  }

  /**
   *
   */
  public int getSaveHighscoreDialog() {
	return JOptionPane.showConfirmDialog(this,
		"Do you want to save your \nhighscore.", "Highscore",
		DO_NOTHING_ON_CLOSE, JOptionPane.PLAIN_MESSAGE,
		ViewHelper.createImageIcon(DIALOG_BALL));
  }

  public void setMessage(String message) {
	if (message.length() > 8) {
	  game_messages.setFont(new Font("Impact", Font.PLAIN, 20));
	  game_messages.repaint();
	}
	game_messages.setText(message);
  }

  public void showMenu() {
	CardLayout cl = (CardLayout) (cards.getLayout());
	cl.show(cards, MENU);
  }
}
