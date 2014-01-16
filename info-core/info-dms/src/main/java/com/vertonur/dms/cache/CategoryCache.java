package com.vertonur.dms.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vertonur.dao.api.CategoryDAO;
import com.vertonur.pojo.Category;

public class CategoryCache {

	private Map<Integer, Map<Integer, Category>> deptCategories;

	public CategoryCache() {
		deptCategories = new HashMap<Integer, Map<Integer, Category>>();
	}

	public List<Category> getCategories() {
		Collection<Map<Integer, Category>> categoryMap = deptCategories
				.values();
		Iterator<Map<Integer, Category>> iterator = categoryMap.iterator();
		List<Category> categoryList = new LinkedList<Category>();
		while (iterator.hasNext()) {
			Map<Integer, Category> entry = iterator.next();
			categoryList.addAll(entry.values());
		}
		return categoryList;
	}

	/**
	 * 
	 * @param deptId
	 * @return
	 */
	public List<Category> getDeptCategories(int deptId) {
		Map<Integer, Category> categoryMap = deptCategories.get(deptId);
		if (categoryMap == null) {
			categoryMap = new LinkedHashMap<Integer, Category>();
			deptCategories.put(deptId, categoryMap);
		}

		Collection<Category> categoryCollection = categoryMap.values();
		List<Category> categories = new LinkedList<Category>();
		categories.addAll(categoryCollection);
		return categories;
	}

	/**
	 * 
	 * @param deptId
	 * @param categoryId
	 * @return
	 */
	public Category getCategory(int deptId, int categoryId) {
		Map<Integer, Category> categories = deptCategories.get(deptId);
		if (categories == null)
			return null;
		else
			return categories.get(categoryId);
	}

	public void addCategories(List<Category> categories) {
		for (Category category : categories)
			addCategory(category.getDepartment().getId(), category);
	}

	public void updateCategory(int deptId, CategoryDAO categoryDao) {
		reloadCategories(deptId, categoryDao);
	}

	public void addCategory(int deptId, Category category) {
		Map<Integer, Category> categories = deptCategories.get(deptId);
		if (categories == null) {
			// TODO: add sorting
			categories = new LinkedHashMap<Integer, Category>();
			deptCategories.put(deptId, categories);
		}
		categories.put(category.getId(), category);
	}

	public void reloadCategories(int deptId, CategoryDAO categoryDao) {
		List<Category> reloadedCategories = categoryDao
				.getDeptCategorys(deptId);
		Map<Integer, Category> categories = new LinkedHashMap<Integer, Category>();
		for (Category deptCategory : reloadedCategories)
			categories.put(deptCategory.getId(), deptCategory);
		deptCategories.put(deptId, categories);
	}

	public void removeCategory(int deptId, int categoryId) {
		Map<Integer, Category> categories = deptCategories.get(deptId);
		categories.remove(categoryId);
		if (categories.isEmpty())
			deptCategories.remove(deptId);
	}
}
