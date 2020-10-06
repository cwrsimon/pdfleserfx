//TODO License Header
package de.wesim.pdfleserfx.backend.pojos;

import java.time.LocalDateTime;

// TODO Add Indexes
public class BookSettings {

	public String filename;

	public String path;

	public String background_color = null;

	public int crop_top = 0;
	public int crop_right = 0;
	public int crop_bottom = 0;
	public int crop_left = 0;
	public int current_page = 1;
	public int dpi = 96;
	public LocalDateTime last_read = LocalDateTime.now();

}
