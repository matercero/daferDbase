package es.dafer.tercero.ma.test;


import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtercero
 */
public class JavaDBFReaderTest {

    public static void main(String args[]) {

        try {

            // create a DBFReader object
            //
//            File fileDbf = new File("C:\\Users\\mtercero.INTRANET\\Workspace\\daferDbase\\src\\main\\webapp\\resources\\p_temcon.dbf");
            File fileDbf = new File("C:\\Users\\mtercero.INTRANET\\Workspace\\daferDbase\\src\\main\\webapp\\resources\\TEMCON.DBF");
            InputStream inputStream = new FileInputStream(fileDbf); // take dbf file as program argument
            DBFReader reader = new DBFReader(inputStream);

            // get the field count if you want for some reasons like the following
            //
            int numberOfFields = reader.getFieldCount();

            // use this count to fetch all field information
            // if required
            //
            for (int i = 0; i < numberOfFields; i++) {

                DBFField field = reader.getField(i);

                // do something with it if you want
                // refer the JavaDoc API reference for more details
                //
                System.out.println("Cabecera: " + field.getName());
            }

            // Now, lets us start reading the rows
            //
            // Now, lets us start reading the rows
            //
            Object[] rowObjects;
            int c =0; int d = 0;
            while ((rowObjects = reader.nextRecord()) != null) {

                for (int i = 0; i < rowObjects.length; i++) {

                    System.out.println(rowObjects[i]);
                    if (rowObjects[i] != null) {
                        if (rowObjects[i].equals("C")) {
                            c++;
                        } else {
                            d++;
                        }
                    }
                }
            }

            System.out.println("C  = " + c + "\nD = " + d);
//            System.out.println("Num. de registro = " + i);
                // By now, we have itereated through all of the rows

                inputStream.close();
            }   catch (DBFException e) {

            System.out.println(e.getMessage());
        } catch (IOException e) {

            System.out.println(e.getMessage());
        }
    }
}
