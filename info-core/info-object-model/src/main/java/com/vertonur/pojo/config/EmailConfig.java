package com.vertonur.pojo.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INFO_COR_EML_CNG")
public class EmailConfig implements Config{

	private int id;
	private String sender;
	private String smtpHost;
	private int smtpPort;
	private int smtpSSLPort;
	private boolean requireAuth;
	private boolean requireSSL;
	private String smtpUsername;
	private String smtpPwd;
	private String emailFormat;

	@Id
	@GeneratedValue
	@Column(name = "EML_CNG_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "EML_CNG_SENDER")
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	@Column(name = "EML_CNG_SMTP_HOST")
	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	@Column(name = "EML_CNG_SMTP_PORT")
	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	@Column(name = "EML_CNG_RQR_AUTH")
	public boolean isRequireAuth() {
		return requireAuth;
	}

	@Column(name = "EML_CNG_SMTP_SSL_PORT")
	public int getSmtpSSLPort() {
		return smtpSSLPort;
	}

	public void setSmtpSSLPort(int smtpSSLPort) {
		this.smtpSSLPort = smtpSSLPort;
	}

	public void setRequireAuth(boolean requireAuth) {
		this.requireAuth = requireAuth;
	}

	@Column(name = "EML_CNG_RQR_SSL")
	public boolean isRequireSSL() {
		return requireSSL;
	}

	public void setRequireSSL(boolean requireSSL) {
		this.requireSSL = requireSSL;
	}

	@Column(name = "EML_CNG_SMTP_USR_NME")
	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	@Column(name = "EML_CNG_SMTP_PWD")
	public String getSmtpPwd() {
		return smtpPwd;
	}

	public void setSmtpPwd(String smtpPwd) {
		this.smtpPwd = smtpPwd;
	}

	@Column(name = "EML_CNG_EML_FMT")
	public String getEmailFormat() {
		return emailFormat;
	}

	public void setEmailFormat(String emailFormat) {
		this.emailFormat = emailFormat;
	}
}
