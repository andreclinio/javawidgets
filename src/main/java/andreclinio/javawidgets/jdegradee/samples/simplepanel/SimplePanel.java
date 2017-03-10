package andreclinio.javawidgets.jdegradee.samples.simplepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

import andreclinio.javawidgets.jdegradee.JDegradee;
import andreclinio.javawidgets.jdegradee.JDegradeePanel;
import andreclinio.javawidgets.jdegradee.renderers.JDegradeeIndexRenderer;

/**
 * @author André Clinio
 */
public class SimplePanel {

  /**
   * Text index renderer used in demo.
   */
  final static private JDegradeeIndexRenderer TB = new JDegradeeIndexRenderer(new Font("monospaced", Font.PLAIN, 12),
    Color.black);

  /**
   * Radio button panel construction
   *
   * @param jdeg  degradee panel
   * @return panel
   */
  static private JPanel buildVHPanel(final JDegradeePanel jdeg) {
    final JRadioButton vTog = new JRadioButton("Vertical");
    vTog.addActionListener(ae -> jdeg.setOrientation(JDegradee.Orientation.VERTICAL));

    final JRadioButton hTog = new JRadioButton("Horizointal");
    hTog.addActionListener(ae -> jdeg.setOrientation(JDegradee.Orientation.HORIZONTAL));

    final ButtonGroup vhGrp = new ButtonGroup();
    vhGrp.add(vTog);
    vhGrp.add(hTog);
    final JPanel panel = new JPanel();
    panel.add(vTog);
    panel.add(hTog);

    jdeg.setOrientation(JDegradee.Orientation.HORIZONTAL);
    hTog.setSelected(true);

    return panel;
  }

  /**
   * Toggle panel construction
   *
   * @param jdeg  degradee panel
   * @return  panel
   */
  static private JPanel buildTextPanel(final JDegradeePanel jdeg) {
    final JRadioButton tog = new JRadioButton("Draw indexes");
    tog.addActionListener(ae -> {
      final boolean state = tog.isSelected();
      jdeg.clearItemRenderer();
      if (state) {
        jdeg.addItemRenderer(TB);
      }
      jdeg.repaint();
    });
    final JPanel panel = new JPanel();
    panel.add(tog);
    return panel;
  }

  /**
   * Main
   *
   * @param args arguments
   */
  static public void main(final String[] args) {
    SwingUtilities.invokeLater(() -> {
      final JFrame frame = new JFrame();
      frame.setMinimumSize(new Dimension(300, 300));

      final JDegradeePanel jdeg = new JDegradeePanel();
      final JPanel vhPanel = buildVHPanel(jdeg);
      final JPanel txtPanel = buildTextPanel(jdeg);

      final JPanel panel = (JPanel) frame.getContentPane();
      panel.setLayout(new BorderLayout());
      panel.add(jdeg, BorderLayout.CENTER);
      panel.add(vhPanel, BorderLayout.SOUTH);
      panel.add(txtPanel, BorderLayout.NORTH);

      frame.setSize(new Dimension(600, 150));
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    });
  }
}
