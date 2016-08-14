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
	 * ��ȡϵͳ������Ӧ�ó������Ϣ
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
				//ϵͳ����
				isUser =false;
			}else {
				//�û�����
				isUser = true;
			}
			boolean isSD;
			if (applicationInfo.FLAG_EXTERNAL_STORAGE == (applicationInfo.FLAG_SYSTEM & flags)) {
				//��װ����SD��
				isSD = true;
			}else {
				//�ֻ���
				isSD = false;
			}
			
			AppInfo appInfo = new AppInfo(name, icon, packageName, versionName, isSD, isUser);
			list.add(appInfo);
		}
		
		return list;
	}
}
