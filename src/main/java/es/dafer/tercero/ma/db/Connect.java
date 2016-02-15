/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.dafer.tercero.ma.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author mtercero
 */
public class Connect {

    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Connect.class);

    //public static void main(String[] args) throws SQLException {
    public void conect() throws SQLException {
//        Connection conexion = null;
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            // Establecemos la conexión con la base de datos. 
//            conexion = DriverManager.getConnection("jdbc:mysql://localhost:8080/dafer2", "root", "root");
//            // Preparamos la consulta 
//            Statement s = conexion.createStatement();
//            ResultSet rs = s.executeQuery("select * from clientes LIMIT 30");
//            // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
//            while (rs.next()) {
//                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // Cerramos la conexion a la base de datos. 
//            conexion.close();
//        }
    }

    public static Connection getConexion(Properties prop) {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Establecemos la conexión con la base de datos.
            conexion = DriverManager.getConnection(prop.getProperty("pathConnectMysql"),
                    prop.getProperty("dbuser"), prop.getProperty("dbpassword"));
            logger.info("Conexion "+!conexion.isClosed()+" esta abierta. EXITO");
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException : " + e.getMessage());
        } catch (SQLException e) {
            logger.error("SQLException : " + e.getMessage());
        }
        return conexion;
    }
}
