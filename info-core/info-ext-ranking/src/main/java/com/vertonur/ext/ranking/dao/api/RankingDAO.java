/**
 * 
 */
package com.vertonur.ext.ranking.dao.api;

import java.util.List;

import com.vertonur.dao.api.ExtendedBeanDAO;
import com.vertonur.ext.ranking.bean.PointConfig;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.bean.UserPoint;

/**
 * @author Vertonur
 * 
 */
public interface RankingDAO extends ExtendedBeanDAO<Ranking, Integer> {

	public int saveRanking(Ranking ranking);

	public void deleteRanking(Ranking ranking);

	public Ranking getRankingById(int rankingId);

	public long getRankingSum();

	public Ranking getRankingByPoints(int points, boolean isTimeRanking);

	public Ranking getAscFirstRankingGtPoints(int points, boolean isTimeRanking);

	public Ranking getDescFirstRankingLtPoints(int points, boolean isTimeRanking);

	public Ranking getDescFirstRankingLePoints(int points, boolean isTimeRanking);

	public void updateRanking(Ranking ranking);

	public List<UserPoint> getUserPointsByRankingId(int rankingId);

	public UserPoint getUserPointByUserId(int userId);

	public List<UserPoint> getUserPointsBySectionPoints(int lowerLimitPoint,
			int upperLimitPoint);

	public List<UserPoint> getUserPointsGePoints(int lowerLimitPoint);

	public void updateUserPoint(UserPoint userPoint);

	public int saveUserPoint(UserPoint userPoint);

	/**
	 * There must be one and only one PointConfig in database
	 * 
	 * @return PointConfig
	 */
	public PointConfig getPointConfig();

	public int savePointConfig(PointConfig config);

	public void updatePointConfig(PointConfig config);

	/**
	 * Get the first ranking in the chain
	 * 
	 * @return Ranking
	 */
	public Ranking getFirstRanking();

	/**
	 * Get the first time ranking in the chain
	 * 
	 * @return Ranking
	 */
	public Ranking getFirstTimeRanking();
}
