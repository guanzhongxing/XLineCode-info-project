package com.vertonur.dms;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.dms.exception.SavingCommentToLockedInfoException;
import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.ModerationStatus;

public interface ModerationService {

	/**
	 * Modify infos or comments of a user as a moderator and generate a
	 * moderation log for the operation
	 * 
	 * @param originalContent
	 * @param info
	 * @param moderator
	 * @param reason
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void modifyContent(String originalContent, AbstractInfo content,
			int moderatorId, String reason);

	/**
	 * Delete infos of a user as a moderator and generate a moderation log for
	 * the operation
	 * 
	 * @param infoId
	 * @param moderatorId
	 * @param reason
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void deleteInfo(int infoId, int moderatorId, String reason);

	/**
	 * Delete comments of a user as a moderator and generate a moderation log
	 * for the operation
	 * 
	 * @param cmtId
	 * @param moderatorId
	 * @param reason
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void deleteComment(int cmtId, int moderatorId, String reason);

	/**
	 * Approve a new info as a moderator
	 * 
	 * @param infoId
	 * @param logId
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void approveInfo(int infoId, int logId);

	/**
	 * Reject a new info as a moderator
	 * 
	 * @param infoId
	 * @param logId
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void rejectInfo(int infoId, int logId);

	/**
	 * Approve a new comment as a moderator
	 * 
	 * @param cmtId
	 * @param logId
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void approveComment(int cmtId, int logId);

	/**
	 * Reject a new comment as a moderator
	 * 
	 * @param cmtId
	 * @param logId
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void rejectComment(int cmtId, int logId);

	/**
	 * Get the log list according to the moderation status, the pagination index
	 * , the moderator and the category id
	 * 
	 * @param categoryId
	 * @param start
	 * @param moderatorId
	 * @param statuses
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public List<ModerationLog> getLogs(int categoryId, int start,
			int moderatorId, ModerationStatus... statuses);

	/**
	 * Get the log list according to the moderation status, the pagination start
	 * and the moderator
	 * 
	 * @param start
	 * @param moderatorId
	 * @param statuses
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public List<ModerationLog> getLogs(int start, int moderatorId,
			ModerationStatus... statuses);

	/**
	 * Get log num of the specified categoryId ,moderator and statuses
	 * 
	 * @param categoryId
	 * @param moderatorId
	 * @param statuses
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public long getLogNum(int categoryId, int moderatorId,
			ModerationStatus... statuses);

	/**
	 * Get log num of the specified moderator and the specified statuses
	 * 
	 * @param moderatorId
	 * @param statuses
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public long getLogNum(int moderatorId, ModerationStatus... statuses);

	/**
	 * Get num of the pending logs
	 * 
	 * @param categoryId
	 * @return
	 */
	public long getPendingLogNum(int categoryId);

	/**
	 * Used by a user to save the info, if moderation is turn on for a category
	 * or department, the returned status will be DEFERRED ,which means a
	 * mankind audit is needed for the content of the new info and the info is
	 * waiting to be auditted.<br>
	 * Status of PENDING means that moderators are busy with their moderation
	 * and the info is waiting to be assigned to a moderator.<br>
	 * Status of APPROVED means that no mankind intervention is needed and the
	 * info is publicly visible
	 * 
	 * @param info
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_USER)
	public ModerationStatus saveInfo(Info info);

	/**
	 * Used by a user to update the info
	 * 
	 * @param info
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_USER)
	public ModerationStatus updateInfo(Info info);

	/**
	 * Used by a user to save the comment, if moderation is turn on for a
	 * category or department, the returned status will be DEFERRED ,which means
	 * a mankind audit is needed for the content of the new comment and the
	 * comment is waiting to be auditted.<br>
	 * Status of PENDING means that moderators are busy with their moderation
	 * and the comment is waiting to be assigned to a moderator.<br>
	 * Status of APPROVED means that no mankind intervention is needed and the
	 * comment is publicly visible
	 * 
	 * @param cmt
	 * @return
	 * @throws SavingCommentToLockedInfoException
	 */
	@RolesAllowed({ RoleEnum.ROLE_USER, RoleEnum.ROLE_GUEST })
	public ModerationStatus saveComment(Comment cmt)
			throws SavingCommentToLockedInfoException;

	/**
	 * Used by a user to update the comment
	 * 
	 * @param cmt
	 * @return
	 */
	@RolesAllowed(RoleEnum.ROLE_USER)
	public ModerationStatus updateComment(Comment cmt);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void setDigestionNum(int digestionNum);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void setAssignPendingLogInterval(int interval);

	/**
	 * Move an info from an enclosing category to another category
	 * 
	 * @param infoId
	 * @param newCategoryId
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void moveInfo(int infoId, int newCategoryId, int moderatorId,
			String reason);

	/**
	 * Lock an info and no comment can be created for this info
	 * 
	 * @param infoId
	 * @param moderatorId
	 * @param reason
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void lockInfo(int infoId, int moderatorId, String reason);

	/**
	 * 
	 * @param infoId
	 * @param moderatorId
	 * @param reason
	 */
	@RolesAllowed(RoleEnum.ROLE_MODERATOR)
	public void unlockInfo(int infoId, int moderatorId, String reason);
}
