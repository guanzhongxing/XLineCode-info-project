package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.Category;

public interface CategoryDAO extends GenericDAO<Category, Integer> {

	Category getCategoryById(int categoryId);

	List<Category> getDeptCategorys(int deptId);

	List<Category> getCategorys();

	void updateCategory(Category category);

	Integer saveCategory(Category category);

	boolean deleteCategory(Category category);
}
