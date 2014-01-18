package com.vertonur.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.vertonur.dao.api.AttachmentDAO;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.pojo.config.AttachmentConfig.AttmCngType;

/**
 * @author Vertonur
 * 
 */
public class AttachmentHibernateDAO extends
		GenericHibernateDAO<Attachment, Integer> implements AttachmentDAO {

	public Integer saveAttachment(Attachment attm) {
		return (Integer) getSession().save(attm);
	}

	public Attachment getAttmById(int attmId) {
		return findById(attmId, false);
	}

	public void updateAttachment(Attachment attm) {
		getSession().update(attm);
	}

	public AttachmentConfig getAttmConfigById(int id) {
		Criteria crit = getSession().createCriteria(AttachmentConfig.class);
		crit.add(Restrictions.eq("id", id));
		return (AttachmentConfig) crit.list().get(0);
	}

	public AttachmentConfig getSysAttmConfig() {
		Criteria crit = getSession().createCriteria(AttachmentConfig.class);
		crit.add(Restrictions.eq("configTyp", AttmCngType.SYS));
		List<?> list = crit.list();
		if (list.size() != 0)
			return (AttachmentConfig) list.get(0);
		else
			return null;
	}

	public Integer saveAttachmentConfig(AttachmentConfig attmConfig) {
		return (Integer) getSession().save(attmConfig);
	}

	public void updateAttachmentConfig(AttachmentConfig attmConfig) {
		getSession().update(attmConfig);
	}

	@Override
	public void deleteAttachment(Attachment attm) {
		getSession().delete(attm);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Attachment> getBcsAttms() {
		Criteria crit = getSession().createCriteria(Attachment.class,"attm");
		crit.createAlias("attm.attmInfo", "attmInfo");
		crit.add(Restrictions.eq("attmInfo.attachmentType", AttachmentInfo.AttachmentType.BCS));
		return crit.list();
	}
}
