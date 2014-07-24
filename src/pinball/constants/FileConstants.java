package pinball.constants;

import java.io.File;

/**
 * Defines constants like file names
 */
public interface FileConstants {
  public static final String DB_FILE = System.getProperty("user.dir")
	  + File.separator + "highscores.txt";
}
