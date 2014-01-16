package com.vertonur.pojo.statistician;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.vertonur.pojo.Info;

@Embeddable
public class CategoryStatistician implements Serializable {

	private static final long serialVersionUID = 1L;
	private int infoNum;
	private int commentNum;
	private int toModerateNum;
	private Info latestInfo;

	@Column(name = "CAT_STAT_INFO_NUM")
	public int getInfoNum() {
		return infoNum;
	}

	public void setInfoNum(int infoNum) {
		this.infoNum = infoNum;
	}

	public void increaseInfoNum() {
		infoNum++;
	}

	public void decreaseInfoNum() {
		infoNum--;
	}

	/**
	 * @return num of infoes and comments to be moderated
	 */
	@Column(name = "CAT_STAT_TO_MODERATE_NUM")
	public int getToModerateNum() {
		return toModerateNum;
	}

	public void setToModerateNum(int toModerateNum) {
		this.toModerateNum = toModerateNum;
	}

	public void increaseToModerateNum() {
		toModerateNum++;
	}

	public void decreaseToModerateNum() {
		toModerateNum--;
	}

	@Column(name = "CAT_STAT_CMT_NUM")
	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public void increaseCommentNum() {
		commentNum++;
	}

	public void decreaseCommentNum() {
		commentNum--;
	}

	@OneToOne
	@JoinColumn(name = "CAT_LATEST_INFO_ID")
	public Info getLatestInfo() {
		return latestInfo;
	}

	public void setLatestInfo(Info latestInfo) {
		this.latestInfo = latestInfo;
	}
}
