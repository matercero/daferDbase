package es.dafer.tercero.ma.main;

import com.linuxense.javadbf.DBFException;
import es.dafer.tercero.ma.db.UpdateEstadoClientes;
import static es.dafer.tercero.ma.main.DBFWriterTest.prop;
import es.dafer.tercero.ma.utils.JDBFException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
public class Principal extends JPanel {

    static Logger logger = Logger.getLogger("daferDbase Principal");
    static FileHandler fh;

    private static final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");
    static JFrame frame = new JFrame("Generación TENCOM Dafer.");

    JFormattedTextField inputD, inputH;
    Box box = Box.createVerticalBox();

    JButton jbt1 = new JButton("Crear fichero DBF");
    JButton jbt2 = new JButton("Cerrar");
    // UPDATE LOS REGITROS CONTABILIZADOS
    final JButton jbt3 = new JButton("Actualizar Estado Facturas Clientes");

//    JButton jbt4 = new JButton("Button4");
    public Principal() {

        JLabel labelD, labelH;

        labelD = new JLabel("Fecha Desde: dd/mm/yyyy");
        //  inputD = new JFormattedTextField(df.format(new Date()));
        inputD = new JFormattedTextField("31/01/2014");
        inputD.setColumns(10);
//        inputD.setMaximumSize(new Dimension(10, 0));

        labelH = new JLabel("Hasta: ");
        // inputH = new JFormattedTextField(df.format(new Date()));
        inputH = new JFormattedTextField("31/08/2014");
        inputH.setColumns(10);
//        inputH.setMaximumSize(new Dimension(10, 0));

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
                    dateD = DF.parse(inputD.getText());
                    dateH = DF.parse(inputH.getText());

                } catch (ParseException ex) {
                    Logger.getLogger(Principal.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

                if (source instanceof JButton) {
                    Date[] args = {dateD, dateH};
                    try {
                        try {
                            int result = DBFWriterTest.WriterDbf(frame, args, logger);
                            if (result == 0) {
                                JOptionPane.showMessageDialog(frame, "Fichero creado correctamente.");
                                JOptionPane.showMessageDialog(frame, "NOTA: Actualizar campos !! ");
                                jbt3.setEnabled(true);
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

        box.add(jbt3, BorderLayout.CENTER);
        jbt3.setEnabled(false);
        jbt3.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 Object source = e.getSource();
                  if (source instanceof JButton) {
                     try {
                         int result = DBFWriterTest.updateEstado();
                         if (result == 0) {
                                JOptionPane.showMessageDialog(frame, "Actualizado Estado "
                                        + " de Factura Clientes. Correctamente");
                                jbt3.setEnabled(false);
                                logger.info("Proceso finalizado correctamente.");
                            } else {
                                logger.warning("ERROR: se ha producido un error.");
                            }
                     } catch (SQLException ex) {
                         Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                     }
                  }
            }
        });

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
        frame.setSize(500, 500);
        frame.setLayout(new FlowLayout());
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
            logger.log(Level.INFO, "Path log: {0}", prop.getProperty("pathLog"));
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Main: {0}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Main: {0}", e.getMessage());
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
            logger.log(Level.SEVERE, "getProperties IOException: {0}", ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.log(Level.SEVERE, "getProperties IOException: {0}", e.getMessage());
                }
            }
        }

    }
}
