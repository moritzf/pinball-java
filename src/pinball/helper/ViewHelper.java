package pinball.helper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import pinball.constants.LayoutConstants;
import pinball.view.PinballView;

/**
 * Helper methods for the View.
 */
public class ViewHelper implements LayoutConstants {

    /**
     * Calculates the size of the given image and returns it as a dimension
     * object
     *
     * @param path
     *            The path of the image
     * @return Dimension
     */
    public static Dimension imageSize(String path) {
	BufferedImage img = null;
	try {
	    img = ImageIO.read(PinballView.class.getResource(path));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return new Dimension(img.getWidth(), img.getHeight());
    }

    /**
     * Calculates the size of the given image and returns it as a dimension
     * object
     *
     * @param icon
     *            Icon containing the image
     * @return Dimension
     */
    public static Dimension imageSize(Icon icon) {
	return new Dimension(icon.getIconWidth(), icon.getIconHeight());
    }

    /**
     * Adds vertical space
     *
     * @param vertical_space
     *            Vertical space to be added to the component
     */
    public static Component verticalSpace(int vertical_space) {
	return Box.createRigidArea(new Dimension(
		(int) (APP_WIDTH * SCALING_FACTOR),
		(int) (vertical_space * SCALING_FACTOR)));
    }

    /**
     * Adds horizontal space
     *
     * @param vertical_space
     *            Vertical space to be added to the component
     * @param height
     *            Height of the vertical space
     */
    public static Component horizontalSpace(int horizontal_space, double height) {
	return Box.createRigidArea(new Dimension(
		(int) (horizontal_space * SCALING_FACTOR),
		(int) (height * SCALING_FACTOR)));
    }

    /**
     * Create image icon
     *
     * @param path_to_image
     *            The image path
     * @return ImageIcon
     */
    public static ImageIcon createImageIcon(String path) {
	BufferedImage img = null;
	try {
	    img = ImageIO.read(PinballView.class.getResource(path));
	    img = createResizedCopy(img,
		    (int) (img.getWidth() * SCALING_FACTOR),
		    (int) (img.getHeight() * SCALING_FACTOR), true);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return new ImageIcon(img);
    }

    /**
     * Scales image. Source:
     * http://stackoverflow.com/questions/4257497/how-to-cast
     * -convert-a-bufferedimage-into-an-image
     *
     * @param originalImage
     * @param scaledWidth
     * @param scaledHeight
     * @param preserveAlpha
     * @return
     */
    public static BufferedImage createResizedCopy(BufferedImage originalImage,
	    int scaledWidth, int scaledHeight, boolean preserveAlpha) {

	BufferedImage after = new BufferedImage(scaledWidth, scaledHeight,
		BufferedImage.TYPE_INT_ARGB);
	AffineTransform at = new AffineTransform();
	at.scale(SCALING_FACTOR, SCALING_FACTOR);
	AffineTransformOp scaleOp = new AffineTransformOp(at,
		AffineTransformOp.TYPE_BILINEAR);
	after = scaleOp.filter(originalImage, after);
	return after;
    }

    /**
     * Absolute positioning for component using component.getMaximumSize()
     *
     * @param component
     * @param x
     * @param y
     */
    public static void setBounds(Component component, int x, int y) {
	component.setBounds((int) (x * SCALING_FACTOR),
		(int) (y * SCALING_FACTOR), (int) component.getMaximumSize()
		.getWidth(), (int) component.getMaximumSize()
		.getHeight());
    }

    /**
     * Absolute positioning for component using user-defined width and height
     *
     * @param component
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void setBounds(Component component, int x, int y, int width,
	    int height) {
	component.setBounds((int) (x * SCALING_FACTOR),
		(int) (y * SCALING_FACTOR), (int) (width * SCALING_FACTOR),
		(int) (height * SCALING_FACTOR));
    }

}
