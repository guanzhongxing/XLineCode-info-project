package com.vertonur.dms;

import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;

public abstract class GenericService {

	protected static final DAOManager manager;

	static {
		manager = PojoUtil.getDAOManager();
	}
}
