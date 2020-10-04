package de.wesim.pdfleserfx.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class DBService {

	
	private static DBService _instance;
	private Connection conn;
	private DataSource datasource;

	private DBService() {
		// https://www.baeldung.com/spring-jdbc-jdbctemplate
		var h2_file = ConfigurationService.getInstance().getH2_file();
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/springjdbc");
        dataSource.setUsername("guest_user");
        dataSource.setPassword("guest_password");
		try {
			this.conn = DriverManager.
			        getConnection("jdbc:h2:" + h2_file, "sa", "");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        // add application code here
	}
	
	public static DBService getInstance() {
		if (_instance == null) {
			_instance = new DBService();
		}
		return _instance;
	}
	
	public void closeConnection() {
		if (this.conn != null)
			try {
				this.conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}
