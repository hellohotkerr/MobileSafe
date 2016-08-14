package com.itbaojin.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.itbaojin.mobilesafe.bean.AppInfo;

public class AppEngine {

	/**
	 * 获取系统中所有应用程序的信息
	 */
	public static List<AppInfo> getAppInfo(Context context){
		List<AppInfo> list = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			String packageName = packageInfo.packageName;
			String versionName = packageInfo.versionName;
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			int uid = applicationInfo.uid;
			Drawable icon = applicationInfo.loadIcon(pm);
			String name = applicationInfo.loadLabel(pm).toString();
			boolean isUser ;
			int flags = applicationInfo.flags;
			if ((applicationInfo.FLAG_SYSTEM & flags) == applicationInfo.FLAG_SYSTEM) {
				//系统程序
				isUser =false;
			}else {
				//用户程序
				isUser = true;
			}
			boolean isSD;
			if (applicationInfo.FLAG_EXTERNAL_STORAGE == (applicationInfo.FLAG_SYSTEM & flags)) {
				//安装到了SD卡
				isSD = true;
			}else {
				//手机中
				isSD = false;
			}
			
			AppInfo appInfo = new AppInfo(name, icon, packageName, versionName, isSD, isUser);
			list.add(appInfo);
		}
		
		return list;
	}
}
