package com.vertonur.dms;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.dms.exception.AttachmentSizeExceedException;
import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.User;
import com.vertonur.pojo.AttachmentInfo.AttachmentType;
import com.vertonur.pojo.config.AttachmentConfig;

public interface AttachmentService {

	public AttachmentConfig getSysAttmConfig();

	public AttachmentConfig getAttmConfigById(int id);

	public Attachment getAttmById(int attmId);

	public List<Attachment> getBcsAttms();

	void deleteAttachment(Attachment attm);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public void updateAttachment(Attachment attm);

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

	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public Attachment uploadInfoEmbededImage(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, int fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException;

	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public Attachment uploadImage(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, int fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException;

	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public Attachment uploadAttchment(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, int fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException;
}
