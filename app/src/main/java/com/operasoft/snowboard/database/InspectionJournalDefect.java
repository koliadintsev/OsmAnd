package com.operasoft.snowboard.database;

public class InspectionJournalDefect extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="inspection_journal_id")
	private String journalId;

	@Column(name="inspection_checklist_item_id")
	private String itemId;

	@Column(name="notes")
	private String notes;

	public String getJournalId() {
		return journalId;
	}

	public void setJournalId(String journalId) {
		this.journalId = journalId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
