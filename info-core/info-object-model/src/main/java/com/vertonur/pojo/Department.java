package com.vertonur.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity(name = "INFO_COR_DEPT")
public class Department implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;
	private Date createdTime;
	private Admin creator;
	private boolean deprecated;
	private boolean moderated;
	private Set<Admin> admins = new HashSet<Admin>();
	private int priority;

	public Department() {
	}

	@Id
	@GeneratedValue
	@Column(name = "DEPT_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "DEPT_NME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DEPT_DESC", nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "DEPT_CREATEDTIME", nullable = false, updatable = false)
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	@Column(name = "DEPT_DEPRECATED")
	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	@Column(name = "DEPT_MODERATED")
	public boolean isModerated() {
		return moderated;
	}

	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	@OneToOne
	@JoinColumn(name = "CREATOR_ID", nullable = false, updatable = false)
	public Admin getCreator() {
		return creator;
	}

	public void setCreator(Admin creator) {
		this.creator = creator;
	}

	@ManyToMany
	@JoinTable(name = "INFO_COR_DEPT_ADMIN", joinColumns = { @JoinColumn(name = "DEPT_ID") }, inverseJoinColumns = { @JoinColumn(name = "USR_ID") })
	public Set<Admin> getAdmins() {
		return admins;
	}

	public void setAdmins(Set<Admin> admins) {
		this.admins = admins;
	}

	public void addAdmins(Set<Admin> admins) {
		for (Admin admin : admins)
			admin.addDepartment(this);
		this.admins = admins;
	}

	public void removeAdmin(Admin admin) {
		admins.remove(admin);
	}

	@Column(name = "DEPT_PRTY", nullable = false)
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
