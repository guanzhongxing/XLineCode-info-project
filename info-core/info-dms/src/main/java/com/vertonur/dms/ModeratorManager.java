package com.vertonur.dms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.vertonur.dao.api.ModerationLogDAO;
import com.vertonur.dao.api.ModeratorDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.dms.exception.ModeratorManagerNotInitializedException;
import com.vertonur.dms.robot.AssignPendingLogRobot;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.ModerationStatus;
import com.vertonur.pojo.Moderator;
import com.vertonur.pojo.User;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.security.Permission;
import com.vertonur.pojo.security.Permission.PermissionType;

public class ModeratorManager {

	private final static Object lock = new Object();
	private int digestionNum;
	private static ModeratorManager manager;
	private static DAOManager daoManager;
	private ScheduledThreadPoolExecutor executor;
	private AssignPendingLogRobot robot;

	/**
	 * The first generic parameter Integer: category ids<br>
	 * The second generic parameter Integer: digesting num of moderators<br>
	 * The third generic parameter Integer: moderator ids
	 */
	private Map<Integer, Map<Integer, Queue<Integer>>> workloadRecord = new HashMap<Integer, Map<Integer, Queue<Integer>>>();
	private ScheduledFuture<?> future;

	private ModeratorManager() {
		CategoryService categoryService = new CategoryServiceImpl();
		GroupServiceImpl groupService = new GroupServiceImpl();
		List<Category> categories = categoryService.getCategories();
		Map<Integer, Set<User>> categoryModerators = new HashMap<Integer, Set<User>>();
		for (Category category : categories) {
			int id = category.getId();
			List<Group> groups = groupService.getAuditPermissionGroups(id);
			for (Group group : groups) {
				Set<User> users = group.getUsers();
				categoryModerators.put(id, users);
			}
		}

		daoManager = PojoUtil.getDAOManager();
		Set<Integer> categoryIds = categoryModerators.keySet();
		for (int categoryId : categoryIds) {
			Map<Integer, Queue<Integer>> digestingModerators = workloadRecord
					.get(categoryId);
			if (digestingModerators == null) {
				digestingModerators = new HashMap<Integer, Queue<Integer>>();
				workloadRecord.put(categoryId, digestingModerators);
			}

			Set<User> users = categoryModerators.get(categoryId);
			for (User user : users) {
				Moderator moderator = (Moderator) user;
				int digestingNum = moderator
						.getCategoryDigestingNum(categoryId);
				Queue<Integer> moderatorIds = digestingModerators
						.get(digestingNum);
				if (moderatorIds == null) {
					moderatorIds = new ConcurrentLinkedQueue<Integer>();
					digestingModerators.put(digestingNum, moderatorIds);
				}

				moderatorIds.add(moderator.getId());
			}
		}

		RuntimeParameterService service = new RuntimeParameterServiceImpl();
		ModerationConfig config = service.getModerationConfig();
		digestionNum = config.getDigestionNum();

		assignPendingLogs();
		startAssignPendingLogRobot(config.getThreadPoolSize(),
				config.getAssignPendingLogInterval());
	}

	/**
	 * One moderator can moderate several categories, in this case,
	 * categoryModerators will contain the same moderator with different
	 * category id as a key
	 * 
	 * @param categoryModerators
	 */
	public static void init() {
		synchronized (lock) {
			if (manager == null)
				manager = new ModeratorManager();
		}
	}

	static ModeratorManager getManager() {
		if (manager == null)
			new ModeratorManagerNotInitializedException(
					"Moderator manager must be initialized before using, use init method of this class to initialize the manager.");
		return manager;
	}

	/**
	 * get moderator by category id and digesting num , return null if there is
	 * no moderator available
	 * 
	 * @param categoryId
	 * @return
	 */
	Moderator getModerator(int categoryId) {
		Moderator moderator = null;
		Map<Integer, Queue<Integer>> digestingModerators = workloadRecord
				.get(categoryId);
		Set<Integer> digestingNums = digestingModerators.keySet();
		for (Integer digestingNum : digestingNums) {
			if (digestingNum < digestionNum) {
				Queue<Integer> moderatorIds = digestingModerators
						.get(digestingNum);
				// e.g: moderatorIds of digestingNum value 0 is empty when all
				// moderators have content to
				// moderate after system start up. Or there is moderators
				// available with a lower digestingNum
				if (!moderatorIds.isEmpty()) {
					Integer moderatorId = moderatorIds.poll();
					ModeratorDAO dao = daoManager.getModeratorDAO();
					moderator = dao.getModeratorById(moderatorId);
					moderator.increaseCategoryDigestingNum(categoryId);
					dao.updateModerator(moderator);

					int num = moderator.getCategoryDigestingNum(categoryId);
					moderatorIds = digestingModerators.get(num);
					if (moderatorIds == null) {
						moderatorIds = new ConcurrentLinkedQueue<Integer>();
						digestingModerators.put(num, moderatorIds);
					}
					moderatorIds.add(moderatorId);
					break;
				}
			}
		}

		return moderator;
	}

	void setDigestionNum(int digestionNum) {
		this.digestionNum = digestionNum;
	}

	void addModerator(Set<Integer> categoryIds, int moderatorId) {
		for (int categoryId : categoryIds) {
			Map<Integer, Queue<Integer>> digestingModerators = workloadRecord
					.get(categoryId);
			Queue<Integer> moderatorIds = digestingModerators.get(0);
			if (moderatorIds == null) {
				moderatorIds = new ConcurrentLinkedQueue<Integer>();
				digestingModerators.put(0, moderatorIds);
			}

			moderatorIds.add(moderatorId);
		}
	}

	/**
	 * When audit permissions of a moderator change, update work load record
	 * with digesting num and category id for the moderator in two sections: 1.
	 * add the moderator to the workload record according to the newly assigned
	 * category and his digesting num(which will be 0 in the case of newly
	 * assigned category) 2.remove the moderator from the workload record
	 * according to unassigned category and his digesting num and assign the
	 * deferred logs of the moderator to other moderators if is there any
	 * 
	 * @param moderator
	 */
	void rearrangeModeratorWorkload(Moderator moderator) {
		Set<Group> groups = moderator.getGroups();
		Set<Integer> categoryIds = new HashSet<Integer>();
		int moderatorId = moderator.getId();
		ModeratorDAO moderatorDao = daoManager.getModeratorDAO();
		if (!moderator.isLocked()) {
			for (Group group : groups) {
				Set<Permission> permissions = group.getPermissions();
				for (Permission permission : permissions) {
					if (PermissionType.AUDIT_CATEGORY == permission.getType()) {
						int categoryId = Integer.parseInt(permission
								.getProprietaryMark());
						categoryIds.add(categoryId);
					}
				}
			}

			// add the moderator to the new assign categories if it's not done
			// yet
			for (int categoryId : categoryIds) {
				Integer selfDigestingNum = moderator
						.getCategoryDigestingNum(categoryId);
				if (selfDigestingNum == null) {
					selfDigestingNum = new Integer(0);
					moderator.setCategoryDigestingNum(categoryId,
							selfDigestingNum);
				}

				Map<Integer, Queue<Integer>> digestingModerators = workloadRecord
						.get(categoryId);
				if (digestingModerators == null) {
					digestingModerators = new HashMap<Integer, Queue<Integer>>();
					workloadRecord.put(categoryId, digestingModerators);
				}
				Queue<Integer> moderatorIds = digestingModerators
						.get(selfDigestingNum);
				if (moderatorIds == null) {
					moderatorIds = new ConcurrentLinkedQueue<Integer>();
					digestingModerators.put(0, moderatorIds);
				}
				if (!moderatorIds.contains(moderatorId))
					moderatorIds.add(moderatorId);
			}
		}

		// remove moderator from categories that are not assigned to the
		// moderator
		ModerationLogDAO dao = daoManager.getModerationLogDAO();
		Set<Integer> digestingCategoriyId = new HashSet<Integer>(
				workloadRecord.keySet());
		digestingCategoriyId.removeAll(categoryIds);
		for (int categoryId : digestingCategoriyId) {
			Integer selfDigestingNum = moderator
					.getCategoryDigestingNum(categoryId);
			if (selfDigestingNum == null) {
				selfDigestingNum = new Integer(0);
				moderator.setCategoryDigestingNum(categoryId, selfDigestingNum);
			}
			moderator.setCategoryDigestingNum(categoryId, 0);

			Map<Integer, Queue<Integer>> digestingModerators = workloadRecord
					.get(categoryId);
			Queue<Integer> moderatorIds = digestingModerators
					.get(selfDigestingNum);
			if (moderatorIds != null && moderatorIds.contains(moderatorId))
				moderatorIds.remove(moderatorId);

			List<ModerationLog> logs = dao.getLogs(categoryId, 0, digestionNum,
					moderatorId, ModerationStatus.DEFERRED);
			for (ModerationLog log : logs) {
				Moderator availableModerator = getModerator(categoryId);
				if (availableModerator != null)
					log.setModerator(availableModerator);
				else {
					log.setStatus(ModerationStatus.PENDING);
					log.setModerator(null);
				}

				dao.updateLog(log);
			}
		}

		moderatorDao.updateModerator(moderator);
	}

	protected void decreaseModetorDigestingNum(int categoryId,
			Moderator moderator) {
		int moderatorId = moderator.getId();
		Map<Integer, Queue<Integer>> digestingModerators = workloadRecord
				.get(categoryId);
		Integer selfDigestingNum = moderator
				.getCategoryDigestingNum(categoryId);
		Queue<Integer> moderatorIds = digestingModerators.get(selfDigestingNum);
		moderatorIds.remove(moderatorId);
		moderator.decreaseCategoryDigestingNum(categoryId);
		ModeratorDAO dao = daoManager.getModeratorDAO();
		dao.updateModerator(moderator);

		selfDigestingNum = moderator.getCategoryDigestingNum(categoryId);
		moderatorIds = digestingModerators.get(selfDigestingNum);
		if (moderatorIds == null) {
			moderatorIds = new ConcurrentLinkedQueue<Integer>();
			digestingModerators.put(selfDigestingNum, moderatorIds);
		}
		moderatorIds.add(moderatorId);
	}

	public void assignPendingLogs() {
		ModerationLogDAO dao = daoManager.getModerationLogDAO();
		int start = 0;
		List<ModerationLog> pendingLogs = dao.getPendingLogs(start,
				digestionNum);
		while (pendingLogs.size() > 0) {
			for (ModerationLog pendingLog : pendingLogs) {
				int categoryId = pendingLog.getCategory().getId();
				Moderator moderator = getModerator(categoryId);
				if (moderator != null) {
					pendingLog.setStatus(ModerationStatus.DEFERRED);
					pendingLog.setModerator(moderator);
					dao.updateLog(pendingLog);
				} else
					break;
			}

			start += pendingLogs.size();
			pendingLogs = dao.getPendingLogs(start, digestionNum);
		}

	}

	private void startAssignPendingLogRobot(int poolSize, int assignLogInterval) {
		executor = new ScheduledThreadPoolExecutor(poolSize);
		robot = new AssignPendingLogRobot(this);
		future = executor.scheduleWithFixedDelay(robot, assignLogInterval,
				assignLogInterval, TimeUnit.SECONDS);
	}

	void setAssignPendingLogInterval(int interval) {
		future.cancel(false);
		future = executor.scheduleWithFixedDelay(robot, interval, interval,
				TimeUnit.SECONDS);
	}

	private void shutdownRobots() {
		executor.shutdown();
	}

	public static void destroy() {
		synchronized (lock) {
			manager.shutdownRobots();
			manager = null;
		}
	}
}
