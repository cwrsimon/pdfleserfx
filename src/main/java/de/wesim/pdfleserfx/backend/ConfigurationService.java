package de.wesim.pdfleserfx.backend;

import com.dieselpoint.norm.Database;
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
    private final Database db;

    private ConfigurationService() {
        this.preferred_dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        if (this.preferred_dpi == 0) {
            this.preferred_dpi = 96;
        }

        // home directory
        // TODO APP_LOCAL on WIndows
        var config_dirs_root = System.getenv("LOCALAPPDATA");
        if (config_dirs_root == null) {
            config_dirs_root = System.getProperty("user.home");
        }
        if (config_dirs_root != null) {
            var user_home = Paths.get(config_dirs_root);
            this.app_config_directory = user_home.resolve(".pdfleserfx");
            if (!Files.exists(this.app_config_directory)) {
                try {
                    Files.createDirectory(this.app_config_directory);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        var h2_file = app_config_directory.resolve("config.db").toAbsolutePath().toString();
        System.out.println(h2_file);
        this.db = new Database();
        db.setJdbcUrl("jdbc:h2:" + h2_file + ";database_to_upper=false");
        db.setUser("sa");
        db.setPassword("");
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
}
