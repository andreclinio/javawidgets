package andreclinio.javawidgets.jdegradee.renderers;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import andreclinio.javawidgets.jdegradee.JDegradee;

/**
 * Interface de definifição do objeto que define o objeto a ser desenhado dento
 * de um elmento de degradeé.
 *
 * @author André Clinio
 */
public interface JDegradeeRenderer {

  /**
   * Consulta o texto a ser desenhado dentro de um elmento.
   *
   * @param jDegradee o widget
   * @param g2d o graphics a ser utilizado.
   * @param rect a área relativa ao item
   * @param index o índice do elemento
   *
   */
  public void render(final JDegradee jDegradee, final Graphics2D g2d, final Rectangle2D rect, final int index);

}
