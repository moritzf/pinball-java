package pinball.view;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.CollisionAdapter;
import org.dyn4j.geometry.Vector2;

import pinball.controller.PinballControllerInterface;

/**
 *
 */
public class MyCollisionAdapter extends CollisionAdapter {

  private PinballView view;
  private PinballControllerInterface controller;

  /**
   *
   */
  public MyCollisionAdapter(PinballView view,
	  PinballControllerInterface controller) {
	this.view = view;
	this.controller = controller;
  }

  @Override
  public boolean collision(Body body1, Body body2) {
	if (body1.equals(view.circBumper1)) {
	  view.ball.setLinearVelocity(new Vector2(0, -3));
	  controller.addPoints(50);
	} else if (body2.equals(view.circBumper1)) {
	  view.ball.setLinearVelocity(new Vector2(0, -3));
	  controller.addPoints(50);
	} else if (body1.equals(view.circBumper2)) {
	  view.ball.setLinearVelocity(new Vector2(0, -10));
	  controller.addPoints(50);
	} else if (body2.equals(view.circBumper2)) {
	  view.ball.setLinearVelocity(new Vector2(0, -10));
	  controller.addPoints(50);
	} else if (body1.equals(view.circBumper3)) {
	  view.ball.setLinearVelocity(new Vector2(0.5, -3));
	  controller.addPoints(50);
	} else if (body2.equals(view.circBumper3)) {
	  view.ball.setLinearVelocity(new Vector2(0.5, -3));
	  controller.addPoints(50);
	} else if (body1.equals(view.circBumper4)) {
	  view.ball.setLinearVelocity(new Vector2(1, 10));
	  controller.addPoints(50);
	} else if (body2.equals(view.circBumper4)) {
	  view.ball.setLinearVelocity(new Vector2(1, -10));
	  controller.addPoints(50);
	} else if (body1.equals(view.circBumper5)) {
	  view.ball.setLinearVelocity(new Vector2(1, 10));
	  controller.addPoints(50);
	} else if (body2.equals(view.circBumper5)) {
	  view.ball.setLinearVelocity(new Vector2(1, 10));
	  controller.addPoints(50);
	} else if (body1.equals(view.circBumper6)) {
	  view.ball.setLinearVelocity(new Vector2(1, 10));
	  controller.addPoints(50);
	} else if (body2.equals(view.circBumper6)) {
	  view.ball.setLinearVelocity(new Vector2(1, 10));
	  controller.addPoints(50);
	}
	return true;
  }
}
