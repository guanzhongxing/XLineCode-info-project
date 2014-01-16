package com.vertonur.dms;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.pojo.config.CommentConfig;
import com.vertonur.pojo.config.EmailConfig;
import com.vertonur.pojo.config.InfoConfig;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.config.PrivateMsgConfig;
import com.vertonur.pojo.config.SystemContextConfig;
import com.vertonur.pojo.config.UserConfig;

public class RuntimeParameterServiceTest {
	private DataGenerator saver;
	private SystemContextService service;

	@Before
	public void setUp() throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();
		service.beginTransaction();
		saver = new DataGenerator();
		saver.loginAdmin();
		service.commitTransaction();
	}

	@After
	public void tearDown() throws Exception {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}

	@Test
	public void testUpdateAttachmentConfig() {
		service.beginTransaction();
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		AttachmentConfig config = runtimeParameterService.getAttachmentConfig();
		config.setAttmtEnabled(false);
		runtimeParameterService.updateAttachmentConfig(config);
		service.commitTransaction();
	}

	@Test
	public void testUpdateCommentConfig() {
		service.beginTransaction();
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		CommentConfig config = runtimeParameterService.getCommentConfig();
		config.setCmtPgnOffset(0);
		runtimeParameterService.updateCommentConfig(config);
		service.commitTransaction();
	}

	@Test
	public void testUpdateEmailConfig() {
		service.beginTransaction();
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		EmailConfig config = runtimeParameterService.getEmailConfig();
		config.setRequireAuth(false);
		runtimeParameterService.updateEmailConfig(config);
		service.commitTransaction();
	}

	@Test
	public void testUpdateInfoConfig() {
		service.beginTransaction();
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		InfoConfig config = runtimeParameterService.getInfoConfig();
		config.setHottestInfoGeCmts(0);
		runtimeParameterService.updateInfoConfig(config);
		service.commitTransaction();
	}

	@Test
	public void testUpdateModerationConfig() {
		service.beginTransaction();
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		ModerationConfig config = runtimeParameterService.getModerationConfig();
		config.setAssignPendingLogInterval(0);
		runtimeParameterService.updateModerationConfig(config);
		service.commitTransaction();
	}

	@Test
	public void testUpdatePmConfig() {
		service.beginTransaction();
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		PrivateMsgConfig config = runtimeParameterService.getPmConfig();
		config.setNewPrivateMsgInDays(0);
		runtimeParameterService.updatePmConfig(config);
		service.commitTransaction();
	}

	@Test
	public void testUpdateSystemContextConfig() {
		service.beginTransaction();
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		SystemContextConfig config = runtimeParameterService
				.getSystemContextConfig();
		config.setAcctActivationSessionTiming(0);
		runtimeParameterService.updateSystemContextConfig(config);
		service.commitTransaction();
	}

	@Test
	public void testUpdateUserConfig() {
		service.beginTransaction();
		RuntimeParameterService runtimeParameterService = service
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		UserConfig config = runtimeParameterService.getUserConfig();
		config.setAvatarHeight(0);
		runtimeParameterService.updateUserConfig(config);
		service.commitTransaction();
	}
}
