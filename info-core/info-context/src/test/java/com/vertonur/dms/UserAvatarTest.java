package com.vertonur.dms;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;

import org.hibernate.ObjectNotFoundException;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.AttachmentInfo.AttachmentType;
import com.vertonur.pojo.AttachmentInfo.FileType;
import com.vertonur.pojo.config.AttachmentConfig;

public class UserAvatarTest {
	private DataGenerator saver;
	private SystemContextService service;
	private AttachmentConfig config;

	public void setUp(AttachmentType attachmentType) throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
		service.beginTransaction();
		saver = new DataGenerator();
		config = service.getDataManagementService(
				ServiceEnum.RUNTIME_PARAMETER_SERVICE).getAttachmentConfig();
		if (AttachmentType.LOCAL.equals(attachmentType)) {
			config.setUploadFileSystem(AttachmentType.LOCAL);
			config.setAvatarRoot(System.getProperty("user.home")
					+ "/info-project/upload/avatar");
			config.setDefaultAvatarURI("defaultAvatar.jpeg");
		}
		service.commitTransaction();
	}

	@After
	public void tearDown() throws Exception {
		service.beginTransaction();
		saver.loginAdmin();
		int id = saver.getAdminId();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(id);
		Attachment avatar = admin.getAvatar();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		attachmentService.deleteAttachment(avatar);

		admin.setAvatar(null);
		userService.updateAdmin(admin);
		service.commitTransaction();

		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}

	@Test
	public void testSetUpUserDefaultAvatar_Bcs() throws Exception {
		setUp(AttachmentType.BCS);

		assertEquals(AttachmentType.BCS, config.getUploadFileSystem());
		testSetUpUserDefaultAvatar();
	}

	private void testSetUpUserDefaultAvatar() throws Exception {
		service.beginTransaction();
		saver.loginAdmin();
		int id = saver.getAdminId();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(id);
		Attachment avatar = admin.getAvatar();
		assertNotNull(avatar);
		AttachmentInfo info = avatar.getAttmInfo();
		assertNotNull(info);
		assertEquals(config.getUploadFileSystem(), info.getAttachmentType());
		assertTrue(config.getDefaultAvatarURI().contains(info.getFileName()));
		assertEquals(FileType.FILE, info.getFileType());
		assertEquals(true, info.isUploadConfirmed());
		service.commitTransaction();
	}

	@Test
	public void testSetUpUserDefaultAvatar_Local() throws Exception {
		setUp(AttachmentType.LOCAL);

		assertEquals(AttachmentType.LOCAL, config.getUploadFileSystem());
		service.beginTransaction();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		int id = saver.getAdminId();
		Admin admin = userService.getAdminById(id);
		userService.setUpDefaultAvatar(config.getUploadFileSystem(),
				config.getAvatarRoot(), admin);
		service.commitTransaction();
		testSetUpUserDefaultAvatar();
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testSetUpUserDefaultAvatar_WithAvatar_Bcs() throws Exception {
		setUp(AttachmentType.BCS);

		assertEquals(AttachmentType.BCS, config.getUploadFileSystem());
		testSetUpUserDefaultAvatar_WithAvatar();
	}

	private void testSetUpUserDefaultAvatar_WithAvatar() throws Exception {
		service.beginTransaction();
		saver.loginAdmin();
		int id = saver.getAdminId();
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(id);

		// upload an avatar and check value
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(DataGenerator.UPLOAD_AVATAR);
		File file = new File(url.toURI());
		FileInputStream inputStream = new FileInputStream(file);
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mimeType = fileNameMap.getContentTypeFor(file.getName());
		userService.setUpAvatar(config.getUploadFileSystem(), inputStream,
				mimeType, config.getAvatarRoot(), file.getName(),
				file.length(), admin);
		service.commitTransaction();

		service.beginTransaction();
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		admin = userService.getAdminById(id);
		Attachment avatar = admin.getAvatar();
		assertNotNull(avatar);
		AttachmentInfo info = avatar.getAttmInfo();
		assertNotNull(info);
		assertEquals(config.getUploadFileSystem(), info.getAttachmentType());
		assertTrue(info.getFileName().contains(DataGenerator.UPLOAD_AVATAR));
		assertEquals(FileType.FILE, info.getFileType());
		assertEquals(true, info.isUploadConfirmed());
		service.commitTransaction();
		// upload an avatar and check value end

		// set up the default avatar and check value
		service.beginTransaction();
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		admin = userService.getAdminById(id);
		avatar = admin.getAvatar();
		userService.setUpDefaultAvatar(config.getUploadFileSystem(),
				config.getAvatarRoot(), admin);
		service.commitTransaction();

		service.beginTransaction();
		userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		admin = userService.getAdminById(id);
		avatar = admin.getAvatar();
		assertNotNull(avatar);
		info = avatar.getAttmInfo();
		assertNotNull(info);
		assertEquals(config.getUploadFileSystem(), info.getAttachmentType());
		assertTrue(config.getDefaultAvatarURI().contains(info.getFileName()));
		assertEquals(FileType.FILE, info.getFileType());
		assertEquals(true, info.isUploadConfirmed());
		// set up the default avatar and check value

		// check if the previous upload avatar deleted
		int avatarId = saver.getAvatarId();
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		avatar = attachmentService.getAttmById(avatarId);
		assertNull(avatar);
		// check if the previous upload avatar deleted end
		service.commitTransaction();
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testSetUpUserDefaultAvatar_WithAvatar_Local() throws Exception {
		setUp(AttachmentType.LOCAL);

		assertEquals(AttachmentType.LOCAL, config.getUploadFileSystem());
		testSetUpUserDefaultAvatar_WithAvatar();
	}
}
