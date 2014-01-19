package com.vertonur.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Admin;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.Info.InfoType;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.ModerationStatus;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Group.GroupType;
import com.vertonur.pojo.statistician.CategoryStatistician;

public class CommonDataGenerator {
	public static String PASSWORD = "12345";

	public static Admin generateAdmin() {
		Admin admin = new Admin();
		admin.setName("admin");
		admin.setActivated(true);
		admin.setGender(1);
		admin.setEmail(UUID.randomUUID().toString());
		admin.setPassword(PASSWORD);

		return admin;
	}

	public static User generateUser() {
		return generateUsers(1).get(0);
	}

	public static List<User> generateUsers(int userNum) {
		List<User> users = new ArrayList<User>();
		for (int i = 1; i <= userNum; i++) {
			User user = new User();
			user.setName("user_" + i);
			user.setActivated(true);
			user.setGender(1);
			user.setEmail(UUID.randomUUID().toString());
			user.setPassword(PASSWORD);
			users.add(user);
		}

		return users;
	}

	public static Moderator generateModerator() {
		return generateModerators(1).get(0);
	}

	public static List<Moderator> generateModerators(int moderatorNum) {
		List<Moderator> moderators = new ArrayList<Moderator>();
		for (int i = 1; i <= moderatorNum; i++) {
			Moderator moderator = new Moderator();
			moderator.setName("user_" + i);
			moderator.setActivated(true);
			moderator.setGender(1);
			moderator.setPassword(PASSWORD);
			moderator.setEmail(UUID.randomUUID().toString());
			moderators.add(moderator);
		}

		return moderators;
	}

	public static Category generateCategory(Admin admin) {
		Category category = new Category();
		category.setCreatedTime(new Date());
		category.setName("test");
		category.setCreator(admin);
		CategoryStatistician statistician = new CategoryStatistician();
		category.setStatistician(statistician);

		return category;
	}

	public static Department generateDepartment(Admin admin, boolean moderated) {
		Department dept = new Department();
		dept.setCreatedTime(new Date());
		dept.setName("test");
		dept.setDescription("desc");
		dept.setCreator(admin);
		dept.setModerated(moderated);

		return dept;
	}

	public static Comment generateComment(User user, Info info) {
		Comment cmt = new Comment();
		cmt.setAuthor(user);
		cmt.setContent("test");
		cmt.setCreatedTime(new Date());
		cmt.setSubject("test");
		cmt.setInfo(info);

		return cmt;
	}

	public static Info generateInfo(User user, Category category) {
		return generateInfo(user, category, "test");
	}

	public static Info generateInfo(User user, Category category, String content) {
		Info info = new Info();
		info.setAuthor(user);
		info.setCategory(category);
		info.setContent(content);
		info.setCreatedTime(new Date());

		return info;
	}

	public static Info generateCategoryAnnouncement(User user, Category category) {
		return generateCategoryAnnouncement(user, category, "test");
	}

	public static Info generateCategoryAnnouncement(User user,
			Category category, String content) {
		Info info = generateInfo(user, category, content);
		info.setInfoType(InfoType.CATEGORY_ANNOUNCEMENT);

		return info;
	}

	public static Info generateDeptAnnouncement(User user, Category category) {

		return generateDeptAnnouncement(user, category, "test");
	}

	public static Info generateDeptAnnouncement(User user, Category category,
			String content) {
		Info info = generateInfo(user, category, content);
		info.setInfoType(InfoType.DEPARTMENT_ANNOUNCEMENT);

		return info;
	}

	public static Info generateSystemAnnouncement(User user, Category category) {

		return generateSystemAnnouncement(user, category, "test");
	}

	public static Info generateSystemAnnouncement(User user, Category category,
			String content) {
		Info info = generateInfo(user, category, content);
		info.setInfoType(InfoType.SYSTEM_ANNOUNCEMENT);

		return info;
	}

	public static ModerationLog generateModerationLog(Admin admin,
			ModerationStatus status, AbstractInfo info) {
		ModerationLog log = new ModerationLog();
		log.setArchiveContent(info.getContent());
		log.setInfoAuthor(info.getAuthor());
		log.setModeratedDate(new Date());
		log.setModerator(admin);
		log.setModifiedInfo(info);
		log.setReason("test");
		log.setStatus(status);

		return log;
	}

	public static Group generateGroup() {
		Group group = new Group();
		group.setDeletable(true);
		group.setDeprecated(false);
		group.setDescription("test group");
		group.setEditable(true);
		group.setGroupType(GroupType.GENERIC_USR);
		group.setName("test_group");
		group.setNestedLevel(0);

		return group;
	}
}
