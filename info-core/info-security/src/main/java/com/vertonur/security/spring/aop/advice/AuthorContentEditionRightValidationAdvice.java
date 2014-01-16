package com.vertonur.security.spring.aop.advice;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.pojo.AbstractInfo;
import com.vertonur.security.exception.InsufficientPermissionException;

public class AuthorContentEditionRightValidationAdvice implements
		MethodBeforeAdvice {

	/**
	 * Validate if the user updating this content is the author of the content
	 */
	public void before(Method paramMethod, Object[] paramArrayOfObject,
			Object paramObject) throws Throwable {
		AbstractInfo content = (AbstractInfo) paramArrayOfObject[0];
		int authorId = content.getAuthor().getId();
		Authentication token = (Authentication) SecurityContextHolder
				.getContext().getAuthentication();
		int userId = (Integer) token.getPrincipal();
		if (authorId != userId)
			throw new InsufficientPermissionException(
					"You are not allowed to edit this content,it seems that you are not the author of the content.");
	}
}
