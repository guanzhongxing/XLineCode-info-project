package com.vertonur.dao.api;

import com.vertonur.dao.util.PojoUtil;

public class DaoManagerPropertyNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7012064069737002858L;

	public DaoManagerPropertyNotFoundException() {
		super("Property [" + PojoUtil.INFO_DAO_MANAGER_IMPL_CLASS
				+ "] is not set in the system, to use ["
				+ PojoUtil.class.getCanonicalName()
				+ "] to get a dao manager,set the property with ["
				+ System.class.getCanonicalName() + "] first");
	}
}
