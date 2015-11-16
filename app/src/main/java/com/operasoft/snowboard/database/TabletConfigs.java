package com.operasoft.snowboard.database;

public class TabletConfigs extends Dto {

	private static final long serialVersionUID = 1L;

	// Turn by turn navigation feature
	public static final String TBT_NAV_WITH_VOICE = "with_voice";
	public static final String TBT_NAV_WITHOUT_VOICE = "without_voice";
	public static final String TBT_NAV_DISABLED = "disabled";

	@Column(name = "company_id")
	private String companyId;

	// with_voice
	@Column(name = "turn_by_turn")
	private String turnByTurn;

	// 1 
	@Column(name = "turn_count")
	private int turnCount;

	// 50 
	@Column(name = "turn_distance")
	private int turnDistance;

	// normal
	@Column(name = "turn_display")
	private String turnDisplay;

	public static TabletConfigs getDefault(String compId) {
		TabletConfigs config = new TabletConfigs();
		config.turnByTurn = "with_voice";
		config.turnCount = 1;
		config.turnDistance = 50;
		config.turnDisplay = "normal";
		config.companyId = compId;
		return config;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getTurnByTurn() {
		return turnByTurn;
	}

	public void setTurnByTurn(String turnByTurn) {
		this.turnByTurn = turnByTurn;
	}

	public int getTurnCount() {
		return turnCount;
	}

	public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
	}

	public int getTurnDistance() {
		return turnDistance;
	}

	public void setTurnDistance(int turnDistance) {
		this.turnDistance = turnDistance;
	}

	public String getTurnDisplay() {
		return turnDisplay;
	}

	public void setTurnDisplay(String turnDisplay) {
		this.turnDisplay = turnDisplay;
	}

}
