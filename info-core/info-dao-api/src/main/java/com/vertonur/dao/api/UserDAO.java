/**
 * 
 */
package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.User;

/**
 * @author Vertonur
 * 
 */
public interface UserDAO extends GenericDAO<User, Integer> {

	User getUserById(int userId);
	
	User getUserByEmail(String email);

	User getLatestUser();

	User getGuest(User guestExample);

	List<User> getUsers(int start, int offset);

	// search users by name through exact mode
	List<User> getUsersByName_EM(String userName);

	/**
	 * search users by name through ambiguous mode and by group id,if the user
	 * name is "" then it will return users with the group id and if the group
	 * id is 0 then it will return users with user name matching.Otherwise it's
	 * the same as {@link getUsers}
	 * 
	 * @param userName
	 * @param start
	 * @param offset
	 * @param groupId
	 * @return
	 */
	List<User> getUsersByNameAndGroupId_AM(String userName, int start,
			int offset, int groupId);

	/**
	 * get user num by name through ambiguous mode and by group id,if the user
	 * name is "" or null then it will return num of users with the group id and
	 * if the group id is 0 then it will return num of users with user name
	 * matching.Otherwise it's the same as {@link getUsers}
	 * 
	 * @param userName
	 * @param groupId
	 * @return
	 */
	long getUserNumByNameAndGroupId_AM(String userName, int groupId);

	Integer saveUser(User user);

	boolean updateUser(User user);

	boolean deleteUser(User user);

	long getUserNum();

	User getFirstUser();
}
