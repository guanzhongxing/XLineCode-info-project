package com.vertonur.dms;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.PrivateMessage;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadInfo;
import com.vertonur.pojo.UserReadPrivateMessage;
import com.vertonur.pojo.statistician.InfoStatistician;

public interface InfoService {

	/**
	 * Delete the info created by the user that is invoking this method
	 * 
	 * @param info
	 */
	@RolesAllowed(RoleEnum.ROLE_USER)
	public void deleteInfo(int infoId);

	public long getAllInfoNum();

	public List<Info> getInfosByTitle(String infoTitle);

	public Info getInfoById(int infoId);

	public List<Info> getHottestInfosByCategory(int userId, int categoryId);

	public List<Info> getRecentInfosByCategory(int userId, int categoryId);

	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_MODERATOR })
	public void saveUserReadInfo(UserReadInfo userReadInfo);

	public List<Info> getNewInfosByCategory(int categoryId, int startPoint);

	public List<Info> getInfosByCategory(int userId, int categoryId, int start)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException;

	public long getInfoNumByCategory(int categoryId);

	public List<Info> getInfosByUser(User user, int start);

	public long getInfoNumByCreator(User user);

	/**
	 * Get comments of the specified info with the pagination start index
	 * 
	 * @param info
	 * @param paginationStart
	 * @return a comment list with entries num equal with or less than system
	 *         config pagination size, or 0 if there is no more comment
	 */
	public List<Comment> getCommentsByInfo(Info info, int paginationStart);

	public long getCommentNumByInfo(Info info);

	/**
	 * Get start index of the page the comment in
	 * 
	 * @param commentId
	 * @return
	 */
	public int getCommentPageIndex(int commentId);

	/**
	 * Delete the comment created by the user that is invoking this method
	 * 
	 * @param comment
	 */
	@RolesAllowed(RoleEnum.ROLE_USER)
	public void deleteComment(int commentId);

	public long getCommentNumOfAllInfos();

	public List<Comment> getCommentsByUser(User theUser, int paginationStart);

	public long getCommentNumByUser(User user);

	public Comment getCommentById(int commentId);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public int savePrivateMsg(PrivateMessage pm);

	public PrivateMessage getPrivateMessageById(int pmId);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public void saveUserReadPriateMsg(UserReadPrivateMessage userReadPm);

	public List<PrivateMessage> getNewPrivateMsgsByReceiver(User user);

	public List<PrivateMessage> getPrivateMsgsByReceiver(User user, int start);

	public long getPrivateMsgNumByReceiver(User user);

	public long getPrivateMsgNumByAuthor(User user);

	public List<PrivateMessage> getPrivateMsgsByAuthor(User user, int start);

	public void updateInfoStatistician(int infoId, InfoStatistician statistician);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public void lockInfo(int infoId);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public void unlockInfo(int infoId);

	List<Info> getCategoryAnnouncements(int categoryId);

	List<Info> getDeptAnnouncements(int departmentId);

	List<Info> getSystemAnnouncements();
}
