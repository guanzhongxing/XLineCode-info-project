package com.vertonur.security.spring.aop.datasource;

import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.security.Permission;

public class AttachmentPermissionDataSource extends PermissionDataSource {

	@Override
	protected void setProprietaryMark(Permission permissionExample, Object param) {
		Attachment attachment = (Attachment) param;
		AbstractInfo holder = attachment.getAttmHolder();
		setProprietaryMark(permissionExample, holder);
	}
}
