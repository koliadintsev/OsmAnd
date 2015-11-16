package com.operasoft.snowboard.database;

public class AssetTypes extends Dto{
	private static final long serialVersionUID = 1L;
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Column(name = "company_id")
	private String company_id;
	
	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	@Column(name = "name")
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description")
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "asset_icon_id")
	private String asset_icon_id;
	
	public String getAsset_icon_id() {
		return asset_icon_id;
	}

	public void setAsset_icon_id(String asset_icon_id) {
		this.asset_icon_id = asset_icon_id;
	}

	@Column(name = "division_id")
	private String division_id;
	
	public String getDivision_id() {
		return division_id;
	}

	public void setDivision_id(String division_id) {
		this.division_id = division_id;
	}

	@Column(name = "status_code_id")
	private String status_code_id;
	
	public String getStatus_code_id() {
		return status_code_id;
	}

	public void setStatus_code_id(String status_code_id) {
		this.status_code_id = status_code_id;
	}

	@Column(name = "asset_inspection_template_id")
	private String asset_inspection_template_id;
	
	public String getAsset_inspection_template_id() {
		return asset_inspection_template_id;
	}

	public void setAsset_inspection_template_id(String asset_inspection_template_id) {
		this.asset_inspection_template_id = asset_inspection_template_id;
	}

	@Column(name = "bounding_box_size")
	private int bounding_box_size;
	
	public int getBounding_box_size() {
		return bounding_box_size;
	}

	public void setBounding_box_size(int bounding_box_size) {
		this.bounding_box_size = bounding_box_size;
	}

	@Column(name = "radius_unit")
	private String radius_unit;
	
	public String getRadius_unit() {
		return radius_unit;
	}

	public void setRadius_unit(String radius_unit) {
		this.radius_unit = radius_unit;
	}

	@Column(name = "create_on_tablet")
	private int create_on_tablet;
	
	public int getCreate_on_tablet() {
		return create_on_tablet;
	}

	public void setCreate_on_tablet(int create_on_tablet) {
		this.create_on_tablet = create_on_tablet;
	}

	@Column(name = "force_color")
	private String force_color;
	public String getForce_color() {
		return force_color;
	}

	public void setForce_color(String force_color) {
		this.force_color = force_color;
	}
	
}
