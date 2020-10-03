package de.wesim.pdfleserfx.backend;

import com.dieselpoint.norm.Database;
import com.dieselpoint.norm.DbException;

import de.wesim.pdfleserfx.backend.pojos.BookConfiguration;

import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConfigurationService {

    private static ConfigurationService _instance;
    private int preferred_dpi;
    private Path app_config_directory;
    private final Database db;

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
        var h2_file = app_config_directory.resolve("config.db").toAbsolutePath().toString();
        this.db = new Database();
        this.db.setJdbcUrl("jdbc:h2:" + h2_file + ";database_to_upper=false");
        this.db.setUser("sa");
        this.db.setPassword("");
        // create tables if necessary
        try {
        	var entries = db.sql("select count(*) from book_configuration").first(Long.class);
        	System.out.println("Entries: " + entries);
        } catch (DbException e) {
        	db.createTable(BookConfiguration.class);
        }
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

    public Database getDb() {
        return this.db;
    }
    
    public BookConfiguration findDbEntryForFile(Path file) {
    	var name = file.getFileName().toString();
    	List<BookConfiguration> entries = 
    			this.db.where("filename=?", name)
                                .results(BookConfiguration.class);
    	System.out.println(entries.size());
    	if (entries.size() > 0) {
            return entries.get(0);
        }
        var new_instance = new BookConfiguration();
        new_instance.filename = name;
        db.insert(new_instance);
        System.out.println("Neue ID: " + new_instance.id);
        return new_instance;
    	/*
        var db = ConfigurationService.getInstance().getDb();
        var new_config = new BookConfiguration();
        new_config.path = path.toAbsolutePath().toString();
        new_config.dpi = 300;
        System.out.println("Inserting");
        db.createTable(BookConfiguration.class);
        db.
        db.insert(new_config);
        db.close();
        */
    }
}
