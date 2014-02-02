package com.vertonur.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.vertonur.pojo.security.Group;
import com.vertonur.pojo.statistician.UserMsgStatistician;

@Entity(name = "INFO_COR_USR")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USR_TYPE", discriminatorType = DiscriminatorType.STRING)
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String password;
	private Attachment avatar;
	private int gender;
	private String email;
	private String signature;
	private Date regTime;
	private Date lastLoginDate;
	private boolean attmEnabled;
	private boolean canDownloadAttms;
	private boolean locked;
	private boolean activated = false;

	private String qq;
	private String msn;
	private String webSite;
	private String interests;
	private String location;

	private UserPreferences userPres;
	private List<Attachment> attachments;

	private UserMsgStatistician statistician;

	private Set<AbstractInfo> comments = new HashSet<AbstractInfo>();
	private Set<AbstractInfo> infos = new HashSet<AbstractInfo>();
	private Set<Group> groups = new HashSet<Group>();

	public User() {
		setRegTime(new Date());
		statistician = new UserMsgStatistician();
	}

	@Id
	@GeneratedValue
	@Column(name = "USR_ID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "USR_AVATAR_ID")
	public Attachment getAvatar() {
		return avatar;
	}

	public void setAvatar(Attachment avatar) {
		this.avatar = avatar;
	}

	@Column(name = "USR_NME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "USR_PWD", nullable = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "USR_GENDER")
	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	@Column(name = "USR_EML", unique = true, nullable = false)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "USR_SIGNATURE")
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Column(name = "USR_REGTIME", nullable = false, updatable = false)
	public Date getRegTime() {
		return regTime;
	}

	public void setRegTime(Date regTime) {
		this.regTime = regTime;
	}

	@Column(name = "USR_LAST_LOGON_DAT")
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	@Column(name = "USR_ATTM_ENABLED")
	public boolean isAttmEnabled() {
		return attmEnabled;
	}

	public void setAttmEnabled(boolean attmEnabled) {
		this.attmEnabled = attmEnabled;
	}

	@Column(name = "USR_CAN_DWN_ATTMS")
	public boolean isCanDownloadAttms() {
		return canDownloadAttms;
	}

	public void setCanDownloadAttms(boolean canDownloadAttms) {
		this.canDownloadAttms = canDownloadAttms;
	}

	@Column(name = "USR_QQ")
	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	@Column(name = "USR_MSN")
	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	@Column(name = "USR_WEBSITE")
	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	@Column(name = "USR_INTERESTS")
	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	@Column(name = "USR_LOCATION")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "USR_USR_PREFERENCE_ID")
	public UserPreferences getUserPres() {
		return userPres;
	}

	public void setUserPres(UserPreferences userPres) {
		this.userPres = userPres;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FK_USR_STATISTICIAN_ID")
	public UserMsgStatistician getStatistician() {
		return statistician;
	}

	public void setStatistician(UserMsgStatistician statistician) {
		this.statistician = statistician;
	}

	@OneToMany(mappedBy = "author")
	public Set<AbstractInfo> getComments() {
		return comments;
	}

	public void setComments(Set<AbstractInfo> comments) {
		this.comments = comments;
	}

	@OneToMany(mappedBy = "author")
	public Set<AbstractInfo> getInfos() {
		return infos;
	}

	public void setInfos(Set<AbstractInfo> infos) {
		this.infos = infos;
	}

	public void removeInfo(Info info) {
		infos.remove(info);
	}

	@Column(name = "USR_IS_LOCKED")
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Column(name = "USR_IS_ACTIVATED")
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@ManyToMany
	@JoinTable(name = "INFO_COR_USR_GRP", joinColumns = { @JoinColumn(name = "USR_ID") }, inverseJoinColumns = { @JoinColumn(name = "GRP_ID") })
	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public void addGroup(Group group) {
		groups.add(group);
	}

	public void addGroups(Set<Group> newGroups) {
		groups.addAll(newGroups);
	}

	@OneToMany(mappedBy = "uploader")
	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
}