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

	public void confirmEmbeddedImageUpload(AbstractInfo holder, int attachmentId);

	/**
	 * record download num of the attachment and return the downloadable url
	 * 
	 * @param attmId
	 * @return downloadable url
	 */
	public String downloadAttachment(int attmId);

	public Integer saveAttachmentInfo(AttachmentInfo attmInfo);

	/**
	 * Upload an image that is emmbeded in the content of an info.Value of
	 * uploadRoot depends on the AttachmentType.For AttachmentType.BCS, download
	 * url of the Attachment is set when this method finish.For the
	 * AttachmentType.LOCAL, physicalFilename of the Attachment should be used
	 * to assemble the download url for the web app.
	 * 
	 * @param attachmentType
	 * @param inputStream
	 * @param mimeType
	 * @param uploadRoot
	 * @param fileName
	 * @param fileSize
	 * @param attmComment
	 * @param user
	 * @param info
	 * @return
	 * @throws AttachmentSizeExceedException
	 * @throws IOException
	 */
	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public Attachment uploadInfoEmbededImage(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, long fileSize, User user)
			throws AttachmentSizeExceedException, IOException;

	/**
	 * Upload an attachment. If the attachment is an image and the
	 * AttachmentType is LOCAL, a thumb is generated for that image
	 * 
	 * @param attachmentType
	 * @param inputStream
	 * @param mimeType
	 * @param uploadRoot
	 * @param fileName
	 * @param fileSize
	 * @param attmComment
	 * @param user
	 * @param info
	 * @return
	 * @throws AttachmentSizeExceedException
	 * @throws IOException
	 */
	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public Attachment uploadAttchment(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, long fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException;
}
