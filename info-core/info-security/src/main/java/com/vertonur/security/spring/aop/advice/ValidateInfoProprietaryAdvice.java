package com.vertonur.security.spring.aop.advice;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.dao.api.InfoDAO;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.pojo.Info;
import com.vertonur.security.exception.InsufficientPermissionException;

public class ValidateInfoProprietaryAdvice implements MethodBeforeAdvice {

	/**
	 * Validate if the user updating this content is the author of the content
	 */
	public void before(Method paramMethod, Object[] paramArrayOfObject,
			Object paramObject) throws Throwable {
		int infoId = (Integer) paramArrayOfObject[0];
		InfoDAO infoDao = PojoUtil.getDAOManager().getInfoDAO();
		Info info = infoDao.getInfoById(infoId);
		int authorId = info.getAuthor().getId();
		Authentication token = (Authentication) SecurityContextHolder
				.getContext().getAuthentication();
		int userId = (Integer) token.getPrincipal();
		if (authorId != userId)
			throw new InsufficientPermissionException(
					"You are not allowed to lock this info,it seems that you are not the author of this info.");
	}
}
