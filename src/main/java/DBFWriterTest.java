
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import es.dafer.tercero.ma.db.Connect;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private static final String PATH_FILE = "C:\\TEMCON_"; //PARA WINDOWS

//    public static void main(String[] args) throws DBFException, IOException {
//        WriterDbf(args);
//    }
    public static void WriterDbf(Date args[])
            throws DBFException, IOException, SQLException {

        // let us create field definitions first
        // we will go for 3 fields        
        System.out.println("Entra dbf");

        DBFField fields[] = new DBFField[TOTAL];
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
        DBFWriter writer = new DBFWriter();
        writer.setFields(fields);

        // now populate DBFWriter
        /**
         * **********
         */
        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yy");
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Establecemos la conexión con la base de datos. 
            conexion = DriverManager.getConnection("jdbc:mysql://localhost/dafer2", "root", "root");
            // Preparamos la consulta 
            Statement s = conexion.createStatement();
            //ResultSet rs = s.executeQuery("select * from clientes LIMIT 30");
            ResultSet rs = s.executeQuery(
                    "SELECT DISTINCT fc.serie, fc.numero,  fc.fecha,"
                    + "replace(TRUNCATE(fc.baseimponible, 2),'.',',') BASEBAS,"
                    + "replace(TRUNCATE(fc.impuestos, 2),'.',',') IMPTBAS "
                    + "FROM    dafer2.facturas_clientes fc,"
                    + "        dafer2.estadosfacturasclientes efc "
                    + "WHERE efc.id = 1"
                    + "  AND DATE(fc.fecha) BETWEEN '2015/01/01' AND '2015/01/31'"
                    + "  ORDER BY fc.fecha DESC;");
            // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
            while (rs.next()) {
                System.out.println(rs.getString(1) + " " + rs.getInt(2) + " " + dt1.format(rs.getDate(3)));
            
            }
            
            /***/            
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

            SimpleDateFormat dt = new SimpleDateFormat("HHmm_ddMMyyyy");
            String nameFile = PATH_FILE + dt.format(new Date()) + ".dbf";
            System.out.println("Fichero creado: " + nameFile);

            File fileDbf = new File(nameFile);
            FileOutputStream fos = new FileOutputStream(fileDbf);
            writer.write(fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerramos la conexion a la base de datos. 
            conexion.close();
        }
    }
}
