package com.vertonur.pojo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity(name = "INFO_COR_ADM")
@DiscriminatorValue("Admin")
public class Admin extends Moderator {

	private static final long serialVersionUID = 1L;
	private Set<Category> categories = new HashSet<Category>();
	private Set<Department> Departments = new HashSet<Department>();

	public Admin() {
	}

	@ManyToMany(mappedBy = "admins")
	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	protected void addCategory(Category category) {
		categories.add(category);
	}

	protected void removeCategory(Category category) {
		categories.remove(category);
	}

	@ManyToMany(mappedBy = "admins")
	public Set<Department> getDepartments() {
		return Departments;
	}

	public void setDepartments(Set<Department> Departments) {
		this.Departments = Departments;
	}

	public void addDepartment(Department Department) {
		Departments.add(Department);
	}

	public void removeDepartment(Department Department) {
		Departments.remove(Department);
	}
}