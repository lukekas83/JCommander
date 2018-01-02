package jcommander;

import java.awt.Font;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class JCommanderSettingsMng {
    private String homePath = null;
    private String osFileSeparator = null;
    private String configPath = null;
    private String configFile = "";  

    private JCommanderSettings makeDefualtConfig() {
        JCommanderSettings config = new JCommanderSettings();
        config.setLocaleArg1("pl");
        config.setLocaleArg2("PL");
        config.setSide(-1);
        config.setFontName("Tahoma");
        config.setFontStyle(Font.PLAIN);
        config.setFontSize(11);
        config.setStyleName("Windows");
        return config;
    }

    public JCommanderSettingsMng() {
        homePath = System.getProperty("user.home");
        osFileSeparator = System.getProperty("file.separator");
        configPath = homePath + osFileSeparator + ".jcommander";
        File file = null;
        if (!(file = new File(configPath)).exists()) {
            file.mkdir();
        }
        configFile += configPath + osFileSeparator + JCommanderSettings.ConfigFileName;        
        file = null;
    }

    public void saveConfiguration(JCommanderSettings config) {
        try {
        	XMLEncoder out = new XMLEncoder (new FileOutputStream(configFile));
            out.writeObject(config);
            out.close();
        } catch (Exception ex) { }
    }

    public JCommanderSettings getConfiguration() {
        JCommanderSettings config = null;
        try {
        	XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(configFile)));        	
        	config = (JCommanderSettings)decoder.readObject();
            decoder.close();
        } catch (IOException ex) {            
            return makeDefualtConfig();
        }

        return config;
    }
}
