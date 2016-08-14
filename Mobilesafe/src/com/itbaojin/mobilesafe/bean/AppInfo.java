package com.itbaojin.mobilesafe.bean;

import android.R.string;
import android.graphics.drawable.Drawable;

public class AppInfo {
	private String name;
	private Drawable icon;
	private String packageName;
	private String versionName;
	private boolean isSD;
	private boolean isUser;
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
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public boolean isSD() {
		return isSD;
	}
	public void setSD(boolean isSD) {
		this.isSD = isSD;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	public AppInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AppInfo(String name, Drawable icon, String packageName,
			String versionName, boolean isSD, boolean isUser) {
		super();
		this.name = name;
		this.icon = icon;
		this.packageName = packageName;
		this.versionName = versionName;
		this.isSD = isSD;
		this.isUser = isUser;
	}
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", icon=" + icon + ", packageName="
				+ packageName + ", versionName=" + versionName + ", isSD="
				+ isSD + ", isUser=" + isUser + "]";
	}

}
