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

/**
 *
 * @author mtercero
 */
public class Connect {

    //public static void main(String[] args) throws SQLException {
    public void conect() throws SQLException {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Establecemos la conexi�n con la base de datos. 
            conexion = DriverManager.getConnection("jdbc:mysql://localhost/dafer2", "root", "root");
            // Preparamos la consulta 
            Statement s = conexion.createStatement();
            ResultSet rs = s.executeQuery("select * from clientes LIMIT 30");
            // Recorremos el resultado, mientras haya registros para leer, y escribimos el resultado en pantalla. 
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerramos la conexion a la base de datos. 
            conexion.close();
        }
    }
}
