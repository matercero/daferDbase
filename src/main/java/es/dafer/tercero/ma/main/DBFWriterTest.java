package es.dafer.tercero.ma.main;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import es.dafer.tercero.ma.db.Connect;
import static es.dafer.tercero.ma.main.Principal.logger;
import es.dafer.tercero.ma.utils.JDBFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JTextField;

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

    /**
     *
     * @param frame
     * @param args
     * @param box
     * @param logger
     * @return 0=OK ; -1=ERROR
     * @throws DBFException
     * @throws IOException
     * @throws SQLException
     * @throws es.dafer.tercero.ma.utils.JDBFException
     */
    public static int WriterDbf(JFrame frame, Date args[], Logger logger)
            throws DBFException, IOException, SQLException, JDBFException {

        int resultado = 0;
        setProperties();

        logger.info("Iniciando WriterDbf....");

        DBFField fields[] = new DBFField[TOTAL];
        setFields(fields);

        DBFWriter writer = new DBFWriter();
        writer.setFields(fields);

        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd");
        String fechaDesde = dt1.format(args[0]);
        String fechaHasta = dt1.format(args[1]);
        Connection conexion = null;

        try {

            conexion = Connect.getConexion(prop, logger);

            setCabecera(conexion, writer, fechaDesde, fechaHasta);

            setDetalle(conexion, writer, fechaDesde, fechaHasta);

            /*            Creacion de File TENCOM             */
            SimpleDateFormat dt = new SimpleDateFormat("ddMMyyyy_HHmm");
            String nameFile = PATH_FILE + dt.format(new Date()) + ".dbf";
            File fileDbf = new File(nameFile);
            logger.log(Level.INFO, "Fichero creado: {0}", nameFile);

            FileOutputStream fos = new FileOutputStream(fileDbf);
            writer.write(fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.INFO, "ERROR: {0}", e.getMessage());
            resultado = -1;
        } finally {
            // Cerramos la conexion a la base de datos. 
            conexion.close();
        }

        return resultado;
    }

    private static void setProperties() {

        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");
            prop.load(input);

            // get the property value and print it out
            if (Integer.valueOf(prop.getProperty("flagOS")) < 0) {
                PATH_FILE = prop.getProperty("pathFileLinux");
            } else {
                PATH_FILE = prop.getProperty("pathFileWin");
            }

            logger.log(Level.CONFIG, "PATH_FILE {0}", PATH_FILE);
            logger.log(Level.CONFIG, "Conectado a bbdd = {0}", prop.getProperty("pathConnectMysql"));

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

    private static void setFields(DBFField[] fields) throws UnsupportedEncodingException {
        logger.info("Creacion fichero .dbf.");

        /* 
        TIPREG,C,1	DOCFEC,D	
        DOCSER,C,2	DOCNUM,C,6	
        CODTIP,C,2	CODMOD,C,2	
        CODTER,C,5	CTACON,C,12	
        BASEBAS,N,10,2	IMPTBAS,N,10,2	
        PORNOR,N,6,2	RECBAS,N,10,2	
        PORREC,N,6,2	PORTES,N,11,2	
        PORFIN,N,6,2	RFDPP,N,11,2	
        DESHOR,N,11,2	DESKM,N,11,2	
        TOTFAC,N,14,2	
        FECVTO1,D	IMPVTO1,N,10,2	
        FECVTO2,D	IMPVTO2,N,10,2	
        FECVTO3,D	IMPVTO3,N,10,2	
        FECVTO4,D	IMPVTO4,N,10,2	
        FECVTO5,D	IMPVTO5,N,10,2	
        FECVTO6,D	IMPVTO6,N,10,2	
        DIETENT,N,10,2	
        CODFORPAG,C,3	TIPFORPAG,C,1								
         */
        int i = 0;

        fields[i] = new DBFField();
        fields[i].setName("TIPREG");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(1);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("DOCFEC");
        fields[i].setDataType(DBFField.FIELD_TYPE_D);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("DOCSER");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("DOCNUM");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(6);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("CODTIP");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("CODMOD");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("CODTER");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(5);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("CTACON");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(12);
        logger.info("Campo " + i + "-" + fields[i].getName() + " - DataType : " + getFieldDataType(fields[i].getDataType()));

        fields[++i] = new DBFField();
        fields[i].setName("BASEBAS");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);
//        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("IMPTBAS");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);
//        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("PORNOR");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(6);
        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("RECBAS");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("PORREC");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(6);
        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("PORTES");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(11);
        fields[i].setDecimalCount(2);
        logger.info("Campo " + i + "-" + fields[i].getName() + " - DataType : " + getFieldDataType(fields[i].getDataType()));

        fields[++i] = new DBFField();
        fields[i].setName("PORFIN");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(6);
        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("RFDPP");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(11);
        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("DESHOR");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(11);
        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("DESKM");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(11);
        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("TOTFAC");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(14);
        logger.info("Campo " + i + "-" + fields[i].getName() + " - DataType : " + getFieldDataType(fields[i].getDataType()));

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO1");
        fields[i].setDataType(DBFField.FIELD_TYPE_D);
//        fields[i].setFieldLength(8);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO1");
        //fields[i].setDataType(DBFField.FIELD_TYPE_N);
        // Es numerico pero para insertar valor "XXX,YY" 
        //hay que meterlo como string y despues cambiarlo en dbfmanager -> Structura
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(10);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO2");
        fields[i].setDataType(DBFField.FIELD_TYPE_D);
//        fields[i].setFieldLength(8);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO2");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO3");
        fields[i].setDataType(DBFField.FIELD_TYPE_D);
//        fields[i].setFieldLength(8);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO3");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO4");
        fields[i].setDataType(DBFField.FIELD_TYPE_D);
//        fields[i].setFieldLength(8);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO4");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO5");
        fields[i].setDataType(DBFField.FIELD_TYPE_D);
//        fields[i].setFieldLength(8);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO5");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("FECVTO6");
        fields[i].setDataType(DBFField.FIELD_TYPE_D);
//        fields[i].setFieldLength(8);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("IMPVTO6");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("DIETENT");
        fields[i].setDataType(DBFField.FIELD_TYPE_N);
        fields[i].setFieldLength(10);
        fields[i].setDecimalCount(2);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("CODFORPAG");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(3);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        fields[++i] = new DBFField();
        fields[i].setName("TIPFORPAG");
        fields[i].setDataType(DBFField.FIELD_TYPE_C);
        fields[i].setFieldLength(1);
        logger.log(Level.INFO, "Campo {0}-{1} - DataType : {2}", new Object[]{i, fields[i].getName(), getFieldDataType(fields[i].getDataType())});

        logger.log(Level.INFO, "Total columnas en fichero .dbf = {0}", i);
    }

    private static void setDetalle(Connection conexion, DBFWriter writer, String fechaDesde, String fechaHasta) throws SQLException, DBFException, ParseException {
        Statement s = conexion.createStatement();
        String sql = getConsulta(fechaDesde, fechaHasta);
        logger.log(Level.INFO, "DETALLE Consulta SQL = {0}", sql);
        ResultSet rs = s.executeQuery(sql);
        // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
        String auxFecha = "";
        Object rowData[] = new Object[TOTAL];
        int i, cnt = 0;
        while (rs.next()) {
            i = 0;
//                System.out.println(dt1.format(rs.getDate(1)) + " " + rs.getString(2) + " " + rs.getString(3)
//                        + " " + rs.getString("CODMOD") + " " + rs.getDouble("BASEBAS") + " " + rs.getString("IMPTBAS"));
            rowData[i] = "D"; //TIPREG
            auxFecha = dt.format(rs.getDate("DOCFEC"));
            rowData[++i] = dt.parse(auxFecha);
            rowData[++i] = rs.getString("DOCSER");
            rowData[++i] = rs.getString("DOCNUM");
            rowData[++i] = "IV"; //CODTIP
            rowData[++i] = rs.getString("CODMOD");
            rowData[++i] = rs.getString("CODTER");//CODTER
            rowData[++i] = null; //CTACON
            rowData[++i] = rs.getString("BASEBAS");
            rowData[++i] = rs.getString("IMPTBAS");
            rowData[++i] = rs.getDouble("PORNOR");
            rowData[++i] = rs.getDouble("RECBAS");
            rowData[++i] = rs.getDouble("PORREC");
            rowData[++i] = rs.getDouble("PORTES");
            rowData[++i] = null; // rs.getDouble("PORFIN");
            rowData[++i] = rs.getDouble("RFDPP");
            rowData[++i] = rs.getDouble("DESHOR");
            rowData[++i] = rs.getDouble("DESKM");
            rowData[++i] = null; //rs.getString("TOTFAC");
            rowData[++i] = null; //rs.getString("FECVTO1");
            rowData[++i] = null; //rs.getString("IMPVTO1");
            rowData[++i] = null; //rs.getString("FECVTO2");
            rowData[++i] = null; //rs.getString("IMPVTO2");
            rowData[++i] = null; //rs.getString("FECVTO3");
            rowData[++i] = null; //rs.getString("IMPVTO3");
            rowData[++i] = null; //rs.getString("FECVTO4");
            rowData[++i] = null; //rs.getString("IMPVTO4");
            rowData[++i] = null; //rs.getString("FECVTO5");
            rowData[++i] = null; //rs.getString("IMPVTO5");
            rowData[++i] = null; //rs.getString("FECVTO6");
            rowData[++i] = null; //rs.getString("IMPVTO6");
            rowData[++i] = rs.getDouble("DIETENT");
            rowData[++i] = rs.getString("CODFORPAG");
            rowData[++i] = rs.getString("TIPFORPAG");

            writer.addRecord(rowData);
            rowData = new Object[TOTAL];
            cnt++;
        }
        logger.log(Level.INFO, "DETALLE Total registros = {0}", cnt);
    }

    private static void setCabecera(Connection conexion, DBFWriter writer, String fechaDesde, String fechaHasta) throws SQLException, DBFException, ParseException {
        Statement s = conexion.createStatement();
        String sql = getConsulta(fechaDesde, fechaHasta);

        logger.log(Level.INFO, "CABECERA Consulta SQL = {0}", sql);
        ResultSet rs = s.executeQuery(sql);
        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
        String auxFecha, auxTOTFAC = "";
        // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
        Object rowData[] = new Object[TOTAL];

        int i, cnt = 0;
        while (rs.next()) {
            i = 0;
            rowData[i] = "C"; //TIPREG
            auxFecha = dt.format(rs.getDate("DOCFEC"));
            rowData[++i] = dt.parse(auxFecha);
            rowData[++i] = rs.getString("DOCSER");
            rowData[++i] = rs.getString("DOCNUM");
            rowData[++i] = "IV"; //CODTIP
            rowData[++i] = rs.getString("CODMOD");
            rowData[++i] = rs.getString("CODTER"); //CODTER
            rowData[++i] = rs.getString("CTACON");
            rowData[++i] = rs.getString("BASEBAS");
            rowData[++i] = rs.getString("IMPTBAS");
            rowData[++i] = null; //PORNOR vacio
            rowData[++i] = rs.getDouble("RECBAS");
            rowData[++i] = null; //PORREC vacio
            rowData[++i] = null; //PORTES vacio
            rowData[++i] = rs.getDouble("PORFIN");
            rowData[++i] = null; //RFDPP vacio
            rowData[++i] = null; //DESHOR vacio
            rowData[++i] = rs.getDouble("DESKM");
            auxTOTFAC = rs.getString("TOTFAC");
            rowData[++i] = auxTOTFAC;
            rowData[++i] = dt.parse(getFECVTO(dt.parse(auxFecha), rs.getString("numero_vencimientos"), rs.getString("dias_entre_vencimiento")));
            rowData[++i] = getIMPVTO(auxTOTFAC, rs.getString("numero_vencimientos"), rs.getString("dias_entre_vencimiento"));
            rowData[++i] = null; //rs.getString("FECVTO2");
            rowData[++i] = null; //rs.getInt("IMPVTO2");
            rowData[++i] = null; //rs.getString("FECVTO3");
            rowData[++i] = null; //rs.getInt("IMPVTO3");
            rowData[++i] = null; //rs.getString("FECVTO4");
            rowData[++i] = null; //rs.getInt("IMPVTO4");
            rowData[++i] = null; //rs.getString("FECVTO5");
            rowData[++i] = null; //rs.getInt("IMPVTO5");
            rowData[++i] = null; //rs.getString("FECVTO6");
            rowData[++i] = null; //rs.getInt("IMPVTO6");
            rowData[++i] = null; //rs.getDouble("DIETENT");
            rowData[++i] = rs.getString("CODFORPAG");
            rowData[++i] = rs.getString("TIPFORPAG");

            writer.addRecord(rowData);
            rowData = new Object[TOTAL];
            cnt++;
        }
        logger.log(Level.INFO, "CABECERA Total registros = {0}", cnt);

    }

    private static String getConsulta(String fechaDesde, String fechaHasta) {
        return "SELECT DISTINCT "
                + "fc.fecha DOCFEC, " //1
                + "fc.serie DOCSER, " //2
                + "LPAD(fc.numero ,6,'0') DOCNUM, " //3
                + "cc.codigo CTACON, "
                + "c.codcli CODTER, "
                + "replace(TRUNCATE(fc.baseimponible, 2),'.',',') BASEBAS, " //4
                + "replace(TRUNCATE(fc.impuestos, 2),'.',',') IMPTBAS, " // 5
                + "ac.tiposiva_id CODMOD, " //6
                + "replace(TRUNCATE(fc.total, 2),'.',',') AS TOTFAC, "
                + " '' PORNOR, '' RECBAS, '' PORREC, '' PORTES, '' PORFIN, '' RFDPP, '' DESHOR, "
                + " '' DESKM, '' TOTFAC, "
                + " '' DIETENT, '' CODFORPAG, '' TIPFORPAG,"
                + " fp.numero_vencimientos, fp.dias_entre_vencimiento "
                + "FROM dafer2.facturas_clientes fc"
                + "   , dafer2.estadosfacturasclientes efc"
                + "   , dafer2.albaranesclientes ac"
                + "   , dafer2.cuentascontables cc"
                + "   , dafer2.clientes c"
                + "   , dafer2.formapagos fp "
                + "WHERE efc.id = 1"
                + " AND ac.facturas_cliente_id = fc.id "
                + " AND c.id = fc.cliente_id "
                + " AND c.cuentascontable_id = cc.id "
                + " AND fp.cliente_id = c.id "
                + " AND DATE(fc.fecha) BETWEEN '" + fechaDesde + "' AND '" + fechaHasta + "'"
                + " ORDER BY fc.numero DESC;";
    }

    private static String getFieldDataType(byte dataType) throws UnsupportedEncodingException {
        byte[] byteArray = new byte[]{dataType};
        return new String(byteArray, "UTF-8");
    }

    private static String getFECVTO(Date auxFecha, String numeroVencimiento, String diaEntreVencimiento) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
        Date fecha = null;
        switch (Integer.parseInt(numeroVencimiento)) {
            case 1:
                fecha = auxFecha;
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
        return dt.format(fecha);
    }

    private static String getIMPVTO(String auxTOTFAC, String numeroVencimiento, String diaEntreVencimiento) {
        String impvto = null;
        switch (Integer.parseInt(numeroVencimiento)) {
            case 1:
                impvto = auxTOTFAC;
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
        return impvto;
    }
}
