package pinball.view;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.CollisionAdapter;

/**
 *
 */
public class MyCollisionAdapter extends CollisionAdapter {

  private PinballView view;

  /**
   *
   */
  public MyCollisionAdapter(PinballView view) {
	this.view = view;
  }

  @Override
  public boolean collision(Body body1, Body body2) {
	return true;
  }

}
