package andreclinio.javawidgets.jdegradee.samples.minimumpanel;


import andreclinio.javawidgets.jdegradee.JDegradee;

import javax.swing.*;
import java.awt.*;

/**
 * Minimum Test
 */
public class MinimumPanel {

    /**
     * Main
     *
     * @param args arguments
     */
    static public void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
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
        });
    }
}
