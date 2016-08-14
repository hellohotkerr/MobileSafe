package com.itbaojin.mobilesafe.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//��ѯMd5ֵ�Ƿ������ݿ���
public class AntiVirusDao {
	/**
	 * ��ѯMd5ֵ�Ƿ��ڲ������ݿ���
	 * @param context
	 * @param md5
	 * @return
	 */
	public static boolean queryAntiVirus(Context context,String md5){
		boolean ishave = false;
		//1.��ȡ���ݿ��·��
		File file = new File(context.getFilesDir(), "antivirus.db");
		SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.query("datable", null, "md5=?", new String[]{md5}, null, null, null);
		if (cursor.moveToNext()) {
			ishave = true;
		}
		cursor.close();
		database.close();
		return ishave;
	}
	
}
