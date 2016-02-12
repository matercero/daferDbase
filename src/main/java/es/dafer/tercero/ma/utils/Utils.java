/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.dafer.tercero.ma.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author mangel.tercero
 */
public class Utils {

    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Utils.class);

    private static Properties prop = new Properties();
    private static String PATH_FILE = ""; 

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

    /**
     * @return the prop
     */
    public Properties getProp() {
        if (prop == null) {
            setProperties();
        }
        return prop;
    }

    /**
     * @param aProp the prop to set
     */
    public void setProp(Properties aProp) {
        prop = aProp;
    }

    /**
     * @return the PATH_FILE
     */
    public static String getPATH_FILE() {
        return PATH_FILE;
    }

    /**
     * @param aPATH_FILE the PATH_FILE to set
     */
    public static void setPATH_FILE(String aPATH_FILE) {
        PATH_FILE = aPATH_FILE;
    }
}
