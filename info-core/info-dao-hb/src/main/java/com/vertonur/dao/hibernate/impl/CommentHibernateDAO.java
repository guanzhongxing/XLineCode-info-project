package com.vertonur.dao.hibernate.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.CommentDAO;
import com.vertonur.pojo.Comment;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.User;

/**
 * @author Vertonur
 * 
 */
public class CommentHibernateDAO extends
		AbstractInfoHibernateDAO<Comment, Integer> implements CommentDAO {

	@SuppressWarnings("unchecked")
	public long getCommentNumByUser(User user) {
		Criteria crit = getSession().createCriteria(Comment.class, "cmt");
		crit.add(Restrictions.eq("cmt.author", user));
		crit.add(Restrictions.eq("cmt.state.pending", false));
		crit.add(Restrictions.eq("cmt.state.deprecated", false));

		crit.createAlias("cmt.info", "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		Long tmp = (Long) results.get(0);
		return tmp.intValue();
	}

	@SuppressWarnings("unchecked")
	public long getCommentNumByInfo(Info info) {
		Criteria crit = getSession().createCriteria(Comment.class, "cmt");
		crit.add(Restrictions.eq("cmt.info", info));
		crit.add(Restrictions.eq("cmt.state.pending", false));
		crit.add(Restrictions.eq("cmt.state.deprecated", false));
		crit.createAlias("cmt.info", "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getCommentNumByCommentId(int commentId) {
		DetachedCriteria subquery = DetachedCriteria.forClass(Comment.class,
				"subCmt");
		subquery.add(Restrictions.eq("id", commentId));
		subquery.add(Property.forName("subCmt.info").eqProperty("cmt.info"));
		subquery.setProjection(Projections.property("createdTime"));

		Criteria crit = getSession().createCriteria(Comment.class, "cmt");
		crit.add(Restrictions.eq("state.pending", false));
		crit.add(Restrictions.eq("state.deprecated", false));
		crit.add(Property.forName("createdTime").gt(subquery));
		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	@SuppressWarnings("unchecked")
	public long getCommentNumByCategory(int categoryId) {
		Criteria crit = getSession().createCriteria(Comment.class, "cmt");
		crit.add(Restrictions.eq("cmt.state.pending", false));
		crit.add(Restrictions.eq("cmt.state.deprecated", false));

		crit.createAlias("cmt.info", "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.id", categoryId));
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	@SuppressWarnings("unchecked")
	public long getCommentNumOfAllInfos() {
		Criteria crit = getSession().createCriteria(Comment.class, "cmt");
		crit.add(Restrictions.eq("cmt.state.pending", false));
		crit.add(Restrictions.eq("cmt.state.deprecated", false));

		crit.createAlias("cmt.info", "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Comment> getCommentsByUser(User user, int start, int offset) {
		Criteria crit = getSession().createCriteria(Comment.class, "cmt");
		crit.add(Restrictions.eq("cmt.state.pending", false));
		crit.add(Restrictions.eq("cmt.state.deprecated", false));
		crit.add(Restrictions.eq("cmt.author", user));

		crit.createAlias("cmt.info", "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.setFirstResult(start);
		crit.setMaxResults(offset);
		crit.addOrder(Order.desc("createdTime"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public Comment getLatestComment(int infoId, Date date) {
		Criteria crit = getSession().createCriteria(Comment.class, "cmt");
		crit.add(Restrictions.eq("cmt.state.pending", false));
		crit.add(Restrictions.eq("cmt.state.deprecated", false));
		if (date != null)
			crit.add(Restrictions.lt("cmt.createdTime", date));

		crit.createAlias("cmt.info", "info");
		crit.add(Restrictions.eq("info.id", infoId));
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.addOrder(Order.desc("cmt.createdTime"));
		List<Comment> results = crit.list();
		if (!results.isEmpty())
			return (Comment) results.get(0);
		else
			return null;
	}

	public boolean deleteComment(Comment comment) {
		return makeTransient(comment);
	}

	public Integer saveComment(Comment comment) {
		return (Integer) getSession().save(comment);
	}

	public void updateComment(Comment comment) {
		getSession().update(comment);
	}

	public Comment getCommentById(int id) {
		return getContentById(id, false, false);
	}

	public Comment getPendingCommentById(int id) {
		return getContentById(id, true, false);
	}

	@SuppressWarnings("unchecked")
	public List<Comment> getCommentsByInfo(Info Info, int start, int offset) {
		Criteria crit = getSession().createCriteria(Comment.class, "cmt");
		crit.add(Restrictions.eq("cmt.state.pending", false));
		crit.add(Restrictions.eq("cmt.state.deprecated", false));

		crit.createAlias("cmt.info", "info");
		crit.add(Restrictions.eq("info", Info));
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.setFirstResult(start);
		crit.setMaxResults(offset);
		crit.addOrder(Order.desc("cmt.createdTime"));
		return crit.list();
	}

	@Override
	public Comment getFirstComment() {
		return getFirstContent();
	}
}
