package es.dafer.tercero.ma.main;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import es.dafer.tercero.ma.db.Connect;
import static es.dafer.tercero.ma.main.Principal.logger;
import es.dafer.tercero.ma.utils.JDBFException;
import static es.dafer.tercero.ma.utils.JDBField.setFields;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

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
    static SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");

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
        setFields(fields, logger);

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
            logger.log(Level.INFO, "Exception: {0}", e.getMessage());
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

    private static void setDetalle(Connection conexion, DBFWriter writer, String fechaDesde, String fechaHasta) throws SQLException, DBFException, ParseException {
        Statement s = conexion.createStatement();
        String sql = getConsulta(fechaDesde, fechaHasta);
        logger.log(Level.INFO, "DETALLE Consulta SQL = {0}", sql);
        ResultSet rs = s.executeQuery(sql);
        // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
        String auxFecha = "";
        Object rowData[] = new Object[TOTAL];
        int i, cnt = 0;
        while (rs.next()) {
            i = 0;
            rowData[i] = "D"; //TIPREG
            auxFecha = dt.format(rs.getDate("DOCFEC"));
            rowData[++i] = dt.parse(auxFecha);
            rowData[++i] = rs.getString("DOCSER").trim();
            rowData[++i] = rs.getString("DOCNUM").trim();
            rowData[++i] = "IV"; //CODTIP
            rowData[++i] = rs.getString("CODMOD").trim();
            rowData[++i] = rs.getString("CODTER").trim();
            rowData[++i] = null; //CTACON
            rowData[++i] = rs.getString("BASEBAS").trim();
            rowData[++i] = rs.getString("IMPTBAS").trim();
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
            rowData[++i] = rs.getString("CODFORPAG").trim();
            rowData[++i] = rs.getString("TIPFORPAG").trim();

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

        String auxFechaFactura, auxTOTFAC = "";
        // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
        Object rowData[] = new Object[TOTAL];

        Integer i, k, cnt = 0;
        while (rs.next()) {
            i = 0;
            rowData[i] = "C"; //TIPREG
            auxFechaFactura = dt.format(rs.getDate("DOCFEC"));
            rowData[++i] = dt.parse(auxFechaFactura);
            rowData[++i] = rs.getString("DOCSER").trim();
            rowData[++i] = rs.getString("DOCNUM").trim();
            rowData[++i] = "IV"; //CODTIP
            rowData[++i] = rs.getString("CODMOD").trim();
            rowData[++i] = rs.getString("CODTER").trim();
            rowData[++i] = rs.getString("CTACON").trim();
            rowData[++i] = rs.getString("BASEBAS").trim();
            rowData[++i] = rs.getString("IMPTBAS").trim();
            rowData[++i] = null; //PORNOR vacio
            rowData[++i] = rs.getDouble("RECBAS");
            rowData[++i] = null; //PORREC vacio
            rowData[++i] = null; //PORTES vacio
            rowData[++i] = rs.getDouble("PORFIN");
            rowData[++i] = null; //RFDPP vacio
            rowData[++i] = null; //DESHOR vacio
            rowData[++i] = rs.getDouble("DESKM");
            auxTOTFAC = rs.getString("TOTFAC").trim();
            rowData[++i] = auxTOTFAC;
            // FECHA E IMPUESTOS VENCIMIENTOS
            k = setFEC_IMPVTO(rowData, i, auxFechaFactura, auxTOTFAC, rs.getString("numero_vencimientos"), rs.getString("dias_entre_vencimiento"));
//            rowData[++i] = dt.parse(getFECVTO(dt.parse(auxFecha), rs.getString("numero_vencimientos"), rs.getString("dias_entre_vencimiento")));
//            rowData[++i] = getIMPVTO(auxTOTFAC, rs.getString("numero_vencimientos"), rs.getString("dias_entre_vencimiento"));
//            rowData[++i] = null; //rs.getString("FECVTO2");
//            rowData[++i] = null; //rs.getInt("IMPVTO2");
//            rowData[++i] = null; //rs.getString("FECVTO3");
//            rowData[++i] = null; //rs.getInt("IMPVTO3");
//            rowData[++i] = null; //rs.getString("FECVTO4");
//            rowData[++i] = null; //rs.getInt("IMPVTO4");
//            rowData[++i] = null; //rs.getString("FECVTO5");
//            rowData[++i] = null; //rs.getInt("IMPVTO5");
//            rowData[++i] = null; //rs.getString("FECVTO6");
//            rowData[++i] = null; //rs.getInt("IMPVTO6");

            rowData[++k] = null; //rs.getDouble("DIETENT");
            rowData[++k] = rs.getString("CODFORPAG").trim();
            rowData[++k] = rs.getString("TIPFORPAG").trim();

            writer.addRecord(rowData);
            rowData = new Object[TOTAL];
            cnt++;
        }
        logger.log(Level.INFO, "CABECERA Total registros = {0}", cnt);

    }

    private static String getConsulta(String fechaDesde, String fechaHasta) {
        return "SELECT DISTINCT "
                + "fc.fecha DOCFEC, "
                + "fc.serie DOCSER, "
                + "LPAD(fc.numero ,6,'0') DOCNUM, "
                + "cc.codigo CTACON, "
                + "c.codcli CODTER, "
                + "replace(TRUNCATE(fc.baseimponible, 2),'.',',') BASEBAS, "
                + "replace(TRUNCATE(fc.impuestos, 2),'.',',') IMPTBAS, "
                + "ac.tiposiva_id CODMOD, "
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

    private static String getFECVTO(Date auxFecha, String numeroVencimiento, String diaEntreVencimiento) {
        Date fecha = null;
        switch (Integer.parseInt(numeroVencimiento)) {
            case 1:
                if (diaEntreVencimiento != null && diaEntreVencimiento.equalsIgnoreCase("1")) {
                    fecha = auxFecha;
                } else if (diaEntreVencimiento != null && diaEntreVencimiento.equalsIgnoreCase("2")) {
                    fecha = auxFecha;
                }
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

    private static Integer setFEC_IMPVTO(Object[] rowData, Integer i, String auxFecha, String auxTOTFAC, String numeroVencimiento, String diaEntreVencimiento) throws ParseException {
        if (numeroVencimiento != null) {
            switch (Integer.parseInt(numeroVencimiento)) {
                case 1:
                    rowData[++i] = dt.parse(auxFecha);
                    rowData[++i] = auxTOTFAC;
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
                    break;
                case 2:
                    rowData[++i] = dt.parse(auxFecha);
                    float auxTOTFAC2 = Float.parseFloat(auxTOTFAC);
                    rowData[++i] = auxTOTFAC2/2;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dt.parse(auxFecha)); // Configuramos la fecha que se recibe
                    calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(diaEntreVencimiento));
                    
                    calendar.getTime();
                    
                    rowData[++i] = null; 
                    rowData[++i] = String.valueOf(auxTOTFAC2/2);
                    rowData[++i] = null; //rs.getString("FECVTO3");
                    rowData[++i] = null; //rs.getInt("IMPVTO3");
                    rowData[++i] = null; //rs.getString("FECVTO4");
                    rowData[++i] = null; //rs.getInt("IMPVTO4");
                    rowData[++i] = null; //rs.getString("FECVTO5");
                    rowData[++i] = null; //rs.getInt("IMPVTO5");
                    rowData[++i] = null; //rs.getString("FECVTO6");
                    rowData[++i] = null; //rs.getInt("IMPVTO6");
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        } // IF
        return i;
    }

}
