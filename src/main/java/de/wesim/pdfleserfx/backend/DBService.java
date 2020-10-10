// Licensed under GNU General Public License v3.0, see 'LICENSE' for details.
package de.wesim.pdfleserfx.backend;

import de.wesim.pdfleserfx.backend.pojos.BookSettings;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DBService {

	private static DBService _instance;
	private final DriverManagerDataSource dataSource;
	private final JdbcTemplate jdbc_template;

	private DBService() {
		// https://www.baeldung.com/spring-jdbc-jdbctemplate
		var h2_file = ConfigurationService.getInstance().getH2_file();
		this.dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:" + h2_file + ";database_to_upper=false");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		this.jdbc_template = new JdbcTemplate(this.dataSource);
		// add application code here
	}

	public static DBService getInstance() {
		if (_instance == null) {
			_instance = new DBService();
		}
		return _instance;
	}

	// TODO Do we still need this?
	public void closeConnection() {
	}

	public boolean createSchema() {
		var sql = """
				create table if not exists book_settings (
					filename varchar(255) primary key,
					path varchar(255),
					background_color varchar(255),
					crop_top integer,
					crop_right integer,
					crop_bottom integer,
					crop_left   integer,
					current_page    integer,
					dpi integer,
					last_read timestamp
				);
				""";
		this.jdbc_template.execute(sql);
		return true;
	}

	public BookSettings findDbEntryForFile(Path file) {
		var name = file.getFileName().toString();

		try {
			var entry = this.jdbc_template.queryForObject("select * from book_settings where filename=?; ",
					(result_set, row_num) -> {
						var ret_value = new BookSettings();
						ret_value.filename = result_set.getString("filename");
						ret_value.path = result_set.getString("path");
						ret_value.dpi = result_set.getInt("dpi");
						ret_value.crop_bottom = result_set.getInt("crop_bottom");
						ret_value.crop_top = result_set.getInt("crop_top");
						ret_value.crop_left = result_set.getInt("crop_left");
						ret_value.crop_right = result_set.getInt("crop_right");
						ret_value.current_page = result_set.getInt("current_page");
						ret_value.background_color = result_set.getString("background_color");
						var last_read_timestamp = result_set.getTimestamp("last_read");
						if (last_read_timestamp != null)
							ret_value.last_read = last_read_timestamp.toLocalDateTime();
						return ret_value;
					}, name);
			return entry;

		} catch (DataAccessException e) {
			System.out.println("Nothing found for " + name);
			var path = file.toAbsolutePath().toString();
			this.jdbc_template.update("""
					insert into book_settings (filename,path,last_read)
					values (?,?,?);
					""", name, path, LocalDateTime.now());
		}
		return null;
	}

	public int update(BookSettings current_settings) {
		var rows_updated = this.jdbc_template.update("""
				update book_settings
				set dpi = ?,
				last_read = ?,
				background_color = ?,
				current_page = ?,
				crop_top = ?,
				crop_bottom = ?,
				crop_left = ?,
				crop_right = ?
				where filename = ?;
				""", current_settings.dpi, current_settings.last_read, current_settings.background_color,
				current_settings.current_page, current_settings.crop_top, current_settings.crop_bottom,
				current_settings.crop_left, current_settings.crop_right, current_settings.filename);
		return rows_updated;
	}

	public List<Path> getLast5Files() {

		try {
			var entries = this.jdbc_template.query("select path from book_settings order by last_read desc LIMIT 5;",
					(result_set, row_num) -> {
						var path_string = result_set.getString("path");
						return Paths.get(path_string);
					});
			return entries;

		} catch (DataAccessException e) {
			// TODO Do something reasonable here....
			e.printStackTrace();
		}
		return null;
	}

}
