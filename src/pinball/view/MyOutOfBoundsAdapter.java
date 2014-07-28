package pinball.view;

import org.dyn4j.collision.BoundsAdapter;
import org.dyn4j.collision.Collidable;

import pinball.controller.PinballControllerInterface;

public class MyOutOfBoundsAdapter extends BoundsAdapter {

  private PinballView view;
  private PinballControllerInterface controller;

  /**
   * @param pinballView
   * @param controller
   */
  public MyOutOfBoundsAdapter(PinballView view,
	  PinballControllerInterface controller) {
	this.view = view;
	this.controller = controller;
  }

  @Override
  public <E extends Collidable> void outside(E collidable) {
	if (collidable.getId().equals(view.ball.getId())) {
	  controller.decreaseLives();
	}
  }
}
