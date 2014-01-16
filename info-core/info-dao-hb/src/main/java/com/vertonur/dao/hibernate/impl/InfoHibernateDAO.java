package com.vertonur.dao.hibernate.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.InfoDAO;
import com.vertonur.pojo.Info;
import com.vertonur.pojo.Info.InfoType;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadInfo;

//TODO: add updated time property of info and change sorting property of info ordering to this property
public class InfoHibernateDAO extends AbstractInfoHibernateDAO<Info, Integer>
		implements InfoDAO {

	@SuppressWarnings("unchecked")
	public List<Info> getInfosByUser(User user, int start, int offset) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		crit.add(Restrictions.eq("info.author", user));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.setFirstResult(start);
		crit.setMaxResults(offset);
		crit.addOrder(Order.desc("info.infoType"));
		crit.addOrder(Order.desc("info.createdTime"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<Info> getInfosByTitle(String title) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		crit.add(Restrictions.like("info.title", title, MatchMode.ANYWHERE));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.addOrder(Order.desc("info.infoType"));
		crit.addOrder(Order.desc("info.createdTime"));
		List<Info> infos = crit.list();
		return infos;
	}

	public List<Info> getInfosByCategory(int categoryId, int start, int offset) {
		List<Info> infos = getInfosByCategory(categoryId, 0, start, offset);
		for (Info info : infos)
			info.setNewToUser(false);
		return infos;
	}

	@SuppressWarnings("unchecked")
	private List<Info> getInfosByCategory(int categoryId, int hours, int start,
			int offset) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		if (hours > 0) {
			Calendar c = new GregorianCalendar();
			c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - hours);
			crit.add(Restrictions.ge("info.createdTime", c.getTime()));
		}
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.add(Restrictions.eq("category.id", categoryId));

		crit.setFirstResult(start);
		crit.setMaxResults(offset);
		crit.addOrder(Order.desc("info.infoType"));
		crit.addOrder(Order.desc("info.createdTime"));
		return crit.list();
	}

	public List<Info> getNewInfosByCategory(int categoryId, int hours,
			int start, int offset) {
		return getInfosByCategory(categoryId, hours, start, offset);
	}

	@SuppressWarnings("unchecked")
	public List<Info> getRecentInfosByCategory(int categoryId,
			Date specifiedDate, int recentInfosEntries) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		crit.add(Restrictions.ge("info.createdTime", specifiedDate));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);
		
		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.add(Restrictions.eq("category.id", categoryId));

		crit.addOrder(Order.desc("info.infoType"));
		crit.addOrder(Order.desc("info.createdTime"));
		crit.setMaxResults(recentInfosEntries);
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<Info> getHottestInfosByCategory(int categoryId,
			int hottestInfosDef, int hottestInfosEntries) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.ge("info.statistician.commentNum",
				hottestInfosDef));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.id", categoryId));
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.addOrder(Order.desc("info.infoType"));
		crit.addOrder(Order.desc("info.statistician.commentNum"));
		crit.addOrder(Order.desc("info.createdTime"));
		crit.setMaxResults(hottestInfosEntries);
		return crit.list();
	}

	public Info getInfoById(int id) {
		return getContentById(id, false, false);
	}

	@Override
	public Info getPendingInfoById(int id) {
		return getContentById(id, true, false);
	}

	public Info getLatestInfo(int categoryId, Date date) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		if (date != null)
			crit.add(Restrictions.lt("info.createdTime", date));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.add(Restrictions.eq("category.id", categoryId));

		crit.addOrder(Order.desc("info.createdTime"));
		List<?> results = crit.list();
		if (results.isEmpty()) {
			return null;
		} else {
			Info info = (Info) results.get(0);
			return info;
		}
	}

	public Info getFirstInfo(Date date) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		if (date != null)
			crit.add(Restrictions.gt("info.createdTime", date));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.addOrder(Order.asc("info.createdTime"));
		List<?> results = crit.list();
		if (results.isEmpty()) {
			return null;
		} else {
			Info info = (Info) results.get(0);
			return info;
		}
	}

	public Integer saveInfo(Info info) {
		return (Integer) getSession().save(info);
	}

	public void updateInfo(Info info) {
		getSession().update(info);
	}

	public void deleteInfo(Info info) {
		makeTransient(info);
	}

	@SuppressWarnings("unchecked")
	public long getInfoNumByCategory(int categoryId) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.id", categoryId));
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	/**
	 * get the Info number in all categories
	 */
	@SuppressWarnings("unchecked")
	public long getInfoNumInCategories() {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.setProjection(Projections.rowCount());
		List<Long> results = crit.list();
		return (Long) results.get(0);
	}

	public long getAllInfoNum() {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.setProjection(Projections.rowCount());
		Long tmp = (Long) crit.list().get(0);
		return tmp.intValue();
	}

	public long getInfoNumByCreator(User creator) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.author", creator));
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("info.infoType", InfoType.NORMAL));
		disjunction.add(Restrictions.eq("info.infoType", InfoType.STICKY));
		crit.add(disjunction);

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));
		crit.setProjection(Projections.rowCount());
		Long tmp = (Long) crit.list().get(0);
		return tmp.intValue();
	}

	public UserReadInfo getReadInfoByUserAndInfo(User reader, Info Info) {
		Criteria crit = getSession().createCriteria(UserReadInfo.class);
		crit.add(Restrictions.eq("reader", reader));
		crit.add(Restrictions.eq("readInfo", Info));
		return (UserReadInfo) crit.uniqueResult();
	}

	public void saveUserReadInfo(UserReadInfo userReadInfo) {
		getSession().save(userReadInfo);
	}

	@SuppressWarnings("unchecked")
	public Set<UserReadInfo> getSpecifiedUserReadInfos(User reader) {
		Criteria crit = getSession().createCriteria(UserReadInfo.class);
		crit.add(Restrictions.eq("reader", reader));
		return new HashSet<UserReadInfo>(crit.list());
	}

	@SuppressWarnings("unchecked")
	public Set<UserReadInfo> getReadInfosByUserAndInfos(User reader,
			List<Info> infos) {
		Criteria crit = getSession().createCriteria(UserReadInfo.class);
		crit.add(Restrictions.eq("reader", reader));
		if (infos.size() != 0) {
			Disjunction disjunction = Restrictions.disjunction();
			for (Info info : infos)
				disjunction.add(Restrictions.eq("readInfo", info));
			crit.add(disjunction);
		}
		return new HashSet<UserReadInfo>(crit.list());
	}

	@Override
	public Info getFirstInfo() {
		return getFirstContent();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Info> getCategoryAnnouncements(int categoryId) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.infoType",
				InfoType.CATEGORY_ANNOUNCEMENT));
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.id", categoryId));
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.addOrder(Order.desc("info.createdTime"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Info> getDeptAnnouncements(int departmentId) {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.infoType",
				InfoType.DEPARTMENT_ANNOUNCEMENT));
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));

		crit.createAlias("info.category", "category");
		crit.createAlias("category.department", "department");
		crit.add(Restrictions.eq("department.id", departmentId));
		crit.add(Restrictions.eq("department.deprecated", false));

		crit.addOrder(Order.desc("info.createdTime"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Info> getSystemAnnouncements() {
		Criteria crit = getSession().createCriteria(Info.class, "info");
		crit.add(Restrictions.eq("info.infoType", InfoType.SYSTEM_ANNOUNCEMENT));
		crit.add(Restrictions.eq("info.state.pending", false));
		crit.add(Restrictions.eq("info.state.deprecated", false));

		crit.createAlias("info.category", "category");
		crit.add(Restrictions.eq("category.deprecated", false));

		crit.addOrder(Order.desc("info.createdTime"));
		return crit.list();
	}
}
