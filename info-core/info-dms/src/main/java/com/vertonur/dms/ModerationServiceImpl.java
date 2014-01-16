package com.vertonur.dms;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.dao.api.CategoryDAO;
import com.vertonur.dao.api.CommentDAO;
import com.vertonur.dao.api.InfoDAO;
import com.vertonur.dao.api.ModerationLogDAO;
import com.vertonur.dao.api.ModeratorDAO;
import com.vertonur.dms.exception.FailToSetPropertyException;
import com.vertonur.dms.exception.SavingCommentToLockedInfoException;
import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.LogType;
import com.vertonur.pojo.ModerationLog.ModerationStatus;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.State;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadInfo;
import com.vertonur.pojo.config.CommentConfig;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.statistician.CategoryStatistician;
import com.vertonur.pojo.statistician.InfoStatistician;
import com.vertonur.pojo.statistician.UserMsgStatistician;

public class ModerationServiceImpl extends GenericService implements
		ModerationService {
	private static Logger logger = LoggerFactory
			.getLogger(ModerationServiceImpl.class.getCanonicalName());

	private CategoryService categoryService;

	private InfoDAO infoDao;
	private CommentDAO commentDao;
	private ModeratorDAO moderatorDao;
	private ModerationLogDAO moderationLogDao;
	private ModerationConfig moderationConfig;
	private ModeratorManager moderatorManager;
	private RuntimeParameterService runtimeParameterService;

	protected ModerationServiceImpl() {
		categoryService = new CategoryServiceImpl();
		infoDao = manager.getInfoDAO();
		commentDao = manager.getCommentDAO();
		moderationLogDao = manager.getModerationLogDAO();
		moderatorDao = manager.getModeratorDAO();
		runtimeParameterService = new RuntimeParameterServiceImpl();
		moderationConfig = runtimeParameterService.getModerationConfig();
		moderatorManager = ModeratorManager.getManager();
	}

	private ModerationStatus addLog(AbstractInfo content,
			ModerationStatus status, int categoryId) {
		return addLog(content, status, null, null, categoryId);
	}

	private ModerationStatus addLog(AbstractInfo content,
			ModerationStatus status, String reason, String originalContent,
			int categoryId) {
		ModeratorManager moderatorManager = ModeratorManager.getManager();
		Moderator moderator = moderatorManager.getModerator(categoryId);
		if (moderator == null)
			status = ModerationStatus.PENDING;
		ModerationLog log = newLog(moderator, content, status, reason,
				originalContent);
		moderationLogDao.saveLog(log);

		return status;
	}

	private ModerationLog newLog(Moderator moderator, AbstractInfo content,
			ModerationStatus status, String reason, String originalContent) {
		ModerationLog log = new ModerationLog();
		if (ModerationStatus.MODIFIED.equals(status)
				|| ModerationStatus.DELETED.equals(status))
			log.setArchiveContent(originalContent);
		log.setInfoAuthor(content.getAuthor());
		log.setModerator(moderator);
		log.setModeratedDate(new Date());
		log.setModifiedInfo(content);
		log.setStatus(status);
		log.setReason(reason);
		if (content instanceof Info) {
			Info info = (Info) content;
			log.setCategory(info.getCategory());
			log.setLogType(LogType.INFO);
		} else {
			Comment cmt = (Comment) content;
			Info info = cmt.getInfo();
			log.setCategory(info.getCategory());
			log.setLogType(LogType.CMT);
		}

		return log;
	}

	@Override
	public List<ModerationLog> getLogs(int categoryId, int start,
			int moderatorId, ModerationStatus... statuses) {
		return moderationLogDao.getLogs(categoryId, start,
				moderationConfig.getMdrLgPgnOffset(), moderatorId, statuses);
	}

	@Override
	public void modifyContent(String originalContent, AbstractInfo content,
			int moderatorId, String reason) {
		Moderator moderator = moderatorDao.getModeratorById(moderatorId);
		ModerationLog log = newLog(moderator, content,
				ModerationStatus.MODIFIED, reason, originalContent);
		moderationLogDao.saveLog(log);
		if (content instanceof Info) {
			Info info = (Info) content;
			updateInfoData(info);
		} else if (content instanceof Comment)
			commentDao.updateComment((Comment) content);
	}

	@Override
	public void deleteInfo(int infoId, int moderatorId, String reason) {
		Info info = infoDao.getInfoById(infoId);
		deleteContent(info, moderatorId, reason);
	}

	@Override
	public void deleteComment(int cmtId, int moderatorId, String reason) {
		Comment cmt = commentDao.getCommentById(cmtId);
		deleteContent(cmt, moderatorId, reason);
	}

	private void deleteContent(AbstractInfo content, int moderatorId,
			String reason) {
		Moderator moderator = moderatorDao.getModeratorById(moderatorId);
		ModerationLog log = newLog(moderator, content,
				ModerationStatus.DELETED, reason, content.getContent());
		moderationLogDao.saveLog(log);

		deleteContent(content);
	}

	void deleteContent(AbstractInfo content) {
		if (content instanceof Info) {
			Info info = (Info) content;

			Category category = info.getCategory();
			int categoryId = category.getId();
			int departmentId = category.getDepartment().getId();
			CategoryStatistician statistician = category.getStatistician();
			decreaseCategoryStatisticianNum(info, statistician, categoryId,
					departmentId);
			decreaseUserMsgStatisticianNum(info);

			State state = content.getState();
			state.setDeprecated(true);
			updateInfoData(info);

			updateLatestInfo(info, statistician, categoryId, departmentId);
		} else if (content instanceof Comment) {
			Comment cmt = (Comment) content;

			Info info = cmt.getInfo();
			Category category = info.getCategory();
			CategoryStatistician categoryStatistician = category
					.getStatistician();
			categoryStatistician.decreaseCommentNum();
			categoryService.updateStatistician(
					category.getDepartment().getId(), category.getId(),
					categoryStatistician);

			InfoStatistician statistician = info.getStatistician();
			statistician.decreaseCommentNum();
			updateInfoStatistician(info, statistician);

			decreaseUserMsgStatisticianNum(cmt);

			State state = content.getState();
			state.setDeprecated(true);
			commentDao.updateComment(cmt);

			updateLatestComment(cmt, info);
		}
	}

	@Override
	public void approveInfo(int infoId, int logId) {
		moderateInfo(infoId, logId, ModerationStatus.APPROVED);
	}

	@Override
	public void rejectInfo(int infoId, int logId) {
		moderateInfo(infoId, logId, ModerationStatus.REJECTED);
	}

	private void moderateInfo(int infoId, int logId, ModerationStatus status) {
		ModerationLog log = moderationLogDao.getLog(logId);
		log.setStatus(status);
		moderationLogDao.updateLog(log);

		Info info = infoDao.getPendingInfoById(infoId);
		moderatorManager.decreaseModetorDigestingNum(
				info.getCategory().getId(), (Moderator) log.getModerator());

		if (info.getState().isModified())
			postInfoModification(info, status);
		else
			postInfoCreation(info, status);
	}

	@Override
	public void approveComment(int cmtId, int logId) {
		moderateComment(cmtId, logId, ModerationStatus.APPROVED);
	}

	@Override
	public void rejectComment(int cmtId, int logId) {
		moderateComment(cmtId, logId, ModerationStatus.REJECTED);

	}

	private void moderateComment(int cmtId, int logId, ModerationStatus status) {
		ModerationLog log = moderationLogDao.getLog(logId);
		log.setStatus(status);
		moderationLogDao.updateLog(log);

		Comment cmt = commentDao.getPendingCommentById(cmtId);
		moderatorManager.decreaseModetorDigestingNum(cmt.getInfo()
				.getCategory().getId(), (Moderator) log.getModerator());
		if (cmt.getState().isModified())
			postCommentModification(cmt, status);
		else
			postCommentCreation(cmt, status);
	}

	@Override
	public long getLogNum(int categoryId, int moderatorId,
			ModerationStatus... statuses) {
		return moderationLogDao.getLogNum(categoryId, moderatorId, statuses);
	}

	@Override
	public ModerationStatus saveInfo(Info info) {
		persistInfo(info);
		return frontInfoModeration(info, false);
	}

	@Override
	public ModerationStatus updateInfo(Info info) {
		State state = info.getState();
		state.setPending(true);
		state.setModified(true);
		updateInfoData(info);
		return frontInfoModeration(info, true);
	}

	private ModerationStatus frontInfoModeration(Info info,
			boolean fromModification) {
		Category category = info.getCategory();
		int categoryId = category.getId();
		CategoryStatistician statistician = category.getStatistician();

		Department department = category.getDepartment();
		if (department.isModerated() || category.isModerated()) {
			int departmentId = department.getId();

			if (fromModification) {
				decreaseCategoryStatisticianNum(info, statistician, categoryId,
						departmentId);
				updateLatestInfo(info, statistician, categoryId, departmentId);
				decreaseUserMsgStatisticianNum(info);
			}

			statistician.increaseToModerateNum();
			categoryService.updateStatistician(departmentId, categoryId,
					statistician);
			return addLog(info, ModerationStatus.DEFERRED, categoryId);
		} else {
			if (fromModification)
				postInfoModification(info, ModerationStatus.APPROVED);
			else
				postInfoCreation(info, ModerationStatus.APPROVED);

			return ModerationStatus.APPROVED;
		}
	}

	private void decreaseCategoryStatisticianNum(Info info,
			CategoryStatistician statistician, int categoryId, int departmentId) {
		statistician.decreaseInfoNum();
		InfoStatistician infoStatistician = info.getStatistician();
		int cmtNum = infoStatistician.getCommentNum();
		int categoryCmtNum = statistician.getCommentNum();
		statistician.setCommentNum(categoryCmtNum - cmtNum);

		categoryService.updateStatistician(departmentId, categoryId,
				statistician);
	}

	private void updateLatestInfo(Info info, CategoryStatistician statistician,
			int categoryId, int departmentId) {
		InfoStatistician infoStatistician = info.getStatistician();
		if (infoStatistician.isLatestOne()) {
			infoStatistician.setLatestOne(false);
			updateInfoStatistician(info, infoStatistician);

			Info latestInfo = infoDao.getLatestInfo(categoryId,
					info.getCreatedTime());
			if (latestInfo != null) {
				InfoStatistician latestInfoStatistician = latestInfo
						.getStatistician();
				latestInfoStatistician.setLatestOne(true);
				updateInfoStatistician(latestInfo, latestInfoStatistician);
			}

			statistician.setLatestInfo(latestInfo);
			categoryService.updateStatistician(departmentId, categoryId,
					statistician);
		}
	}

	private void decreaseUserMsgStatisticianNum(AbstractInfo content) {
		User user = content.getAuthor();
		UserMsgStatistician userStatistician = user.getStatistician();
		UserService userService = new UserServiceImpl();
		if (content instanceof Info) {
			int index = 0;
			Info info = (Info) content;
			CommentConfig cmtConfig = runtimeParameterService
					.getCommentConfig();
			while (true) {
				List<Comment> cmts = commentDao.getCommentsByInfo(info, index,
						cmtConfig.getCmtPgnOffset());
				int size = cmts.size();
				if (size > 0) {
					for (Comment cmt : cmts) {
						cmt.getState().setDeprecated(true);
						commentDao.updateComment(cmt);

						User cmtUser = cmt.getAuthor();
						UserMsgStatistician cmtUserStatistician = cmtUser
								.getStatistician();
						cmtUserStatistician.decreaseCommentNum();
						userService.updateUserStatistician(cmtUser.getId(),
								cmtUserStatistician);
					}
					index = size;
				} else
					break;
			}

			userStatistician.decreaseInfoNum();
		} else
			userStatistician.decreaseCommentNum();

		userService.updateUserStatistician(user.getId(), userStatistician);
	}

	private void postInfoCreation(Info info, ModerationStatus status) {
		Category category = info.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		if (ModerationStatus.APPROVED.equals(status)) {
			State state = info.getState();
			state.setPending(false);
			updateInfoData(info);

			Department department = category.getDepartment();
			int departmentId = department.getId();
			// set category's statistician
			setLatestInfo(statistician, info);
			increaseUserMsgStatistician(info);
			statistician.increaseInfoNum();
			if (department.isModerated() || category.isModerated()) {
				statistician.decreaseToModerateNum();
			}
			categoryService.updateStatistician(departmentId, category.getId(),
					statistician);
		} else if (ModerationStatus.REJECTED.equals(status)) {
			rejectInfo(statistician, info);
		}
	}

	private void postInfoModification(Info info, ModerationStatus status) {
		postInfoModification(info, status, false);
	}

	private void postInfoModification(Info info, ModerationStatus status,
			boolean fromModerator) {
		Category category = info.getCategory();
		CategoryStatistician statistician = category.getStatistician();
		if (ModerationStatus.APPROVED.equals(status)) {
			Department department = category.getDepartment();
			int departmentId = department.getId();

			State state = info.getState();
			state.setPending(false);
			state.setModified(false);
			updateInfoData(info);

			// set category's statistician
			if (!fromModerator
					&& (department.isModerated() || category.isModerated())) {
				setLatestInfo(statistician, info);
				increaseUserMsgStatistician(info);

				InfoStatistician infoStatistician = info.getStatistician();
				int cmtNum = infoStatistician.getCommentNum();
				int categoryCmtNum = statistician.getCommentNum();
				statistician.setCommentNum(categoryCmtNum + cmtNum);
				statistician.increaseInfoNum();
				statistician.decreaseToModerateNum();
				categoryService.updateStatistician(departmentId,
						category.getId(), statistician);
			}
		} else if (ModerationStatus.REJECTED.equals(status)) {
			rejectInfo(statistician, info);
		}
	}

	private void setLatestInfo(CategoryStatistician statistician, Info info) {
		Info latestInfo = statistician.getLatestInfo();
		InfoStatistician infoStatistician = info.getStatistician();
		if (latestInfo != null) {
			if (latestInfo.getCreatedTime().before(info.getCreatedTime())) {
				InfoStatistician latestInfoStatistician = latestInfo
						.getStatistician();
				latestInfoStatistician.setLatestOne(false);
				updateInfoStatistician(latestInfo, latestInfoStatistician);

				infoStatistician.setLatestOne(true);
				statistician.setLatestInfo(info);
			}
		} else {
			infoStatistician.setLatestOne(true);
			statistician.setLatestInfo(info);
		}
		updateInfoStatistician(info, infoStatistician);
	}

	private void rejectInfo(CategoryStatistician statistician, Info info) {
		State state = info.getState();
		state.setDeprecated(true);
		state.setPending(false);
		infoDao.updateInfo(info);

		Category category = info.getCategory();
		statistician.decreaseToModerateNum();
		categoryService.updateStatistician(category.getDepartment().getId(),
				category.getId(), statistician);
	}

	private void increaseUserMsgStatistician(AbstractInfo info) {
		User user = info.getAuthor();
		UserMsgStatistician userStatistician = user.getStatistician();
		if (info instanceof Info)
			userStatistician.increaseInfoNum();
		else
			userStatistician.increaseCommentNum();
		UserService userService = new UserServiceImpl();
		userService.updateUserStatistician(user.getId(), userStatistician);
	}

	@Override
	public ModerationStatus saveComment(Comment cmt)
			throws SavingCommentToLockedInfoException {
		Info info = cmt.getInfo();
		if (info.isLocked())
			throw new SavingCommentToLockedInfoException(
					"Saving a comment to a locked info with id:" + info.getId()
							+ " is not allowed.");

		cmt.getState().setPending(true);

		commentDao.saveComment(cmt);
		return frontCommentModeration(cmt, false);
	}

	@Override
	public ModerationStatus updateComment(Comment cmt) {
		State state = cmt.getState();
		state.setModified(true);
		state.setPending(true);
		commentDao.updateComment(cmt);
		return frontCommentModeration(cmt, true);
	}

	private void postCommentCreation(Comment cmt, ModerationStatus status) {
		Info info = cmt.getInfo();
		Category category = info.getCategory();
		int categoryId = category.getId();
		CategoryStatistician categoryStatistician = category.getStatistician();
		Department department = category.getDepartment();
		int departmentId = department.getId();
		State state = cmt.getState();
		if (ModerationStatus.APPROVED.equals(status)) {
			state.setPending(false);
			updateCommentState(cmt, state);
			InfoStatistician statistician = info.getStatistician();
			setInfoLatestCmt(statistician, cmt);

			increaseUserMsgStatistician(cmt);
			statistician.increaseCommentNum();
			updateInfoStatistician(info, statistician);

			if (department.isModerated() || category.isModerated()
					|| info.isModerated())
				categoryStatistician.decreaseToModerateNum();

			categoryStatistician.increaseCommentNum();
			categoryService.updateStatistician(departmentId, categoryId,
					categoryStatistician);
		} else if (ModerationStatus.REJECTED.equals(status)) {
			state.setDeprecated(true);
			state.setPending(false);
			updateCommentState(cmt, state);
			categoryStatistician.decreaseToModerateNum();
			categoryService.updateStatistician(departmentId, categoryId,
					categoryStatistician);
		}
	}

	private void postCommentModification(Comment cmt, ModerationStatus status) {
		Info info = cmt.getInfo();
		Category category = info.getCategory();
		int categoryId = category.getId();
		CategoryStatistician categoryStatistician = category.getStatistician();
		Department department = category.getDepartment();
		int departmentId = department.getId();
		State state = cmt.getState();
		if (ModerationStatus.APPROVED.equals(status)) {
			state.setPending(false);
			state.setModified(false);
			updateCommentState(cmt, state);

			if (department.isModerated() || category.isModerated()
					|| info.isModerated()) {
				InfoStatistician statistician = info.getStatistician();
				setInfoLatestCmt(statistician, cmt);

				increaseUserMsgStatistician(cmt);
				statistician.increaseCommentNum();
				updateInfoStatistician(info, statistician);

				categoryStatistician.decreaseToModerateNum();
				categoryStatistician.increaseCommentNum();
				categoryService.updateStatistician(departmentId, categoryId,
						categoryStatistician);
			}
		} else if (ModerationStatus.REJECTED.equals(status)) {
			state.setDeprecated(true);
			state.setPending(false);
			updateCommentState(cmt, state);
			categoryStatistician.decreaseToModerateNum();
			categoryService.updateStatistician(departmentId, categoryId,
					categoryStatistician);
		}
	}

	private void setInfoLatestCmt(InfoStatistician statistician, Comment cmt) {
		Comment latestComment = statistician.getLatestComment();
		if (latestComment != null) {
			if (latestComment.getCreatedTime().before(cmt.getCreatedTime())) {
				markCommentAsLatest(latestComment, false);
				markCommentAsLatest(cmt, true);
				statistician.setLatestComment(cmt);
			}
		} else {
			markCommentAsLatest(cmt, true);
			statistician.setLatestComment(cmt);
		}
	}

	private ModerationStatus frontCommentModeration(Comment cmt,
			boolean fromModification) {
		Info info = cmt.getInfo();
		Category category = info.getCategory();
		Department department = category.getDepartment();
		if (department.isModerated() || category.isModerated()
				|| info.isModerated()) {
			int categoryId = category.getId();
			int departmentId = department.getId();
			CategoryStatistician categoryStatistician = category
					.getStatistician();
			categoryStatistician.increaseToModerateNum();

			if (fromModification) {
				categoryStatistician.decreaseCommentNum();

				InfoStatistician statistician = info.getStatistician();
				statistician.decreaseCommentNum();

				updateLatestComment(cmt, info);
				decreaseUserMsgStatisticianNum(cmt);
			}
			categoryService.updateStatistician(departmentId, categoryId,
					categoryStatistician);
			return addLog(cmt, ModerationStatus.DEFERRED, categoryId);
		} else {
			if (fromModification)
				postCommentModification(cmt, ModerationStatus.APPROVED);
			else
				postCommentCreation(cmt, ModerationStatus.APPROVED);

			return ModerationStatus.APPROVED;
		}
	}

	private void updateLatestComment(Comment cmt, Info info) {
		InfoStatistician statistician = info.getStatistician();
		if (cmt.isLatestOne()) {
			markCommentAsLatest(cmt, false);
			Comment latestCmt = commentDao.getLatestComment(info.getId(),
					cmt.getCreatedTime());
			if (latestCmt != null) {
				latestCmt.setLatestOne(true);
				commentDao.updateComment(latestCmt);
			}
			statistician.setLatestComment(latestCmt);
			updateInfoStatistician(info, statistician);
		}
	}

	private void markCommentAsLatest(Comment cmt, boolean isLatest) {
		cmt.setLatestOne(isLatest);
		commentDao.updateComment(cmt);
	}

	private void updateCommentState(Comment cmt, State state) {
		cmt.setState(state);
		commentDao.updateComment(cmt);
	}

	private void persistInfo(Info info) {
		InfoStatistician infoStatistician = new InfoStatistician();
		info.setStatistician(infoStatistician);
		info.getState().setPending(true);
		infoDao.saveInfo(info);

		UserReadInfo userReadInfo = new UserReadInfo(info.getAuthor(), info,
				new Date());
		infoDao.saveUserReadInfo(userReadInfo);
	}

	private void updateInfoData(Info updatedInfo) {
		infoDao.updateInfo(updatedInfo);
	}

	@Override
	public void setDigestionNum(int digestionNum) {
		moderatorManager.setDigestionNum(digestionNum);
		moderationConfig = runtimeParameterService.getModerationConfig();
		moderationConfig.setDigestionNum(digestionNum);
		runtimeParameterService.updateModerationConfig(moderationConfig);
	}

	@Override
	public long getPendingLogNum(int categoryId) {
		return moderationLogDao.getPendingLogNum(categoryId);
	}

	void updateInfoStatistician(Info info, InfoStatistician statistician) {
		info.setStatistician(statistician);
		infoDao.updateInfo(info);
	}

	@Override
	public List<ModerationLog> getLogs(int start, int moderatorId,
			ModerationStatus... statuses) {
		return moderationLogDao.getLogs(null, start,
				moderationConfig.getMdrLgPgnOffset(), moderatorId, statuses);
	}

	@Override
	public long getLogNum(int moderatorId, ModerationStatus... statuses) {
		return moderationLogDao.getLogNum(moderatorId, statuses);
	}

	@Override
	public void setAssignPendingLogInterval(int interval) {
		moderatorManager.setAssignPendingLogInterval(interval);
		moderationConfig = runtimeParameterService.getModerationConfig();
		moderationConfig.setAssignPendingLogInterval(interval);
		runtimeParameterService.updateModerationConfig(moderationConfig);
	}

	@Override
	public void moveInfo(int infoId, int newCategoryId, int moderatorId,
			String reason) {
		Info info = infoDao.getInfoById(infoId);
		Moderator moderator = moderatorDao.getModeratorById(moderatorId);
		ModerationLog log = newLog(moderator, info, ModerationStatus.MOVED,
				reason, info.getContent());
		moderationLogDao.saveLog(log);

		Category oldCategory = info.getCategory();
		InfoStatistician infoStatistic = info.getStatistician();
		CategoryStatistician categoryStatistic = oldCategory.getStatistician();
		categoryStatistic.decreaseInfoNum();
		int cmtNum = categoryStatistic.getCommentNum();
		cmtNum -= infoStatistic.getCommentNum();
		categoryStatistic.setCommentNum(cmtNum);
		CategoryService categoryService = new CategoryServiceImpl();
		categoryService.updateStatistician(oldCategory.getDepartment().getId(),
				oldCategory.getId(), categoryStatistic);

		CategoryDAO categoryDao = manager.getCategoryDAO();
		Category newCategory = categoryDao.getCategoryById(newCategoryId);
		categoryStatistic = newCategory.getStatistician();
		categoryStatistic.increaseInfoNum();
		cmtNum = categoryStatistic.getCommentNum();
		cmtNum += infoStatistic.getCommentNum();
		categoryStatistic.setCommentNum(cmtNum);
		categoryService.updateStatistician(newCategory.getDepartment().getId(),
				newCategory.getId(), categoryStatistic);

		info.setCategory(newCategory);
		infoDao.updateInfo(info);
	}

	@Override
	public void lockInfo(int infoId, int moderatorId, String reason) {
		Info info = infoDao.getInfoById(infoId);
		if (!info.isLocked()) {
			Moderator moderator = moderatorDao.getModeratorById(moderatorId);
			ModerationLog log = newLog(moderator, info,
					ModerationStatus.LOCKED, reason, info.getContent());
			moderationLogDao.saveLog(log);

			setInfoLockStatus(info, true);
		}
	}

	@Override
	public void unlockInfo(int infoId, int moderatorId, String reason) {
		Info info = infoDao.getInfoById(infoId);
		if (info.isLocked()) {
			Moderator moderator = moderatorDao.getModeratorById(moderatorId);
			ModerationLog log = newLog(moderator, info,
					ModerationStatus.UNLOCKED, reason, info.getContent());
			moderationLogDao.saveLog(log);

			setInfoLockStatus(info, false);
		}
	}

	void setInfoLockStatus(Info info, boolean lock) {
		try {
			Method methodSetDepartment = Info.class.getDeclaredMethod(
					Info.SET_LOCKED_METHOD_NAME, boolean.class);
			methodSetDepartment.setAccessible(true);
			methodSetDepartment.invoke(info, lock);

			infoDao.updateInfo(info);
		} catch (Exception e) {
			int id = info.getId();
			logger.error("Fails to lock info with id:" + id + " .", e);
			throw new FailToSetPropertyException(
					"Fails to lock info with id:"
							+ id
							+ ", this exception might be caused by change of method name ["
							+ Info.SET_LOCKED_METHOD_NAME
							+ "] of  "
							+ Info.class.getSimpleName()
							+ " class.Plz check the existence of the method for ["
							+ Info.class.getCanonicalName()
							+ "] or refer to log for more information.");
		}
	}
}
