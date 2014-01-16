package com.vertonur.ext.ranking.spring.aop;

import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.dao.api.UserDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.ext.ranking.bean.PointConfig;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.bean.UserPoint;
import com.vertonur.ext.ranking.dao.api.RankingDAO;
import com.vertonur.pojo.User;
import com.vertonur.security.spring.PermissionAuthenticationToken;

public class IncreasePointsAdvice implements MethodBeforeAdvice {
	public enum IncreaseType {
		INFO, CMT, ATTM
	}

	private IncreaseType increaseType;
	private String daoImplClass;

	@SuppressWarnings("unchecked")
	public void before(Method arg0, Object[] arg1, Object arg2)
			throws Throwable {
		PermissionAuthenticationToken token = (PermissionAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();

		if (!token.isGuest()) {
			int userId = (Integer) token.getPrincipal();
			DAOManager manager = PojoUtil.getDAOManager();
			RankingDAO dao = (RankingDAO) manager.getExtendedBeanDAO(
					(Class<RankingDAO>) Class.forName(daoImplClass),
					Ranking.class);
			PointConfig config = dao.getPointConfig();
			int points = 0;
			if (increaseType == IncreaseType.INFO)
				points = config.getInfoPoints();
			else if (increaseType == IncreaseType.CMT)
				points = config.getCmtPoints();
			else
				points = config.getUploadAttmPoints();

			UserPoint userPoint = dao.getUserPointByUserId(userId);
			User user;
			if (userPoint == null) {
				UserDAO userDao = manager.getUserDAO();
				user = userDao.getUserById(userId);
				userPoint = new UserPoint();
				userPoint.setUser(user);
				dao.saveUserPoint(userPoint);
			}
			userPoint.increasePoints(points);

			points = userPoint.getPoints();
			Ranking ranking = userPoint.getRanking();
			user = userPoint.getUser();
			long regTime = user.getRegTime().getTime();
			long currentTime = new Date().getTime();
			double interval = currentTime - regTime;
			double hours = interval / DateUtils.MILLIS_PER_HOUR;
			UserDAO userDao = manager.getUserDAO();
			if (ranking == null) {
				ranking = dao.getDescFirstRankingLePoints(points, false);
				if (ranking != null) {
					userPoint.setRanking(ranking);
					user.addGroups(ranking.getGroups());
					userDao.updateUser(user);
				}

				ranking = dao.getDescFirstRankingLePoints(points, true);
				if (ranking != null) {
					double limitHours = ranking.getLimitHours();
					if (hours < limitHours) {
						userPoint.setRanking(ranking);
						user.addGroups(ranking.getGroups());
						userDao.updateUser(user);
					}
				}
			} else {
				int currentRankingPoints = ranking.getPoints();
				boolean isTimeRanking = ranking.isTimeRanking();
				Ranking normalRanking;
				Ranking timeRanking;
				if (isTimeRanking) {
					normalRanking = dao.getDescFirstRankingLePoints(points,
							false);
					timeRanking = ranking.getSeniorRanking();
				} else {
					normalRanking = ranking.getSeniorRanking();
					timeRanking = dao.getDescFirstRankingLePoints(points, true);
				}

				if (normalRanking != null) {
					int normalPoints = normalRanking.getPoints();
					if (normalPoints > currentRankingPoints
							&& points >= normalPoints) {
						userPoint.setRanking(normalRanking);
						user.addGroups(normalRanking.getGroups());
						userDao.updateUser(user);
					}
				}

				if (timeRanking != null) {
					int timePoints = timeRanking.getPoints();
					if (timePoints > currentRankingPoints
							&& points >= timePoints) {
						double limitHours = timeRanking.getLimitHours();
						if (hours < limitHours) {
							userPoint.setRanking(timeRanking);
							user.addGroups(timeRanking.getGroups());
							userDao.updateUser(user);
						}
					}
				}
			}

			dao.updateUserPoint(userPoint);
		}
	}

	public IncreaseType getIncreaseType() {
		return increaseType;
	}

	public void setIncreaseType(IncreaseType increaseType) {
		this.increaseType = increaseType;
	}

	public String getDaoImplClass() {
		return daoImplClass;
	}

	public void setDaoImplClass(String daoImplClass) {
		this.daoImplClass = daoImplClass;
	}

}
