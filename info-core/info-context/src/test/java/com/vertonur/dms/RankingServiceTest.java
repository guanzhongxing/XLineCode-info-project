package com.vertonur.dms;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.bean.UserPoint;
import com.vertonur.ext.ranking.exception.RankingWithPointsExistException;
import com.vertonur.ext.ranking.service.RankingService;
import com.vertonur.pojo.security.Group;

public class RankingServiceTest {
	private int VALUE_1 = 13;
	private int VALUE_2 = 48;
	private int VALUE_3 = 762;
	private int[] points = { 6, 13, 34, 41, 48, 57, 90, 762, 888 };

	private SystemContextService service;
	private DataGenerator saver;

	@Before
	public void init() throws Exception {
		new ClassPathXmlApplicationContext(
				"info-system-context-service-init-beans.xml");
		service = SystemContextService.getService();

		service.beginTransaction();
		saver = new DataGenerator();
		saver.loginAdmin();
		saver.addNightUsers();
		saver.addUserPoint(points);
		saver.addNightGroups();
		service.commitTransaction();
	}

	@Test
	public void testGetRankingService() throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		service.commitTransaction();

		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		assertNotNull(rankingService);
	}

	@Test
	public void testSaveSingleRanking() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addSingleRaning(VALUE_1);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		assertNotNull(ranking_1);

		checkUserPoints(ranking_1, 8);
		int id = saver.getUserIds()[0];
		UserPoint userPoint = rankingService.getUserPointByUserId(id);
		assertNull(userPoint.getRanking());
		service.commitTransaction();
	}

	private void checkUserPoints(Ranking ranking, int expectedSize) {
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		List<UserPoint> list = rankingService.getUserPointsByRankingId(ranking
				.getId());
		int size = list.size();
		assertEquals(expectedSize, size);

		for (UserPoint userPoint : list)
			assertEquals(userPoint.getRanking().getId(), ranking.getId());
	}

	@Test(expected = RankingWithPointsExistException.class)
	public void testSaveSingleRankingWithPointsExistenceException()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addSingleRaning(VALUE_1);
		service.commitTransaction();

		service.beginTransaction();
		saver.addSingleRaning(VALUE_1);
		service.commitTransaction();
	}

	@Test
	public void testSaveTwoRanking() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		assertNotNull(ranking_1);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		assertNotNull(ranking_2);

		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		assertEquals(ranking_1.getId(), juniorRanking_2.getId());
		assertEquals(ranking_2.getId(), seniorRanking_1.getId());

		checkUserPoints(ranking_1, 3);
		checkUserPoints(ranking_2, 5);
		service.commitTransaction();
	}

	@Test
	public void testSaveThreeRanking() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		assertNotNull(ranking_1);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		assertNotNull(ranking_2);
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		assertNotNull(ranking_3);

		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		assertEquals(ranking_1.getId(), juniorRanking_2.getId());
		assertEquals(ranking_2.getId(), seniorRanking_1.getId());

		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		assertEquals(ranking_2.getId(), juniorRanking_3.getId());
		assertEquals(ranking_3.getId(), seniorRanking_2.getId());

		checkUserPoints(ranking_1, 3);
		checkUserPoints(ranking_2, 3);
		checkUserPoints(ranking_3, 2);
		service.commitTransaction();
	}

	@Test
	public void testAdd3MoreGroupsToRanking_1() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		saver.addGroups2Ranking(ranking_1, 3);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		Set<Group> groups = ranking_1.getGroups();
		assertEquals(6, groups.size());
		service.commitTransaction();
	}

	@Test
	public void testInsertNewRankingBeforeSingleRanking()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addSingleRaning(VALUE_1);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking newRanking = new Ranking();
		newRanking.setName("test_ranking_new");
		newRanking.setPoints(5);
		int newRankingId = rankingService.saveRanking(newRanking);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking = rankingService.getRankingById(saver.getRankingId_1());
		newRanking = rankingService.getRankingById(newRankingId);
		Ranking juniorRanking = ranking.getJuniorRanking();
		Ranking seniorRanking = newRanking.getSeniorRanking();
		assertEquals(newRanking.getId(), juniorRanking.getId());
		assertEquals(ranking.getId(), seniorRanking.getId());

		checkUserPoints(ranking, 8);
		checkUserPoints(newRanking, 1);
		service.commitTransaction();
	}

	@Test
	public void testInsertNewRankingBeforeTwoRanking() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking newRanking = new Ranking();
		newRanking.setName("test_ranking_new");
		newRanking.setPoints(5);
		int newRankingId = rankingService.saveRanking(newRanking);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking = rankingService.getRankingById(saver.getRankingId_1());
		newRanking = rankingService.getRankingById(newRankingId);
		Ranking juniorRanking = ranking.getJuniorRanking();
		Ranking seniorRanking = newRanking.getSeniorRanking();
		assertEquals(newRanking.getId(), juniorRanking.getId());
		assertEquals(ranking.getId(), seniorRanking.getId());

		checkUserPoints(juniorRanking, 1);
		checkUserPoints(ranking, 3);
		checkUserPoints(ranking.getSeniorRanking(), 5);
		service.commitTransaction();
	}

	@Test
	public void testInsertNewRankingBetweenTwoRanking() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking newRanking = new Ranking();
		newRanking.setName("test_ranking_new");
		newRanking.setPoints(15);
		int newRankingId = rankingService.saveRanking(newRanking);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking = rankingService.getRankingById(saver.getRankingId_1());
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		newRanking = rankingService.getRankingById(newRankingId);

		Ranking seniorRanking_1 = ranking.getSeniorRanking();
		Ranking juniorRanking_2 = newRanking.getJuniorRanking();
		assertEquals(newRanking.getId(), seniorRanking_1.getId());
		assertEquals(ranking.getId(), juniorRanking_2.getId());

		Ranking seniorRanking_2 = newRanking.getSeniorRanking();
		Ranking juniorRanking_3 = ranking_2.getJuniorRanking();
		assertEquals(ranking_2.getId(), seniorRanking_2.getId());
		assertEquals(newRanking.getId(), juniorRanking_3.getId());

		checkUserPoints(ranking, 1);
		checkUserPoints(newRanking, 2);
		checkUserPoints(ranking_2, 5);
		service.commitTransaction();
	}

	@Test
	public void testInsertNewRankingBeforeThreeRanking() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking newRanking = new Ranking();
		newRanking.setName("test_ranking_new");
		newRanking.setPoints(5);
		int newRankingId = rankingService.saveRanking(newRanking);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking = rankingService.getRankingById(saver.getRankingId_1());
		newRanking = rankingService.getRankingById(newRankingId);
		Ranking juniorRanking = ranking.getJuniorRanking();
		Ranking seniorRanking = newRanking.getSeniorRanking();
		assertEquals(newRanking.getId(), juniorRanking.getId());
		assertEquals(ranking.getId(), seniorRanking.getId());

		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		checkUserPoints(newRanking, 1);
		checkUserPoints(ranking, 3);
		checkUserPoints(ranking_2, 3);
		checkUserPoints(ranking_3, 2);
		service.commitTransaction();
	}

	@Test
	public void testInsertNewRankingInThreeRanking() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking newRanking = new Ranking();
		newRanking.setName("test_ranking_new");
		newRanking.setPoints(135);
		int newRankingId = rankingService.saveRanking(newRanking);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		newRanking = rankingService.getRankingById(newRankingId);

		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		Ranking juniorRanking = newRanking.getJuniorRanking();
		assertEquals(newRanking.getId(), seniorRanking_2.getId());
		assertEquals(ranking_2.getId(), juniorRanking.getId());

		Ranking seniorRanking = newRanking.getSeniorRanking();
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		assertEquals(ranking_3.getId(), seniorRanking.getId());
		assertEquals(newRanking.getId(), juniorRanking_3.getId());

		checkUserPoints(ranking_2.getJuniorRanking(), 3);
		checkUserPoints(ranking_2, 3);
		checkUserPoints(newRanking, 0);
		checkUserPoints(ranking_3, 2);
		service.commitTransaction();
	}

	@Test
	public void testDeleteSingleRanking() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addSingleRaning(VALUE_1);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		rankingService.deleteRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		assertNull(ranking_1);

		int[] ids = saver.getUserIds();
		for (int i = 0; i < ids.length; i++) {
			UserPoint userPoint = rankingService.getUserPointByUserId(ids[i]);
			assertNull(userPoint.getRanking());
		}
		service.commitTransaction();
	}

	@Test
	public void testDeleteTheFirstRankingOf2() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		rankingService.deleteRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		assertNull(ranking_1);

		long sum = rankingService.getRankingSum();
		assertEquals(1, sum);

		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		checkUserPoints(ranking_2, 8);

		int id = saver.getUserIds()[0];
		UserPoint userPoint = rankingService.getUserPointByUserId(id);
		assertNull(userPoint.getRanking());
		service.commitTransaction();
	}

	@Test
	public void testDeleteTheSecondRankingOf2() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		rankingService.deleteRanking(ranking_2);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_2 = rankingService.getRankingById(saver.getRankingId_2());
		assertNull(ranking_2);

		long sum = rankingService.getRankingSum();
		assertEquals(1, sum);

		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		checkUserPoints(ranking_1, 8);
		service.commitTransaction();
	}

	@Test
	public void testDeleteTheSecondRankingOf3() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		rankingService.deleteRanking(ranking_2);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_2 = rankingService.getRankingById(saver.getRankingId_2());
		assertNull(ranking_2);

		long sum = rankingService.getRankingSum();
		assertEquals(2, sum);

		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		checkUserPoints(ranking_1, 3);
		checkUserPoints(ranking_3, 5);
		service.commitTransaction();
	}

	@Test
	public void testDeleteAllThreeRankings() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		rankingService.deleteRanking(ranking_1);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		rankingService.deleteRanking(ranking_2);
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		rankingService.deleteRanking(ranking_3);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		ranking_2 = rankingService.getRankingById(saver.getRankingId_2());
		ranking_3 = rankingService.getRankingById(saver.getRankingId_3());
		assertNull(ranking_1);
		assertNull(ranking_2);
		assertNull(ranking_3);

		long sum = rankingService.getRankingSum();
		assertEquals(0, sum);

		int[] ids = saver.getUserIds();
		for (int i = 0; i < ids.length; i++) {
			UserPoint userPoint = rankingService.getUserPointByUserId(ids[i]);
			assertNull(userPoint.getRanking());
		}
		service.commitTransaction();
	}

	@After
	public void destroy() throws LoginException, SQLException {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}
}
