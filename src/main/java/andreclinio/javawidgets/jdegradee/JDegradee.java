package andreclinio.javawidgets.jdegradee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import andreclinio.javawidgets.jdegradee.adapters.JDegradeeAdapter;
import andreclinio.javawidgets.jdegradee.renderers.JDegradeeRenderer;

/**
 * Elemento de visualiza��o de degrade�
 *
 * @author Andr� Clinio
 */
public class JDegradee extends JPanel {

  /**
   * Layout de um objeto <code>JDegradee</code>
   *
   * @author Tecgraf/PUC-Rio
   */
  public enum Orientation {
    /**
     * Horizontal
     */
    HORIZONTAL,

    /**
     * Vertical
     */
    VERTICAL
  }

  /**
   * Layout corrente
   */
  private Orientation orientation = Orientation.VERTICAL;

  /**
   * Cor das linhas (grid).
   */
  private Color lineColor = Color.black;

  /**
   * Cor prim�ria
   */
  private Color priColor = Color.red;

  /**
   * Cor secund�ria
   */
  private Color secColor = Color.blue;

  /**
   * Array de marcas de cores
   */
  private Color[] markColor;

  /**
   * N�mero de itens (discretiza��o do painel)
   */
  private int numItems;

  /**
   * Indicativo de grid
   */
  private boolean hasGrid = true;

  /**
   * Tamanho da marca (exibi��o)
   */
  final static public int MARK_SIZE = 10;

  /**
   * N�mero m�nimo de itens poss�vel
   */
  final static public int MIN_ITENS = 2;

  /**
   * N�mero m�ximo de itens poss�vel
   */
  final static public int MAX_ITENS = 512;

  /**
   * Adaptadores de eventos de mouse.
   */
  final private ArrayList<JDegradeeAdapter> adapters = new ArrayList<>();

  /**
   * Texto a ser desenhado
   */
  final private ArrayList<JDegradeeRenderer> textBuilders = new ArrayList<>();

  /**
   * Consulta o n�mero de itens.
   *
   * @return o n�mero de itens
   */
  final public int getNumItems() {
    return numItems;
  }

  /**
   * Ajusta o n�mero de itens (discretiza��o do painel)
   *
   * @param numItens o n�mero de itens
   */
  final public void setNumItens(final int numItens) {
    if (numItens < MIN_ITENS || numItens > MAX_ITENS) {
      final String err = "Value out of range (" + MIN_ITENS + "," + MAX_ITENS + ")";
      throw new RuntimeException(err);
    }
    this.numItems = numItens;
    reallocMarks();
    repaint();
  }

  /**
   * Busca do �ndice (pr�ximo) que possui uma marca de cor ajustada
   *
   * @param index o �ndice de busca
   * @return o �ndice (ou -1, se n�o existir)
   */
  private int getNextMarkIndex(final int index) {
    final int last = markColor.length;
    for (int i = index; i < last; i++) {
      if (markColor[i] != null) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Busca do �ndice (anterior) que possui uma marca de cor ajustada
   *
   * @param index o �ndice de busca
   * @return o �ndice (ou -1, se n�o existir)
   */
  private int getPrevMarkIndex(final int index) {
    for (int i = index; i >= 0; i--) {
      if (markColor[i] != null) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Consulta uma cor ajustada com base em um percentual
   *
   * @param ratio o percentual
   * @return a cor
   * @see #calculateColorIndex(int)
   */
  final public Color calculateColorRatio(final double ratio) {
    final int index = (int) Math.round(numItems * ratio);
    return calculateColorIndex(index);
  }

  /**
   * Calcula a cor de um item com base nas marcas e cores (prim�ria e
   * secund�rias)
   *
   * @param index o �ndice
   * @return a cor
   */
  final public Color calculateColorIndex(final int index) {
    if (markColor[index] != null) {
      return markColor[index];
    }
    final int prevIdx = getPrevMarkIndex(index);
    final int nextIdx = getNextMarkIndex(index);
    final Color prevColor;
    final Color nextColor;
    final double factor;

    if (prevIdx < 0 && nextIdx < 0) {
      prevColor = priColor;
      nextColor = secColor;
      factor = ((double) index) / (numItems - 1);
    }
    else if (prevIdx >= 0 && nextIdx >= 0) {
      final double delta = nextIdx - prevIdx;
      final double diff = index - prevIdx;
      factor = diff / delta;
      prevColor = markColor[prevIdx];
      nextColor = markColor[nextIdx];
    }
    else if (prevIdx >= 0 && nextIdx < 0) {
      prevColor = markColor[prevIdx];
      nextColor = secColor;
      final double delta = (numItems - 1) - prevIdx;
      final double diff = index - prevIdx;
      factor = diff / delta;
    }
    else { // (prevIdx < 0 && nextIdx >= 0)
      prevColor = priColor;
      nextColor = markColor[nextIdx];
      final double delta = nextIdx;
      final double diff = index;
      factor = diff / delta;
    }
    return getGradientColor(prevColor, nextColor, factor);
  }

  /**
   * Calcula a �rea ret�ngular de um item
   *
   * @param index o �ndice do elemento
   * @return um ret�ngulo com a �rea �til do item.
   */
  private Rectangle2D getBounds(final int index) {
    final Rectangle2D rect = new Rectangle2D.Double();
    final double width = getWidth();
    final double height = getHeight();
    switch (orientation) {
      case HORIZONTAL:
        final double w = width / numItems;
        rect.setFrame(w * index, 0.0, w, height);

        break;
      case VERTICAL:
        final double h = height / numItems;
        rect.setFrame(0.0, h * index, width, h);

        break;
    }
    return rect;
  }

  /**
   * M�todo interno de <i>loop</i> e desenho dos dias.
   *
   * @param g o contexto gr�fico Java.
   */
  private void drawItems(final Graphics2D g) {
    for (int i = 0; i < numItems; i++) {
      drawItem(g, i);
    }
    for (int i = 0; i < numItems; i++) {
      drawMark(g, i);
    }
  }

  /**
   * Ajusta o text builder do elemento.
   *
   * @param builder o builder
   */
  final public void addItemRenderer(final JDegradeeRenderer builder) {
    textBuilders.add(builder);
  }

  /**
   * Ajusta o text builder do elemento.
   *
   * @param builder o builder
   */
  final public void delItemRenderer(final JDegradeeRenderer builder) {
    textBuilders.remove(builder);
  }

  /**
   * Limpa todos os text builders do elemento.
   */
  final public void clearRenderer() {
    textBuilders.clear();
  }

  /**
   * M�todo interno de <i>loop</i> e desenho dos dias.
   *
   * @param g2d o contexto gr�fico Java.
   * @param index o �ndice do elemento
   */
  private void drawItem(final Graphics2D g2d, final int index) {
    final Rectangle2D rect = getBounds(index);
    final Color color = calculateColorIndex(index);

    g2d.setColor(color);
    g2d.fill(rect);
    if (hasGrid) {
      g2d.setColor(lineColor);
      g2d.draw(rect);
    }

    for (JDegradeeRenderer textBuilder : textBuilders) {
      textBuilder.render(this, g2d, rect, index);
    }
  }

  /**
   * Consulta da regi�o que define uma marca (se houver)
   *
   * @param index o �ndice
   * @return o ret�ngulo (ou <code>null</code>, se n�o houver marca na posi��o)
   */
  final public Rectangle2D getMarkBounds(final int index) {
    final Color color = getMarkColor(index);
    if (color == null) {
      return null;
    }
    final Rectangle2D bndRect = getBounds(index);
    final double cx = bndRect.getCenterX();
    final double cy = bndRect.getCenterY();
    final double sz2 = MARK_SIZE / 2.0;
    final Rectangle2D mrkRect = new Rectangle2D.Double(cx - sz2, cy - sz2, MARK_SIZE, MARK_SIZE);
    return mrkRect;
  }

  /**
   * M�todo interno de <i>loop</i> e desenho dos dias.
   *
   * @param g2d o contexto gr�fico Java.
   * @param index o �ndice dentro do array de marcas
   */
  private void drawMark(final Graphics2D g2d, final int index) {
    final Rectangle2D rect = getMarkBounds(index);
    if (rect == null) {
      return;
    }

    final Color color = getMarkColor(index);
    g2d.setColor(color);
    g2d.fill(rect);
    g2d.setColor(lineColor);
    g2d.draw(rect);
  }

  /**
   * M�todo de desenho do gr�fico.
   *
   * @param g2d o contexto gr�fico Java.
   */
  private void drawPanel(final Graphics2D g2d) {
    drawItems(g2d);
  }

  /**
   * Pesquisa o �ndice pertinence a uma coordenada
   *
   * @param x o valor X
   * @param y o valor Y
   * @return o �ndice (ou -1 se n�o existir)
   */
  private int getIndex(final double x, final double y) {
    for (int index = 0; index < numItems; index++) {
      final Rectangle2D rect = getBounds(index);
      if (rect.contains(x, y)) {
        return index;
      }
    }
    return -1;
  }

  /**
   * Verifica se um �ndice � v�lido
   *
   * @param index o �ndice a ser checado
   * @return um indicativo
   */
  private boolean isValidIndex(final int index) {
    return index >= 0 && index < numItems;
  }

  /**
   * M�todo interno coloca��o dos <i>mouse adapter</i> utilizado no painel; este
   * <i>adapter</i> ser� respons�vel por repassar os eventos para a aplica��o.
   */
  private void addMouseAdapter() {
    final JDegradee self = this;

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(final MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();
        final int index = getIndex(x, y);
        if (!isValidIndex(index)) {
          return;
        }
        final Color color = calculateColorIndex(index);
        for (JDegradeeAdapter adapter : adapters) {
          adapter.mouseClicked(self, index, color, event);
        }
      }

      @Override
      public void mousePressed(final MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();
        final int index = getIndex(x, y);
        if (!isValidIndex(index)) {
          return;
        }
        final Color color = calculateColorIndex(index);
        for (JDegradeeAdapter adapter : adapters) {
          adapter.mousePressed(self, index, color, event);
        }
      }

      @Override
      public void mouseReleased(final MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();
        final int index = getIndex(x, y);
        if (!isValidIndex(index)) {
          return;
        }
        final Color color = calculateColorIndex(index);
        for (JDegradeeAdapter adapter : adapters) {
          adapter.mouseReleased(self, index, color, event);
        }
      }

      @Override
      public void mouseEntered(final MouseEvent event) {
        for (JDegradeeAdapter adapter : adapters) {
          adapter.mouseEntered(self, event);
        }
      }

      @Override
      public void mouseExited(final MouseEvent event) {
        for (JDegradeeAdapter adapter : adapters) {
          adapter.mouseExited(self, event);
        }
      }
    });
  }

  /**
   * Ajuste do comportamento padr�o com a roda do mouse.
   */
  private void addMouseWheelAdapter() {
    final JDegradee self = this;
    addMouseWheelListener(event -> {
      for (JDegradeeAdapter adapter : adapters) {
        adapter.mouseWheelMoved(self, event);
      }
    });
  }

  /**
   * Ajuste do comportamento padr�o com a roda do mouse.
   */
  private void addMouseMoveAdapter() {
    final JDegradee self = this;
    addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(final MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();
        final int index = getIndex(x, y);
        if (!isValidIndex(index)) {
          return;
        }
        final Color color = calculateColorIndex(index);
        for (JDegradeeAdapter adapter : adapters) {
          adapter.mouseDragged(self, index, color, event);
        }

      }

      @Override
      public void mouseMoved(final MouseEvent event) {
        final int x = event.getX();
        final int y = event.getY();
        final int index = getIndex(x, y);
        if (!isValidIndex(index)) {
          return;
        }
        final Color color = calculateColorIndex(index);
        for (JDegradeeAdapter adapter : adapters) {
          adapter.mouseMoved(self, index, color, event);
        }
      }
    });
  }

  /**
   * Realoca��o internal do array de marcas de cores
   */
  private void reallocMarks() {
    final Color[] array = new Color[numItems];
    if (markColor == null) {
      markColor = array;
      return;
    }

    final int oldLen = markColor.length;
    for (int i = 0; i < oldLen; i++) {
      final Color mkColor = markColor[i];
      if (mkColor != null) {
        final int j = (int) Math.round((double) i * numItems / oldLen);
        array[j] = mkColor;
      }
    }
    markColor = array;
  }

  /**
   * Montagem de um gradiente de cores.
   *
   * @param primary a cor prim�ria
   * @param secondary a cor secund�ria
   * @param factor o fator (0.0 at� 1.0)
   * @return um array de cores.
   */
  static private Color getGradientColor(final Color primary, final Color secondary, final double factor) {
    final int r1 = primary.getRed();
    final int g1 = primary.getGreen();
    final int b1 = primary.getBlue();

    final int r2 = secondary.getRed();
    final int g2 = secondary.getGreen();
    final int b2 = secondary.getBlue();

    final int dr = r2 - r1;
    final int dg = g2 - g1;
    final int db = b2 - b1;

    final int red = (int) Math.round(r1 + dr * factor);
    final int green = (int) Math.round(g1 + dg * factor);
    final int blue = (int) Math.round(b1 + db * factor);

    final Color color = new Color(red, green, blue);
    return color;
  }

  /**
   * Desenho do componente.
   *
   * @param g o contexto gr�fico Java.
   */
  @Override
  final public void paintComponent(final Graphics g) {
    final Graphics2D g2d = (Graphics2D) g.create();
    drawPanel(g2d);
  }

  /**
   * Adi��o de um listener
   *
   * @param listener o listener
   */
  final public void addDegradeeListener(final JDegradeeAdapter listener) {
    adapters.add(listener);
  }

  /**
   * Consulta a cor de linha do grid (se estiver habilitado)
   *
   * @return a cor
   */
  final public Color getLineColor() {
    return lineColor;
  }

  /**
   * Ajusta a cor de linha do grid (se estiver habilitado)
   *
   * @param lineColor a cor
   */
  final public void setLineColor(final Color lineColor) {
    this.lineColor = lineColor;
  }

  /**
   * Remo��o de um listener
   *
   * @param listener o listener
   */
  final public void delDegradeeListener(final JDegradeeAdapter listener) {
    adapters.remove(listener);
  }

  /**
   * Consulta da exit�ncia de uma cor de marca
   *
   * @param index o �ndice
   * @return o indicativo
   */
  final public boolean hasMarkColor(final int index) {
    return getMarkColor(index) != null;
  }

  /**
   * Consulta de uma cor de marca
   *
   * @param index o �ndice
   * @return a cor
   */
  final public Color getMarkColor(final int index) {
    return markColor[index];
  }

  /**
   * Ajuste de cor de marca com base em um �ndice
   *
   * @param index o �ndice
   * @param color a cor
   */
  final public void setMarkColorIndex(final int index, final Color color) {
    markColor[index] = color;
    repaint();
  }

  /**
   * Ajuste de cor de marca com base em um percentual
   *
   * @param ratio o percentual
   * @param color a cor
   * @see #setMarkColorIndex(int, Color)
   */
  final public void setMarkColorRatio(final double ratio, final Color color) {
    final int index = (int) Math.round((numItems - 1) * ratio);
    setMarkColorIndex(index, color);
  }

  /**
   * Consulta de cor prim�ria.
   *
   * @return a cor
   */
  final public Color getPrimaryColor() {
    return this.priColor;
  }

  /**
   * Consulta de cor secund�ria.
   *
   * @return a cor
   */
  final public Color getSecondaryColor() {
    return this.secColor;
  }

  /**
   * Ajuste de cor prim�ria
   *
   * @param color a cor
   */
  final public void setPrimaryColor(final Color color) {
    this.priColor = color;
    repaint();
  }

  /**
   * Ajuste de cor secund�ria
   *
   * @param color a cor
   */
  final public void setSecondaryColor(final Color color) {
    this.secColor = color;
    repaint();
  }

  /**
   * Ajuste de indicativo de desenho de grid.
   *
   * @param flag indicativo
   */
  final public void setGridActive(final boolean flag) {
    hasGrid = flag;
    repaint();
  }

  /**
   * Consulta de indicativo de desenho de grid.
   *
   * @return flag indicativo
   */
  final public boolean isGridActive() {
    return hasGrid;
  }

  /**
   * Ajuste de orienta��o.
   *
   * @param orientation orienta��o
   */
  final public void setOrientation(final Orientation orientation) {
    this.orientation = orientation;
    repaint();
  }

  /**
   * Consulta de orienta��o.
   *
   * @return a orienta��o
   */
  final public Orientation getOrientation() {
    return this.orientation;
  }

  /**
   * Construtor padr�o.
   */
  public JDegradee() {
    addMouseAdapter();
    addMouseWheelAdapter();
    addMouseMoveAdapter();
    setNumItens(11);
    setGridActive(false);
    setOrientation(Orientation.HORIZONTAL);
    setPrimaryColor(Color.red);
    setSecondaryColor(Color.blue);
    setMarkColorRatio(0.5, Color.yellow);
    // setMarkColorRatio(0.75, Color.white);
  }

  /**
   * M�todo de testes
   *
   * @param args <i>n�o utilizado</i>
   */
  static public void main(final String[] args) {
    final JFrame frame = new JFrame();

    final JDegradee jdeg = new JDegradee();
    jdeg.setPreferredSize(new Dimension(500, 100));

    final JPanel panel = (JPanel) frame.getContentPane();

    panel.setLayout(new BorderLayout());
    panel.add(jdeg, BorderLayout.CENTER);

    frame.setLocation(300, 300);
    frame.setSize(new Dimension(500, 300));
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
