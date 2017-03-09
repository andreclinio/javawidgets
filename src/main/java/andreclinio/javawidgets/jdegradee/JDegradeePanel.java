package andreclinio.javawidgets.jdegradee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import andreclinio.javawidgets.jdegradee.adapters.JDegradeeStandardAdapter;
import andreclinio.javawidgets.jdegradee.renderers.JDegradeeRenderer;

/**
 * Simple panel with de {@link JDegradee} with two limits buttons.
 *
 * @author André Clinio
 */
public class JDegradeePanel extends JPanel {

  /**
   * Primary color button
   */
  final private JButton priButton = new JButton();

  /**
   * Secondary color button
   */
  final private JButton secButton = new JButton();

  /**
   * Panel
   */
  final private JDegradee jDegradee = new JDegradee();

  /**
   * Simple color chooser
   *
   * @param color old selected color
   *
   * @return new color or <code>null</code> (for cancel operation)
   */
  final private Color chooseColor(final Color color) {
    return JColorChooser.showDialog(jDegradee, "", color);
  }

  /**
   * Button layout
   */
  final private void adjustPrimaryButton() {
    final Color priColor = jDegradee.getPrimaryColor();
    priButton.setBackground(priColor);
    priButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        final Color color = chooseColor(priColor);
        if (color != null) {
          setPrimaryColor(color);
        }
      }
    });
  }

  /**
   * Button layout
   */
  final private void adjustSecondaryButton() {
    final Color secColor = jDegradee.getSecondaryColor();
    secButton.setBackground(secColor);
    secButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        final Color color = chooseColor(secColor);
        if (color != null) {
          setSecondaryColor(color);
        }
      }
    });
  }

  /**
   * Set primary color
   *
   * @param color color
   */
  final public void setPrimaryColor(final Color color) {
    jDegradee.setPrimaryColor(color);
    priButton.setBackground(color);
  }

  /**
   * Set secondary color
   *
   * @param color color
   */
  final public void setSecondaryColor(final Color color) {
    jDegradee.setSecondaryColor(color);
    secButton.setBackground(color);
  }

  /**
   * Adjustment of mouse adapter
   */
  final private void setMouseAdapter() {
    jDegradee.addDegradeeListener(new JDegradeeStandardAdapter("ED", "DEL"));
  }

  /**
   * Adjustement of item renderer
   *
   * @param renderer renderer
   */
  final public void addItemRenderer(final JDegradeeRenderer renderer) {
    jDegradee.addItemRenderer(renderer);
  }

  /**
   * Removal of item renderer
   *
   * @param renderer renderer
   */
  final public void delItemRenderer(final JDegradeeRenderer renderer) {
    jDegradee.delItemRenderer(renderer);
  }

  /**
   * Complete removal of item renderers
   */
  final public void clearItemRenderer() {
    jDegradee.clearRenderer();
  }

  /**
   * Orientation getter
   *
   * @return orientation
   */
  final public JDegradee.Orientation getOrientation() {
    return jDegradee.getOrientation();
  }

  /**
   * Orientation setter
   *
   * @param orientation orinetation
   */
  final public void setOrientation(final JDegradee.Orientation orientation) {
    jDegradee.setOrientation(orientation);
    this.remove(priButton);
    this.remove(secButton);
    switch (orientation) {
      case HORIZONTAL:
        add(priButton, BorderLayout.WEST);
        add(secButton, BorderLayout.EAST);
        break;

      case VERTICAL:
        add(priButton, BorderLayout.NORTH);
        add(secButton, BorderLayout.SOUTH);
        break;
    }
    this.revalidate();
  }

  /**
   * Constructor
   */
  public JDegradeePanel() {
    setLayout(new BorderLayout());
    adjustPrimaryButton();
    adjustSecondaryButton();
    setMouseAdapter();
    add(priButton, BorderLayout.WEST);
    add(secButton, BorderLayout.EAST);

    add(jDegradee, BorderLayout.CENTER);
    setOrientation(JDegradee.Orientation.VERTICAL);
  }
}
