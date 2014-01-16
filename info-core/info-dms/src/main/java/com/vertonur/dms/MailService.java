package com.vertonur.dms;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.vertonur.pojo.config.EmailConfig;

public class MailService {
	private String newCmtNotificationFile;
	private String newCmtNotificationSubject;
	private String newPmNotificationFile;
	private String newPmNotificationSubject;
	private String activateAcctNotificationFile;
	private String activateAcctNotificationSubject;
	private String recoverPwdNotificationFile;
	private String recoverPwdNotificationSubject;

	protected MailService() {
	}

	public void sendMail(String subject, String msg,
			Map<String, String> addresses) throws EmailException {
		EmailConfig config = new RuntimeParameterServiceImpl().getEmailConfig();
		Email email = new HtmlEmail();
		email.setHostName(config.getSmtpHost());
		email.setFrom(config.getSender());
		if (config.isRequireSSL())
			email.setSSL(true);
		if (config.isRequireAuth())
			email.setAuthentication(config.getSmtpUsername(),
					config.getSmtpPwd());

		Set<Entry<String, String>> entry = addresses.entrySet();
		for (Map.Entry<String, String> address : entry)
			email.addTo(address.getValue(), address.getKey());

		email.setSubject(subject);
		if ("html".equalsIgnoreCase(config.getEmailFormat())) {
			((HtmlEmail) email).setHtmlMsg(msg);
			((HtmlEmail) email)
					.setTextMsg("Your email client does not support HTML messages");
		} else
			email.setMsg(msg);

		email.send();
	}

	public String getNewCmtNotificationFile() {
		return newCmtNotificationFile;
	}

	public void setNewCmtNotificationFile(String newCmtNotificationFile) {
		this.newCmtNotificationFile = newCmtNotificationFile;
	}

	public String getNewCmtNotificationSubject() {
		return newCmtNotificationSubject;
	}

	public void setNewCmtNotificationSubject(String newCmtNotificationSubject) {
		this.newCmtNotificationSubject = newCmtNotificationSubject;
	}

	public String getNewPmNotificationFile() {
		return newPmNotificationFile;
	}

	public void setNewPmNotificationFile(String newPmNotificationFile) {
		this.newPmNotificationFile = newPmNotificationFile;
	}

	public String getNewPmNotificationSubject() {
		return newPmNotificationSubject;
	}

	public void setNewPmNotificationSubject(String newPmNotificationSubject) {
		this.newPmNotificationSubject = newPmNotificationSubject;
	}

	public String getActivateAcctNotificationFile() {
		return activateAcctNotificationFile;
	}

	public void setActivateAcctNotificationFile(
			String activateAcctNotificationFile) {
		this.activateAcctNotificationFile = activateAcctNotificationFile;
	}

	public String getActivateAcctNotificationSubject() {
		return activateAcctNotificationSubject;
	}

	public void setActivateAcctNotificationSubject(
			String activateAcctNotificationSubject) {
		this.activateAcctNotificationSubject = activateAcctNotificationSubject;
	}

	public String getRecoverPwdNotificationFile() {
		return recoverPwdNotificationFile;
	}

	public void setRecoverPwdNotificationFile(String recoverPwdNotificationFile) {
		this.recoverPwdNotificationFile = recoverPwdNotificationFile;
	}

	public String getRecoverPwdNotificationSubject() {
		return recoverPwdNotificationSubject;
	}

	public void setRecoverPwdNotificationSubject(
			String recoverPwdNotificationSubject) {
		this.recoverPwdNotificationSubject = recoverPwdNotificationSubject;
	}
}
