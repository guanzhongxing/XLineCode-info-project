package com.vertonur.dms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.vertonur.dao.api.AdminDAO;
import com.vertonur.dao.api.InfoDAO;
import com.vertonur.dao.api.ModeratorDAO;
import com.vertonur.dao.api.PrivateMessageDAO;
import com.vertonur.dao.api.UserDAO;
import com.vertonur.dms.exception.InvalidOldPasswordException;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.PrivateMessage;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadInfo;
import com.vertonur.pojo.UserReadPrivateMessage;
import com.vertonur.pojo.config.UserConfig;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Group.GroupType;
import com.vertonur.pojo.security.Permission;
import com.vertonur.pojo.security.Permission.PermissionType;
import com.vertonur.pojo.statistician.UserMsgStatistician;

public class UserServiceImpl extends GenericService implements UserService {

	private UserDAO userDAO;
	private AdminDAO adminDao;
	private InfoDAO infoDao;
	private ModeratorDAO moderatorDao;
	private PrivateMessageDAO privateMsgDao;
	private UserConfig userConfig;
	private PasswordEncoder passwordEncoder;

	protected UserServiceImpl() {
		adminDao = manager.getAdminDAO();
		infoDao = manager.getInfoDAO();
		privateMsgDao = manager.getPrivateMsgDAO();
		userDAO = manager.getUserDAO();
		moderatorDao = manager.getModeratorDAO();

		userConfig = new RuntimeParameterServiceImpl().getUserConfig();
	}

	public Admin getAdminById(int adminId) {
		return adminDao.getAdminById(adminId);
	}

	public List<Admin> getAdmins() {
		return adminDao.getAdmins();
	}

	public Integer saveAdmin(Admin admin) {
		admin.setActivated(false);
		return adminDao.saveAdmin(admin);
	}

	public void updateAdmin(Admin admin) {
		userDAO.updateUser(admin);
		ModeratorManager moderatorManager = ModeratorManager.getManager();
		moderatorManager.rearrangeModeratorWorkload(admin);
	}

	public User getUserById(int userId) {
		return userDAO.getUserById(userId);
	}

	public boolean deleteUser(User user) {
		return userDAO.deleteUser(user);
	}

	public boolean updateUser(User user) {
		return userDAO.updateUser(user);
	}

	public Integer saveUser(User user) {
		user.setActivated(false);
		return userDAO.saveUser(user);
	}

	public long getUserNum() {
		return userDAO.getUserNum();
	}

	public User getLatestUser() {
		return userDAO.getLatestUser();
	}

	public List<User> getUsers(int start) {
		return userDAO.getUsers(start, userConfig.getUsrPgnOffset());
	}

	public List<User> getUsersByNameAndGroupId_AM(String userName, int start,
			int groupId) {
		return userDAO.getUsersByNameAndGroupId_AM(userName, start,
				userConfig.getUsrPgnOffset(), groupId);
	}

	public long getUserNumByNameAndGroupId_AM(String userName, int groupId) {
		return userDAO.getUserNumByNameAndGroupId_AM(userName, groupId);
	}

	public List<User> getUsersByName_EM(String userName) {
		return userDAO.getUsersByName_EM(userName);
	}

	public Set<UserReadInfo> getReadInfos(User user) {
		return infoDao.getSpecifiedUserReadInfos(user);
	}

	public boolean isReadInfo(User user, Info Info) {
		UserReadInfo readInfo = infoDao.getReadInfoByUserAndInfo(user, Info);
		if (readInfo != null)
			return true;
		return false;
	}

	// return true when all inputted Infos have been read by user
	public boolean confirmReadInfos(User user, List<Info> infos) {
		Set<UserReadInfo> readInfos = infoDao.getReadInfosByUserAndInfos(user,
				infos);
		boolean[] readMarkers = new boolean[infos.size()];
		for (int i = 0; i < infos.size(); i++)
			for (UserReadInfo readInfo : readInfos) {
				if (infos.get(i).getId() == readInfo.getReadInfo().getId()) {
					readMarkers[i] = true;
					break;
				}
			}

		boolean result = true;
		for (int i = 0; i < readMarkers.length; i++)
			result = result & readMarkers[i];

		return result;
	}

	public Set<UserReadPrivateMessage> getReadPrivateMsgs(User user) {
		return privateMsgDao.getReadPrivateMsgsByReceiver(user);
	}

	public boolean confirmReadPrivateMsg(User user, PrivateMessage pm) {
		UserReadPrivateMessage readPm = privateMsgDao
				.getReadPrivateMsgByUserAndPrivateMsg(user, pm);
		if (readPm != null)
			return true;
		return false;
	}

	public void lockUser(User user) {
		user.setLocked(true);
		userDAO.updateUser(user);
		if (user instanceof Moderator) {
			ModeratorManager moderatorManager = ModeratorManager.getManager();
			moderatorManager.rearrangeModeratorWorkload((Moderator) user);
		}
	}

	public void unLockUser(User user) {
		user.setLocked(false);
		userDAO.updateUser(user);
	}

	public void activateUser(int id) {
		User user = userDAO.getUserById(id);
		user.setActivated(true);
		saltPassword(user);
		userDAO.updateUser(user);
	}

	public void updateUserStatistician(int userId,
			UserMsgStatistician statistician) {
		User user = userDAO.getUserById(userId);
		user.setStatistician(statistician);
		userDAO.updateUser(user);
	}

	@Override
	public Moderator getModeratorById(int moderatorId) {
		return moderatorDao.getModeratorById(moderatorId);
	}

	@Override
	public int saveModerator(Moderator moderator) {
		moderator.setActivated(false);
		Integer moderatorId = moderatorDao.saveModerator(moderator);
		Set<Group> groups = moderator.getGroups();
		Set<Integer> categoryIds = new HashSet<Integer>();
		for (Group group : groups) {
			if (group.getGroupType().equals(GroupType.GENERIC_MDR)) {
				Set<Permission> permissions = group.getPermissions();
				for (Permission permission : permissions) {
					if (permission.getType().equals(
							PermissionType.AUDIT_CATEGORY))
						categoryIds.add(Integer.parseInt(permission
								.getProprietaryMark()));
				}
			}
		}
		ModeratorManager moderatorManager = ModeratorManager.getManager();
		moderatorManager.addModerator(categoryIds, moderatorId);
		return moderatorId;
	}

	@Override
	public void updateModerator(Moderator moderator) {
		userDAO.updateUser(moderator);
		ModeratorManager moderatorManager = ModeratorManager.getManager();
		moderatorManager.rearrangeModeratorWorkload(moderator);
	}

	public void changePassword(int userId, String oldPassword,
			String newPassword) throws InvalidOldPasswordException {
		User user = userDAO.getUserById(userId);
		oldPassword = passwordEncoder.encodePassword(oldPassword, userId);
		if (!oldPassword.equals(user.getPassword())) {
			throw new InvalidOldPasswordException();
		}

		user.setPassword(newPassword);
		saltPassword(user);
		userDAO.updateUser(user);
	}

	private void saltPassword(User user) {
		String password = user.getPassword();
		password = passwordEncoder.encodePassword(password, user.getId());
		user.setPassword(password);
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User getUserByEmail(String email) {
		return userDAO.getUserByEmail(email);
	}
}
