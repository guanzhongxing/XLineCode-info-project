package com.vertonur.dms;

import com.vertonur.dao.api.AttachmentDAO;
import com.vertonur.dao.api.ConfigDAO;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.pojo.config.CommentConfig;
import com.vertonur.pojo.config.EmailConfig;
import com.vertonur.pojo.config.InfoConfig;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.config.PrivateMsgConfig;
import com.vertonur.pojo.config.SystemContextConfig;
import com.vertonur.pojo.config.UserConfig;

public class RuntimeParameterServiceImpl extends GenericService implements
		RuntimeParameterService {

	ConfigDAO<InfoConfig, Integer> infoConfigDao;
	ConfigDAO<CommentConfig, Integer> cmtConfigDao;
	ConfigDAO<PrivateMsgConfig, Integer> pmConfigDao;
	ConfigDAO<UserConfig, Integer> userConfigDao;
	ConfigDAO<SystemContextConfig, Integer> systemContextConfigDao;
	ConfigDAO<EmailConfig, Integer> emailConfigDao;
	ConfigDAO<ModerationConfig, Integer> moderationConfigDAO;
	AttachmentDAO attmDao;

	protected RuntimeParameterServiceImpl() {
		infoConfigDao = manager.getInfoConfigDAO();
		cmtConfigDao = manager.getCommetConfigDAO();
		pmConfigDao = manager.getPrivateMsgConfigDAO();
		userConfigDao = manager.getUserConfigDAO();
		systemContextConfigDao = manager.getSystemContextConfigDAO();
		emailConfigDao = manager.getEmailConfigDAO();
		moderationConfigDAO = manager.getModerationConfigDAO();
		attmDao = manager.getAttachmentDAO();
	}

	@Override
	public InfoConfig getInfoConfig() {
		return infoConfigDao.getConfig();

	}

	@Override
	public CommentConfig getCommentConfig() {
		return cmtConfigDao.getConfig();
	}

	@Override
	public PrivateMsgConfig getPmConfig() {
		return pmConfigDao.getConfig();
	}

	@Override
	public UserConfig getUserConfig() {
		return userConfigDao.getConfig();
	}

	@Override
	public AttachmentConfig getAttachmentConfig() {
		return attmDao.getSysAttmConfig();
	}

	@Override
	public SystemContextConfig getSystemContextConfig() {
		return systemContextConfigDao.getConfig();
	}

	@Override
	public void updateAttachmentConfig(AttachmentConfig attachmentConfig) {
		attmDao.updateAttachmentConfig(attachmentConfig);
	}

	@Override
	public void updateCommentConfig(CommentConfig commentConfig) {
		cmtConfigDao.updateConfig(commentConfig);
	}

	@Override
	public void updateInfoConfig(InfoConfig infoConfig) {
		infoConfigDao.updateConfig(infoConfig);
	}

	@Override
	public void updatePmConfig(PrivateMsgConfig pmConfig) {
		pmConfigDao.updateConfig(pmConfig);
	}

	@Override
	public void updateUserConfig(UserConfig userConfig) {
		userConfigDao.updateConfig(userConfig);
	}

	@Override
	public void updateSystemContextConfig(
			SystemContextConfig systemContextConfig) {
		systemContextConfigDao.updateConfig(systemContextConfig);
	}

	@Override
	public EmailConfig getEmailConfig() {
		return emailConfigDao.getConfig();
	}

	@Override
	public void updateEmailConfig(EmailConfig emailConfig) {
		emailConfigDao.updateConfig(emailConfig);
	}

	@Override
	public ModerationConfig getModerationConfig() {
		return moderationConfigDAO.getConfig();
	}

	@Override
	public void updateModerationConfig(ModerationConfig moderationConfig) {
		moderationConfigDAO.updateConfig(moderationConfig);
	}
}
