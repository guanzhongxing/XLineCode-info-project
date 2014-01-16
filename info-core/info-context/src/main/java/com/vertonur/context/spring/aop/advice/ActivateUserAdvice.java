package com.vertonur.context.spring.aop.advice;

import java.lang.reflect.Method;

import org.springframework.aop.AfterReturningAdvice;

import com.vertonur.context.SystemContextService;
import com.vertonur.context.SystemContextService.SystemState;
import com.vertonur.dms.UserService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.pojo.User;

public class ActivateUserAdvice implements AfterReturningAdvice {

	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {
		int userId = (Integer) args[0];
		SystemContextService systemContextService = SystemContextService
				.getService();
		UserService service = systemContextService
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		User user = service.getUserById(userId);
		SystemState state = systemContextService.getState();
		state.updateLatestRegisteredUser(user);
	}
}
