package andreclinio.javawidgets.jdegradee.adapters;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JPopupMenu;

import andreclinio.javawidgets.jdegradee.JDegradee;

/**
 * Default mouse adapter.
 *
 * @author André Clinio
 */
public class JDegradeeStandardAdapter extends JDegradeeAdapter {

  /**
   * Edition text
   */
  final private String editionString;

  /**
   * Deletion text
   */
  final private String deletionString;

  /**
   * Color chooser
   *
   * @param jDegradee widget
   * @param color old selected color
   *
   * @return new chosen color (or {@code null}
   */
  private static Color chooseColor(final JDegradee jDegradee, final Color color) {
    return JColorChooser.showDialog(jDegradee, "", color);
  }

  /**
   * Checks if there is a mark at mouse event coordinates.
   *
   * @param jDegradee widget
   * @param event Java event
   * @return valid index or -1.
   */
  final protected static int getMarkIndexOnEvent(final JDegradee jDegradee, final MouseEvent event) {
    final int numItems = jDegradee.getNumItems();
    for (int idx = 0; idx < numItems; idx++) {
      if (jDegradee.hasMarkColor(idx)) {
        final Rectangle2D rect = jDegradee.getMarkBounds(idx);
        if (rect == null) {
          return -1;
        }
        final double x = event.getX();
        final double y = event.getY();
        if (rect.contains(x, y)) {
          return idx;
        }
      }
    }
    return -1;
  }

  /**
   * Menu popup building
   *
   * @param jDegradee widget
   * @param index index
   * @return menu
   */
  private JPopupMenu buildMarkMenu(final JDegradee jDegradee, final int index) {
    final JPopupMenu menu = new JPopupMenu();

    menu.add(new AbstractAction(deletionString) {
      @Override
      public void actionPerformed(ActionEvent ae) {
        jDegradee.setMarkColorIndex(index, null);
      }
    });

    menu.add(new AbstractAction(editionString) {
      @Override
      public void actionPerformed(ActionEvent ae) {
        final Color oldColor = jDegradee.getMarkColor(index);
        final Color newColor = chooseColor(jDegradee, oldColor);
        if (newColor != null) {
          jDegradee.setMarkColorIndex(index, newColor);
        }
      }
    });

    return menu;
  }

  /**
   * Internal menu method to add marks.
   *
   * @param jDegradee widget
   * @param index selected color index
   * @param event original Java event
   */
  private void tryMarkMenu(final JDegradee jDegradee, final MouseEvent event, final int index) {
    if (event.getButton() != MouseEvent.BUTTON3) {
      return;
    }

    final double x = event.getX();
    final double y = event.getY();
    if (event.isPopupTrigger()) {
      final JPopupMenu menu = buildMarkMenu(jDegradee, index);
      menu.show(jDegradee, (int) x, (int) y);
    }
  }

  /**
   * Try to add mark.
   * @param jDegradee widget
   * @param index selected color index
   * @param color selected color
   * @param event original Java event
   */
  private void tryMarkCreation(final JDegradee jDegradee, final int index, final Color color,
    final MouseEvent event) {
    if (getMarkIndexOnEvent(jDegradee, event) >= 0) {
      return;
    }
    if (event.getClickCount() != 2) {
      return;
    }
    final Color newColor = chooseColor(jDegradee, color);
    if (newColor != null) {
      jDegradee.setMarkColorIndex(index, newColor);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  final public void mouseClicked(final JDegradee jDegradee, final int index, final Color color,
    final MouseEvent event) {
    final int button = event.getButton();
    if (button == MouseEvent.BUTTON1) {
      tryMarkCreation(jDegradee, index, color, event);
    }
    else {
      tryMarkMenu(jDegradee, event, index);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  final public void mousePressed(final JDegradee jDegradee, final int index, final Color color,
    final MouseEvent event) {
    final int idx = getMarkIndexOnEvent(jDegradee, event);
    if (idx >= 0) {
      tryMarkMenu(jDegradee, event, idx);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  final public void mouseReleased(final JDegradee jDegradee, final int index, final Color color,
    final MouseEvent event) {
    final int idx = getMarkIndexOnEvent(jDegradee, event);
    if (idx >= 0) {
      tryMarkMenu(jDegradee, event, idx);
    }
  }

  /**
   * Constructor
   *
   * @param editionString edition text
   * @param deletionString deletion text
   */
  public JDegradeeStandardAdapter(final String editionString, final String deletionString) {
    this.deletionString = deletionString;
    this.editionString = editionString;
  }
}
