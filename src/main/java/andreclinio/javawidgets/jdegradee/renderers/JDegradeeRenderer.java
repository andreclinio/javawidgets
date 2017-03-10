package andreclinio.javawidgets.jdegradee.renderers;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import andreclinio.javawidgets.jdegradee.JDegradee;

/**
 * Extension drawing interface.
 *
 * @author André Clinio
 */
public interface JDegradeeRenderer {

  /**
   * Render anything else inside de element
   *
   * @param jDegradee widget
   * @param g2d graphics for drawing
   * @param rect drawing area
   * @param index element index
   *
   */
   void render(final JDegradee jDegradee, final Graphics2D g2d, final Rectangle2D rect, final int index);

}
