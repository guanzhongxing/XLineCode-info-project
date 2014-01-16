package com.vertonur.dms;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertonur.dao.api.CategoryDAO;
import com.vertonur.dms.cache.CategoryCache;
import com.vertonur.dms.exception.CategoryModerationListNotEmptyException;
import com.vertonur.dms.exception.FailToSetPropertyException;
import com.vertonur.pojo.Category;
import com.vertonur.pojo.Department;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.User;
import com.vertonur.pojo.statistician.CategoryStatistician;

public class CategoryServiceImpl extends GenericService implements
		CategoryService {

	private static Logger logger = LoggerFactory
			.getLogger(CategoryServiceImpl.class.getCanonicalName());
	private CategoryDAO categoryDao;
	private DepartmentService deptService;
	private CategoryCache cache;

	protected CategoryServiceImpl() {
		categoryDao = manager.getCategoryDAO();
		CacheService service = CacheService.getCacheService();
		deptService = new DepartmentServiceImpl();
		cache = service.getCategoryCache();
	}

	public void updateCategory(Category category)
			throws CategoryModerationListNotEmptyException {
		Department department = category.getDepartment();
		if (!category.isModerated() && !department.isModerated())
			if (category.getStatistician().getToModerateNum() != 0) {
				categoryDao.clear();
				throw new CategoryModerationListNotEmptyException(
						"There are pending messages to moderate under the category["
								+ category.getId()
								+ ","
								+ category.getName()
								+ "] being updated.To disable moderation function of this category,"
								+ "those message must be conducted first.");
			}

		categoryDao.updateCategory(category);
		int deptId = department.getId();
		if (category.isDeprecated())
			cache.removeCategory(deptId, category.getId());
		else
			cache.updateCategory(deptId, categoryDao);
	}

	public int saveCategory(int deptId, Category category) {
		int id = categoryDao.saveCategory(category);
		addDepartment(deptId, category);
		return id;
	}

	public Category getCategoryById(int deptId, int categoryId) {
		return getCategoryById(deptId, categoryId, true);
	}

	public Category getCategoryById(int deptId, int categoryId, boolean useCache) {
		Category category = null;
		if (useCache)
			category = cache.getCategory(deptId, categoryId);
		else
			category = categoryDao.getCategoryById(categoryId);
		return category;
	}

	/**
	 * Currently info system will not use this method to delete any data, <br>
	 * instead {@link public void updateCategory(Category category)} and
	 * deprecated property of Category are used to mark a category as deleted
	 * 
	 * @param category
	 * @return
	 */
	public boolean deleteCategory(Category category) {
		int deptId = category.getDepartment().getId();
		cache.removeCategory(deptId, category.getId());
		return categoryDao.deleteCategory(category);
	}

	public void deleteCategories(int deptId) throws CategoryModerationListNotEmptyException {
		List<Category> categories = categoryDao.getDeptCategorys(deptId);
		for (Category category : categories) {
			category.setDeprecated(true);
			updateCategory(category);
		}
	}

	public List<Category> getCategories() {
		return cache.getCategories();
	}

	public List<Category> getDeptCategories(int deptId) {
		return cache.getDeptCategories(deptId);
	}

	/**
	 * @param category
	 * @param user
	 * @param newInfoEntries
	 * @param inXxDays
	 */
	public void hasNewInfosToUser(Category category, User user) {
		InfoService infoService = new InfoServiceImpl();
		UserService userService = new UserServiceImpl();
		List<Info> infos = infoService.getNewInfosByCategory(category.getId(),
				0);
		if (infos.size() != 0) {
			boolean allRead = userService.confirmReadInfos(user, infos);
			if (allRead)
				category.hasNewInfoToUser(false);
		} else
			category.hasNewInfoToUser(false);
	}

	public void updateStatistician(int deptId, int categoryId,
			CategoryStatistician statistician) {
		Category cachedCategory = cache.getCategory(deptId, categoryId);
		cachedCategory.setStatistician(statistician);
		Category category = categoryDao.getCategoryById(categoryId);
		category.setStatistician(statistician);
		categoryDao.updateCategory(category);
	}

	public int getCategoriesCmtNum() {
		int cmtNum = 0;
		List<Category> categories = cache.getCategories();
		for (Category category : categories)
			cmtNum += category.getStatistician().getCommentNum();

		return cmtNum;
	}

	/**
	 * Add the category to the department and update the dapartment in the cache
	 */
	private void addDepartment(int deptId, Category category) {
		Department dept = deptService.getDepartmentById(deptId, false);
		setDepartment(dept, category);
		cache.reloadCategories(deptId, categoryDao);
	}

	void setDepartment(Department dept, Category category) {
		int deptId = dept.getId();
		try {
			Method methodSetDepartment = Category.class.getDeclaredMethod(
					Category.SET_DEPT_METHOD_NAME, Department.class);
			methodSetDepartment.setAccessible(true);
			methodSetDepartment.invoke(category, dept);
		} catch (Exception e) {
			logger.error("Fails to add category with id:" + category.getId()
					+ "to department with id: " + deptId, e);
			throw new FailToSetPropertyException(
					"Fails to add category with id:"
							+ category.getId()
							+ " to department with id: "
							+ deptId
							+ ", this exception might be caused by change of method name ["
							+ Category.SET_DEPT_METHOD_NAME
							+ "] of  "
							+ Category.class.getSimpleName()
							+ " class.Plz check the existence of the method for ["
							+ Category.class.getCanonicalName()
							+ "] or refer to log for more information.");
		}
	}

	@Override
	public void changeDepartment(int deptId, Category category) {
		int staleDeptId = category.getDepartment().getId();
		if (deptId != staleDeptId) {
			Department dept = deptService.getDepartmentById(deptId, false);
			setDepartment(dept, category);
			cache.removeCategory(staleDeptId, category.getId());
			cache.addCategory(deptId, category);
		}
	}
}
