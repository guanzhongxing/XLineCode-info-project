package com.vertonur.dms;

import javax.annotation.security.RolesAllowed;

import com.vertonur.dms.constant.RoleEnum;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.pojo.config.CommentConfig;
import com.vertonur.pojo.config.EmailConfig;
import com.vertonur.pojo.config.InfoConfig;
import com.vertonur.pojo.config.ModerationConfig;
import com.vertonur.pojo.config.PrivateMsgConfig;
import com.vertonur.pojo.config.SystemContextConfig;
import com.vertonur.pojo.config.UserConfig;

public interface RuntimeParameterService {

	AttachmentConfig getAttachmentConfig();

	CommentConfig getCommentConfig();

	InfoConfig getInfoConfig();

	PrivateMsgConfig getPmConfig();

	UserConfig getUserConfig();

	EmailConfig getEmailConfig();

	SystemContextConfig getSystemContextConfig();

	ModerationConfig getModerationConfig();

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void updateAttachmentConfig(AttachmentConfig attachmentConfig);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void updateCommentConfig(CommentConfig commentConfig);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void updateInfoConfig(InfoConfig infoConfig);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void updatePmConfig(PrivateMsgConfig pmConfig);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void updateUserConfig(UserConfig userConfig);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void updateSystemContextConfig(SystemContextConfig systemContextConfig);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void updateEmailConfig(EmailConfig emailConfig);

	@RolesAllowed(RoleEnum.ROLE_ADMIN)
	void updateModerationConfig(ModerationConfig moderationConfig);
}
