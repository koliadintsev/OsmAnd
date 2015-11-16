package com.operasoft.snowboard.database;

public class Route extends Dto {

	private static final long serialVersionUID = 1L;
	public final static String ROUTE_INACTIVE = "4fb779b1-5ae0-4466-9012-6f4aae8ed672";
	public final static String ROUTE_ACTIVE = "4fb779ac-da90-432c-ba22-6f4aae8ed672";
	public final static String SHOW_POPUP = "1";
	public final static int LOOK_AHEAD_TURN_COUNT = 6;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "name")
	private String name;

	@Column(name = "status_code_id")
	private String status;

	@Column(name = "route_group_id")
	private String routeGroupId;

	@Column(name = "season_id")
	private String seasonId;

	@Column(name = "linepath")
	private String linePath;

	@Column(name = "pop_up_alert_sb")
	private String popUp;

	public String getLinePath() {
		return linePath;
	}

	public void setLinePath(String linePath) {
		this.linePath = linePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String company_id) {
		this.companyId = company_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status_code_id) {
		this.status = status_code_id;
	}

	public String getRouteGroupId() {
		return routeGroupId;
	}

	public void setRouteGroupId(String route_group_id) {
		this.routeGroupId = route_group_id;
	}

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String season_id) {
		this.seasonId = season_id;
	}

	public boolean isActive() {
		if (status == null) {
			// Be safe... assume it is active
			return true;
		}

		return !status.equalsIgnoreCase(ROUTE_INACTIVE);
	}

	public String getPopUp() {
		return popUp;
	}

	public void setPopUp(String popUp) {
		this.popUp = popUp;
	}

	/**
	 * indicates if route is considered -1 as new 0 as started with no completed service locations
	 * with service completed in 1,2,...6 past days
	 */
	public int pastDaysServiced = -1;
}
