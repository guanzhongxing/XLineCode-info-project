package com.vertonur.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.vertonur.pojo.config.Config;

@Entity
public class TestConfig implements Config{

	private int id;
	private String testProperty;
	
	@Id
	@GeneratedValue
	@Column(name = "TST_CONFIG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "TST_CONFIG_PROPERTY")
	public String getTestProperty() {
		return testProperty;
	}

	public void setTestProperty(String testProperty) {
		this.testProperty = testProperty;
	}
}
