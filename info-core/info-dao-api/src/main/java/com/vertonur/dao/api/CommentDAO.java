package com.vertonur.dao.api;

import java.util.Date;
import java.util.List;

import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.User;

/**
 * @author Vertonur
 * 
 */
public interface CommentDAO {

	Integer saveComment(Comment comment);

	void updateComment(Comment comment);

	long getCommentNumByUser(User user);

	long getCommentNumByInfo(Info Info);

	/**
	 * Get num of comments with created time greater than the comment with the
	 * id passed in
	 * 
	 * @param commentId
	 * @return
	 */
	long getCommentNumByCommentId(int commentId);

	long getCommentNumByCategory(int categoryId);

	long getCommentNumOfAllInfos();

	List<Comment> getCommentsByUser(User user, int start, int offset);

	List<Comment> getCommentsByInfo(Info Info, int start, int offset);

	/**
	 * Get the latest comment of the info with the specified date.The date
	 * parameter will not be used as a condition if it's null
	 */
	Comment getLatestComment(int infoId, Date date);

	boolean deleteComment(Comment comment);

	Comment getCommentById(int id);

	public Comment getPendingCommentById(int id);

	public Comment getFirstComment();
}
