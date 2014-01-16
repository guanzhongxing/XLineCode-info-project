package com.vertonur.security.spring.aop.datasource;

import com.vertonur.dao.api.AttachmentDAO;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.security.Permission;

public class AttachmentIdPermissionDataSource extends
		AttachmentPermissionDataSource {

	@Override
	protected void setProprietaryMark(Permission permissionExample, Object param) {
		Integer attachmentId = (Integer) param;
		AttachmentDAO dao = PojoUtil.getDAOManager().getAttachmentDAO();
		Attachment attachment = dao.getAttmById(attachmentId);
		super.setProprietaryMark(permissionExample, attachment);
	}
}
