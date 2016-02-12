package es.dafer.tercero.ma.main;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import es.dafer.tercero.ma.db.Connect;
import es.dafer.tercero.ma.utils.JDBFException;
import static es.dafer.tercero.ma.utils.JDBField.setFields;
import es.dafer.tercero.ma.utils.Utils;
import static es.dafer.tercero.ma.utils.Utils.setProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.JFrame;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtercero
 */
public class DBFWriterTest {

    private final static Logger logger = Logger.getLogger(DBFWriterTest.class);
    private static final int TOTAL = 34;
    private static List<String> LISTA_ID_UPDATE = new ArrayList<String>();

    static Properties prop = new Properties();
    static SimpleDateFormat DT = new SimpleDateFormat("yyyyMMdd");

    /**
     *
     * @param frame
     * @param args
     * @param logger
     * @return 0=OK ; -1=ERROR
     * @throws DBFException
     * @throws IOException
     * @throws SQLException
     * @throws es.dafer.tercero.ma.utils.JDBFException
     */
    public static int WriterDbf(JFrame frame, Date args[])
            throws DBFException, IOException, SQLException, JDBFException {

        int resultado = 0;

        setProperties();

        logger.info("Iniciando WriterDbf....");

        DBFField fields[] = new DBFField[TOTAL];
        setFields(fields);

        DBFWriter writer = new DBFWriter();
        writer.setFields(fields);

        String fechaDesde = DT.format(args[0]);
        String fechaHasta = DT.format(args[1]);
        Connection conexion = null;

        try {

            conexion = Connect.getConexion(prop);

            setCabecera(conexion, writer, fechaDesde, fechaHasta);

            setDetalle(conexion, writer, fechaDesde, fechaHasta);

//            Creacion de File TENCOM            
            String nameFile = Utils.getPATH_FILE() + DT.format(new Date()) + ".dbf";
            File fileDbf = new File(nameFile);
            logger.info("Fichero creado: {0} " + nameFile);

//            readDbf(fileDbf);
            FileOutputStream fos = new FileOutputStream(fileDbf);
            writer.write(fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception: {0} " + e.getMessage());
            resultado = -1;
        } finally {
            // Cerramos la conexion a la base de datos. 
            conexion.close();
        }

        return resultado;
    }

    private static void setDetalle(Connection conexion, DBFWriter writer, String fechaDesde, String fechaHasta) throws SQLException, DBFException, ParseException {
        Statement s = conexion.createStatement();
        String sql = getConsulta(fechaDesde, fechaHasta);
        logger.info("DETALLE Consulta SQL = {0} " + sql);
        ResultSet rs = s.executeQuery(sql);
        // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
        String auxFecha = "";
        Object rowData[] = new Object[TOTAL];
        int i, cnt = 0;
        while (rs.next()) {
            i = 0;
            rowData[i] = "D"; //TIPREG
            auxFecha = DT.format(rs.getDate("DOCFEC"));
            rowData[++i] = DT.parse(auxFecha);
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
        logger.info("DETALLE Total registros = {0} " + cnt);
    }

    private static void setCabecera(Connection conexion, DBFWriter writer, String fechaDesde, String fechaHasta) throws SQLException, DBFException, ParseException {
        Statement s = conexion.createStatement();
        String sql = getConsulta(fechaDesde, fechaHasta);
        ResultSet rs = s.executeQuery(sql);

        logger.info("CABECERA Consulta SQL = {0} " + sql);
        String auxFechaFactura, auxTOTFAC = "";
        List<String> ListIdsFactClientesUpdate = new ArrayList<String>();

        // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
        Object rowData[] = new Object[TOTAL];

        Integer i, k, cnt = 0;
        while (rs.next()) {
            i = 0;
            ListIdsFactClientesUpdate.add(rs.getString("ID_FACTURACLIENTE").trim());
            rowData[i] = "C"; //TIPREG
            auxFechaFactura = DT.format(rs.getDate("DOCFEC"));
            rowData[++i] = DT.parse(auxFechaFactura);
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

            rowData[++k] = null; //rs.getDouble("DIETENT");
            rowData[++k] = rs.getString("CODFORPAG").trim();
            rowData[++k] = rs.getString("TIPFORPAG").trim();

            writer.addRecord(rowData);
            rowData = new Object[TOTAL];
            cnt++;
        }
        setLISTA_ID_UPDATE(ListIdsFactClientesUpdate);
        logger.info("CABECERA Total registros = {0} " + cnt);
    }

    private static String getConsulta(String fechaDesde, String fechaHasta) {
        return "SELECT DISTINCT fc.id ID_FACTURACLIENTE, "
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
//                + " LIMIT 0 , 1000;";
    }

    /**
     *
     * @param rowData
     * @param i
     * @param auxFecha
     * @param auxTOTFAC
     * @param numeroVencimiento
     * @param diaEntreVencimiento
     * @return
     * @throws ParseException
     */
    private static Integer setFEC_IMPVTO(Object[] rowData, Integer i, String auxFecha, String auxTOTFAC, String numeroVencimiento, String diaEntreVencimiento) throws ParseException {
        int diasVencimiento = 0;
        float tempTOTFAC;
        numeroVencimiento = (numeroVencimiento == null ? "1" : numeroVencimiento);
        diasVencimiento = (diaEntreVencimiento == null ? 0 : Integer.parseInt(diaEntreVencimiento));

//        logger.info( "numeroVencimiento = {0} | diasVencimiento = {1}", new Object[]{numeroVencimiento, diasVencimiento});
        switch (Integer.parseInt(numeroVencimiento)) {
            case 1:
//                logger.info( "FECVTO1 = {0} | IMPVTO1 = {1}", new Object[]{auxFecha, auxTOTFAC});
                if (diasVencimiento <= 1) {
                    rowData[++i] = DT.parse(auxFecha);
                    rowData[++i] = auxTOTFAC;
                } else {
                    rowData[++i] = sumarDias(DT.parse(auxFecha), diasVencimiento);
                    rowData[++i] = auxTOTFAC;
                }
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
                // Vencimiento 1
                rowData[++i] = DT.parse(auxFecha);
                tempTOTFAC = Float.parseFloat(auxTOTFAC.replace(',', '.'));
                rowData[++i] = String.valueOf(tempTOTFAC / 2).replace('.', ',');

                // Vencimiento 2
                rowData[++i] = sumarDias(DT.parse(auxFecha), diasVencimiento);
                rowData[++i] = String.valueOf(tempTOTFAC / 2).replace('.', ',');

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
                // Vencimiento 1
                rowData[++i] = DT.parse(auxFecha);
                tempTOTFAC = Float.parseFloat(auxTOTFAC.replace(',', '.'));
                rowData[++i] = String.valueOf(tempTOTFAC / 3).replace('.', ',');

                // Vencimiento 2
                Date auxFecVto = sumarDias(DT.parse(auxFecha), diasVencimiento);
                rowData[++i] = auxFecVto;
                rowData[++i] = String.valueOf(tempTOTFAC / 3).replace('.', ',');

                // Vencimiento 3
                rowData[++i] = sumarDias(auxFecVto, diasVencimiento);
                rowData[++i] = String.valueOf(tempTOTFAC / 3).replace('.', ',');

                rowData[++i] = null; //rs.getString("FECVTO4");
                rowData[++i] = null; //rs.getInt("IMPVTO4");
                rowData[++i] = null; //rs.getString("FECVTO5");
                rowData[++i] = null; //rs.getInt("IMPVTO5");
                rowData[++i] = null; //rs.getString("FECVTO6");
                rowData[++i] = null; //rs.getInt("IMPVTO6");
                break;
            default:
                break;
        }
        return i;
    }

    /**
     *
     * @param fecha
     * @param dias a sumar a la fecha
     * @return
     * @throws ParseException
     */
    private static Date sumarDias(Date fecha, int dias) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.add(Calendar.DATE, dias);
        return DT.parse(DT.format(c.getTime()));
    }

   

    private static String getLista_Ids() {
        return "( " + StringUtils.join(LISTA_ID_UPDATE, ',') + " )";
    }

    /**
     * @return the LISTA_ID_UPDATE
     */
    public static List<String> getLISTA_ID_UPDATE() {
        return LISTA_ID_UPDATE;
    }

    /**
     * @param aLISTA_ID_UPDATE the LISTA_ID_UPDATE to set
     */
    public static void setLISTA_ID_UPDATE(List<String> aLISTA_ID_UPDATE) {
        LISTA_ID_UPDATE = aLISTA_ID_UPDATE;
    }

} //CLASS

//TODO.- Una vez creado el .dbf intentar los typedata de BASEBAS
//    private static void readDbf(File fileDbf) {
//        try {
//
//            // create a DBFReader object
//            //
//            InputStream inputStream = new FileInputStream(fileDbf); // take dbf file as program argument
//            DBFReader reader = new DBFReader(inputStream);
//
//            // get the field count if you want for some reasons like the following
//            //
//            int numberOfFields = reader.getFieldCount();
//
//            // use this count to fetch all field information
//            // if required
//            //
//            for (int i = 0; i < numberOfFields; i++) {
//
//                DBFField field = reader.getField(i);
//
//                // do something with it if you want
//                // refer the JavaDoc API reference for more details
//                //
//                System.out.println(field.getName());
//                if (field.getName().equalsIgnoreCase("BASEBAS")) {
//                    field.setDataType(DBFField.FIELD_TYPE_C);
//                    field.setFieldLength(10);
//                    field.setDecimalCount(2);
//                }
//            }
//
//            // Now, lets us start reading the rows
//            Object[] rowObjects;
//
//            while ((rowObjects = reader.nextRecord()) != null) {
//
//                for (int i = 0; i < rowObjects.length; i++) {
//
//                    System.out.println(rowObjects[i]);
//                }
//            }
//
//            // By now, we have itereated through all of the rows
//            inputStream.close();
//        } catch (DBFException e) {
//            System.out.println(e.getMessage());
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
