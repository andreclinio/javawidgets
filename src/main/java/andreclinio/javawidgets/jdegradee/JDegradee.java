package andreclinio.javawidgets.jdegradee;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import andreclinio.javawidgets.jdegradee.adapters.JDegradeeAdapter;
import andreclinio.javawidgets.jdegradee.renderers.JDegradeeRenderer;

/**
 * Degradeé widget
 *
 * @author André Clinio
 */
public class JDegradee extends JPanel {

  /**
   * Layout for widget <code>JDegradee</code>
   *
   * @author André Clinio
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
   * Current layout
   */
  private Orientation orientation = Orientation.VERTICAL;

  /**
   * Line color
   */
  private Color lineColor = Color.black;

  /**
   * Primary color
   */
  private Color priColor = Color.red;

  /**
   * Secondary color
   */
  private Color secColor = Color.blue;

  /**
   * Markers' colors
   */
  private Color[] markColor;

  /**
   * number of itens (discret inside panel)
   */
  private int numItems;

  /**
   * Grid drawing flag
   */
  private boolean hasGrid = true;

  /**
   * Mark size (representation)
   */
  final static public int MARK_SIZE = 10;

  /**
   * Minimum number of itens
   */
  final static public int MIN_ITENS = 2;

  /**
   * Maximum number of itens
   */
  final static public int MAX_ITENS = 512;

  /**
   * Mouse adapaters list
   */
  final private ArrayList<JDegradeeAdapter> adapters = new ArrayList<>();

  /**
   * Texto a ser desenhado
   */
  final private ArrayList<JDegradeeRenderer> textBuilders = new ArrayList<>();

  /**
   * Number of itens
   *
   * @return number of itens
   */
  final public int getNumItems() {
    return numItems;
  }

  /**
   * Sets the new number of ietns
   *
   * @param numItens number of itens
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
   * Search of next index that has a mark
   *
   * @param index search index
   * @return index (or -1 if does not exist)
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
   * Search of previous index that has a mark
   *
   * @param index search index
   * @return index (or -1, if does not exist)
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
   * Query an adjusted color based on a percentage .
   *
   * @param ratio percentage
   * @return color
   * @see #calculateColorIndex(int)
   */
  final public Color calculateColorRatio(final double ratio) {
    final int index = (int) Math.round(numItems * ratio);
    return calculateColorIndex(index);
  }

  /**
   * Calculates the color of an item based on the marks and colors. (primary and
   * ssecondary)
   *
   * @param index index
   * @return color
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
   * Calculates the rectangular area of an item
   *
   * @param index index
   * @return rectangle
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
   * Internal drawing method (loop)
   *
   * @param g graphical Java context
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
   * Item renderer addition
   *
   * @param renderer renderer
   */
  final public void addItemRenderer(final JDegradeeRenderer renderer) {
    textBuilders.add(renderer);
  }

  /**
   * Item renderer removal
   *
   * @param renderer renderer
   */
  final public void delItemRenderer(final JDegradeeRenderer renderer) {
    textBuilders.remove(renderer);
  }

  /**
   * Item renederers full removal.
   */
  final public void clearRenderer() {
    textBuilders.clear();
  }

  /**
   * Internal item draw mthod
   *
   * @param g2d Java graphical context
   * @param index index
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
   * Querying the region that defines a mark (if any)
   *
   * @param index index
   * @return rectangle or {@code null}, if there are no marks
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
   * MArk internal drawing method
   *
   * @param g2d Java graphical context
   * @param index mark index
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
   * Full draweing method
   *
   * @param g2d Java graphical context
   */
  private void drawPanel(final Graphics2D g2d) {
    drawItems(g2d);
  }

  /**
   * Search for index belonging to a coordinate
   *
   * @param x  X value
   * @param y  Y value
   * @return index (or -1 if it does not exist)
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
   * Index validation check
   *
   * @param index index
   * @return flag
   */
  private boolean isValidIndex(final int index) {
    return index >= 0 && index < numItems;
  }

  /**
   * Internal method to use a mouse adapter that resends events to application.
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
   * Internal method for setting mouse wheel
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
   * Internal method for setting mouse move
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
   * Internal mark colors reallocation
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
   * Mounting a color gradient.
   *
   * @param primary primary color
   * @param secondary secondary color
   * @param factor factor (0.0 up to 1.0)
   * @return color
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
   * Componetn drawing
   *
   * @param g Java graphical context
   */
  @Override
  final public void paintComponent(final Graphics g) {
    final Graphics2D g2d = (Graphics2D) g.create();
    drawPanel(g2d);
  }

  /**
   * Listener addition
   *
   * @param listener listener
   */
  final public void addDegradeeListener(final JDegradeeAdapter listener) {
    adapters.add(listener);
  }

  /**
   * Remoção de um listener
   *
   * @param listener o listener
   */
  final public void delDegradeeListener(final JDegradeeAdapter listener) {
    adapters.remove(listener);
  }

  /**
   * Grid line query
   *
   * @return color
   */
  final public Color getLineColor() {
    return lineColor;
  }

  /**
   * Grid line adjustment
   *
   * @param color color
   */
  final public void setLineColor(final Color color) {
    this.lineColor = color;
  }


  /**
   * Check for the existence of a mark color
   * @param index index
   * @return flag
   */
  final public boolean hasMarkColor(final int index) {
    return getMarkColor(index) != null;
  }

  /**
   * Mark color query
   *
   * @param index o índice
   * @return a cor
   */
  final public Color getMarkColor(final int index) {
    return markColor[index];
  }

  /**
   * Mark color adjustment (based on an index)
   *
   * @param index index
   * @param color color
   */
  final public void setMarkColorIndex(final int index, final Color color) {
    markColor[index] = color;
    repaint();
  }

  /**
   * Mark color adjustment (based on percentage)
   *
   * @param ratio ration
   * @param color color
   * @see #setMarkColorIndex(int, Color)
   */
  final public void setMarkColorRatio(final double ratio, final Color color) {
    final int index = (int) Math.round((numItems - 1) * ratio);
    setMarkColorIndex(index, color);
  }

  /**
   * Primary color query
   *
   * @return color
   */
  final public Color getPrimaryColor() {
    return this.priColor;
  }

  /**
   * Secondary color query
   *
   * @return color
   */
  final public Color getSecondaryColor() {
    return this.secColor;
  }

  /**
   * Primary color adjustment
   *
   * @param color color
   */
  final public void setPrimaryColor(final Color color) {
    this.priColor = color;
    repaint();
  }

  /**
   * Secondary color adjustment
   *
   * @param color color
   */
  final public void setSecondaryColor(final Color color) {
    this.secColor = color;
    repaint();
  }

  /**
   * Grid drawing adjustment
   *
   * @param flag flag
   */
  final public void setGridActive(final boolean flag) {
    hasGrid = flag;
    repaint();
  }

  /**
   * Grid drawing query
   *
   * @return flag
   */
  final public boolean isGridActive() {
    return hasGrid;
  }

  /**
   * Orientatiom adjustment
   *
   * @param orientation orientation
   */
  final public void setOrientation(final Orientation orientation) {
    this.orientation = orientation;
    repaint();
  }

  /**
   * Orientation query
   *
   * @return orientation
   */
  final public Orientation getOrientation() {
    return this.orientation;
  }

  /**
   * Constructor
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
}
