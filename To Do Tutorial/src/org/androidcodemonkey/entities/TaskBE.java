package org.androidcodemonkey.entities;

public class TaskBE {
	private String item="";
	private String priority="";
	
	public String getItem() {
		return(item);
	}
	
	public void setItem(String value) {
		this.item = value;
	}
	public String getPriority() {
		return(priority);
	}
	public void setType(String value) {
		this.priority = value;
	}
	public String toString() {
		return(getItem());
	}
}