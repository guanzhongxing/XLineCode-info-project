package com.vertonur.bean;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.context.SystemContextService;
import com.vertonur.dms.AttachmentService;
import com.vertonur.dms.CategoryService;
import com.vertonur.dms.DepartmentService;
import com.vertonur.dms.GroupService;
import com.vertonur.dms.InfoService;
import com.vertonur.dms.ModerationService;
import com.vertonur.dms.PermissionService;
import com.vertonur.dms.UserService;
import com.vertonur.dms.constant.ServiceEnum;
import com.vertonur.dms.exception.Assigned2SubGroupException;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.DeptModerationListNotEmptyException;
import com.vertonur.dms.exception.SavingCommentToLockedInfoException;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.bean.UserPoint;
import com.vertonur.ext.ranking.exception.RankingWithPointsExistException;
import com.vertonur.ext.ranking.service.RankingService;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.ModerationLog.ModerationStatus;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Group.GroupType;
import com.vertonur.pojo.security.Permission;
import com.vertonur.security.spring.LoginPermissionAuthenticationToken;
import com.vertonur.security.util.PermissionUtils;
import com.vertonur.session.UserSession;
import com.vertonur.test.CommonDataGenerator;

public class DataGenerator {
	private static int USERS_NUM = 9;
	private static int GROUPS_NUM = 9;

	private SystemContextService service;
	private int[] userIds = new int[USERS_NUM];
	private int[] groupIds = new int[GROUPS_NUM];
	private int deptId;
	private int categoryId;
	private int infoId;
	private int attachmentId;
	private int rankingId_1;
	private int rankingId_2;
	private int rankingId_3;
	private int timeRankingId_1;
	private int timeRankingId_2;
	private int timeRankingId_3;
	private UserSession session;
	private int infoUserId;
	private String password = CommonDataGenerator.PASSWORD;
	private int cmtUserId;
	private int cmtId;
	private int moderatorId;
	private int adminId = 1;
	private int groupId;

	public DataGenerator() throws LoginException {
		service = SystemContextService.getService();
		generateGuestAuthenticationToken();
		addInfoUser();
		addCmtUser();
	}

	public void loginInfoUser() throws LoginException {
		loginUser(infoUserId + "", password);
	}

	public void loginCmtUser() throws LoginException {
		loginUser(cmtUserId + "", password);
	}

	public void loginModerator() throws LoginException {
		loginUser(moderatorId + "", password);
	}

	public void loginAdmin() throws LoginException {
		loginUser(adminId + "", password);
	}

	public void loginUser(String userId, String pwd) throws LoginException {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				userId, pwd);
		session = service.getNewUserSession(Locale.CHINESE.toString(),
				"127.0.0.1");
		Authentication result = service.login(session, token);
		SecurityContextHolder.getContext().setAuthentication(result);
	}

	public void addNightUsers() {
		addUsers(USERS_NUM);
	}

	public void addUsers(int userNum) {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		List<User> users = CommonDataGenerator.generateUsers(userNum);
		for (int i = 0; i < users.size(); i++) {
			int id = userService.saveUser(users.get(i));
			userService.activateUser(id);
			userIds[i] = id;
		}
	}

	public void addAdmin() {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = CommonDataGenerator.generateAdmin();
		userIds[0] = userService.saveAdmin(admin);
	}

	public Category getCategory() {
		CategoryService categoryService = service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		return categoryService.getCategoryById(deptId, categoryId, false);
	}

	public void addNightGroups() {
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		for (int i = 1; i <= GROUPS_NUM; i++) {
			Group group = new Group();
			group.setName("group_" + i);
			group.setGroupType(GroupType.GENERIC_USR);
			groupIds[i - 1] = groupService.saveGroup(group);
		}
	}

	public void generateGuestAuthenticationToken() throws LoginException {
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		Group defaultGuestGroup = groupService.getGroupById(service
				.getDefaultGuestGroupId());
		User guest = service.getDaoManager().getUserDAO()
				.getUserById(service.getGuestId());

		LoginPermissionAuthenticationToken result = PermissionUtils
				.generateGuestPermissionToken(defaultGuestGroup, guest);
		SecurityContextHolder.getContext().setAuthentication(result);
	}

	public Info getInfo() {
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(categoryId, infoId, false);

		return info;
	}

	public Comment getCmt() {
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Comment cmt = infoService.getCommentById(cmtId);

		return cmt;
	}

	public Admin getAdmin() {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Admin admin = userService.getAdminById(1);
		return admin;
	}

	public User getGuest() {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		User guest = userService.getUserById(service.getGuestId());
		return guest;
	}

	public User getInfoUser() {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		return userService.getUserById(infoUserId);
	}

	public User getCmtUser() {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		return userService.getUserById(cmtUserId);
	}

	public Moderator getModerator() {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Moderator moderator = userService.getModeratorById(moderatorId);
		return moderator;
	}

	public void addSingleRaning(int points)
			throws RankingWithPointsExistException {
		Ranking ranking = generateRanking("test_ranking_1", points);
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		rankingId_1 = rankingService.saveRanking(ranking);
		addGroups2Ranking(ranking, 0);
		rankingService.updateRanking(ranking);
	}

	public void addGroups2Ranking(Ranking ranking, int offset) {
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		for (int i = 0 + offset; i < 3 + offset; i++) {
			Group group = groupService.getGroupById(groupIds[i]);
			ranking.addGroup(group);
		}
	}

	public void addUserPoint(int[] points) {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		int length = points.length;
		for (int i = 0; i < length; i++) {
			User user = userService.getUserById(userIds[i]);
			UserPoint userPoint = generateUserPoint(user, points[i]);
			rankingService.saveUserPoint(userPoint);
		}
	}

	public void addGroup() {
		Group group = CommonDataGenerator.generateGroup();
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		groupId = groupService.saveGroup(group);
	}

	public Group getGroup() {
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		return groupService.getGroupById(groupId);
	}

	public void addTwoRaning(int points_1, int points_2)
			throws RankingWithPointsExistException {
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking = generateRanking("test_ranking_1", points_1);
		rankingId_1 = rankingService.saveRanking(ranking);
		addGroups2Ranking(ranking, 0);
		rankingService.updateRanking(ranking);

		Ranking ranking_2 = generateRanking("test_ranking_2", points_2);
		rankingId_2 = rankingService.saveRanking(ranking_2);
		addGroups2Ranking(ranking_2, 3);
		rankingService.updateRanking(ranking_2);
	}

	public void addThreeRaning(int points_1, int points_2, int points_3)
			throws RankingWithPointsExistException {
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking = generateRanking("test_ranking_1", points_1);
		rankingId_1 = rankingService.saveRanking(ranking);
		addGroups2Ranking(ranking, 0);
		rankingService.updateRanking(ranking);

		Ranking ranking_2 = generateRanking("test_ranking_2", points_2);
		rankingId_2 = rankingService.saveRanking(ranking_2);
		addGroups2Ranking(ranking_2, 3);
		rankingService.updateRanking(ranking_2);

		Ranking ranking_3 = generateRanking("test_ranking_3", points_3);
		rankingId_3 = rankingService.saveRanking(ranking_3);
		addGroups2Ranking(ranking_3, 6);
		rankingService.updateRanking(ranking_3);
	}

	public void addThreeTimeRaning(int points_1, int points_2, int points_3)
			throws RankingWithPointsExistException {
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking = generateTimeRanking("test_ranking_1", points_1, 5);
		timeRankingId_1 = rankingService.saveRanking(ranking);

		Ranking ranking_2 = generateTimeRanking("test_ranking_2", points_2,
				0.00001);
		timeRankingId_2 = rankingService.saveRanking(ranking_2);

		Ranking ranking_3 = generateTimeRanking("test_ranking_3", points_3, 5);
		timeRankingId_3 = rankingService.saveRanking(ranking_3);
	}

	public void addDepartment(boolean moderated) {
		Admin admin = getAdmin();
		DepartmentService deptService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department dept = CommonDataGenerator.generateDepartment(admin,
				moderated);
		deptId = deptService.saveDepartment(dept);
	}

	public void addDepartmentAndCategory() {
		Admin admin = getAdmin();
		DepartmentService deptService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department dept = CommonDataGenerator.generateDepartment(admin, false);
		deptId = deptService.saveDepartment(dept);

		addCategory();
	}

	public int addCategory() {
		Admin admin = getAdmin();
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = CommonDataGenerator.generateCategory(admin);
		categoryId = categoryService.saveCategory(deptId, category);

		return categoryId;
	}

	public ModerationStatus addInfo() {
		return addInfo(deptId, categoryId);
	}

	public ModerationStatus addInfo(int deptId, int categoryId) {
		User user = getInfoUser();
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = categoryService.getCategoryById(deptId, categoryId);

		ModerationService moderationService = (ModerationService) service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		Info info = CommonDataGenerator.generateInfo(user, category);
		ModerationStatus status = moderationService.saveInfo(info);

		infoId = info.getId();
		return status;
	}

	public void addCategoryAnnouncements(int num) {
		for (int i = 0; i < num; i++)
			addCategoryAnnouncement();
	}

	public ModerationStatus addCategoryAnnouncement() {
		return addCategoryAnnouncement(deptId, categoryId);
	}

	public ModerationStatus addCategoryAnnouncement(int deptId, int categoryId) {
		User user = getInfoUser();
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = categoryService.getCategoryById(deptId, categoryId);

		ModerationService moderationService = (ModerationService) service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		Info info = CommonDataGenerator.generateCategoryAnnouncement(user,
				category);
		ModerationStatus status = moderationService.saveInfo(info);

		infoId = info.getId();
		return status;
	}

	public void addDeptAnnouncements(int num) {
		for (int i = 0; i < num; i++)
			addDeptAnnouncement();
	}

	public ModerationStatus addDeptAnnouncement() {
		return addDeptAnnouncement(deptId, categoryId);
	}

	public ModerationStatus addDeptAnnouncement(int deptId, int categoryId) {
		User user = getInfoUser();
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = categoryService.getCategoryById(deptId, categoryId);

		ModerationService moderationService = (ModerationService) service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		Info info = CommonDataGenerator
				.generateDeptAnnouncement(user, category);
		ModerationStatus status = moderationService.saveInfo(info);

		infoId = info.getId();
		return status;
	}

	public void addSystemAnnouncements(int num) {
		for (int i = 0; i < num; i++)
			addSystemAnnouncement();
	}

	public ModerationStatus addSystemAnnouncement() {
		return addSystemAnnouncement(deptId, categoryId);
	}

	public ModerationStatus addSystemAnnouncement(int deptId, int categoryId) {
		User user = getInfoUser();
		CategoryService categoryService = (CategoryService) service
				.getDataManagementService(ServiceEnum.CATEGORY_SERVICE);
		Category category = categoryService.getCategoryById(deptId, categoryId);

		ModerationService moderationService = (ModerationService) service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		Info info = CommonDataGenerator.generateSystemAnnouncement(user,
				category);
		ModerationStatus status = moderationService.saveInfo(info);

		infoId = info.getId();
		return status;
	}

	public void addInfos(int num, int deptId, int categoryId) {
		for (int i = 0; i < num; i++)
			addInfo(deptId, categoryId);
	}

	public void addInfos(int num) {
		for (int i = 0; i < num; i++)
			addInfo();
	}

	public void addCmts(Info info, int num)
			throws SavingCommentToLockedInfoException {
		for (int i = 0; i < num; i++)
			addCmt(info);
	}

	public ModerationStatus addCmt(Info info)
			throws SavingCommentToLockedInfoException {
		User user = getCmtUser();
		ModerationService moderationService = (ModerationService) service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		Comment cmt = CommonDataGenerator.generateComment(user, info);
		ModerationStatus status = moderationService.saveComment(cmt);

		cmtId = cmt.getId();
		return status;
	}

	public int addComment(User user) throws SavingCommentToLockedInfoException {
		InfoService infoService = (InfoService) service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(categoryId, infoId, false);
		Comment cmt = CommonDataGenerator.generateComment(user, info);

		ModerationService moderationService = (ModerationService) service
				.getDataManagementService(ServiceEnum.MODERATION_SERVICE);
		moderationService.saveComment(cmt);

		return cmt.getId();
	}

	private void addCmtUser() {
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		List<Group> groups = groupService.getGroups();
		Group group = null;
		for (Group groupEntry : groups) {
			if ("默认用户群组".equals(groupEntry.getName())) {
				group = groupEntry;
				break;
			}
		}
		HashSet<Group> groupSet = new HashSet<Group>();
		groupSet.add(group);
		User user = CommonDataGenerator.generateUser();
		user.setGroups(groupSet);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		cmtUserId = userService.saveUser(user);
		userService.activateUser(cmtUserId);
	}

	private void addInfoUser() {
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		List<Group> groups = groupService.getGroups();
		Group group = null;
		for (Group groupEntry : groups) {
			if ("默认用户群组".equals(groupEntry.getName())) {
				group = groupEntry;
				break;
			}
		}
		HashSet<Group> groupSet = new HashSet<Group>();
		groupSet.add(group);
		User user = CommonDataGenerator.generateUser();
		user.setGroups(groupSet);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		infoUserId = userService.saveUser(user);
		userService.activateUser(infoUserId);
	}

	public int addModerator() {
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		List<Group> groups = groupService.getGroups();
		Group group = null;
		for (Group groupEntry : groups) {
			if ("默认审核员群组".equals(groupEntry.getName())) {
				group = groupEntry;
				break;
			}
		}
		HashSet<Group> groupSet = new HashSet<Group>();
		groupSet.add(group);
		Moderator moderator = CommonDataGenerator.generateModerator();
		moderator.setGroups(groupSet);
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		moderatorId = userService.saveModerator(moderator);
		userService.activateUser(moderatorId);
		return moderatorId;
	}

	public void removDefaultModeratorGroupAuditPermission(int categoryId)
			throws Assigned2SubGroupException {
		GroupService groupService = service
				.getDataManagementService(ServiceEnum.GROUP_SERVICE);
		List<Group> groups = groupService.getGroups();
		Group group = null;
		for (Group groupEntry : groups) {
			if ("默认审核员群组".equals(groupEntry.getName())) {
				group = groupEntry;
				break;
			}
		}

		Permission permissionTemplate = (Permission) service
				.getSpringBean("auditContentPermissionTemplate");
		permissionTemplate.setProprietaryMark(String.valueOf(categoryId));
		PermissionService permissionService = service
				.getDataManagementService(ServiceEnum.PERMISSION_SERVICE);
		Permission permissionToBeRemoved = permissionService.findByExample(
				permissionTemplate).get(0);
		group.removePermission(permissionToBeRemoved);
		groupService.updateGroup(group);
	}

	public void removDefaultModeratorGroup(Moderator moderator)
			throws Assigned2SubGroupException {
		Set<Group> groups = moderator.getGroups();
		Iterator<Group> iterator = groups.iterator();
		while (iterator.hasNext()) {
			Group groupEntry = iterator.next();
			if ("默认审核员群组".equals(groupEntry.getName())) {
				iterator.remove();
			}
		}

		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		userService.updateModerator(moderator);
	}

	public void addAttachment() {
		addAttachment(1);
	}

	public void addAttachment(int num) {
		InfoService infoService = service
				.getDataManagementService(ServiceEnum.INFO_SERVICE);
		Info info = infoService.getInfoById(categoryId, infoId);
		AttachmentService attachmentService = service
				.getDataManagementService(ServiceEnum.ATTACHMENT_SERVICE);
		for (int i = 0; i < num; i++) {
			Attachment attachment = CommonDataGenerator.generateAttachment(
					info, info.getAuthor());
			AttachmentInfo attachmentInfo = CommonDataGenerator
					.generateAttachmentInfo(attachment);
			attachment.setAttmInfo(attachmentInfo);

			attachmentService.saveAttachment(attachment);
			attachmentService.confirmEmbeddedImageUpload(attachment);;
			setAttachmentId(attachment.getId());
		}
	}

	public static Ranking generateRanking(String name, int point) {
		Ranking ranking = new Ranking();
		ranking.setName(name);
		ranking.setPoints(point);

		return ranking;
	}

	public static Ranking generateTimeRanking(String name, int point,
			double hours) {
		Ranking ranking = new Ranking();
		ranking.setName(name);
		ranking.setPoints(point);
		ranking.setTimeRanking(true);
		ranking.setLimitHours(hours);

		return ranking;
	}

	public static UserPoint generateUserPoint(User user, int point) {
		UserPoint userPoint = new UserPoint();
		userPoint.setPoints(point);
		userPoint.setUser(user);

		return userPoint;
	}

	public void removePermission(String permissionTemplateName, int id) {
		removePermission(permissionTemplateName, String.valueOf(id));
	}

	public void removePermission(String permissionTemplateName,
			String proprietaryMark) {
		LoginPermissionAuthenticationToken token = (LoginPermissionAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Set<Permission> userPermissions = token.getPermissions();
		Permission permissionTemplate = (Permission) service
				.getSpringBean(permissionTemplateName);
		permissionTemplate.setProprietaryMark(proprietaryMark);
		PermissionService permissionService = service
				.getDataManagementService(ServiceEnum.PERMISSION_SERVICE);
		Permission permissionToBeRemoved = permissionService.findByExample(
				permissionTemplate).get(0);
		userPermissions.remove(permissionToBeRemoved);
	}

	public void setUpAddCmtEnvironment_Moderated()
			throws DeptModerationListNotEmptyException, LoginException,
			CategoryModerationListNotEmptyException {
		service.beginTransaction();
		loginAdmin();
		DepartmentService departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		Department department = departmentService
				.getDepartmentById(getDeptId());
		department.setModerated(false);
		departmentService.updateDepartment(department);
		service.commitTransaction();

		service.beginTransaction();
		loginInfoUser();
		addInfo();
		service.commitTransaction();

		service.beginTransaction();
		loginAdmin();
		departmentService = service
				.getDataManagementService(ServiceEnum.DEPARTMENT_SERVICE);
		department = departmentService.getDepartmentById(getDeptId());
		department.setModerated(true);
		departmentService.updateDepartment(department);
		service.commitTransaction();
	}

	public void checkModeratorCategoryDigestingNum(int moderatorId,
			int categoryId, int expectedNum) {
		UserService userService = service
				.getDataManagementService(ServiceEnum.USER_SERVICE);
		Moderator moderator = userService.getModeratorById(moderatorId);
		Integer digestingNum = moderator.getCategoryDigestingNum(categoryId);
		assertEquals(expectedNum, digestingNum.intValue());
	}

	public void logoutUser() {

	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public UserSession getSession() {
		return session;
	}

	public void setSession(UserSession session) {
		this.session = session;
	}

	public int getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(int attachmentId) {
		this.attachmentId = attachmentId;
	}

	public int getRankingId_1() {
		return rankingId_1;
	}

	public void setRankingId_1(int rankingId_1) {
		this.rankingId_1 = rankingId_1;
	}

	public int getRankingId_2() {
		return rankingId_2;
	}

	public void setRankingId_2(int rankingId_2) {
		this.rankingId_2 = rankingId_2;
	}

	public int getRankingId_3() {
		return rankingId_3;
	}

	public void setRankingId_3(int rankingId_3) {
		this.rankingId_3 = rankingId_3;
	}

	public int[] getUserIds() {
		return userIds;
	}

	public void setUserIds(int[] userIds) {
		this.userIds = userIds;
	}

	public int[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(int[] groupIds) {
		this.groupIds = groupIds;
	}

	public int getTimeRankingId_1() {
		return timeRankingId_1;
	}

	public void setTimeRankingId_1(int timeRankingId_1) {
		this.timeRankingId_1 = timeRankingId_1;
	}

	public int getTimeRankingId_2() {
		return timeRankingId_2;
	}

	public void setTimeRankingId_2(int timeRankingId_2) {
		this.timeRankingId_2 = timeRankingId_2;
	}

	public int getTimeRankingId_3() {
		return timeRankingId_3;
	}

	public void setTimeRankingId_3(int timeRankingId_3) {
		this.timeRankingId_3 = timeRankingId_3;
	}

	public int getInfoUserId() {
		return infoUserId;
	}

	public void setInfoUserId(int infoUserId) {
		this.infoUserId = infoUserId;
	}

	public int getCmtUserId() {
		return cmtUserId;
	}

	public void setCmtUserId(int cmtUserId) {
		this.cmtUserId = cmtUserId;
	}

	public int getCmtId() {
		return cmtId;
	}

	public void setCmtId(int cmtId) {
		this.cmtId = cmtId;
	}

	public int getModeratorId() {
		return moderatorId;
	}

	public void setModeratorId(int moderatorId) {
		this.moderatorId = moderatorId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
}
