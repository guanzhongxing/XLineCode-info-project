package com.vertonur.dms;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import com.vertonur.dao.api.CommentDAO;
import com.vertonur.dao.api.InfoDAO;
import com.vertonur.dao.api.PrivateMessageDAO;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.PrivateMessage;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadInfo;
import com.vertonur.pojo.UserReadPrivateMessage;
import com.vertonur.pojo.config.CommentConfig;
import com.vertonur.pojo.config.InfoConfig;
import com.vertonur.pojo.config.PrivateMsgConfig;
import com.vertonur.pojo.statistician.InfoStatistician;
import com.vertonur.util.ServiceUtil;

public class InfoServiceImpl extends GenericService implements InfoService {

	private InfoDAO infoDao;
	private CommentDAO commentDao;
	private PrivateMessageDAO pmDao;

	private UserService userService;
	private ModerationServiceImpl moderationService;

	private InfoConfig infoConfig;
	private CommentConfig cmtConfig;
	private PrivateMsgConfig pmConfig;

	protected InfoServiceImpl() {
		infoDao = manager.getInfoDAO();
		commentDao = manager.getCommentDAO();
		pmDao = manager.getPrivateMsgDAO();
		userService = new UserServiceImpl();
		moderationService = new ModerationServiceImpl();
		RuntimeParameterService runtimeParameterService = new RuntimeParameterServiceImpl();
		infoConfig = runtimeParameterService.getInfoConfig();
		cmtConfig = runtimeParameterService.getCommentConfig();
		pmConfig = runtimeParameterService.getPmConfig();
	}

	public void deleteInfo(int infoId) {
		Info info = infoDao.getInfoById(infoId);
		moderationService.deleteContent(info);
	}

	public long getAllInfoNum() {
		return infoDao.getAllInfoNum();
	}

	public List<Info> getInfosByTitle(String infoTitle) {
		return infoDao.getInfosByTitle(infoTitle);
	}

	public Info getInfoById(int infoId) {
		return infoDao.getInfoById(infoId);
	}

	public List<Info> getHottestInfosByCategory(int userId, int categoryId) {
		List<Info> infos = infoDao.getHottestInfosByCategory(categoryId,
				infoConfig.getHottestInfoGeCmts(),
				infoConfig.getHottestInfoPgnOffset());
		User user = userService.getUserById(userId);
		Set<UserReadInfo> readInfos = userService.getReadInfos(user);
		ServiceUtil.markInfoStatus(infos, readInfos,
				infoConfig.getHottestInfoGeCmts());
		return infos;
	}

	public List<Info> getRecentInfosByCategory(int userId, int categoryId) {
		int recentInfoDef = infoConfig.getRecentInfoInDays();
		GregorianCalendar specifiedDate = new GregorianCalendar();
		int recentDay = specifiedDate.get(Calendar.DATE) - recentInfoDef;
		specifiedDate.set(Calendar.DATE, recentDay);
		User user = userService.getUserById(userId);
		Set<UserReadInfo> readInfos = userService.getReadInfos(user);
		List<Info> infos = infoDao.getRecentInfosByCategory(categoryId,
				specifiedDate.getTime(), infoConfig.getRecentInfoPgnOffset());
		ServiceUtil.markInfoStatus(infos, readInfos,
				infoConfig.getHottestInfoGeCmts());
		return infos;
	}

	public void saveUserReadInfo(UserReadInfo userReadInfo) {
		infoDao.saveUserReadInfo(userReadInfo);
	}

	public List<Info> getNewInfosByCategory(int categoryId, int startPoint) {
		return infoDao.getNewInfosByCategory(categoryId,
				infoConfig.getNewInfoInHours(), startPoint,
				infoConfig.getInfoPgnOffset());
	}

	public List<Info> getInfosByCategory(int userId, int categoryId, int start)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		User user = userService.getUserById(userId);
		Set<UserReadInfo> readInfos = userService.getReadInfos(user);
		int infoPgnOffset = infoConfig.getInfoPgnOffset();
		List<Info> infos = infoDao.getInfosByCategory(categoryId, start,
				infoPgnOffset);
		ServiceUtil.markInfoStatus(infos, readInfos,
				infoConfig.getHottestInfoGeCmts());

		return infos;
	}

	public long getInfoNumByCategory(int categoryId) {
		return infoDao.getInfoNumByCategory(categoryId);
	}

	public List<Info> getInfosByUser(User user, int start) {
		return infoDao.getInfosByUser(user, start,
				infoConfig.getInfoPgnOffset());
	}

	public long getInfoNumByCreator(User user) {
		return infoDao.getInfoNumByCreator(user);
	}

	public List<Comment> getCommentsByInfo(Info info, int paginationStart) {
		return commentDao.getCommentsByInfo(info, paginationStart,
				cmtConfig.getCmtPgnOffset());
	}

	public long getCommentNumByInfo(Info info) {
		return commentDao.getCommentNumByInfo(info);
	}

	public void deleteComment(int commentId) {
		Comment comment = commentDao.getCommentById(commentId);
		moderationService.deleteContent(comment);
	}

	public long getCommentNumOfAllInfos() {
		return commentDao.getCommentNumOfAllInfos();
	}

	public List<Comment> getCommentsByUser(User theUser, int paginationStart) {
		return commentDao.getCommentsByUser(theUser, paginationStart,
				cmtConfig.getCmtPgnOffset());
	}

	public long getCommentNumByUser(User theUser) {
		return commentDao.getCommentNumByUser(theUser);
	}

	public Comment getCommentById(int commentId) {
		return commentDao.getCommentById(commentId);
	}

	public int savePrivateMsg(PrivateMessage pm) {
		return pmDao.savePrivateMsg(pm);
	}

	public PrivateMessage getPrivateMessageById(int pmId) {
		return pmDao.getPrivateMessageById(pmId);
	}

	public void saveUserReadPriateMsg(UserReadPrivateMessage userReadPm) {
		pmDao.saveUserReadPriateMsg(userReadPm);
	}

	public List<PrivateMessage> getNewPrivateMsgsByReceiver(User user) {
		return pmDao.getNewPrivateMsgsByReceiver(user,
				pmConfig.getNewPrivateMsgInDays(),
				pmConfig.getNewPrivateMsgNum());
	}

	public List<PrivateMessage> getPrivateMsgsByReceiver(User user, int start) {
		List<PrivateMessage> newPms;
		if (start == 0)
			newPms = getNewPrivateMsgsByReceiver(user);
		else
			newPms = new ArrayList<PrivateMessage>();

		List<PrivateMessage> pms;
		int paginationOffset = pmConfig.getPrivateMsgPgnOffset();
		int remainedPmEntries = paginationOffset - newPms.size();
		if (remainedPmEntries > 0)
			pms = pmDao.getPrivateMsgsByReceiver(user, start + newPms.size(),
					remainedPmEntries);
		else
			pms = new ArrayList<PrivateMessage>();

		Set<UserReadPrivateMessage> readPms = userService
				.getReadPrivateMsgs(user);
		if (readPms != null) {
			for (PrivateMessage pm : newPms)
				for (UserReadPrivateMessage readPm : readPms)
					if (pm.getId() == readPm.getReadPm().getId()) {
						pm.setNewToReceiver(false);
						pm.setReadToReceiver(true);
					}

			for (PrivateMessage pm : pms) {
				pm.setNewToReceiver(false);
				for (UserReadPrivateMessage readPm : readPms)
					if (pm.getId() == readPm.getReadPm().getId())
						pm.setReadToReceiver(true);
			}
		}
		pms.addAll(0, newPms);

		return pms;
	}

	public long getPrivateMsgNumByReceiver(User user) {
		return pmDao.getPrivateMsgNumByReceiver(user);
	}

	public List<PrivateMessage> getPrivateMsgsByAuthor(User user, int start) {
		return pmDao.getPrivateMsgsByAuthor(user, start,
				pmConfig.getPrivateMsgPgnOffset());
	}

	/**
	 * Update statistician both in cache and db
	 */
	public void updateInfoStatistician(int infoId, InfoStatistician statistician) {
		Info info = infoDao.getInfoById(infoId);
		moderationService.updateInfoStatistician(info, statistician);
	}

	@Override
	public int getCommentPageIndex(int commentId) {
		long num = commentDao.getCommentNumByCommentId(commentId);
		int offset = cmtConfig.getCmtPgnOffset();
		return new Long(num / offset * offset).intValue();
	}

	@Override
	public void lockInfo(int infoId) {
		Info info = infoDao.getInfoById(infoId);
		moderationService.setInfoLockStatus(info, true);
	}

	@Override
	public void unlockInfo(int infoId) {
		Info info = infoDao.getInfoById(infoId);
		moderationService.setInfoLockStatus(info, false);
	}

	@Override
	public List<Info> getCategoryAnnouncements(int categoryId) {
		return infoDao.getCategoryAnnouncements(categoryId);
	}

	@Override
	public List<Info> getDeptAnnouncements(int departmentId) {
		return infoDao.getDeptAnnouncements(departmentId);
	}

	@Override
	public List<Info> getSystemAnnouncements() {
		return infoDao.getSystemAnnouncements();
	}

	@Override
	public long getPrivateMsgNumByAuthor(User user) {
		return pmDao.getPrivateMsgNumByAuthor(user);
	}
}
