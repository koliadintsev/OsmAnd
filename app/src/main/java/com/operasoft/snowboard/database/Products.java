package com.operasoft.snowboard.database;

public class Products extends Dto implements Comparable {

	private static final long serialVersionUID = 1L;

	public static final String PRODUCT_TYPE_EVENT = "E";
	public static final String PRODUCT_TYPE_SEASON = "S";
	
	public static final String PRODUCT_TYPE = "Product";
	public static final String SERVICE_TYPE = "Services";
	
	public static final String ACTIVE_STATUS_CODE = "50444c09-7120-466d-8fca-12d2ae8ed672";
	

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "name")
	private String name;

	@Column(name = "description_short")
	private String description_short;

	@Column(name = "description_long")
	private String description_long;

	@Column(name = "uom_id")
	private String uom_id;

	@Column(name = "route_group_id")
	private String route_group_id;

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "deleted_date")
	private String deleted_date;

	@Column(name = "status_code_id")
	private String status_code_id;

	@Column(name = "creator_id")
	private String creator_id;

	@Column(name = "product_type")
	private String product_type;

	@Column(name = "import_id")
	private String import_id;

	@Column(name = "non_taxable")
	private int non_taxable;

	@Column(name = "foreman_daily_worksheet")
	private int foreman_daily_worksheet;

	@Column(name = "product_code")
	private String product_code;

	@Column(name = "type")
	private String type;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription_short() {
		return description_short;
	}

	public void setDescription_short(String description_short) {
		this.description_short = description_short;
	}

	public String getDescription_long() {
		return description_long;
	}

	public void setDescription_long(String description_long) {
		this.description_long = description_long;
	}

	public String getUom_id() {
		return uom_id;
	}

	public void setUom_id(String uom_id) {
		this.uom_id = uom_id;
	}

	public String getRoute_group_id() {
		return route_group_id;
	}

	public void setRoute_group_id(String route_group_id) {
		this.route_group_id = route_group_id;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public boolean isSeason() {
		return this.product_type.equals(PRODUCT_TYPE_SEASON);
	}

	public boolean isEvent() {
		return this.product_type.equals(PRODUCT_TYPE_EVENT);
	}

	public boolean isdeleted() {
		return (deleted != 0);
	}

	public String getDeleted_date() {
		return deleted_date;
	}

	public void setDeleted_date(String deleted_date) {
		this.deleted_date = deleted_date;
	}

	public String getStatus_code_id() {
		return status_code_id;
	}

	public void setStatus_code_id(String status_code_id) {
		this.status_code_id = status_code_id;
	}

	public boolean isActive() {
		return status_code_id == ACTIVE_STATUS_CODE;
	}
	
	public String getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(String creator_id) {
		this.creator_id = creator_id;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

	public String getImport_id() {
		return import_id;
	}

	public void setImport_id(String import_id) {
		this.import_id = import_id;
	}

	public int getNon_taxable() {
		return non_taxable;
	}

	public boolean isNon_taxable() {
		return (non_taxable != 0);
	}

	public void setNon_taxable(int non_taxable) {
		this.non_taxable = non_taxable;
	}

	public int getForeman_daily_worksheet() {
		return foreman_daily_worksheet;
	}

	public void setForeman_daily_worksheet(int foreman_daily_worksheet) {
		this.foreman_daily_worksheet = foreman_daily_worksheet;
	}

	public boolean isForeman_daily_worksheet() {
		return (foreman_daily_worksheet != 0);
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isService() {
		if (type == null) {
			return false;
		}
		return type.equalsIgnoreCase(SERVICE_TYPE);
	}
	
	public boolean isProduct() {
		if (type == null) {
			return false;
		}
		return type.equalsIgnoreCase(PRODUCT_TYPE);		
	}

	@Override
	public int compareTo(Object a) {
		Products b = (Products) a;
		
		return name.compareToIgnoreCase(b.getName());
	}
}