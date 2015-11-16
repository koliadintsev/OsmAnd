package com.operasoft.snowboard.database;

public class Login extends Dto {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Column(name = "company_id")
	String						companyId;

	@Column(name = "user_id")
	String						userId;

	@Column(name = "session_id")
	String						sessionId;

	@Column(name = "login")
	String						login;

	@Column(name = "logout")
	String						logout;

	@Column(name = "ip_address")
	String						ipAddress;

	@Column(name = "browser_type")
	String						browserType;

	@Column(name = "browser_version")
	String						browserVersion;

	@Column(name = "session_end_type")
	String						sessionEndType;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogout() {
		return logout;
	}

	public void setLogout(String logout) {
		this.logout = logout;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getBrowserType() {
		return browserType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}

	public String getSessionEndType() {
		return sessionEndType;
	}

	public void setSessionEndType(String sessionEndType) {
		this.sessionEndType = sessionEndType;
	}

}
