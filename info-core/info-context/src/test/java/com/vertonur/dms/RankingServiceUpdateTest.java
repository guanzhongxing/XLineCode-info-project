package com.vertonur.dms;

import static junit.framework.Assert.assertEquals;

import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.exception.RankingWithPointsExistException;
import com.vertonur.ext.ranking.service.RankingService;

public class RankingServiceUpdateTest {
	private int VALUE_1 = 13;
	private int VALUE_2 = 48;
	private int VALUE_3 = 762;

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
		saver.addNightGroups();
		saver.addNightUsers();
		service.commitTransaction();
	}

	@Test
	public void testUpdateSingleRankingToGreaterValue() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addSingleRaning(VALUE_1);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		ranking_1.setPoints(VALUE_2);
		ranking_1.setName(name);
		rankingService.updateRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		assertEquals(VALUE_2, ranking_1.getPoints());
		assertEquals(name, ranking_1.getName());
		service.commitTransaction();
	}

	@Test
	public void testUpdateSingleRankingToSmallerValue() throws LoginException,
			RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addSingleRaning(VALUE_1);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		ranking_1.setPoints(7);
		ranking_1.setName(name);
		rankingService.updateRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		assertEquals(7, ranking_1.getPoints());
		assertEquals(name, ranking_1.getName());
		service.commitTransaction();
	}

	@Test
	public void testUpdateTheFirstRankingOf2ToBiggestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		ranking_1.setPoints(VALUE_3);
		ranking_1.setName(name);
		rankingService.updateRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		assertEquals(VALUE_3, ranking_1.getPoints());
		assertEquals(name, ranking_1.getName());

		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		assertEquals(null, juniorRanking_2);
		assertEquals(seniorRanking_2.getId(), ranking_1.getId());

		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		assertEquals(ranking_2.getId(), juniorRanking_1.getId());
		assertEquals(null, seniorRanking_1);
		service.commitTransaction();
	}

	@Test
	public void testUpdateTheFirstRankingOf2ToSmallestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		ranking_1.setPoints(7);
		ranking_1.setName(name);
		rankingService.updateRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		assertEquals(7, ranking_1.getPoints());
		assertEquals(name, ranking_1.getName());

		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		assertEquals(ranking_1.getId(), juniorRanking_2.getId());
		assertEquals(seniorRanking_2, null);

		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		assertEquals(null, juniorRanking_1);
		assertEquals(ranking_2.getId(), seniorRanking_1.getId());
		service.commitTransaction();
	}

	@Test
	public void testUpdateTheSecondRankingOf2ToBiggestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		ranking_2.setPoints(VALUE_3);
		ranking_2.setName(name);
		rankingService.updateRanking(ranking_2);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_2 = rankingService.getRankingById(saver.getRankingId_2());
		assertEquals(VALUE_3, ranking_2.getPoints());
		assertEquals(name, ranking_2.getName());

		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		assertEquals(null, juniorRanking_1);
		assertEquals(seniorRanking_1.getId(), ranking_2.getId());

		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		assertEquals(ranking_1.getId(), juniorRanking_2.getId());
		assertEquals(null, seniorRanking_2);
		service.commitTransaction();
	}

	@Test
	public void testUpdateTheSecondRankingOf2ToSmallestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addTwoRaning(VALUE_1, VALUE_2);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		ranking_2.setPoints(7);
		ranking_2.setName(name);
		rankingService.updateRanking(ranking_2);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_2 = rankingService.getRankingById(saver.getRankingId_2());
		assertEquals(7, ranking_2.getPoints());
		assertEquals(name, ranking_2.getName());

		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		assertEquals(ranking_2.getId(), juniorRanking_1.getId());
		assertEquals(seniorRanking_1, null);

		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		assertEquals(null, juniorRanking_2);
		assertEquals(ranking_1.getId(), seniorRanking_2.getId());
		service.commitTransaction();
	}

	@Test
	public void testUpdateTheFirstRankingOf3ToSmallestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		ranking_1.setPoints(1);
		ranking_1.setName(name);
		rankingService.updateRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());

		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_1);
		assertEquals(ranking_2.getId(), seniorRanking_1.getId());

		assertEquals(ranking_1.getId(), juniorRanking_2.getId());
		assertEquals(seniorRanking_2.getId(), ranking_3.getId());

		assertEquals(ranking_2.getId(), juniorRanking_3.getId());
		assertEquals(null, seniorRanking_3);

		service.commitTransaction();
	}

	@Test
	public void testUpdateTheFirstRankingOf3ToMediumValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		ranking_1.setPoints(88);
		ranking_1.setName(name);
		rankingService.updateRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_2);
		assertEquals(seniorRanking_2.getId(), ranking_1.getId());

		assertEquals(ranking_2.getId(), juniorRanking_1.getId());
		assertEquals(ranking_3.getId(), seniorRanking_1.getId());

		assertEquals(ranking_1.getId(), juniorRanking_3.getId());
		assertEquals(null, seniorRanking_3);

		service.commitTransaction();
	}

	@Test
	public void testUpdateTheFirstRankingOf3ToBiggestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		ranking_1.setPoints(1883);
		ranking_1.setName(name);
		rankingService.updateRanking(ranking_1);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		ranking_1 = rankingService.getRankingById(saver.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_2);
		assertEquals(seniorRanking_2.getId(), ranking_3.getId());

		assertEquals(ranking_2.getId(), juniorRanking_3.getId());
		assertEquals(ranking_1.getId(), seniorRanking_3.getId());

		assertEquals(ranking_3.getId(), juniorRanking_1.getId());
		assertEquals(null, seniorRanking_1);

		service.commitTransaction();
	}

	@Test
	public void testUpdateTheSecondRankingOf3ToSmallestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		ranking_2.setPoints(1);
		ranking_2.setName(name);
		rankingService.updateRanking(ranking_2);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();

		ranking_2 = rankingService.getRankingById(saver.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();

		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_2);
		assertEquals(ranking_1.getId(), seniorRanking_2.getId());

		assertEquals(ranking_2.getId(), juniorRanking_1.getId());
		assertEquals(ranking_3.getId(), seniorRanking_1.getId());

		assertEquals(ranking_1.getId(), juniorRanking_3.getId());
		assertEquals(null, seniorRanking_3);

		service.commitTransaction();
	}

	@Test
	public void testUpdateTheSecondRankingOf3ToMediumValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		ranking_2.setPoints(188);
		ranking_2.setName(name);
		rankingService.updateRanking(ranking_2);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();

		ranking_2 = rankingService.getRankingById(saver.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();

		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_1);
		assertEquals(ranking_2.getId(), seniorRanking_1.getId());

		assertEquals(ranking_1.getId(), juniorRanking_2.getId());
		assertEquals(ranking_3.getId(), seniorRanking_2.getId());

		assertEquals(ranking_2.getId(), juniorRanking_3.getId());
		assertEquals(null, seniorRanking_3);

		service.commitTransaction();
	}

	@Test
	public void testUpdateTheSecondRankingOf3ToBiggestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		ranking_2.setPoints(1889);
		ranking_2.setName(name);
		rankingService.updateRanking(ranking_2);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();

		ranking_2 = rankingService.getRankingById(saver.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();

		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_1);
		assertEquals(ranking_3.getId(), seniorRanking_1.getId());

		assertEquals(ranking_1.getId(), juniorRanking_3.getId());
		assertEquals(ranking_2.getId(), seniorRanking_3.getId());

		assertEquals(ranking_3.getId(), juniorRanking_2.getId());
		assertEquals(null, seniorRanking_2);

		service.commitTransaction();
	}

	@Test
	public void testUpdateTheThirdRankingOf3ToSmallestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		ranking_3.setPoints(1);
		ranking_3.setName(name);
		rankingService.updateRanking(ranking_3);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();

		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();

		ranking_3 = rankingService.getRankingById(saver.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_3);
		assertEquals(ranking_1.getId(), seniorRanking_3.getId());

		assertEquals(ranking_3.getId(), juniorRanking_1.getId());
		assertEquals(ranking_2.getId(), seniorRanking_1.getId());

		assertEquals(ranking_1.getId(), juniorRanking_2.getId());
		assertEquals(null, seniorRanking_2);

		service.commitTransaction();
	}

	@Test
	public void testUpdateTheThirdRankingOf3ToMediumValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		ranking_3.setPoints(30);
		ranking_3.setName(name);
		rankingService.updateRanking(ranking_3);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();

		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();

		ranking_3 = rankingService.getRankingById(saver.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_1);
		assertEquals(ranking_3.getId(), seniorRanking_1.getId());

		assertEquals(ranking_1.getId(), juniorRanking_3.getId());
		assertEquals(ranking_2.getId(), seniorRanking_3.getId());

		assertEquals(ranking_3.getId(), juniorRanking_2.getId());
		assertEquals(null, seniorRanking_2);

		service.commitTransaction();
	}

	@Test
	public void testUpdateTheThirdRankingOf3ToBiggestValue()
			throws LoginException, RankingWithPointsExistException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		service.commitTransaction();

		String name = "test_changed";
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getRankingId_3());
		ranking_3.setPoints(3009);
		ranking_3.setName(name);
		rankingService.updateRanking(ranking_3);
		service.commitTransaction();

		service.beginTransaction();
		rankingService = service.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		Ranking juniorRanking_1 = ranking_1.getJuniorRanking();
		Ranking seniorRanking_1 = ranking_1.getSeniorRanking();

		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		Ranking juniorRanking_2 = ranking_2.getJuniorRanking();
		Ranking seniorRanking_2 = ranking_2.getSeniorRanking();

		ranking_3 = rankingService.getRankingById(saver.getRankingId_3());
		Ranking juniorRanking_3 = ranking_3.getJuniorRanking();
		Ranking seniorRanking_3 = ranking_3.getSeniorRanking();

		assertEquals(null, juniorRanking_1);
		assertEquals(ranking_2.getId(), seniorRanking_1.getId());

		assertEquals(ranking_1.getId(), juniorRanking_2.getId());
		assertEquals(ranking_3.getId(), seniorRanking_2.getId());

		assertEquals(ranking_2.getId(), juniorRanking_3.getId());
		assertEquals(null, seniorRanking_3);

		service.commitTransaction();
	}

	@After
	public void destroy() throws LoginException, SQLException {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}
}
