package com.vertonur.dao.api;

import java.io.Serializable;

import com.vertonur.pojo.config.Config;

public interface ConfigDAO<T extends Config, ID extends Serializable> extends GenericDAO<T, ID>{

	T getConfig();
	int saveConfig(T config);
	void updateConfig(T config);
}
