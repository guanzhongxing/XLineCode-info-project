/**
 * 
 */
package com.vertonur.ext.ranking.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.hibernate.impl.ExtendedBeanHibernateDAO;
import com.vertonur.ext.ranking.bean.PointConfig;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.bean.UserPoint;
import com.vertonur.ext.ranking.dao.api.RankingDAO;

/**
 * @author Vertonur
 * 
 */
public class RankingHibernateDAO extends
		ExtendedBeanHibernateDAO<Ranking, Integer> implements RankingDAO {

	public RankingHibernateDAO(Class<Ranking> clazz) {
		super(clazz);
	}

	public int saveRanking(Ranking ranking) {
		return (Integer) getSession().save(ranking);
	}

	public void deleteRanking(Ranking ranking) {
		makeTransient(ranking);
	}

	public Ranking getRankingById(int rankingId) {
		Ranking ranking = (Ranking) getSession().get(getPersistentClass(),
				rankingId);
		return ranking;
	}

	public void updateRanking(Ranking ranking) {
		getSession().update(ranking);
	}

	public Ranking getAscFirstRankingGtPoints(int points, boolean timeRanking) {
		return getFirstRankingByPoints(points, timeRanking, 1, 0);
	}

	/**
	 * 
	 * @param points
	 * @param timeRanking
	 * @param compareMode
	 *            1:Greater than ,0: lesser than,2: lesser than or equal to,3
	 *            greater than or equal to
	 * @param order
	 *            1:desc,0:asc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Ranking getFirstRankingByPoints(int points, boolean timeRanking,
			int compareMode, int order) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.eq("timeRanking", timeRanking));

		if (compareMode == 1)
			crit.add(Restrictions.gt("points", points));
		else if (compareMode == 0)
			crit.add(Restrictions.lt("points", points));
		else if (compareMode == 2)
			crit.add(Restrictions.le("points", points));
		else
			crit.add(Restrictions.ge("points", points));

		if (order == 1)
			crit.addOrder(Order.desc("points"));
		else
			crit.addOrder(Order.asc("points"));

		List<Ranking> list = crit.list();
		if (list.size() != 0)
			return list.get(0);

		return null;
	}

	public Ranking getDescFirstRankingLtPoints(int points, boolean timeRanking) {
		return getFirstRankingByPoints(points, timeRanking, 0, 1);
	}

	public Ranking getDescFirstRankingLePoints(int points, boolean isTimeRanking) {
		return getFirstRankingByPoints(points, isTimeRanking, 2, 1);
	}

	/**
	 * Return the user point found, or return null if not found
	 */
	@SuppressWarnings("unchecked")
	public UserPoint getUserPointByUserId(int userId) {
		Criteria crit = getSession().createCriteria(UserPoint.class);
		crit.add(Restrictions.eq("user.id", userId));
		List<UserPoint> list = crit.list();
		if (list.size() != 0)
			return list.get(0);

		return null;
	}

	public void updateUserPoint(UserPoint userPoint) {
		getSession().update(userPoint);
	}

	public int saveUserPoint(UserPoint userPoint) {
		return (Integer) getSession().save(userPoint);
	}

	/**
	 * Return the point config found, or return null if not found
	 */
	@SuppressWarnings("unchecked")
	public PointConfig getPointConfig() {
		Criteria crit = getSession().createCriteria(PointConfig.class);
		List<PointConfig> list = crit.list();
		if (list.size() != 0)
			return list.get(0);

		return null;
	}

	public int savePointConfig(PointConfig config) {
		return (Integer) getSession().save(config);
	}

	public void updatePointConfig(PointConfig config) {
		getSession().update(config);
	}

	@SuppressWarnings("unchecked")
	public Ranking getRankingByPoints(int points, boolean isTimeRanking) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.eq("points", points));
		crit.add(Restrictions.eq("timeRanking", isTimeRanking));
		List<Ranking> list = crit.list();
		if (list.size() != 0)
			return list.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<UserPoint> getUserPointsByRankingId(int rankingId) {
		Criteria crit = getSession().createCriteria(UserPoint.class);
		crit.add(Restrictions.eq("ranking.id", rankingId));
		List<UserPoint> list = crit.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<UserPoint> getUserPointsBySectionPoints(int lowerLimitPoint,
			int upperLimitPoint) {
		Criteria crit = getSession().createCriteria(UserPoint.class);
		crit.add(Restrictions.ge("points", lowerLimitPoint));
		crit.add(Restrictions.lt("points", upperLimitPoint));
		List<UserPoint> list = crit.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<UserPoint> getUserPointsGePoints(int lowerLimitPoint) {
		Criteria crit = getSession().createCriteria(UserPoint.class);
		crit.add(Restrictions.ge("points", lowerLimitPoint));
		List<UserPoint> list = crit.list();
		return list;
	}

	public long getRankingSum() {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.setProjection(Projections.rowCount());
		Long tmp = (Long) crit.list().get(0);
		return tmp.intValue();
	}

	public Ranking getFirstRanking() {
		return getFirstRankingByPoints(0, false, 3, 0);
	}

	public Ranking getFirstTimeRanking() {
		return getFirstRankingByPoints(0, true, 3, 0);
	}
}
