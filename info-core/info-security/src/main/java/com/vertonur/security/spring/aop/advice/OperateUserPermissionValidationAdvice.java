package com.vertonur.security.spring.aop.advice;

import java.lang.reflect.Method;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.dao.api.UserDAO;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.User;

public class OperateUserPermissionValidationAdvice extends
		PermissionValidationAdvice {

	public void before(Method paramMethod, Object[] paramArrayOfObject,
			Object paramObject) throws Throwable {
		Authentication token = (Authentication) SecurityContextHolder
				.getContext().getAuthentication();
		int userId = (Integer) token.getPrincipal();
		UserDAO dao = PojoUtil.getDAOManager().getUserDAO();
		User user = dao.getUserById(userId);
		if (user instanceof Admin) {
			User operatedUser = (User)paramArrayOfObject[0];
			if(user.getId()!=operatedUser.getId()){
				super.before(paramMethod, paramArrayOfObject, paramObject);
			}
		}
	}
}
