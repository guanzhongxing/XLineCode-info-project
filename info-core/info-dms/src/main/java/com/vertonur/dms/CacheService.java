package com.vertonur.dms;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.dao.api.CategoryDAO;
import com.vertonur.dao.api.DepartmentDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.dao.util.PojoUtil;
import com.vertonur.dms.cache.CategoryCache;
import com.vertonur.dms.cache.DepartmentCache;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Department;

public class CacheService {
	private static final Logger logger = LoggerFactory
			.getLogger(com.vertonur.dms.CacheService.class.getCanonicalName());
	private static CacheService cacheService;
	private static final Object OBJ_LOCK = new Object();

	private DepartmentCache departmentCache;
	private CategoryCache categoryCache;

	private CacheService() {
		logger.info("Initializing cache service... ...");
		departmentCache = new DepartmentCache();
		categoryCache = new CategoryCache();
		DAOManager manager = PojoUtil.getDAOManager();
		DepartmentDAO dao = manager.getDepartmentDAO();
		CategoryDAO categoryDAO = manager.getCategoryDAO();
		List<Department> depts = dao.getDepartments();
		// init cache data
		for (Department department : depts) {
			departmentCache.addDepartment(department);
			List<Category> categories = categoryDAO.getDeptCategorys(department
					.getId());
			categoryCache.addCategories(categories);
		}

		logger.info("Done of loading departments, categories and informations into respective cache.");
	}

	protected static CacheService getCacheService() {
		if (cacheService == null)
			synchronized (OBJ_LOCK) {
				cacheService = new CacheService();
			}

		return cacheService;
	}

	protected DepartmentCache getDepartmentCache() {
		return departmentCache;
	}

	protected CategoryCache getCategoryCache() {
		return categoryCache;
	}

	public static void destroy() {
		cacheService = null;
	}
}
