package es.dafer.tercero.ma.main;

import com.linuxense.javadbf.DBFException;
import static es.dafer.tercero.ma.main.DBFWriterTest.prop;
import es.dafer.tercero.ma.utils.JDBFException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtercero
 */
public class Principal extends JPanel {

    static Logger logger = Logger.getLogger("MyLog");
    static FileHandler fh;

    static JFrame frame = new JFrame();

    JButton jbt1 = new JButton("Crear fichero DBF");
    JButton jbt2 = new JButton("Cerrar");
    JFormattedTextField inputD, inputH;

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

//    JButton jbt3 = new JButton("Button3");
//    JButton jbt4 = new JButton("Button4");
    public Principal() {

        Box box = Box.createVerticalBox();

        JLabel labelD, labelH;

        labelD = new JLabel("Fecha Desde: dd/mm/yyyy");
      //  inputD = new JFormattedTextField(df.format(new Date()));
        inputD = new JFormattedTextField("01/02/2015");
        inputD.setColumns(20);

        labelH = new JLabel("Hasta: ");
       // inputH = new JFormattedTextField(df.format(new Date()));
        inputH = new JFormattedTextField("20/02/2015");
        inputH.setColumns(20);

        box.add(Box.createVerticalStrut(10));
        box.add(labelD);
        box.add(inputD);
        box.add(labelH);
        box.add(inputH);

        box.add(jbt1);
        jbt1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();

                Date dateD = new Date();
                Date dateH = new Date();
                try {
                    dateD = df.parse(inputD.getText());
                    dateH = df.parse(inputH.getText());

                } catch (ParseException ex) {
                    Logger.getLogger(Principal.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

                if (source instanceof JButton) {
                    JButton btn = (JButton) source;
                    // Go ahead and do what you like
                    Date[] args = {dateD, dateH};
                    try {
                        try {
                            int result = DBFWriterTest.WriterDbf(frame, args, logger);
                            if (result == 0) {
                                JOptionPane.showMessageDialog(frame, "Fichero creado correctamente.");
                                logger.info("Proceso finalizado correctamente.");
                            } else {
                                logger.warning("ERROR: se ha producido un error.");

                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Principal.class
                                    .getName()).log(Level.SEVERE, null, ex);

                        } catch (JDBFException ex) {
                            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (DBFException ex) {
                        Logger.getLogger(Principal.class
                                .getName()).log(Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        Logger.getLogger(Principal.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

//        box.add(Box.createVerticalStrut(10));
//        box.add(jbt4);
//        box.add(Box.createVerticalStrut(10));
        //BOTON SALIR
        box.add(Box.createVerticalStrut(30));
        box.add(jbt2, BorderLayout.CENTER);
        jbt2.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(box);
    }

    public static void createAndShowGui() {
        frame.add(new Principal());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        try {
            getProperties();
            // This block configure the logger with handler and formatter  
            fh = new FileHandler(prop.getProperty("pathLog"));
            logger.addHandler(fh);
            logger.info("Path log: " + prop.getProperty("pathLog"));
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);            

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Iniciando proceso....");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }//main

    private static void getProperties() {
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            // load a properties file
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
