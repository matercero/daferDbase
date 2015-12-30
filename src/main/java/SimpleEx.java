
import com.linuxense.javadbf.DBFException;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtercero
 */
public class SimpleEx extends JPanel {

    JButton jbt1 = new JButton("Crear fichero DBF");
//    JButton jbt2 = new JButton("Salir");
//    JButton jbt3 = new JButton("Button3");
//    JButton jbt4 = new JButton("Button4");

    public SimpleEx() {
        Box box = Box.createVerticalBox();
        box.add(jbt1);
        jbt1.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Object source = e.getSource();
                        if (source instanceof JButton) {
                            JButton btn = (JButton) source;
                            // Go ahead and do what you like
                            String[] args = null;
                            try {
                                try {
                                    DBFWriterTest.WriterDbf(args);
                                } catch (SQLException ex) {
                                    Logger.getLogger(SimpleEx.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } catch (DBFException ex) {
                                Logger.getLogger(SimpleEx.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(SimpleEx.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });

//        box.add(Box.createVerticalStrut(10));
//        box.add(jbt2);
//        box.add(Box.createVerticalStrut(10));
//        box.add(jbt3);
//        box.add(Box.createVerticalStrut(10));
//        box.add(jbt4);
//        box.add(Box.createVerticalStrut(10));

        add(box);
    }

    public static void createAndShowGui() {
        JFrame frame = new JFrame();
        frame.add(new SimpleEx());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }
}