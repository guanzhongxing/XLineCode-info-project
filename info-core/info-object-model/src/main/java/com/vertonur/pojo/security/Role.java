package com.vertonur.pojo.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


@Entity(name = "INFO_COR_ROLE")
public class Role {

	private int id;
	private String name;
	private String description;
	private Set<Group> groups = new HashSet<Group>();
	
	public Role() {
	}
	
	@Id
	@GeneratedValue
	@Column(name = "ROLE_ID")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "ROLE_NME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "ROLE_DESC")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@ManyToMany(mappedBy = "roles")
	public Set<Group> getGroups() {
		return groups;
	}
	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
	public void addGroup(Group group) {
		groups.add(group);
	} 
}
