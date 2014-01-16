package com.vertonur.dao.api;

import com.vertonur.pojo.AttachmentInfo;

public interface AttachmentInfoDAO extends GenericDAO<AttachmentInfo, Integer> {

	Integer saveAttachmentInfo(AttachmentInfo attmInfo);
}
