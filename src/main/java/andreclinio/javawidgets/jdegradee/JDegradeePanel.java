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
 * @author André Clinio
 */
public class JDegradeePanel extends JPanel {

  /**
   * Botão de seleção de cor primária
   */
  final private JButton priButton = new JButton();

  /**
   * Botão de seleçÃo de cor secundária
   */
  final private JButton secButton = new JButton();

  /**
   * Painel de degradee
   */
  final private JDegradee jDegradee = new JDegradee();

  /**
   * Escolha de uma cor
   *
   * @param color a cor antiga já selecionada
   *
   * @return uma cor (ou <code>null</code> em caso de desistência)
   */
  final private Color chooseColor(final Color color) {
    return JColorChooser.showDialog(jDegradee, "", color);
  }

  /**
   * Ajusta o layout do botão primário
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
   * Ajusta o layout do botão secundário
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
   * Ajuste da cor primária
   *
   * @param color a cor
   */
  final public void setPrimaryColor(final Color color) {
    jDegradee.setPrimaryColor(color);
    priButton.setBackground(color);
  }

  /**
   * Ajuste da cor primária
   *
   * @param color a cor
   */
  final public void setSecondaryColor(final Color color) {
    jDegradee.setSecondaryColor(color);
    secButton.setBackground(color);
  }

  /**
   * Ajuste de listener de mouse
   */
  final private void setMouseAdapter() {
    jDegradee.addDegradeeListener(new JDegradeeStandardAdapter("ED", "DEL"));
  }

  /**
   * Ajusta o text builder do elemento.
   *
   * @param renderer o builder
   */
  final public void addItemRenderer(final JDegradeeRenderer renderer) {
    jDegradee.addItemRenderer(renderer);
  }

  /**
   * Ajusta o text builder do elemento.
   *
   * @param renderer o builder
   */
  final public void delItemRenderer(final JDegradeeRenderer renderer) {
    jDegradee.delItemRenderer(renderer);
  }

  /**
   * Limpa todos os text builders do elemento.
   */
  final public void clearItemRenderer() {
    jDegradee.clearRenderer();
  }

  /**
   * Consulta a orientação do elemento degradeé interno
   *
   * @return a orientação
   */
  final public JDegradee.Orientation getOrientation() {
    return jDegradee.getOrientation();
  }

  /**
   * Ajuste de orientação.
   *
   * @param orientation orientação
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
   * Construtor padrão.
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
