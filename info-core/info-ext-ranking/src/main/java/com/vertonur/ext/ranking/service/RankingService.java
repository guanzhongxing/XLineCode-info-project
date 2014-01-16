package com.vertonur.ext.ranking.service;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.ExtendedBeanService;
import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.ext.ranking.bean.PointConfig;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.bean.UserPoint;
import com.vertonur.ext.ranking.exception.RankingWithPointsExistException;

public interface RankingService extends ExtendedBeanService<Ranking> {

	@RolesAllowed(RoleEnum.ROLE_USER)
	public int saveRanking(Ranking ranking)
			throws RankingWithPointsExistException;

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void deleteRanking(Ranking ranking);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void deleteRanking(int rankingId);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void deleteRanking(int[] rankingIds);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public Ranking getRankingById(int rankingId);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void updateRanking(Ranking ranking)
			throws RankingWithPointsExistException;

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public Ranking getAscFirstRankingGtPoints(int points, boolean isTimeRanking);

	/**
	 * Get the first ranking in the chain
	 * 
	 * @return Ranking
	 */
	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public Ranking getFirstRanking();

	/**
	 * Get the first time ranking in the chain
	 * 
	 * @return Ranking
	 */
	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public Ranking getFirstTimeRanking();

	@RolesAllowed(RoleEnum.ROLE_USER)
	public UserPoint getUserPointByUserId(int userId);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public void updateUserPoint(UserPoint userPoint);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public int saveUserPoint(UserPoint userPoint);

	@RolesAllowed(RoleEnum.ROLE_USER)
	public long getRankingSum();

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public List<UserPoint> getUserPointsByRankingId(int rankingId);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public PointConfig getPointConfig();

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public int savePointConfig(PointConfig config);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	public void updatePointConfig(PointConfig config);

}
