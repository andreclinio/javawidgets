package andreclinio.javawidgets.jdegradee.samples.simplepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import andreclinio.javawidgets.jdegradee.JDegradee;
import andreclinio.javawidgets.jdegradee.JDegradeePanel;
import andreclinio.javawidgets.jdegradee.renderers.JDegradeeIndexRenderer;

/**
 * @author Andr� Clinio
 */
public class SimplePanel {

  /**
   * Texto
   */
  final static private JDegradeeIndexRenderer TB = new JDegradeeIndexRenderer(new Font("monospaced", Font.PLAIN, 12),
    Color.black);

  /**
   * Constru��o de painel de toggles.
   *
   * @param jdeg o degradee panel
   * @return o painel
   */
  static private JPanel buildVHPanel(final JDegradeePanel jdeg) {
    final JRadioButton vTog = new JRadioButton("V");
    vTog.addActionListener(ae -> jdeg.setOrientation(JDegradee.Orientation.VERTICAL));

    final JRadioButton hTog = new JRadioButton("H");
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
   * Constru��o de painel de toggles.
   *
   * @param jdeg o degradee panel
   * @return o painel
   */
  static private JPanel buildTextPanel(final JDegradeePanel jdeg) {
    final JRadioButton tog = new JRadioButton("Text");
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
   * M�todo de testes
   *
   * @param args <i>n�o utilizado</i>
   */
  static public void main(final String[] args) {
    final JFrame frame = new JFrame();

    final JDegradeePanel jdeg = new JDegradeePanel();
    jdeg.setPreferredSize(new Dimension(500, 100));

    final JPanel panel = (JPanel) frame.getContentPane();
    final JPanel vhPanel = buildVHPanel(jdeg);
    final JPanel txtPanel = buildTextPanel(jdeg);

    panel.setLayout(new BorderLayout());
    panel.add(jdeg, BorderLayout.CENTER);
    panel.add(vhPanel, BorderLayout.SOUTH);
    panel.add(txtPanel, BorderLayout.NORTH);

    frame.setLocation(300, 300);
    frame.setSize(new Dimension(500, 300));
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
