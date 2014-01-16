package com.vertonur.context.spring.aop.advice;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import com.vertonur.context.SystemContextService;
import com.vertonur.dms.RuntimeParameterService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.dms.exception.FailToSetPropertyException;
import com.vertonur.pojo.config.SystemContextConfig;

public class UpdateSessionTimeoutConfigAdvice implements AfterReturningAdvice {
	private static Logger logger = LoggerFactory
			.getLogger(UpdateSessionTimeoutConfigAdvice.class
					.getCanonicalName());

	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {
		SystemContextService systemContextService = SystemContextService
				.getService();
		RuntimeParameterService runtimeParameterService = systemContextService
				.getDataManagementService(ServiceEnum.RUNTIME_PARAMETER_SERVICE);
		SystemContextConfig config = runtimeParameterService
				.getSystemContextConfig();
		int sessionTiming = config.getSessionTiming();
		try {
			Method methodSetDepartment = SystemContextService.class
					.getDeclaredMethod(
							SystemContextService.SET_SESSION_TIMEOUT_METHOD_NAME,
							int.class);
			methodSetDepartment.setAccessible(true);
			methodSetDepartment.invoke(systemContextService, sessionTiming);
		} catch (Exception e) {
			logger.error("Fails to update session timing.", e);
			throw new FailToSetPropertyException(
					"Fails to update session timing , this exception might be caused by change of method name ["
							+ SystemContextService.SET_SESSION_TIMEOUT_METHOD_NAME
							+ "] of  "
							+ SystemContextService.class.getSimpleName()
							+ " class.Plz check the existence of the method for ["
							+ SystemContextService.class.getCanonicalName()
							+ "] or refer to log for more information.");
		}

		long loginSessionTiming = config.getLoginSessionTiming();
		try {
			Method methodSetDepartment = SystemContextService.class
					.getDeclaredMethod(
							SystemContextService.SET_LOGIN_SESSION_TIMEOUT_METHOD_NAME,
							long.class);
			methodSetDepartment.setAccessible(true);
			methodSetDepartment
					.invoke(systemContextService, loginSessionTiming);
		} catch (Exception e) {
			logger.error("Fails to update session timing.", e);
			throw new FailToSetPropertyException(
					"Fails to update session timing , this exception might be caused by change of method name ["
							+ SystemContextService.SET_LOGIN_SESSION_TIMEOUT_METHOD_NAME
							+ "] of  "
							+ SystemContextService.class.getSimpleName()
							+ " class.Plz check the existence of the method for ["
							+ SystemContextService.class.getCanonicalName()
							+ "] or refer to log for more information.");
		}
	}
}
