package com.vertonur.dms;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.config.AttachmentConfig;

public interface AttachmentService {

	public AttachmentConfig getSysAttmConfig();

	public AttachmentConfig getAttmConfigById(int id);

	public Attachment getAttmById(int attmId);

	public List<Attachment> getBcsAttms();

	void deleteAttachment(Attachment attm);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public void updateAttachment(Attachment attm);

	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public Integer saveAttachment(Attachment attm);

	public void confirmEmbeddedImageUpload(Attachment attm);

	/**
	 * record download num of the attachment and return the downloadable url
	 * 
	 * @param attmId
	 * @return downloadable url
	 */
	public String downloadAttachment(int attmId);

	public Integer saveAttachmentInfo(AttachmentInfo attmInfo);
}
