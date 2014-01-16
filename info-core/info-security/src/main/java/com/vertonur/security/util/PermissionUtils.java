package com.vertonur.security.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.pojo.User;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;
import com.vertonur.pojo.security.Permission.Level;
import com.vertonur.pojo.security.Role;
import com.vertonur.security.spring.LoginPermissionAuthenticationToken;
import com.vertonur.security.spring.PermissionAuthenticationToken;

public class PermissionUtils {

	public static String assemblePermissionProprietaryMarkNotSetMsg(
			Permission permissionExample) {
		StringBuilder sb = new StringBuilder();
		sb.append("Permission example with name [");
		sb.append(permissionExample.getName());
		sb.append("]");
		sb.append(",desciption [");
		sb.append(permissionExample.getDescription());
		sb.append("]");
		sb.append(",component [");
		sb.append(permissionExample.getComponent());
		sb.append("]");
		sb.append(",type [");
		sb.append(permissionExample.getType());
		sb.append("] is not set with proprietaryMark property.");

		return sb.toString();
	}

	/**
	 * Iterate Level enum, check every Level element against parameter
	 * permissions, if level property of any permission entry in permissions
	 * matches the element,put that permission into the Set&lt;Permission&gt;
	 * entry of a level map and finally return that map when all enum elements
	 * are checked.If there is no permission with the corresponding enum
	 * element,an empty Set&lt;Permission&gt; is associated with the element.
	 * 
	 * @param permissions
	 * @return
	 */
	private static Map<Level, Set<Permission>> permissionSet2LevelMap(
			Set<Permission> permissions) {
		Level[] levels = Level.values();
		Map<Level, Set<Permission>> levelMap = new HashMap<Level, Set<Permission>>();
		for (Level level : levels) {
			Set<Permission> levelPermissions = new HashSet<Permission>();
			for (Permission permission : permissions) {
				Level permissionLevel = permission.getLevel();
				if (permissionLevel.equals(level))
					levelPermissions.add(permission);
			}

			levelMap.put(level, levelPermissions);
		}

		return levelMap;
	}

	/**
	 * Algorithm: lower level permission must be met to proceed to the higher
	 * level,eg: Level_1 permission must be met before processing Level_2
	 * permission. A certain level is said to have permission if one of the
	 * permissions in the same level is met
	 * 
	 * @param userPermissions
	 * @param levelMap
	 *            level map that contains permissions required for an operation
	 */
	public static boolean checkPermissions(Set<Permission> requiredPermissions) {
		PermissionAuthenticationToken token = (PermissionAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Set<Permission> userPermissions = token.getPermissions();
		Map<Level, Set<Permission>> levelMap = permissionSet2LevelMap(requiredPermissions);

		boolean hasPermission = true;
		if (userPermissions.size() != 0) {
			Set<Permission> permissions;
			List<Boolean> resultList;
			Map<Level, List<Boolean>> resultMap = new HashMap<Level, List<Boolean>>();
			Level[] levels = Level.values();
			for (Level level : levels) {
				permissions = levelMap.get(level);
				if (!permissions.isEmpty()) {
					resultList = new ArrayList<Boolean>();
					for (Permission permission : permissions) {
						if (userPermissions.contains(permission))
							resultList.add(true);
						else
							resultList.add(false);
					}

					resultMap.put(level, resultList);
				}
			}

			if (!requiredPermissions.isEmpty()) {
				int length = levels.length;
				for (int i = 0; i < length; i++) {
					Level level = levels[i];
					resultList = resultMap.get(level);
					boolean levelPermission = true;
					if (resultList != null)
						for (Boolean value : resultList)
							levelPermission &= value;

					hasPermission &= levelPermission;
					if (hasPermission == false)
						break;
				}
			}
		} else
			hasPermission = false;

		return hasPermission;
	}

	public static LoginPermissionAuthenticationToken generateGuestPermissionToken(
			Group defaultGuestGroup, User guest) {
		Set<Permission> permissions = defaultGuestGroup.getPermissions();
		Set<Role> roles = defaultGuestGroup.getRoles();
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (Role role : roles) {
			GrantedAuthority authority = new SimpleGrantedAuthority(
					role.getName());
			authorities.add(authority);
		}

		/**
		 * new another set from permissions set to prevent lazy loading related
		 * exception of orm frameworks
		 */
		LoginPermissionAuthenticationToken result = new LoginPermissionAuthenticationToken(
				guest.getId(), guest.getPassword(), authorities,
				new HashSet<Permission>(permissions));
		result.setGuest(true);
		return result;
	}
}
