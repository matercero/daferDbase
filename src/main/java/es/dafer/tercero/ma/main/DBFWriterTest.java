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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
    private static final int TOTAL = 36;
    private static List<String> LISTA_ID_UPDATE = new ArrayList<String>();
    private static final String TB_ALBARANESCLIENTES = "ALBARANESCLIENTES";
    private static final String TB_ALBARANESCLIENTESREPARACIONES = "ALBARANESCLIENTESREPARACIONES";
    private static final String CAMPO_ALBARANESCLIENTES = "PRECIO";
    private static final String CAMPO_ALBARANESCLIENTESREPARACIONES = "BASEIMPONIBLE";

    private static final String TIPREG = "TIPREG";
    private static final String DOCFEC = "DOCFEC";
    private static final String DOCSER = "DOCSER";
    private static final String DOCNUM = "DOCNUM";
    private static final String CODTIP = "CODTIP";
    private static final String CODMOD = "CODMOD";
    private static final String CODTER = "CODTER";
    private static final String CTACON = "CTACON";
    private static final String BASEBAS = "BASEBAS";

    static Properties prop = new Properties();
    static SimpleDateFormat DT = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat DT_FILE = new SimpleDateFormat("yyyyMMdd_HHmm");

    /**
     *
     * @param frame
     * @param args parametros de fechaDesde y fechaHasta
     * @param mapArgumentos object que recoge variables para mostrar resumen
     * final
     * @return 0=OK ; -1=ERROR
     * @throws DBFException
     * @throws IOException
     * @throws SQLException
     * @throws es.dafer.tercero.ma.utils.JDBFException
     */
    public static int WriterDbf(JFrame frame, Date args[], Map<String, String> mapArgumentos)
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

            setCabecera(conexion, writer, fechaDesde, fechaHasta, mapArgumentos);

            setDetalle(conexion, writer, fechaDesde, fechaHasta, mapArgumentos);

//            Creacion de File TENCOM            
            String nameFile = Utils.getPATH_FILE() + DT_FILE.format(new Date()) + ".dbf";
            File fileDbf = new File(nameFile);
            logger.info("Fichero creado: " + nameFile);
            mapArgumentos.put("FILEPATH", fileDbf.getAbsolutePath());
            //TODO
// La idea es. Una vez cargado el fichero, leerlo para cambiar el tipo de fichero de String --> Numeric
//            readDbf(fileDbf);
            FileOutputStream fos = new FileOutputStream(fileDbf);
            writer.write(fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception: " + e.getMessage());
            resultado = -1;
        } finally {
            // Cerramos la conexion a la base de datos. 
            conexion.close();
        }

        return resultado;
    }

    private static void setDetalle(Connection conexion, DBFWriter writer, String fechaDesde, String fechaHasta,
            Map<String, String> mapArgumentos) throws SQLException, DBFException, ParseException {
        Statement s = conexion.createStatement();
        String sql = getConsulta(fechaDesde, fechaHasta);
        logger.info("DETALLE Consulta SQL = " + sql);

        // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
        Map<String, String> mapAlbaranesClientes;
        Map<String, String> mapAlbaranesClientesReparaciones;
        Map<String, String> mapRowData;
        String auxFecha, idFacturaCliente = "";
        Integer i, cnt = 0;
        boolean masDeUnCentroCoste = false;
        boolean isReparacion = false;

        Object rowData[] = new Object[TOTAL];
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            i = 0;
            mapRowData = new HashMap<String, String>();
            idFacturaCliente = rs.getString("ID_FACTURACLIENTE").trim();
            mapRowData.put(TIPREG, "D");
            rowData[i] = "D"; //TIPREG
            auxFecha = DT.format(rs.getDate("DOCFEC"));
            mapRowData.put(DOCFEC, auxFecha);
            rowData[++i] = DT.parse(auxFecha);
            mapRowData.put(DOCSER, rs.getString("DOCSER").trim());
            rowData[++i] = rs.getString("DOCSER").trim();
            mapRowData.put(DOCNUM, rs.getString("DOCNUM").trim());
            rowData[++i] = rs.getString("DOCNUM").trim();
            mapRowData.put(CODTIP, "IV");
            rowData[++i] = "IV"; //CODTIP
            mapRowData.put(CODMOD, rs.getString("CODMOD").trim());
            rowData[++i] = rs.getString("CODMOD").trim();
            mapRowData.put(CODTER, rs.getString("CODTER").trim());
            rowData[++i] = rs.getString("CODTER").trim();
            mapRowData.put(CTACON, rs.getString("CTACON").trim());
            rowData[++i] = null; //CTACON
            mapRowData.put(BASEBAS, rs.getString("BASEBAS").trim());
            rowData[++i] = rs.getString("BASEBAS").trim();
            rowData[++i] = rs.getString("IMPTBAS").trim();
            rowData[++i] = rs.getDouble("PORNOR");
            rowData[++i] = rs.getDouble("RECBAS");
            rowData[++i] = rs.getDouble("PORREC");
            rowData[++i] = rs.getDouble("PORTES");
            rowData[++i] = null; // rs.getDouble("PORFIN");
            rowData[++i] = rs.getDouble("RFDPP");
            rowData[++i] = rs.getDouble("DESHOR");
            rowData[++i] = null; //rs.getDouble("DESKM");
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

            mapAlbaranesClientes = getAlbaranesClientes(conexion, idFacturaCliente,
                    TB_ALBARANESCLIENTES, CAMPO_ALBARANESCLIENTES);
            mapAlbaranesClientesReparaciones = getAlbaranesClientes(conexion, idFacturaCliente,
                    TB_ALBARANESCLIENTESREPARACIONES, CAMPO_ALBARANESCLIENTESREPARACIONES);

            if (mapAlbaranesClientes != null && mapAlbaranesClientes.size() > 0) {
                masDeUnCentroCoste = mapAlbaranesClientes.size() > 1;
                logger.info("DETALLE masDeUnCentroCoste=  " + masDeUnCentroCoste);
                // Inserto el primer albaran en la linea Detalle actual
                // Si hubiera otro CentrodeCostes se add despues 
                //    en una nueva linea con los campos 
                // TIPREG='D' DOCFEC=auxFecha DOCSER DOCNUM CODTIP='IV' CODMOD CODTER CTACON BASEBAS
                // masDeUnCentroCoste = TRUE
                for (Map.Entry<String, String> entry : mapAlbaranesClientes.entrySet()) {
                    rowData[++i] = entry.getValue().trim(); //BASEIMPCC _C
                    rowData[++i] = Double.parseDouble(entry.getKey().trim()); //CC _N
                    break;
                }
            } else {
                //Para el caso que no tuviera albaran relacionado con la factura.
                rowData[++i] = null; //BASEIMPCC _C
                rowData[++i] = null; //CC _N
            }

            writer.addRecord(rowData);
            rowData = new Object[TOTAL];
            cnt++;

            if (masDeUnCentroCoste) {
                //Nuevo registro Detalle rowData
                logger.info("DETALLE mapAlbaranesClientesReparaciones =  " + isReparacion);
                insertarNuevoDetalleAlbaran(rowData, mapRowData, mapAlbaranesClientes, isReparacion);
                writer.addRecord(rowData);
                rowData = new Object[TOTAL];
                cnt++;

                if (mapAlbaranesClientesReparaciones != null && mapAlbaranesClientesReparaciones.size() > 0) {
                     logger.info("DETALLE mapAlbaranesClientesReparaciones =  " + isReparacion);
                    isReparacion = true;
                    insertarNuevoDetalleAlbaran(rowData, mapRowData, mapAlbaranesClientesReparaciones, isReparacion);
                    isReparacion = false;
                    writer.addRecord(rowData);
                    rowData = new Object[TOTAL];
                    cnt++;
                }
                masDeUnCentroCoste = false;
            }
            mapRowData = null;
        } //while 
        mapArgumentos.put("REGDET", cnt.toString());
        logger.info("DETALLE Total registros =  " + cnt);
    }

    private static void setCabecera(Connection conexion, DBFWriter writer, String fechaDesde, String fechaHasta,
            Map<String, String> mapArgumentos) throws SQLException, DBFException, ParseException {
        Statement s = conexion.createStatement();
        String sql = getConsulta(fechaDesde, fechaHasta);
        ResultSet rs = s.executeQuery(sql);

        logger.info("CABECERA Consulta SQL =  " + sql);
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
            rowData[++i] = null; //rs.getDouble("DESKM");
            auxTOTFAC = rs.getString("TOTFAC").trim();
            rowData[++i] = auxTOTFAC;
            // FECHA E IMPUESTOS VENCIMIENTOS
            k = setFEC_IMPVTO(rowData, i, auxFechaFactura, auxTOTFAC, rs.getString("numero_vencimientos"), rs.getString("dias_entre_vencimiento"));

            rowData[++k] = null; //rs.getDouble("DIETENT");
            rowData[++k] = rs.getString("CODFORPAG").trim();
            rowData[++k] = rs.getString("TIPFORPAG").trim();

            rowData[++k] = null; //BASEIMPCC
            rowData[++k] = null; //CC

            writer.addRecord(rowData);
            rowData = new Object[TOTAL];
            cnt++;
        }
        mapArgumentos.put("REGCAB", cnt.toString());
        setLISTA_ID_UPDATE(ListIdsFactClientesUpdate);
        logger.info("CABECERA Total registros = " + cnt);
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

    //private static MapMap getAlbaranesClientes(Connection conexion, String idFactura) throws SQLException {
    /**
     *
     * @param conexion
     * @param idFactura
     * @param tabla a cruzar
     * @param campo a sumar
     * @return Un mapa<k,v> donde k = centrodecoste ; v = Baseimponible
     * @throws SQLException
     */
    private static Map getAlbaranesClientes(Connection conexion, String idFactura, String tabla, String campo) throws SQLException {
        Statement s = conexion.createStatement();
        Map<String, String> mm = new HashMap<String, String>();
        String sql = " SELECT ac.facturas_cliente_id idFactura, "
                + " replace(TRUNCATE(SUM(ac." + campo + "), 2),'.',',') BASEIMPCC, cc.codigo CC "
                + " FROM " + tabla + " ac, centrosdecostes cc "
                + " WHERE facturas_cliente_id = '" + idFactura + "'"
                + " AND ac.centrosdecoste_id = cc.id "
                + " GROUP BY centrosdecoste_id ";
        logger.info(sql.toString());
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            logger.info("idFact=" + rs.getString("idFactura") + " BASEIMPCC= " + rs.getString("BASEIMPCC") + " CC= " + rs.getString("CC"));
            mm.put(rs.getString("CC"), rs.getString("BASEIMPCC"));
        }
        logger.info(">> " + tabla + " Size =  " + mm.size());
        return mm;
    }

    private static void insertarNuevoDetalleAlbaran(Object[] rowData, Map<String, String> mapRowData,
            Map<String, String> mapAlbaranesClientes1, boolean isReparacion) throws ParseException {
        if (!isReparacion) {
            int i = 0;
            rowData[i] = mapRowData.get(TIPREG).trim(); //TIPREG          
            rowData[++i] = DT.parse(mapRowData.get(DOCFEC).trim());
            rowData[++i] = mapRowData.get(DOCSER).trim();
            rowData[++i] = mapRowData.get(DOCNUM).trim();
            rowData[++i] = mapRowData.get(CODTIP).trim(); //CODTIP
            rowData[++i] = mapRowData.get(CODMOD).trim();
            rowData[++i] = mapRowData.get(CODTER).trim();
            rowData[++i] = mapRowData.get(CTACON).trim(); //CTACON
            rowData[++i] = mapRowData.get(BASEBAS).trim();
            rowData[++i] = null; //mapRowData.get("IMPTBAS").trim();
            rowData[++i] = null; // "PORNOR"
            rowData[++i] = null; // "RECBAS"
            rowData[++i] = null; // "PORREC"
            rowData[++i] = null; // "PORTES"
            rowData[++i] = null; // rs.getDouble("PORFIN");
            rowData[++i] = null; //rs.getDouble("RFDPP");
            rowData[++i] = null; //rs.getDouble("DESHOR");
            rowData[++i] = null; //rs.getDouble("DESKM");
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
            rowData[++i] = null; //rs.getDouble("DIETENT");
            rowData[++i] = null; //rs.getString("CODFORPAG").trim();
            rowData[++i] = null; //rs.getString("TIPFORPAG").trim();

            boolean salta1 = true;
            for (Map.Entry<String, String> entry : mapAlbaranesClientes1.entrySet()) {
                //hay que coger la siguiente iteracion. La 1º ya esta incluida
                if (salta1) {
                    salta1 = false;
                    continue;
                }
                rowData[++i] = entry.getValue().trim(); //BASEIMPCC _C
                rowData[++i] = Double.parseDouble(entry.getKey().trim()); //CC _N
                break;
            }
        } else {
            // isReparacion ; Detalle para albaranClienteReparacion
             int i = 0;
            rowData[i] = mapRowData.get(TIPREG).trim(); //TIPREG          
            rowData[++i] = DT.parse(mapRowData.get(DOCFEC).trim());
            rowData[++i] = mapRowData.get(DOCSER).trim();
            rowData[++i] = mapRowData.get(DOCNUM).trim();
            rowData[++i] = mapRowData.get(CODTIP).trim(); //CODTIP
            rowData[++i] = mapRowData.get(CODMOD).trim();
            rowData[++i] = mapRowData.get(CODTER).trim();
            rowData[++i] = mapRowData.get(CTACON).trim(); //CTACON
            rowData[++i] = mapRowData.get(BASEBAS).trim();
            rowData[++i] = null; //mapRowData.get("IMPTBAS").trim();
            rowData[++i] = null; // "PORNOR"
            rowData[++i] = null; // "RECBAS"
            rowData[++i] = null; // "PORREC"
            rowData[++i] = null; // "PORTES"
            rowData[++i] = null; // rs.getDouble("PORFIN");
            rowData[++i] = null; //rs.getDouble("RFDPP");
            rowData[++i] = null; //rs.getDouble("DESHOR");
            rowData[++i] = null; //rs.getDouble("DESKM");
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
            rowData[++i] = null; //rs.getDouble("DIETENT");
            rowData[++i] = null; //rs.getString("CODFORPAG").trim();
            rowData[++i] = null; //rs.getString("TIPFORPAG").trim();
            
            for (Map.Entry<String, String> entry : mapAlbaranesClientes1.entrySet()) {
                rowData[++i] = entry.getValue().trim(); //BASEIMPCC _C
                rowData[++i] = Double.parseDouble(entry.getKey().trim()); //CC _N
                break;
            }
        }
    }

} //CLASS
