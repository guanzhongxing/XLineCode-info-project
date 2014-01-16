package com.vertonur.ext.ranking.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.vertonur.pojo.User;

@Entity(name = "INFO_EXT_USR_PNT")
public class UserPoint {

	private int id;
	private Ranking ranking;
	private User user;
	private int points = 0;

	@Id
	@GeneratedValue
	@Column(name = "USR_PNT_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToOne(targetEntity = Ranking.class)
	@JoinColumn(name = "USR_PNT_RNK")
	public Ranking getRanking() {
		return ranking;
	}

	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "USR_PNT_USR")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "USR_PNT_PNTS")
	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void increasePoints(int points) {
		this.points += points;
	}
}
