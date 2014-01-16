package com.vertonur.pojo.statistician;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.vertonur.pojo.User;

@Entity(name = "INFO_COR_USR_STS")
public class UserMsgStatistician implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int commentNum;
	private int infoNum;

	private User user;

	// private InfoDAO InfoDAO;
	// private PrivateMessageDAO privateMsgDAO;

	public UserMsgStatistician() {
		commentNum = 0;
		infoNum = 0;
	}

	@Id
	@GeneratedValue
	@Column(name = "USER_STS_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "USER_STS_CMT_NUM")
	public int getCommentNum() {
		return commentNum;
	}

	@SuppressWarnings("unused")
	private void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public void increaseCommentNum() {
		commentNum++;
	}

	public void decreaseCommentNum() {
		commentNum--;
	}

	@Column(name = "USER_STS_INFO_NUM")
	public int getInfoNum() {
		return infoNum;
	}

	@SuppressWarnings("unused")
	private void setInfoNum(int infoNum) {
		this.infoNum = infoNum;
	}

	public void increaseInfoNum() {
		infoNum++;
	}

	public void decreaseInfoNum() {
		infoNum--;
	}

	@OneToOne(mappedBy = "statistician")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
