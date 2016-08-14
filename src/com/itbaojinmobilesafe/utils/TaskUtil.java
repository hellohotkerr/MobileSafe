package com.itbaojinmobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class TaskUtil {
	/**
	 * ��ȡ�������еĽ��̵ĸ���
	 * @return
	 */
	public static int getProcessCount(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		return runningAppProcesses.size();
	}
	/**
	 * ��ȡʣ���ڴ�
	 * @return
	 */
	public static long getAvailableRam(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//��ȡ�ڴ����Ϣ,���浽memoryinfo��
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		//��ȡ���е��ڴ�
		//outInfo.availMem;
//		//��ȡ�ܵ��ڴ�
//		outInfo.totalMem;
		return outInfo.availMem;
	}
	
	/**
	 * ��ȡ�ܵ��ڴ�
	 * @return
	 * @deprecated
	 */
	public static long getTotalRam(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//��ȡ�ڴ����Ϣ,���浽memoryinfo��
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		//��ȡ���е��ڴ�
		//outInfo.availMem;
//		//��ȡ�ܵ��ڴ�
//		outInfo.totalMem;
		return outInfo.totalMem;//16�汾֮�ϲ���,֮����û�е�
	}
	/**
	 * ���ݵͰ汾
	 * @return
	 */
	public static long getTotalRam(){
		File file = new File("/proc/meminfo");
		StringBuilder sb = new StringBuilder();
		try {
			//��ȡ�ļ�
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readLine = br.readLine();
			//��ȡ����
			char[] charArray = readLine.toCharArray();
			for (char c : charArray) {
				if (c>='0' && c<='9') {
					sb.append(c);
				}
			}
			String string = sb.toString();
			//ת����long
			long parseLong = Long.parseLong(string);
			return parseLong*1024;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
