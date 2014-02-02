package com.vertonur.dms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.dms.exception.AttachmentSizeExceedException;
import com.vertonur.dms.exception.InvalidOldPasswordException;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.PrivateMessage;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadInfo;
import com.vertonur.pojo.UserReadPrivateMessage;
import com.vertonur.pojo.AttachmentInfo.AttachmentType;
import com.vertonur.pojo.statistician.UserMsgStatistician;

public interface UserService {

	public Admin getAdminById(int adminId);

	public List<Admin> getAdmins();

	/**
	 * NOTE:password of the admin must be raw type as it's salted by the system
	 * automatically when activating this admin
	 * 
	 * @param admin
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public Integer saveAdmin(Admin admin);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void updateAdmin(Admin admin);

	@RolesAllowed({ RoleEnum.ROLE_MODERATOR, RoleEnum.ROLE_ADMIN })
	public void updateModerator(Moderator moderator);

	public User getUserById(int userId);
	
	public User getUserByEmail(String email);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public boolean deleteUser(User user);

	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_MODERATOR,
			RoleEnum.ROLE_ADMIN })
	public boolean updateUser(User user);

	/**
	 * Persist a user record, which is pending to be activated. NOTE:password of
	 * the user must be raw type as it's salted by the system automatically when
	 * activating this user
	 * 
	 * @param user
	 * @return
	 */
	@RolesAllowed({ RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GUEST })
	public Integer saveUser(User user);

	public long getUserNum();

	public User getLatestUser();

	public List<User> getUsers(int start);

	/**
	 * Search users by name through ambiguous mode and by group id,if the user
	 * name is "" then it will return users with the group id and if the group
	 * id is 0 then it will return users with user name matching.Otherwise it's
	 * the same as {@link getUsers}
	 * 
	 * @param userName
	 * @param start
	 * @param groupId
	 * @return
	 */
	public List<User> getUsersByNameAndGroupId_AM(String userName, int start,
			int groupId);

	/**
	 * Get user num by name through ambiguous mode and by group id,if the user
	 * name is "" or null then it will return num of users with the group id and
	 * if the group id is 0 then it will return num of users with user name
	 * matching.Otherwise it's the same as {@link getUsers}
	 * 
	 * @param userName
	 * @param groupId
	 * @return
	 */
	public long getUserNumByNameAndGroupId_AM(String userName, int groupId);

	public List<User> getUsersByName_EM(String userName);

	public Set<UserReadInfo> getReadInfos(User user);

	public boolean isReadInfo(User user, Info Info);

	// return true when all inputted Infos have been read by user
	public boolean confirmReadInfos(User user, List<Info> infos);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public Set<UserReadPrivateMessage> getReadPrivateMsgs(User user);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public boolean confirmReadPrivateMsg(User user, PrivateMessage pm);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void lockUser(User user);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void unLockUser(User user);

	public void activateUser(int id);

	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public void updateUserStatistician(int userId,
			UserMsgStatistician statistician);

	public Moderator getModeratorById(int moderatorId);

	/**
	 * NOTE:password of the moderator must be raw type as it's salted by the
	 * system automatically when activating this moderator
	 * 
	 * @param moderator
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public int saveModerator(Moderator moderator);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public void changePassword(int userId, String oldPassword,
			String newPassword) throws InvalidOldPasswordException;
	
	public void setUpAvatar(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String avatarRoot,
			String fileName, long fileSize, User user)
			throws AttachmentSizeExceedException, IOException;

	public void setUpDefaultAvatar(AttachmentType attachmentType,
			String avatarRoot, User user) throws IOException, URISyntaxException;
}
