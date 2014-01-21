package com.vertonur.dms;

import static junit.framework.Assert.assertEquals;

import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.security.auth.login.LoginException;

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

	@Test
	public void testUploadAttachment2Bcs() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.BCS, false, false);
	}

	@Test
	public void testUploadImage2Local() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.LOCAL, true, false);
	}

	@Test
	public void testUploadImage2Bcs() throws LoginException, URISyntaxException {
		uploadAttachment(AttachmentType.BCS, true, false);
	}

	@Test
	public void testUploadEmbeddedImage2Local() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.LOCAL, false, true);
	}

	@Test
	public void testUploadEmbeddedImage2Bcs() throws LoginException,
			URISyntaxException {
		uploadAttachment(AttachmentType.BCS, false, true);
	}

	private void uploadAttachment(AttachmentType attachmentType,
			boolean isImage, boolean isEmbeddedImage) throws LoginException,
			URISyntaxException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		if (isEmbeddedImage) {
			if (AttachmentType.BCS.equals(attachmentType))
				saver.addEmbeddedImageAttachment2Bcs();
			else
				saver.addEmbeddedImageAttachment2Local();
		} else if (isImage) {
			if (AttachmentType.BCS.equals(attachmentType))
				saver.addImageAttachment2Bcs();
			else
				saver.addImageAttachment2Local();
		} else {
			if (AttachmentType.BCS.equals(attachmentType))
				saver.addAttachment2Bcs();
			else
				saver.addAttachment();
		}
		service.commitTransaction();

		service.beginTransaction();
		saver.loginInfoUser();
		int id = saver.getAttachmentId();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		Attachment attachment = attachmentService.getAttmById(id);
		AttachmentInfo info = attachment.getAttmInfo();
		assertEquals(attachmentType, info.getAttachmentType());
		if (isEmbeddedImage)
			assertEquals(FileType.EMBEDDED_IMAGE, info.getFileType());
		else
			assertEquals(FileType.FILE, info.getFileType());
		if (isImage || isEmbeddedImage)
			assertEquals(DataGenerator.ATTACHMENT_DEMO_IMAGE_SIZE,
					info.getFilesize());
		else
			assertEquals(DataGenerator.TXT_FILE_SIZE, info.getFilesize());
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
