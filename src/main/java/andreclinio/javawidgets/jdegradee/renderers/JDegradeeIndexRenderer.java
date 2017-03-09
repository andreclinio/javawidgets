package andreclinio.javawidgets.jdegradee.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import andreclinio.javawidgets.jdegradee.JDegradee;

/**
 * @author André Clinio
 */
public class JDegradeeIndexRenderer implements JDegradeeRenderer {

  /**
   * Fonte.
   */
  final private Font font;

  /**
   * Cor selecionada
   */
  final private Color fontColor;

  /**
   * Desenho de uma string centrada dentro (não clipado) de um retângulo
   *
   * @param g2d o contexto gráfico Java
   * @param text o texto
   * @param box o retângulo
   */
  final private void drawStringInBox(final Graphics2D g2d, final String text, final Rectangle2D box) {
    final double x = box.getCenterX();
    final double y = box.getCenterY();
    final FontMetrics fm = g2d.getFontMetrics();
    final Rectangle2D r = fm.getStringBounds(text, g2d);
    final double w = r.getWidth();
    final double h = r.getHeight();
    final float xc = (float) (x - (w / 2.0));
    final float yc = (float) (y + (h / 2.0));

    g2d.drawString(text, xc, yc);
  }

  /**
   * Consulta a string formatada com o índice
   * 
   * @param index o índice
   * @return a string
   */
  final private String getText(final int index) {
    return "(" + (index + 1) + ")";
  }

  /**
   * {@inheritDoc}
   */
  final public void render(final JDegradee jDegradee, final Graphics2D g2d, final Rectangle2D rect, final int index) {
    final String text = getText(index);
    if (text == null) {
      return;
    }
    g2d.setFont(font);
    g2d.setColor(fontColor == null ? Color.black : fontColor);
    final Rectangle2D txtRect = new Rectangle2D.Double();
    switch (jDegradee.getOrientation()) {
      case HORIZONTAL: {
        final double h = rect.getHeight() / 2.0;
        final double y = rect.getCenterY();
        txtRect.setFrame(rect.getMinX(), y, rect.getWidth(), h);
        break;
      }
      case VERTICAL: {
        final double w = rect.getWidth() / 2.0;
        final double x = rect.getMinX();
        txtRect.setFrame(x, rect.getMinY(), w, rect.getHeight());
        break;
      }
    }
    drawStringInBox(g2d, text, txtRect);
  }

  /**
   * Construtor padrão.
   * 
   * @param font fonte
   * @param fontColor cor da fonte
   */
  public JDegradeeIndexRenderer(final Font font, final Color fontColor) {
    this.fontColor = fontColor;
    this.font = font;
  }
}
