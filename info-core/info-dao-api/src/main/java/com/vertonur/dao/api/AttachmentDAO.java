package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.config.AttachmentConfig;

public interface AttachmentDAO extends GenericDAO<Attachment, Integer> {

	Integer saveAttachment(Attachment attm);

	void confirmAttachmentUpload(Attachment attm);

	Attachment getAttmById(int attmId);

	void updateAttachment(Attachment attm);

	void deleteAttachment(Attachment attm);

	AttachmentConfig getAttmConfigById(int id);

	AttachmentConfig getSysAttmConfig();

	Integer saveAttachmentConfig(AttachmentConfig attmConfig);

	void updateAttachmentConfig(AttachmentConfig attmConfig);
	
	public List<Attachment> getBcsAttms();
}
