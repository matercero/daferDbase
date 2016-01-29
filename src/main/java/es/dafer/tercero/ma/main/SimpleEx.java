package es.dafer.tercero.ma.main;

import com.linuxense.javadbf.DBFException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.Format;
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
public class SimpleEx extends JPanel {

    static Logger logger = Logger.getLogger("MyLog");
    static FileHandler fh;

    static JFrame frame = new JFrame();

    JButton jbt1 = new JButton("Crear fichero DBF");
    JButton jbt2 = new JButton("Cerrar");
    JFormattedTextField inputD, inputH;

//    JButton jbt3 = new JButton("Button3");
//    JButton jbt4 = new JButton("Button4");
    public SimpleEx() {

        Box box = Box.createVerticalBox();

        JLabel labelD, labelH;
        Format fechaDesde = DateFormat.getDateInstance(DateFormat.SHORT);
        Format fechaHasta = DateFormat.getDateInstance(DateFormat.SHORT);
        labelD = new JLabel("Fecha Desde: ");
        inputD = new JFormattedTextField(fechaDesde);
        labelH = new JLabel("Hasta: ");
        inputD = new JFormattedTextField(fechaHasta);
        inputD.setValue(new Date());
        inputD.setColumns(10);
        inputH = new JFormattedTextField(fechaHasta);
        inputH.setValue(new Date());
        inputH.setColumns(10);

        box.add(Box.createVerticalStrut(10));
        box.add(labelD);
        box.add(inputD);
        box.add(labelH);
        box.add(inputH);

        box.add(jbt1);
        jbt1.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date dateD = new Date();
                Date dateH = new Date();
                try {
                    dateD = formatter.parse(inputD.getText());
                    dateH = formatter.parse(inputH.getText());
                } catch (ParseException ex) {
                    Logger.getLogger(SimpleEx.class.getName()).log(Level.SEVERE, null, ex);
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
        frame.add(new SimpleEx());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {

        try {

            // This block configure the logger with handler and formatter  
            fh = new FileHandler("D:\\_eme3\\LogFileDafer.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages  
            logger.info("Iniciando proceso....");

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }
}
