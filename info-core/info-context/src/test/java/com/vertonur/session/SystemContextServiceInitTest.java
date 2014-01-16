package com.vertonur.session;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.vertonur.bean.TestConfig;
import com.vertonur.context.SystemContextService;
import com.vertonur.dao.api.ConfigDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dms.SystemService;
import com.vertonur.dms.UserService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.pojo.statistician.SystemStatistician;
import com.vertonur.test.LayerInitializer;

public class SystemContextServiceInitTest {

	@Before
	public void setUp() {
		LayerInitializer.setDaoManageImplClass();
	}

	@Test
	public void testContextServiceInit() throws Exception {
		SystemContextService.init();
		SystemContextService systemContextService = SystemContextService
				.getService();
		systemContextService.beginTransaction();
		UserService service = systemContextService
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		long num = service.getUserNum();
		assertEquals(2, num);

		SystemService systemService = systemContextService
				.getDataManagementService(ServiceEnum.SYSTEM_SERVICE);
		SystemStatistician systemStatistician = systemService
				.getSystemStatistician();
		int id = systemStatistician.getGuestId();
		assertEquals(id, systemContextService.getGuestId());
		systemContextService.commitTransaction();

		systemContextService.shutdown();
	}

	@Test
	public void testContextServiceInitThroughBeanConfig() {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		SystemContextService systemContextService = SystemContextService
				.getService();
		DAOManager manager = systemContextService.getDaoManager();
		manager.beginTransaction();
		ConfigDAO<TestConfig, Integer> dao = manager
				.getExtendedConfigDAO(TestConfig.class);
		TestConfig config = new TestConfig();
		config.setTestProperty("test");
		dao.saveConfig(config);
	}

	//TODO: To be removed when upgraded to 0.8.2
	@Test
	public void changePassword() {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		SystemContextService systemContextService = SystemContextService
				.getService();
		PasswordEncoder passwordEncoder = (PasswordEncoder) systemContextService
				.getSpringBean("passwordEncoder");
		for (int i = 1; i < 22; i++) {
			String password = passwordEncoder.encodePassword("12345", i);
			System.out.println(password);
		}
	}
}
