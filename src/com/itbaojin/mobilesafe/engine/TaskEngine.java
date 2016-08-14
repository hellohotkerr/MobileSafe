package com.itbaojin.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.itbaojin.mobilesafe.bean.TaskInfo;

public class TaskEngine {
	/**
	 * 获取系统中所有进程信息
	 * @return
	 */
	public static List<TaskInfo> getTaskAllInfo(Context context){
		List<TaskInfo> list = new ArrayList<TaskInfo>();
		//1.进程的管理者
		ActivityManager activityManager = (ActivityManager)  context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		//2.获取所有正在运行的进程信息
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		//遍历集合
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			TaskInfo taskInfo = new TaskInfo();
			//3.获取相应的信息
			//获取进程的名称,获取包名
			String packageName = runningAppProcessInfo.processName;
			taskInfo.setPackageName(packageName);
			//获取进程所占的内存空间,int[] pids : 输入几个进程的pid,就会返回几个进程所占的空间
//			MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			android.os.Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			int totalPss = memoryInfo[0].getTotalPss();
			long ramSize = totalPss*1024;
			taskInfo.setRamSize(ramSize);
			try {
				//获取application信息
				//packageName : 包名     flags:指定信息标签
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				//获取图标
				Drawable icon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				//获取名称
				String name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				//获取程序的所有标签信息,是否是系统程序是以标签的形式展示
				int flags = applicationInfo.flags;
				boolean isUser;
				//判断是否是用户程序
				if ((applicationInfo.FLAG_SYSTEM & flags) == applicationInfo.FLAG_SYSTEM) {
					//系统程序
					isUser = false;
				}else{
					//用户程序
					isUser = true;
				}
				//保存信息
				taskInfo.setUser(isUser);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			//taskinfo添加到集合
			list.add(taskInfo);
		}
		return list;
	}
}
