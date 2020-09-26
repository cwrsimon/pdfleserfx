package de.wesim.pdfleserfx.backend;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConfigurationService {

	
	private static ConfigurationService _instance;
	private int preferred_dpi;
	
	private ConfigurationService() {
        this.preferred_dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        if (this.preferred_dpi == 0) this.preferred_dpi = 96;
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
	
}
