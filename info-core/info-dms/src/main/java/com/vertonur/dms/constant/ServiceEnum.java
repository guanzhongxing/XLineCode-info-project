package com.vertonur.dms.constant;

import com.vertonur.dms.AttachmentService;
import com.vertonur.dms.CategoryService;
import com.vertonur.dms.DepartmentService;
import com.vertonur.dms.FileService;
import com.vertonur.dms.GroupService;
import com.vertonur.dms.InfoService;
import com.vertonur.dms.MailService;
import com.vertonur.dms.ModerationService;
import com.vertonur.dms.PermissionService;
import com.vertonur.dms.RoleService;
import com.vertonur.dms.RuntimeParameterService;
import com.vertonur.dms.SystemService;
import com.vertonur.dms.TemplateService;
import com.vertonur.dms.UserService;

public interface ServiceEnum {
	public static final Class<AttachmentService> ATTACHMENT_SERVICE = AttachmentService.class;
	public static final Class<CategoryService> CATEGORY_SERVICE = CategoryService.class;
	public static final Class<DepartmentService> DEPARTMENT_SERVICE = DepartmentService.class;
	public static final Class<FileService> FILE_SERVICE = FileService.class;
	public static final Class<InfoService> INFO_SERVICE = InfoService.class;
	public static final Class<SystemService> SYSTEM_SERVICE = SystemService.class;
	public static final Class<UserService> USER_SERVICE = UserService.class;
	public static final Class<RoleService> ROLE_SERVICE = RoleService.class;
	public static final Class<GroupService> GROUP_SERVICE = GroupService.class;
	public static final Class<PermissionService> PERMISSION_SERVICE = PermissionService.class;
	public static final Class<MailService> MAIL_SERVICE = MailService.class;
	public static final Class<TemplateService> TEMPLATE_SERVICE = TemplateService.class;
	public static final Class<ModerationService> MODERATION_SERVICE = ModerationService.class;
	public static final Class<RuntimeParameterService> RUNTIME_PARAMETER_SERVICE = RuntimeParameterService.class;
}
