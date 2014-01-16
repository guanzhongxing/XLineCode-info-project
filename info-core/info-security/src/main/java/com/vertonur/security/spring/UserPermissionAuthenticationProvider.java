package com.vertonur.security.spring;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPermissionAuthenticationProvider extends
		DaoAuthenticationProvider {

	protected Authentication createSuccessAuthentication(Object principal,
			Authentication authentication, UserDetails user) {

		PermissionUser permissionUser = (PermissionUser) user;
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		LoginPermissionAuthenticationToken result = new LoginPermissionAuthenticationToken(
				Integer.parseInt(permissionUser.getUsername()),
				authentication.getCredentials(), user.getAuthorities(),
				permissionUser.getPermissions());
		result.setDetails(authentication.getDetails());

		return result;
	}
}
