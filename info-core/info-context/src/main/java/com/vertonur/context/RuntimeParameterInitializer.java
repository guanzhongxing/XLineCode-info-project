package com.vertonur.context;

import com.vertonur.dao.api.AttachmentDAO;
import com.vertonur.dao.api.ConfigDAO;
import com.vertonur.dao.manager.DAOManager;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.pojo.config.CommentConfig;
import com.vertonur.pojo.config.EmailConfig;
import com.vertonur.pojo.config.InfoConfig;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.config.PrivateMsgConfig;
import com.vertonur.pojo.config.SystemContextConfig;
import com.vertonur.pojo.config.UserConfig;

public class RuntimeParameterInitializer {

	private InfoConfig infoConfig;
	private CommentConfig commentConfig;
	private PrivateMsgConfig privateMessageConfig;
	private UserConfig userConfig;
	private AttachmentConfig attachmentConfig;
	private SystemContextConfig systemContextConfig;
	private EmailConfig emailConfig;
	private ModerationConfig moderationConfig;

	public void init(DAOManager manager) {
		ConfigDAO<InfoConfig, Integer> infoConfigDao = manager
				.getInfoConfigDAO();
		ConfigDAO<CommentConfig, Integer> cmtConfigDao = manager
				.getCommetConfigDAO();
		ConfigDAO<PrivateMsgConfig, Integer> pmConfigDao = manager
				.getPrivateMsgConfigDAO();
		ConfigDAO<UserConfig, Integer> userConfigDao = manager
				.getUserConfigDAO();
		ConfigDAO<SystemContextConfig, Integer> systemContextConfigDao = manager
				.getSystemContextConfigDAO();
		ConfigDAO<EmailConfig, Integer> emailConfigDao = manager
				.getEmailConfigDAO();
		ConfigDAO<ModerationConfig, Integer> moderationConfigDAO = manager
				.getModerationConfigDAO();
		AttachmentDAO attmDao = manager.getAttachmentDAO();

		/*
		 * any of the config objects can be used to see if there is any config
		 * record in database.if it's ,then the records from db are used,
		 * otherwise, the manager will load config beans from xml file
		 */
		InfoConfig initMark = infoConfigDao.getConfig();
		if (initMark == null) {
			infoConfigDao.saveConfig(infoConfig);
			cmtConfigDao.saveConfig(commentConfig);
			pmConfigDao.saveConfig(privateMessageConfig);
			systemContextConfigDao.saveConfig(systemContextConfig);
			userConfigDao.saveConfig(userConfig);
			emailConfigDao.saveConfig(emailConfig);
			attmDao.saveAttachmentConfig(attachmentConfig);
			moderationConfigDAO.saveConfig(moderationConfig);
		}
	}

	public void setInfoConfig(InfoConfig infoConfig) {
		this.infoConfig = infoConfig;
	}

	public void setCommentConfig(CommentConfig commentConfig) {
		this.commentConfig = commentConfig;
	}

	public void setPrivateMessageConfig(PrivateMsgConfig privateMessageConfig) {
		this.privateMessageConfig = privateMessageConfig;
	}

	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	public void setSystemContextConfig(SystemContextConfig systemContextConfig) {
		this.systemContextConfig = systemContextConfig;
	}

	public void setEmailConfig(EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
	}

	public void setModerationConfig(ModerationConfig moderationConfig) {
		this.moderationConfig = moderationConfig;
	}

	public void setAttachmentConfig(AttachmentConfig attachmentConfig) {
		this.attachmentConfig = attachmentConfig;
	}
}
