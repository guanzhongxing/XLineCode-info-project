package com.vertonur.pojo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "INFO_COR_GST")
@DiscriminatorValue("Guest")
public class Guest extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Guest() {
	}
}
