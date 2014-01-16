package com.vertonur.dao.hibernate.impl;

import com.vertonur.dao.api.AttachmentInfoDAO;
import com.vertonur.pojo.AttachmentInfo;

/**
 * @author Vertonur
 * 
 */
public class AttachmentInfoHibernateDAO extends
		GenericHibernateDAO<AttachmentInfo, Integer> implements
		AttachmentInfoDAO {

	public Integer saveAttachmentInfo(AttachmentInfo attmInfo) {
		return (Integer) getSession().save(attmInfo);
	}
}
