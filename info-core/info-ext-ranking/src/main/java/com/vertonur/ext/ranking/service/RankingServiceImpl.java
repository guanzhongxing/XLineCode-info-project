package com.vertonur.ext.ranking.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import com.vertonur.dao.api.UserDAO;
import com.vertonur.dms.AbstractExtendedBeanService;
import com.vertonur.ext.ranking.bean.PointConfig;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.bean.UserPoint;
import com.vertonur.ext.ranking.dao.api.RankingDAO;
import com.vertonur.ext.ranking.exception.RankingWithPointsExistException;
import com.vertonur.pojo.User;
import com.vertonur.pojo.security.Group;

/**
 * @author Vertonur
 * 
 */
public class RankingServiceImpl extends AbstractExtendedBeanService<Ranking>
		implements RankingService {

	private RankingDAO dao;
	private UserDAO userDao;

	protected RankingServiceImpl(String daoImplClass) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, ClassNotFoundException {
		super(daoImplClass, Ranking.class);
		dao = (RankingDAO) super.getExtendedBeanDAO();
		userDao = manager.getUserDAO();

	}

	public int saveRanking(Ranking ranking)
			throws RankingWithPointsExistException {
		int points = ranking.getPoints();
		boolean isTimeRanking = ranking.isTimeRanking();
		Ranking foundRanking = dao.getRankingByPoints(points, isTimeRanking);
		if (foundRanking != null) {
			throwRankingWithPointsExistException(points);
		}

		int rankingId = dao.saveRanking(ranking);
		Set<Group> groups = ranking.getGroups();
		// TODO:move code below to robot to execute in midnight for performance
		// issue
		foundRanking = dao.getDescFirstRankingLtPoints(points, isTimeRanking);
		if (foundRanking != null) {
			List<UserPoint> list = dao.getUserPointsByRankingId(foundRanking
					.getId());
			for (UserPoint userPoint : list) {
				int userPoints = userPoint.getPoints();
				if (userPoints >= points) {
					userPoint.setRanking(ranking);
					dao.updateUserPoint(userPoint);
					User user = userPoint.getUser();
					user.addGroups(groups);
					userDao.updateUser(user);
				}
			}

			weaveRelation(foundRanking, ranking);
		} else {
			foundRanking = dao
					.getAscFirstRankingGtPoints(points, isTimeRanking);
			if (foundRanking != null) {
				List<UserPoint> list = dao.getUserPointsBySectionPoints(points,
						foundRanking.getPoints());
				for (UserPoint userPoint : list) {
					userPoint.setRanking(ranking);
					dao.updateUserPoint(userPoint);
					User user = userPoint.getUser();
					user.addGroups(groups);
					userDao.updateUser(user);
				}

				weaveRelation(ranking, foundRanking);
			} else {
				List<UserPoint> list = dao.getUserPointsGePoints(points);
				for (UserPoint userPoint : list) {
					userPoint.setRanking(ranking);
					dao.updateUserPoint(userPoint);
					User user = userPoint.getUser();
					user.addGroups(groups);
					userDao.updateUser(user);
				}
			}
		}

		return rankingId;
	}

	public void deleteRanking(Ranking ranking) {
		// TODO:move code below to robot to execute in midnight for performance
		// issue
		int rankingId = ranking.getId();
		Ranking juniorRanking = ranking.getJuniorRanking();
		Ranking seniorRanking = ranking.getSeniorRanking();

		int points = ranking.getPoints();
		if (!ranking.isTimeRanking()) {
			if (seniorRanking == null)
				seniorRanking = dao.getAscFirstRankingGtPoints(points, true);

			if (juniorRanking == null)
				juniorRanking = dao.getDescFirstRankingLtPoints(points, true);

		} else {
			if (seniorRanking == null)
				seniorRanking = dao.getAscFirstRankingGtPoints(points, false);

			if (juniorRanking == null)
				juniorRanking = dao.getDescFirstRankingLtPoints(points, false);
		}

		List<UserPoint> list = dao.getUserPointsByRankingId(rankingId);
		if (seniorRanking != null) {
			for (UserPoint userPoint : list) {
				userPoint.setRanking(seniorRanking);
				dao.updateUserPoint(userPoint);
				User user = userPoint.getUser();
				user.addGroups(seniorRanking.getGroups());
				userDao.updateUser(user);
			}
		} else {
			for (UserPoint userPoint : list) {
				userPoint.setRanking(juniorRanking);
				dao.updateUserPoint(userPoint);
			}
		}

		juniorRanking = ranking.getJuniorRanking();
		if (juniorRanking != null)
			ranking.setSeniorRanking(null);
		seniorRanking = ranking.getSeniorRanking();
		if (juniorRanking != null && seniorRanking != null)
			weaveRelation(juniorRanking, seniorRanking);
		else if (juniorRanking != null) {
			juniorRanking.setSeniorRanking(null);
			dao.updateRanking(juniorRanking);
		} else if (seniorRanking != null) {
			seniorRanking.setJuniorRanking(null);
			dao.updateRanking(seniorRanking);
		}
		dao.deleteRanking(ranking);
	}

	/**
	 * Weave the new senior ranking between the junior ranking and senior
	 * ranking(ranking A) of the junior ranking if ranking A is not
	 * null.Otherwise, just weave the new senior ranking with the junior
	 * ranking.
	 * 
	 * @param juniorRanking
	 * @param newSeniorRanking
	 */
	private void weaveRelation(Ranking juniorRanking, Ranking newSeniorRanking) {
		Ranking oldSeniorRanking = juniorRanking.getSeniorRanking();
		juniorRanking.setSeniorRanking(newSeniorRanking);
		newSeniorRanking.setJuniorRanking(juniorRanking);
		dao.updateRanking(juniorRanking);
		if (oldSeniorRanking != null) {
			oldSeniorRanking.setJuniorRanking(newSeniorRanking);
			newSeniorRanking.setSeniorRanking(oldSeniorRanking);
			dao.updateRanking(newSeniorRanking);
		}
	}

	public Ranking getRankingById(int rankingId) {
		return dao.getRankingById(rankingId);
	}

	public void updateRanking(Ranking ranking)
			throws RankingWithPointsExistException {
		int points = ranking.getPoints();
		boolean isTimeRanking = ranking.isTimeRanking();
		Ranking foundRanking = dao.getRankingByPoints(points, isTimeRanking);
		if (foundRanking != null && (foundRanking.getId() != ranking.getId())) {
			throwRankingWithPointsExistException(points);
		}

		foundRanking = dao.getDescFirstRankingLtPoints(points, isTimeRanking);
		if (foundRanking != null) {
			Ranking seniorRanking = foundRanking.getSeniorRanking();
			if (seniorRanking == null
					|| (seniorRanking.getId() != ranking.getId())) {
				removeCurrentRanking(ranking);
				foundRanking.setSeniorRanking(ranking);
				ranking.setJuniorRanking(foundRanking);
				if (seniorRanking != null) {
					seniorRanking.setJuniorRanking(ranking);
					ranking.setSeniorRanking(seniorRanking);
				}

				dao.updateRanking(foundRanking);
			}
		} else {
			foundRanking = dao
					.getAscFirstRankingGtPoints(points, isTimeRanking);
			if (foundRanking != null)
				if (foundRanking.getId() != ranking.getId()) {
					removeCurrentRanking(ranking);
					weaveRelation(ranking, foundRanking);
				}
		}

		dao.updateRanking(ranking);
	}

	/**
	 * Remove the current ranking from the ranking chain and set the junior and
	 * the senior ranking of it to null
	 * 
	 * @param ranking
	 */
	private void removeCurrentRanking(Ranking ranking) {
		Ranking seniorRanking = ranking.getSeniorRanking();
		Ranking juniorRanking = ranking.getJuniorRanking();
		ranking.setSeniorRanking(null);
		ranking.setJuniorRanking(null);
		List<UserPoint> list = dao.getUserPointsByRankingId(ranking.getId());
		if (seniorRanking != null) {
			for (UserPoint userPoint : list) {
				userPoint.setRanking(seniorRanking);
				dao.updateUserPoint(userPoint);
				User user = userPoint.getUser();
				user.addGroups(seniorRanking.getGroups());
			}
		} else {
			for (UserPoint userPoint : list) {
				userPoint.setRanking(juniorRanking);
				dao.updateUserPoint(userPoint);
			}
		}

		if (juniorRanking != null && seniorRanking != null) {
			juniorRanking.setSeniorRanking(null);
			weaveRelation(juniorRanking, seniorRanking);
		} else if (juniorRanking != null) {
			juniorRanking.setSeniorRanking(null);
			dao.updateRanking(juniorRanking);
		} else if (seniorRanking != null) {
			seniorRanking.setJuniorRanking(null);
			dao.updateRanking(seniorRanking);
		}
	}

	public UserPoint getUserPointByUserId(int userId) {
		return dao.getUserPointByUserId(userId);
	}

	public void updateUserPoint(UserPoint userPoint) {
		dao.updateUserPoint(userPoint);
	}

	public int saveUserPoint(UserPoint userPoint) {
		return dao.saveUserPoint(userPoint);
	}

	private void throwRankingWithPointsExistException(int points)
			throws RankingWithPointsExistException {
		StringBuilder sb = new StringBuilder();
		sb.append("Ranking with points [");
		sb.append(points);
		sb.append("] already exists,rankings with the same points are not allow");
		throw new RankingWithPointsExistException(sb.toString());
	}

	public long getRankingSum() {
		return dao.getRankingSum();
	}

	public List<UserPoint> getUserPointsByRankingId(int rankingId) {
		return dao.getUserPointsByRankingId(rankingId);
	}

	public int savePointConfig(PointConfig config) {
		return dao.savePointConfig(config);
	}

	public void updatePointConfig(PointConfig config) {
		dao.updatePointConfig(config);
	}

	public PointConfig getPointConfig() {
		return dao.getPointConfig();
	}

	public Ranking getAscFirstRankingGtPoints(int points, boolean isTimeRanking) {
		return dao.getAscFirstRankingGtPoints(points, isTimeRanking);
	}

	public void deleteRanking(int rankingId) {
		Ranking ranking = dao.getRankingById(rankingId);
		deleteRanking(ranking);
	}

	public void deleteRanking(int[] rankingIds) {
		for (int id : rankingIds)
			deleteRanking(id);
	}

	public Ranking getFirstRanking() {
		return dao.getFirstRanking();
	}

	public Ranking getFirstTimeRanking() {
		return dao.getFirstTimeRanking();
	}
}
