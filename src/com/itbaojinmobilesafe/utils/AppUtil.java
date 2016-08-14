package com.itbaojinmobilesafe.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class AppUtil {
	/**
	 * 获取SD卡可用空间
	 */
	public static long getAvailableSD(){
		 //获取SD卡路径
		 File path = Environment.getExternalStorageDirectory();
		 //硬盘的API操作
         StatFs stat = new StatFs(path.getPath());
         long blockSize = stat.getBlockSize();//获取每块的大小
         long totalBlocks = stat.getBlockCount();//获取总块数
         long availableBlocks = stat.getAvailableBlocks();//获取可用的块数
         return availableBlocks*blockSize;
	}
	/**
	 *获取内存可用空间
	 * @return
	 */
	public static long getAvailableROM(){
		//获取内存路径
		File path = Environment.getDataDirectory();
		//硬盘的API操作
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();//获取每块的大小
        long totalBlocks = stat.getBlockCount();//获取总块数
        long availableBlocks = stat.getAvailableBlocks();//获取可用的块数
        return availableBlocks*blockSize;
	}
}
