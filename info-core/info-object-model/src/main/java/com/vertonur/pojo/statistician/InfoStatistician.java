package com.vertonur.pojo.statistician;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.vertonur.pojo.Comment;

@Embeddable
public class InfoStatistician {

	private int commentNum;
	private int clickThroughRate;
	private int toModerateNum;
	private boolean isLatestOne;

	private Comment latestComment;

	public InfoStatistician() {
	}

	@Column(name = "INFO_COMMENT_NUM")
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

	@Column(name = "INFO_CLICK_THROUGH_RATE")
	public int getClickThroughRate() {
		return clickThroughRate;
	}

	public void setClickThroughRate(int clickThroughRate) {
		this.clickThroughRate = clickThroughRate;
	}

	public void addClickThroughRate() {
		clickThroughRate++;
	}

	/**
	 * @return num of comments to be moderated
	 */
	@Column(name = "INFO_TO_MODERATE_NUM")
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

	@Column(name = "INFO_IS_LATEST_INFO")
	public boolean isLatestOne() {
		return isLatestOne;
	}

	public void setLatestOne(boolean isLatestOne) {
		this.isLatestOne = isLatestOne;
	}

	@OneToOne
	@JoinColumn(name = "INFO_LATEST_COMMENT_ID")
	public Comment getLatestComment() {
		return latestComment;
	}

	public void setLatestComment(Comment latestComment) {
		this.latestComment = latestComment;
	}
}