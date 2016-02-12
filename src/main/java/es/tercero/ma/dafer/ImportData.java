/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.tercero.ma.dafer;

import es.dafer.tercero.ma.db.Connect;
import static es.dafer.tercero.ma.utils.Utils.getPATH_FILE;
import static es.dafer.tercero.ma.utils.Utils.setPATH_FILE;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.BasicConfigurator;

/**
 * Clase para importar e actualizar las cuentas contables de Clientes y
 * Proveedores
 *
 * @author mangel.tercero
 */
public class ImportData {

    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImportData.class);
    static Properties prop = new Properties();

    public static void main(String[] args) {
        ImportData obj = new ImportData();
        obj.run();
    }

    public void run() {
        BasicConfigurator.configure();
        setProperties();

        String csvCliente = "D:\\_eme3\\Terceros\\Cliente.csv";
        logger.info("Fichero cliente.csv " + csvCliente);
        String csvProveed = "D:\\_eme3\\Terceros\\PROVEED.csv";
        logger.info("Fichero proveedor.csv " + csvProveed);

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        Map< String, String> clienteData = new HashMap<String, String>();
        Map<String, List<String>> proveedData = new HashMap<String, List<String>>();

        try {

            //Clientes
            br = new BufferedReader(new FileReader(csvCliente));
            while ((line = br.readLine()) != null) {
                String[] cliente = line.split(cvsSplitBy);
                clienteData.put(cliente[2], cliente[3]);
//                logger.info("Cliente " + cliente[1] + " [CIF= " + cliente[2]
//                        + " , CTACON=" + cliente[3] + "]");
            }

            //PROVEEDORES
            List<String> valSetOne = new ArrayList<String>();
            br = new BufferedReader(new FileReader(csvProveed));
            while ((line = br.readLine()) != null) {
                String[] proveed = line.split(cvsSplitBy);
                valSetOne.add(proveed[1]);
                valSetOne.add(proveed[3]);
                proveedData.put(proveed[2], valSetOne);
                valSetOne = new ArrayList<String>();
//                logger.info("proveed " + proveed[1] + "[CIF= " + proveed[3]
//                        + " , CTACON=" + proveed[2] + "]");
            }

            actualizaTabla(clienteData, proveedData);

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        logger.info("Proceso Terminado.");
    }

    private void actualizaTabla(Map<String, String> dataCliente, Map<String, List<String>> dataProveed) throws IOException {
        Connection conexion = null;
        PreparedStatement stmt = null;

        File file = new File("updateClientesProveedores.sql");
        FileWriter writer = null;
        try {
            // creates the file
            file.createNewFile();
            // creates a FileWriter Object
            writer = new FileWriter(file);
            // Writes the content to the file
            conexion = Connect.getConexion(prop);

            stmt = conexion.prepareStatement("UPDATE clientes JOIN cuentascontables ON clientes.cuentascontable_id = cuentascontables.id\n"
                    + "  SET cuentascontables.codigo = ?  WHERE clientes.cif = ?;");

            for (Map.Entry<String, String> entry : dataCliente.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                stmt.setString(1, value); //CTACON
                stmt.setString(2, key); //CIF
                //logger.info(stmt.toString());               
                writer.write(stmt.toString().trim() + "\n");
            }
            logger.info("Clientes leidos y escrito en fichero.");
            writer.flush();
            
            
            //PROVEEDORES
            stmt = conexion.prepareStatement("INSERT INTO  dafer2.cuentascontables (id ,codigo ,nombre ,nombre_cuenta_abierta ,nombre_cuenta_externa) "
                    + "VALUES(NULL, ?, ?, '', '');)");

            //stmt2 = conexion.prepareStatement("UPDATE dafer2.Proveedores SET cuentascontable_id = ? WHERE cif = ? ");
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, List<String>> entry : dataProveed.entrySet()) {
                String ctacon = entry.getKey();
                List<String> values = entry.getValue();
                stmt.setString(1, ctacon); //CTACON
                stmt.setString(2, values.get(0)); //Nombre
                
                sb = new StringBuffer("UPDATE dafer2.Proveedores SET cuentascontable_id =");
                sb.append("X");
                sb.append("  WHERE cif = ");
                sb.append("'" + values.get(1) + "'");
                
                //logger.info(stmt.toString());               
                writer.write(stmt.toString().trim() + "\n");
                writer.write(sb.toString().trim() + "\n");
                sb.delete(0, sb.length()-1);
            }
            logger.info("Proveedores leidos y escrito en fichero.");
            writer.flush();

//            int retorno = stmt.executeUpdate();
//            if (retorno > 0) {
//                System.out.println("Insertado correctamente");
//            }
        } catch (SQLException sqle) {
            logger.error("SQLState = " + sqle.getMessage());
            logger.error("SQLErrorCode = " + sqle.getMessage());
        } catch (Exception e) {
            logger.error("Exception = " + e.getMessage());
        } finally {
            if (conexion != null) {
                try {
                    stmt.close();
                    conexion.close();
                } catch (Exception e) {
                    logger.error("Exception = " + e.getMessage());
                }
                writer.close();
            }
        }
    }

    public static void setProperties() {

        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");
            prop.load(input);

            // get the property value and print it out
            if (Integer.valueOf(prop.getProperty("flagOS")) < 0) {
                setPATH_FILE(prop.getProperty("pathFileLinux"));
            } else {
                setPATH_FILE(prop.getProperty("pathFileWin"));
            }

            logger.info("PATH_FILE {0} " + getPATH_FILE());
            logger.info("Conectado a bbdd = " + prop.getProperty("pathConnectMysql"));

        } catch (IOException ex) {
            logger.error("IOException " + ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    logger.error("IOException " + ex.getMessage());
                }
            }
        }

    }

}
