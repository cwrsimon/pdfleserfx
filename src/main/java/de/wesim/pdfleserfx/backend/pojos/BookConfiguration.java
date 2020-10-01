/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.backend.pojos;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="book_configuration")
public class BookConfiguration {
	
        @Id
	@GeneratedValue
	public long id;

        @Column
	public String path;

	@Column
	public int dpi;

}
