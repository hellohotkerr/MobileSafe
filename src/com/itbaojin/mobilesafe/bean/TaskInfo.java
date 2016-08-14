package com.itbaojin.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private String name;
	private Drawable icon;
	private long ramSize;
	private String packageName;
	private boolean isUser;
	//checkbox是否被选中
	private boolean isChecked = false;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public long getRamSize() {
		return ramSize;
	}

	public void setRamSize(long ramSize) {
		this.ramSize = ramSize;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isUser() {
		return isUser;
	}

	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}

	public TaskInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TaskInfo(String name, Drawable icon, long ramSize,
			String packageName, boolean isUser) {
		super();
		this.name = name;
		this.icon = icon;
		this.ramSize = ramSize;
		this.packageName = packageName;
		this.isUser = isUser;
	}

	@Override
	public String toString() {
		return "TaskInfo [name=" + name + ", icon=" + icon + ", ramSize="
				+ ramSize + ", packageName=" + packageName + ", isUser="
				+ isUser + "]";
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
}
