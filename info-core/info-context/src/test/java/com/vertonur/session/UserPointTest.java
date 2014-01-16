package com.vertonur.session;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vertonur.bean.DataGenerator;
import com.vertonur.context.SystemContextService;
import com.vertonur.dms.exception.SavingCommentToLockedInfoException;
import com.vertonur.ext.ranking.bean.PointConfig;
import com.vertonur.ext.ranking.bean.Ranking;
import com.vertonur.ext.ranking.bean.UserPoint;
import com.vertonur.ext.ranking.service.RankingService;
import com.vertonur.pojo.Admin;

public class UserPointTest {
	private int VALUE_1 = 13;
	private int VALUE_2 = 48;
	private int VALUE_3 = 762;
	private int TIME_VALUE_1 = 23;
	private int TIME_VALUE_2 = 148;
	private int TIME_VALUE_3 = 768;
	private int cmtPoints = 7;
	private int infoPoints = 8;
	private int uploadAttmPoints = 9;
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
		saver.addThreeRaning(VALUE_1, VALUE_2, VALUE_3);
		saver.addThreeTimeRaning(TIME_VALUE_1, TIME_VALUE_2, TIME_VALUE_3);

		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		PointConfig config = rankingService.getPointConfig();
		config.setCmtPoints(cmtPoints);
		config.setInfoPoints(infoPoints);
		config.setUploadAttmPoints(uploadAttmPoints);
		rankingService.updatePointConfig(config);
		service.commitTransaction();
	}

	@Test
	public void testSaveInfo() throws LoginException {
		addInfo(1);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Admin admin = saver.getAdmin();
		UserPoint userPoint = rankingService
				.getUserPointByUserId(admin.getId());
		Ranking ranking = userPoint.getRanking();
		assertNull(ranking);
		assertEquals(userPoint.getPoints(), infoPoints);
		service.commitTransaction();
	}

	@Test
	public void testSave2Info() throws LoginException {
		addInfo(2);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		checkInfoRanking(ranking_1, 2, false);
		service.commitTransaction();
	}

	private void checkInfoRanking(Ranking ranking, int factor,
			boolean isTimeRanking) {
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Admin admin = saver.getAdmin();
		UserPoint userPoint = rankingService
				.getUserPointByUserId(admin.getId());
		Ranking userPointranking = userPoint.getRanking();
		assertEquals(isTimeRanking, userPointranking.isTimeRanking());
		assertEquals(userPointranking.getId(), ranking.getId());
		assertEquals(userPoint.getPoints(), factor * infoPoints);
	}

	@Test
	public void testSave3Info() throws LoginException {
		addInfo(3);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getTimeRankingId_1());
		checkInfoRanking(ranking_1, 3, true);
		service.commitTransaction();
	}

	@Test
	public void testSave10Info() throws LoginException {
		addInfo(10);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		checkInfoRanking(ranking_2, 10, false);
		service.commitTransaction();
	}

	@Test
	public void testSave20Info() throws LoginException {
		addInfo(20);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		checkInfoRanking(ranking_2, 20, false);
		service.commitTransaction();
	}

	@Test
	public void testSave100Info() throws LoginException {
		addInfo(100);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getTimeRankingId_3());
		checkInfoRanking(ranking_3, 100, true);
		service.commitTransaction();
	}

	private void addInfo(int num) throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		for (int i = 0; i < num; i++) {
			saver.addInfo();
		}
		service.commitTransaction();
	}

	@Test
	public void testSaveCmt() throws LoginException,
			SavingCommentToLockedInfoException {
		addComment(1);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		checkCmtRanking(ranking_1, 1, false);
		service.commitTransaction();
	}

	private void checkCmtRanking(Ranking ranking, int factor,
			boolean isTimeRanking) {
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Admin admin = saver.getAdmin();
		UserPoint userPoint = rankingService
				.getUserPointByUserId(admin.getId());
		Ranking userPointRanking = userPoint.getRanking();
		assertEquals(isTimeRanking, userPointRanking.isTimeRanking());
		assertEquals(userPointRanking.getId(), ranking.getId());
		assertEquals(userPoint.getPoints(), infoPoints + factor * cmtPoints);
	}

	@Test
	public void testSaveCmtWithGuest() throws LoginException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		service.commitTransaction();

		service.beginTransaction();
		saver.generateGuestAuthenticationToken();
		saver.addComment(saver.getGuest());
		service.commitTransaction();

		service.beginTransaction();
		saver.loginAdmin();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		int id = service.getGuestId();
		UserPoint userPoint = rankingService.getUserPointByUserId(id);
		assertNull(userPoint);
		service.commitTransaction();
	}

	@Test
	public void testSave3Cmt() throws LoginException,
			SavingCommentToLockedInfoException {
		addComment(3);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getTimeRankingId_1());
		checkCmtRanking(ranking_1, 3, true);
		service.commitTransaction();
	}

	@Test
	public void testSave20Cmt() throws LoginException,
			SavingCommentToLockedInfoException {
		addComment(20);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		checkCmtRanking(ranking_2, 20, false);
		service.commitTransaction();
	}

	@Test
	public void testSave110Cmt() throws LoginException,
			SavingCommentToLockedInfoException {
		addComment(110);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getTimeRankingId_3());
		checkCmtRanking(ranking_3, 110, true);
		service.commitTransaction();
	}

	private void addComment(int num) throws LoginException,
			SavingCommentToLockedInfoException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.addInfo();
		Admin admin = saver.getAdmin();
		for (int i = 0; i < num; i++)
			saver.addComment(admin);
		service.commitTransaction();
	}

	@Test
	public void testSaveAttachment() throws LoginException {
		addAttachment(1);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getRankingId_1());
		checkAttachmentRanking(ranking_1, 1, false);
		service.commitTransaction();
	}

	private void checkAttachmentRanking(Ranking ranking, int factor,
			boolean isTimeRanking) {
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		UserPoint userPoint = rankingService.getUserPointByUserId(saver
				.getInfoUserId());
		Ranking userPointranking = userPoint.getRanking();
		assertEquals(userPointranking.getId(), ranking.getId());
		assertEquals(isTimeRanking, userPointranking.isTimeRanking());
		assertEquals(userPoint.getPoints(), infoPoints + factor
				* uploadAttmPoints);
	}

	@Test
	public void testSave2Attachment() throws LoginException {
		addAttachment(2);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_1 = rankingService.getRankingById(saver
				.getTimeRankingId_1());
		checkAttachmentRanking(ranking_1, 2, true);
		service.commitTransaction();
	}

	@Test
	public void testSave20Attachment() throws LoginException {
		addAttachment(20);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_2 = rankingService.getRankingById(saver
				.getRankingId_2());
		checkAttachmentRanking(ranking_2, 20, false);
		service.commitTransaction();
	}

	@Test
	public void testSave100Attachment() throws LoginException {
		addAttachment(100);

		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		Ranking ranking_3 = rankingService.getRankingById(saver
				.getTimeRankingId_3());
		checkAttachmentRanking(ranking_3, 100, true);
		service.commitTransaction();
	}

	private void addAttachment(int num) throws LoginException {
		service.beginTransaction();
		saver.loginAdmin();
		saver.addDepartmentAndCategory();
		saver.loginInfoUser();
		saver.addInfo();
		saver.addAttachment(num);
		service.commitTransaction();
	}

	@After
	public void destroy() throws LoginException, SQLException {
		service.logout(saver.getSession());
		SecurityContextHolder.clearContext();
		service.shutdown();
	}

	@Test
	public void testUpdatePointConfig() {
		service.beginTransaction();
		RankingService rankingService = service
				.getExtendedBeanService(RankingService.class);
		PointConfig config = rankingService.getPointConfig();
		config.setCmtPoints(0);
		rankingService.updatePointConfig(config);
		service.commitTransaction();
	}
}
