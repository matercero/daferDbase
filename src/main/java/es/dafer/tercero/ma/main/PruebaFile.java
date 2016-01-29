package es.dafer.tercero.ma.main;


import java.io.File;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mangel.tercero
 */
public class PruebaFile {

    public static void main(String args[]) {
        
        File fichero = new File("d:\\_eme3\\fichero.txt");
        try {
            // A partir del objeto File creamos el fichero físicamente
            if (fichero.createNewFile()) {
                System.out.println("El fichero se ha creado correctamente");
            } else {
                System.out.println("No ha podido ser creado el fichero");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
