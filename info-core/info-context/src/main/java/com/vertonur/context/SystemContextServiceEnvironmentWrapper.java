package com.vertonur.context;

import java.util.Set;

import com.vertonur.pojo.AttachmentInfo.AttachmentType;
import com.vertonur.pojo.ExtendedBean;
import com.vertonur.pojo.config.Config;

public class SystemContextServiceEnvironmentWrapper {

	public SystemContextServiceEnvironmentWrapper(String daoManagerImplClass,
			Set<Class<? extends ExtendedBean>> extendedBeans,
			Set<Class<? extends Config>> extendedConfigs,
			Set<String> externalConfigXmls, String daoConfigFileName,
			AttachmentType uploadFileSystem, String avatarRoot) {
		SystemContextService.setDaoManagerImplClass(daoManagerImplClass);
		SystemContextService.setExtendedBeans(extendedBeans);
		SystemContextService.setExtendedConfigs(extendedConfigs);
		SystemContextService.setExternalConfigXmls(externalConfigXmls);
		SystemContextService.setDaoConfigFileName(daoConfigFileName);
		SystemContextService.setUploadFileSystem(uploadFileSystem);
		SystemContextService.setAvatarRoot(avatarRoot);
	}
}
