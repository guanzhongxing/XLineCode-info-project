package com.vertonur.dms;

import static junit.framework.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import org.hibernate.ObjectNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.AttachmentInfo.AttachmentType;
import com.vertonur.pojo.AttachmentInfo.FileType;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.security.exception.InsufficientPermissionException;

public class AttachmentServiceTest {
	private SystemContextService service;
	private DataGenerator saver;

	@Before
	public void init() throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
		service.beginTransaction();
		saver = new DataGenerator();
		service.commitTransaction();
	}

	@Test
	public void testUploadAttachmentPermission() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		saver.addAttachment();
		service.commitTransaction();
	}

	@Test
	public void testUploadAttachment() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.LOCAL, false, false);
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testDeleteAttachment() throws LoginException,
			URISyntaxException {
		deleteAttachment(AttachmentType.LOCAL, false, false);
	}

	@Test
	public void testUploadAttachment2Bcs() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.BCS, false, false);
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testDeleteBcsAttachment() throws LoginException,
			URISyntaxException {
		deleteAttachment(AttachmentType.BCS, false, false);
	}

	@Test
	public void testUploadImage2Local() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.LOCAL, true, false);
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testDeleteLocalImage() throws LoginException,
			URISyntaxException {
		deleteAttachment(AttachmentType.LOCAL, true, false);
	}

	@Test
	public void testUploadImage2Bcs() throws LoginException, URISyntaxException {
		uploadAttachment(AttachmentType.BCS, true, false);
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testDeleteBcsImage() throws LoginException, URISyntaxException {
		deleteAttachment(AttachmentType.BCS, true, false);
	}

	@Test
	public void testUploadEmbeddedImage2Local() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.LOCAL, false, true);

		service.beginTransaction();
		int id = saver.getAttachmentId();
		int infoId = saver.getInfoId();
		int categoryId = saver.getCategoryId();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		InfoService infoService = (InfoService) service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(categoryId, infoId);
		attachmentService.confirmEmbeddedImageUpload(info, id);
		service.commitTransaction();

		service.beginTransaction();
		id = saver.getAttachmentId();
		attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		Attachment attachment = attachmentService.getAttmById(id);
		assertEquals(true, attachment.getAttmInfo().isUploadConfirmed());
		service.commitTransaction();
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testDeleteLocalEmbeddedImage() throws LoginException,
			URISyntaxException {
		deleteAttachment(AttachmentType.LOCAL, false, true);
	}

	@Test
	public void testUploadEmbeddedImage2LocalWithoutConfirm()
			throws LoginException, URISyntaxException {
		uploadAttachment(AttachmentType.LOCAL, false, true);

		service.beginTransaction();
		int id = saver.getAttachmentId();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		Attachment attachment = attachmentService.getAttmById(id);
		assertEquals(false, attachment.getAttmInfo().isUploadConfirmed());
		assertNull(attachment.getAttmHolder());
		service.commitTransaction();
	}

	@Test
	public void testUploadEmbeddedImage2Bcs() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.BCS, false, true);

		service.beginTransaction();
		int id = saver.getAttachmentId();
		int infoId = saver.getInfoId();
		int categoryId = saver.getCategoryId();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		InfoService infoService = (InfoService) service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(categoryId, infoId);
		attachmentService.confirmEmbeddedImageUpload(info, id);
		service.commitTransaction();

		service.beginTransaction();
		id = saver.getAttachmentId();
		attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		Attachment attachment = attachmentService.getAttmById(id);
		assertEquals(true, attachment.getAttmInfo().isUploadConfirmed());
		service.commitTransaction();
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testDeleteBcsEmbeddedImage() throws LoginException,
			URISyntaxException {
		deleteAttachment(AttachmentType.BCS, false, true);
	}

	@Test
	public void testUploadEmbeddedImage2BcsWithoutConfirm()
			throws LoginException, URISyntaxException {
		uploadAttachment(AttachmentType.BCS, false, true);

		service.beginTransaction();
		int id = saver.getAttachmentId();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		Attachment attachment = attachmentService.getAttmById(id);
		assertEquals(false, attachment.getAttmInfo().isUploadConfirmed());
		assertNull(attachment.getAttmHolder());
		service.commitTransaction();
	}

	private void uploadAttachment(AttachmentType attachmentType,
			boolean isImage, boolean isEmbeddedImage) throws LoginException,
			URISyntaxException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();

		if (AttachmentType.BCS.equals(attachmentType)) {
			AttachmentConfig config = service.getDataManagementService(
					ServiceEnum.RUNTIME_PARAMETER_SERVICE)
					.getAttachmentConfig();
			config.setUploadFileSystem(AttachmentType.BCS);
			config.setUploadRoot("/upload");
		}
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		if (isEmbeddedImage)
			saver.addEmbeddedImageAttachment();
		else if (isImage)
			saver.addImageAttachment();
		else
			saver.addAttachment();

		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		int id = saver.getAttachmentId();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		Attachment attachment = attachmentService.getAttmById(id);
		AttachmentInfo info = attachment.getAttmInfo();
		assertEquals(attachmentType, info.getAttachmentType());
		assertNotNull(info.getFileName());
		assertNotNull(info.getFilePath());
		if (isEmbeddedImage)
			assertEquals(FileType.EMBEDDED_IMAGE, info.getFileType());
		else
			assertEquals(FileType.FILE, info.getFileType());
		if (isImage || isEmbeddedImage)
			assertEquals(DataGenerator.ATTACHMENT_DEMO_IMAGE_SIZE,
					info.getFilesize());
		else
			assertEquals(DataGenerator.TXT_FILE_SIZE, info.getFilesize());

		if (AttachmentType.LOCAL.equals(info.getAttachmentType())) {
			File file = new File(info.getFilePath());
			assertTrue(file.exists());
			if (isImage || isEmbeddedImage)
				assertEquals(DataGenerator.ATTACHMENT_DEMO_IMAGE_SIZE,
						file.length());
			else
				assertEquals(DataGenerator.TXT_FILE_SIZE, file.length());
		}
		service.commitTransaction();
	}

	private void deleteAttachment(AttachmentType attachmentType,
			boolean isImage, boolean isEmbeddedImage) throws LoginException,
			URISyntaxException {
		uploadAttachment(attachmentType, isImage, isEmbeddedImage);

		service.beginTransaction();
		int id = saver.getAttachmentId();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		Attachment attachment = attachmentService.getAttmById(id);
		attachmentService.deleteAttachment(attachment);
		service.commitTransaction();

		service.beginTransaction();
		int infoId = saver.getInfoId();
		int categoryId = saver.getCategoryId();
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(categoryId, infoId);
		assertEquals(0, info.getAttachments().size());

		id = saver.getAttachmentId();
		attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		attachment = attachmentService.getAttmById(id);
		assertNull(attachment);
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUploadAttachmentWithoutUploadPermission()
			throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("uploadAttmPermissionTemplate",
				saver.getCategoryId());
		saver.addAttachment();
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testUploadAttachmentWithoutDownloadPermission()
			throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.removePermission("downloadAttmPermissionTemplate",
				saver.getCategoryId());
		saver.addAttachment();
		service.commitTransaction();
	}

	@Test
	public void testDownloadAttachmentPermission() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.loginInfoUser();
		saver.addInfo();
		saver.addAttachment();
		service.commitTransaction();

		service.beginTransaction();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		attachmentService.downloadAttachment(saver.getAttachmentId());
		service.commitTransaction();

		service.beginTransaction();
		attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		Attachment attachment = attachmentService.getAttmById(saver
				.getAttachmentId());
		assertEquals(1, attachment.getAttmInfo().getDownloadCount());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDownloadAttachmentWithoutUploadPermission()
			throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		saver.removePermission("uploadAttmPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		saver.addAttachment();
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDownloadAttachmentWithoutDownloadPermission()
			throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		saver.addAttachment();
		saver.removePermission("downloadAttmPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		attachmentService.downloadAttachment(saver.getAttachmentId());
		service.commitTransaction();
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testDownloadAttachmentWithoutPermission() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		saver.addAttachment();
		saver.removePermission("uploadAttmPermissionTemplate",
				saver.getCategoryId());
		saver.removePermission("downloadAttmPermissionTemplate",
				saver.getCategoryId());
		service.commitTransaction();

		service.beginTransaction();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		attachmentService.downloadAttachment(saver.getAttachmentId());
		service.commitTransaction();
	}

	@After
	public void destroy() throws LoginException, SQLException {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}
}
