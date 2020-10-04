package de.wesim.pdfleserfx.backend;

import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConfigurationService {

    private static ConfigurationService _instance;
    private int preferred_dpi;
    private Path app_config_directory;
    private final String h2_file;

    private ConfigurationService() {
        this.preferred_dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        if (this.preferred_dpi == 0) {
            this.preferred_dpi = 96;
        }

        // find the right home directory
        var config_dirs_root = System.getenv("LOCALAPPDATA");
        var config_dir_name = "pdfleserfx";
        if (config_dirs_root == null) {
            config_dirs_root = System.getProperty("user.home");
            config_dir_name = ".pdfleserfx";
        }
        if (config_dirs_root != null) {
            var user_home = Paths.get(config_dirs_root);
            this.app_config_directory = user_home.resolve(config_dir_name);
            if (!Files.exists(this.app_config_directory)) {
                try {
                    Files.createDirectory(this.app_config_directory);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        this.h2_file = app_config_directory.resolve("config.db").toAbsolutePath().toString();
        /*
        this.db = new Database();
        this.db.setJdbcUrl( + ";database_to_upper=false");
        this.db.setUser("sa");
        this.db.setPassword("");
        // create tables if necessary
        try {
        	var entries = db.sql("select count(*) from book_configuration").first(Long.class);
        	System.out.println("Entries: " + entries);
        } catch (DbException e) {
        	db.createTable(BookConfiguration.class);
        }
        */
    }

    public static ConfigurationService getInstance() {
        if (_instance == null) {
            _instance = new ConfigurationService();
        }
        return _instance;
    }

    public ObservableList<Number> getDpiResolutions() {
        var resolutions = Arrays.asList(preferred_dpi, 100, 200, 300, 400, 500);
        Collections.sort(resolutions);
        return FXCollections.observableArrayList(resolutions);
    }

   
	public String getH2_file() {
		return h2_file;
	}
    
    
}
