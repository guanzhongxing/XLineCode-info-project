package com.vertonur.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.vertonur.pojo.statistician.CategoryStatistician;

@Entity(name = "INFO_COR_CAT")
public class Category implements Serializable {

	public static final String SET_DEPT_METHOD_NAME = "setDepartment";
	private static final long serialVersionUID = 1L;

	private int id;
	@Size(min = 1, max = 20, message = "{length.range}")
	private String name;
	@Size(min = 10, max = 100, message = "{length.range}")
	private String description;
	private Date createdTime;
	private Admin creator;
	private boolean moderated;
	private boolean deprecated;
	private int priority;

	@Embedded
	private CategoryStatistician statistician;

	private boolean hasNewInfo = true;

	private Department department;
	private Set<Admin> admins = new HashSet<Admin>();

	public Category() {
	}

	@Id
	@GeneratedValue
	@Column(name = "CAT_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "CAT_NME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "CAT_DESC")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "CAT_CREATED_TIME", nullable = false, updatable = false)
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	@OneToOne
	@JoinColumn(name = "CREATOR_ID", nullable = false, updatable = false)
	public Admin getCreator() {
		return creator;
	}

	public void setCreator(Admin creator) {
		this.creator = creator;
	}

	public void setStatistician(CategoryStatistician statistician) {
		this.statistician = statistician;
	}

	public CategoryStatistician getStatistician() {
		return statistician;
	}

	@ManyToOne(targetEntity = Department.class)
	@JoinColumn(name = "DEPT_ID")
	public Department getDepartment() {
		return department;
	}

	/**
	 * Reserved to be used by CategoryService to set department,people should
	 * use analogy method in CategoryService instead, if need.NOTE: name change
	 * of this method must also update value of constant SET_DEPT_METHOD_NAME
	 * 
	 * @param department
	 */
	@SuppressWarnings("unused")
	private void setDepartment(Department department) {
		this.department = department;
	}

	@ManyToMany
	@JoinTable(name = "INFO_COR_CAT_ADMIN", joinColumns = { @JoinColumn(name = "CAT_ID") }, inverseJoinColumns = { @JoinColumn(name = "USR_ID") })
	public Set<Admin> getAdmins() {
		return admins;
	}

	@SuppressWarnings("unused")
	// same as setDepartment()
	private void setAdmins(Set<Admin> admins) {
		this.admins = admins;
	}

	public void addAdmins(Set<Admin> admins) {
		for (Admin admin : admins)
			admin.addCategory(this);
		this.admins = admins;
	}

	public void removeAdmins() {
		for (Admin admin : admins)
			admin.removeCategory(this);
		admins.clear();
	}

	public void removeAdmin(Admin admin) {
		admins.remove(admin);
	}

	@Transient
	public boolean isHasNewInfo() {
		return hasNewInfo;
	}

	public void hasNewInfoToUser(boolean has) {
		hasNewInfo = has;
	}

	@Column(name = "CAT_MODERATED")
	public boolean isModerated() {
		return moderated;
	}

	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	@Column(name = "CAT_DEPRECATED")
	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	@Column(name = "CAT_PRTY", nullable = false)
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}