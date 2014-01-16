package com.vertonur.ext.ranking.bean;

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

import com.vertonur.pojo.ExtendedBean;
import com.vertonur.pojo.security.Group;

@Entity(name = "INFO_EXT_RNK")
public class Ranking extends ExtendedBean {

	private int id;
	private String name;
	private int points;
	private boolean timeRanking;
	private double limitHours;
	private Set<Group> groups = new HashSet<Group>();
	private Ranking juniorRanking;
	private Ranking seniorRanking;

	@Id
	@GeneratedValue
	@Column(name = "RNK_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "RNK_NME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "RNK_PNTS")
	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	@Column(name = "RNK_IS_TM_RNK")
	public boolean isTimeRanking() {
		return timeRanking;
	}

	public void setTimeRanking(boolean timeRanking) {
		this.timeRanking = timeRanking;
	}

	@Column(name = "RNK_LMT_HRS")
	public double getLimitHours() {
		return limitHours;
	}

	public void setLimitHours(double limitHours) {
		this.limitHours = limitHours;
	}

	@ManyToMany
	@JoinTable(name = "INFO_EXT_RNK_GRP", joinColumns = { @JoinColumn(name = "RNK_ID") }, inverseJoinColumns = { @JoinColumn(name = "GRP_ID") })
	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public void addGroup(Group group) {
		groups.add(group);
	}

	@OneToOne(mappedBy = "seniorRanking")
	public Ranking getJuniorRanking() {
		return juniorRanking;
	}

	public void setJuniorRanking(Ranking juniorRanking) {
		this.juniorRanking = juniorRanking;
	}

	@OneToOne
	@JoinColumn(name = "RNK_SNR_RNK")
	public Ranking getSeniorRanking() {
		return seniorRanking;
	}

	public void setSeniorRanking(Ranking seniorRanking) {
		this.seniorRanking = seniorRanking;
	}
}
