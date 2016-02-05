/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.dafer.tercero.ma.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * crear el properties la primera vez
 *
 * @author mangel.tercero
 */
public class propert {

    public String getConfigFile() {

        Properties prop = new Properties();
        String filePath = "";

        try {

            InputStream inputStream
                    = getClass().getClassLoader().getResourceAsStream("config.properties");

            prop.load(inputStream);
            filePath = prop.getProperty("testMail");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;

    }

    public static void main(String[] args) {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

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
}
