package es.dafer.tercero.ma.main;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import es.dafer.tercero.ma.db.Connect;
import static es.dafer.tercero.ma.main.SimpleEx.logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtercero
 */
public class DBFWriterTest {

    private static final int TOTAL = 34;
    private static String PATH_FILE = "C:\\"; //PARA WINDOWS DAFAULT
    static Properties prop = new Properties();

//    public static void main(String[] args) throws DBFException, IOException {
//        WriterDbf(args);
//    }
    /**
     *
     * @param args
     * @return 0=OK ; -1=ERROR
     * @throws DBFException
     * @throws IOException
     * @throws SQLException
     */
    public static int WriterDbf(JFrame frame, Date args[], Logger logger)
            throws DBFException, IOException, SQLException {

        int resultado = 0;
        getProperties();

        logger.info("Iniciando WriterDbf....");

        DBFField fields[] = new DBFField[TOTAL];
        setFields(fields);

        DBFWriter writer = new DBFWriter();
        writer.setFields(fields);

        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yy");
        Connection conexion = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Establecemos la conexión con la base de datos. 
            prop.getProperty("pathConnectMysql");
            conexion = DriverManager.getConnection(prop.getProperty("pathConnectMysql"),
                    prop.getProperty("dbuser"), prop.getProperty("dbpassword"));
            logger.info("Conexion a bbdd EXITO");
            // Preparamos la consulta 
            Statement s = conexion.createStatement();
            //ResultSet rs = s.executeQuery("select * from clientes LIMIT 30");
            String sql = "SELECT DISTINCT fc.serie, fc.numero,  fc.fecha,"
                    + "replace(TRUNCATE(fc.baseimponible, 2),'.',',') BASEBAS,"
                    + "replace(TRUNCATE(fc.impuestos, 2),'.',',') IMPTBAS "
                    + "FROM    dafer2.facturas_clientes fc,"
                    + "        dafer2.estadosfacturasclientes efc "
                    + "WHERE efc.id = 1"
                    + "  AND DATE(fc.fecha) BETWEEN '2015/01/01' AND '2015/01/05'"
                    + "  ORDER BY fc.fecha DESC;";
            logger.info("Consulta SQL =" + sql);
            ResultSet rs = s.executeQuery(sql);
            // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
            while (rs.next()) {
                System.out.println(rs.getString(1) + " " + rs.getInt(2) + " " + dt1.format(rs.getDate(3)));
            }

            /**
             *
             */
            Object rowData[] = new Object[TOTAL];
            rowData[0] = "C";
            rowData[1] = new Date();
            rowData[2] = "30";
            writer.addRecord(rowData);

            rowData = new Object[TOTAL];
            rowData[0] = "D";
            rowData[1] = new Date();
            rowData[2] = "30";
            writer.addRecord(rowData);

            rowData = new Object[TOTAL];
            rowData[0] = "C";
            rowData[1] = new Date();
            rowData[2] = "30";
            writer.addRecord(rowData);
            
            /*
            Creacion de File TENCOM
            */
            SimpleDateFormat dt = new SimpleDateFormat("HHmm_ddMMyyyy");
            String nameFile = PATH_FILE + dt.format(new Date()) + ".dbf";
            File fileDbf = new File(nameFile);
            System.out.println("Fichero creado: " + nameFile);
            logger.info("Fichero creado: " + nameFile);
            FileOutputStream fos = new FileOutputStream(fileDbf);
            writer.write(fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            resultado = -1;
        } finally {
            // Cerramos la conexion a la base de datos. 
            conexion.close();
        }

        return resultado;
    }

    private static void getProperties() {

        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            if (Integer.valueOf(prop.getProperty("flagOS")) < 0) {
                PATH_FILE = prop.getProperty("pathFileLinux");
            } else {
                PATH_FILE = prop.getProperty("pathFileWin");
            }

            logger.info("PATH_FILE " + PATH_FILE);
            logger.info("Conectado a bbdd = " + prop.getProperty("pathConnectMysql"));

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

    private static void setFields(DBFField[] fields) {
        int i = 0;

        fields[i] = new DBFField();
        fields[i].setName("TIPREG");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(1);

        fields[++i] = new DBFField();
        fields[i].setName("DOCFEC");
        fields[i].setDataType(DBFField.FIELD_TYPE_D);

        fields[++i] = new DBFField();
        fields[i].setName("DOCSER");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(2);

        fields[++i] = new DBFField();
        fields[i].setName("DOCNUM");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(6);

        fields[++i] = new DBFField();
        fields[i].setName("CODTIP");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(2);

        fields[++i] = new DBFField();
        fields[i].setName("CODMOD");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(2);

        fields[++i] = new DBFField();
        fields[i].setName("CODTER");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(5);

        fields[++i] = new DBFField();
        fields[i].setName("CTACON");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(12);

        fields[++i] = new DBFField();
        fields[i].setName("BASEBAS");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("IMPTBAS");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("PORNOR");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(6);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("RECBAS");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("PORREC");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(6);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("PORTES");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(11);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("PORFIN");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(6);

        fields[++i] = new DBFField();
        fields[i].setName("RFDPP");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(11);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("DESHOR");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(11);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("DESKM");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(11);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("TOTFAC");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(14);

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO1");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(8);

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO1");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO2");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(8);

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO2");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO3");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(8);

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO3");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO4");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(8);

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO4");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO5");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(8);

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO5");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO6");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(8);

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO6");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);

        fields[++i] = new DBFField();
        fields[i].setName("DIETENT");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        fields[i].setDecimalCount(2);

        fields[++i] = new DBFField();
        fields[i].setName("CODFORPAG");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(3);

        fields[++i] = new DBFField();
        fields[i].setName("TIPFORPAG");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(1);
        System.out.println("Column total = " + i);
        logger.info("Column total = " + i);
    }
}
