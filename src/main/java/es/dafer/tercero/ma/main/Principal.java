package es.dafer.tercero.ma.main;

import static es.dafer.tercero.ma.main.DBFWriterTest.prop;
import es.dafer.tercero.ma.utils.DateLabelFormatter;
import es.dafer.tercero.ma.utils.JDBFException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtercero
 */
public class Principal extends JPanel {

    private final static Logger logger = Logger.getLogger(Principal.class);
    static FileHandler fh;

    private static final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");
    static JFrame frame = new JFrame("DAFER. Generación Fichero contabilidad TENCOM.");

    JFormattedTextField inputD, inputH;
    JDatePickerImpl datePickerFrom, datePickerTo;

    Box box = Box.createVerticalBox();

    JButton jbtGenFileDBF = new JButton("Crear fichero DBF");
    JButton jbtCerrar = new JButton("Cerrar");
    // UPDATE LOS REGITROS CONTABILIZADOS
    final JButton jbt3 = new JButton("Actualizar Estado Facturas Clientes");
//    JButton jbt4 = new JButton("Button4");

    public Principal() {

        JLabel labelD, labelH;
        labelD = new JLabel("Fecha desde: ");
        labelH = new JLabel("Hasta: ");

        UtilDateModel modelFrom = new UtilDateModel();
        UtilDateModel modelTo = new UtilDateModel();
        modelFrom.setDate(2015, 8, 25);
        modelFrom.setSelected(true);
        modelTo.setDate(2015, 9, 25);
        modelTo.setSelected(true);

        JDatePanelImpl datePanelFrom = new JDatePanelImpl(modelFrom);
        JDatePanelImpl datePanelTo = new JDatePanelImpl(modelTo);

        datePickerFrom = new JDatePickerImpl(datePanelFrom, new DateLabelFormatter());
        datePickerFrom.setMaximumSize(new Dimension(200, 30));

        datePickerTo = new JDatePickerImpl(datePanelTo, new DateLabelFormatter());
        datePickerTo.setMaximumSize(new Dimension(200, 30));

        box.add(Box.createVerticalStrut(20));
        box.add(labelD);
        box.add(datePickerFrom, BorderLayout.CENTER);
        box.add(labelH);
        box.add(datePickerTo, BorderLayout.CENTER);

        box.add(jbtGenFileDBF, BorderLayout.LINE_START);
        jbtGenFileDBF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();

                Date dateD = new Date();
                Date dateH = new Date();
                dateD = (Date) datePickerFrom.getModel().getValue();
                dateH = (Date) datePickerTo.getModel().getValue();
                Map<String,String> mapArgumentos = new HashMap<String,String>();

                if (source instanceof JButton) {
                    try {
                        Date[] argsDates = {dateD, dateH};
                        int result = DBFWriterTest.WriterDbf(frame, argsDates, mapArgumentos);
                        if (result == 0) {
                            JOptionPane.showMessageDialog(frame, "Fichero creado correctamente.");
                            String msg =  "<html>Fichero generado: "  + mapArgumentos.get("FILEPATH")
                                    + "<br>Registros Cabeceras generados:  " +mapArgumentos.get("REGCAB")
                                    + "<br>Registros Detalles generados:  " +mapArgumentos.get("REGDET") 
                                    + "</html>";                                    

                            JOptionPane.showMessageDialog(frame,msg, "Resumen proceso", 
                                    JOptionPane.INFORMATION_MESSAGE);
                           
                            jbt3.setEnabled(true);
                            logger.info("Proceso finalizado correctamente.");
                        } else {
                            logger.error("ERROR: se ha producido un error.");
                        }
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        java.util.logging.Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JDBFException ex) {
                        java.util.logging.Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        box.add(jbt3, BorderLayout.CENTER);
        jbt3.setEnabled(false);
//        jbt3.addActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                Object source = e.getSource();
//                if (source instanceof JButton) {
//                    int result = DBFWriterTest.updateEstado();
//                    if (result == 0) {
//                        JOptionPane.showMessageDialog(frame, "Actualizado Estado "
//                                + " de Factura Clientes. Correctamente");
//                        jbt3.setEnabled(false);
//                        logger.info("Proceso finalizado correctamente.");
//                    } else {
//                        logger.warn("ERROR: se ha producido un error.");
//                    }
//                }
//            }
//        });

        //BOTON SALIR       
        frame.getContentPane().add(BorderLayout.SOUTH, jbtCerrar);
        jbtCerrar.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(box);
    }

    public static void createAndShowGui() {
        frame.add(new Principal());
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        try {
            getProperties();
            BasicConfigurator.configure();
            logger.info("Path log: " + prop.getProperty("pathLog"));
        } catch (SecurityException e) {
            logger.error("Error SecurityException: " + e.getMessage());
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
            logger.error("Error IOException: " + ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("Error IOException: " + e.getMessage());
                }
            }
        }

    }
}
