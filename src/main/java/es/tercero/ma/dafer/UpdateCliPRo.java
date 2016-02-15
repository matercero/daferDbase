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
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class UpdateCliPRo {

    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UpdateCliPRo.class);
    static Properties prop = new Properties();
    private static final String UPDATE_CLIENTE = "UPDATE clientes JOIN cuentascontables ON clientes.cuentascontable_id = cuentascontables.id \n"
            + "SET cuentascontables.codigo = ?  WHERE clientes.cif = ?;";
    private static final String UPDATE_PROVEEDOR = "UPDATE proveedores "
                    + " SET  proveedores.cuentascontable_id = (SELECT cuentascontables.id "
                    + " FROM cuentascontables WHERE cuentascontables.codigo = ?) "
                    + " WHERE proveedores.cif = ? ;";
    private static final String FILE_NAME ="SqlUpdateClientesProveedores.sql";
    private static final String PATH_FILE_READ = "F:\\Desarrollo\\Proyectos\\Dafer\\Terceros\\ok\\";
    
    public static void main(String[] args) {
        UpdateCliPRo obj = new UpdateCliPRo();
        obj.run();
    }

    public void run() {
        BasicConfigurator.configure();
        setProperties();

        String csvCliente =  PATH_FILE_READ + "Cliente.csv";
        logger.info("Fichero cliente.csv " + csvCliente);
        String csvProveed = PATH_FILE_READ + "PROVEED.csv";
        logger.info("Fichero proveedor.csv " + csvProveed);

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        Map< String, String> clienteData = new HashMap<String, String>();
        Map< String, String> proveedData = new HashMap<String, String>();
//        Map<String, List<String>> proveedData = new HashMap<String, List<String>>();

        try {

            //Clientes
            br = new BufferedReader(new FileReader(csvCliente));
            while ((line = br.readLine()) != null) {
                String[] cliente = line.split(cvsSplitBy);
                if (!clienteData.containsKey(cliente[2])) {
                      // Key=CIFCLI ;value=CTACON
                    clienteData.put(cliente[2], cliente[3]);
                } else {
                    logger.info("Cliente " + cliente[1] + " CIF= " + cliente[2]
                            + " CTACON= " + cliente[3] + " Repetido CIF con cliente con CTACON =" + clienteData.get(cliente[2]));
                }
            }

            //PROVEEDORES
            br = new BufferedReader(new FileReader(csvProveed));
            while ((line = br.readLine()) != null) {
                String[] proveed = line.split(cvsSplitBy);
                if (!proveedData.containsKey(proveed[3])) {
                    // Key=CIFPRO ;value= CTACON
                    proveedData.put(proveed[3], proveed[2]);
                } else {
                    logger.info("Proveedor " + proveed[1] + " CIF= " + proveed[2]
                            + " CTACON= " + proveed[3] + " Repetido CIF con Proveedor con CTACON =" + proveedData.get(proveed[3]));
                }
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

    private void actualizaTabla(Map<String, String> dataCliente, Map<String, String> dataProveed) throws IOException {
        Connection conexion = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;

        File file = new File(FILE_NAME);
        logger.info("Fichero creado " + file.getAbsolutePath());
        FileWriter writer = null;
        try {
            // creates the file
            file.createNewFile();
            // creates a FileWriter Object
            writer = new FileWriter(file);
            // Writes the content to the file
            conexion = Connect.getConexion(prop);

            stmt = conexion.prepareStatement(UPDATE_CLIENTE);
            //CLIENTES
            logger.info("Tamanyo Clientes = " + dataCliente.size());
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
            stmt2 = conexion.prepareStatement(UPDATE_PROVEEDOR);
            for (Map.Entry<String, String> entry : dataProveed.entrySet()) {
                String ctacon = entry.getKey();
                String cif = entry.getValue();
                stmt2.setString(1, ctacon); //CTACON
                stmt2.setString(2, cif); //Nombre
                writer.write(stmt2.toString().trim() + "\n");
            }
            logger.info("Proveedores leidos y escrito en fichero.");
            writer.flush();

        } catch (SQLException sqle) {
            logger.error("SQLState = " + sqle.getMessage());
            logger.error("SQLErrorCode = " + sqle.getMessage());
        } catch (IOException e) {
            logger.error("Exception = " + e.getMessage());
        } finally {
            if (conexion != null) {
                try {
                    stmt.close();
                    stmt2.close();
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
