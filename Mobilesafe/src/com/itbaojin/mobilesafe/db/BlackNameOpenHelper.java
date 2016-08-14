package com.itbaojin.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNameOpenHelper extends SQLiteOpenHelper {
	//�������������ʵ�����ݿ������ʱ���ܷ���ȥʹ�ñ���,ͬʱҲ�������ȥ�޸ı���
	public static final String DB_NAME="info";

	/**	context : ������
	* name :�����ݿ�����
	* factory���α깤��
	* version : ���ݿ�İ汾��
	*/
	public BlackNameOpenHelper(Context context) {
		super(context, "blacknum.db", null, 1);
	}
	
	//��һ�δ������ݿ�ĵ���,������ṹ
	@Override
	public void onCreate(SQLiteDatabase db) {
		//������ṹ   �ֶ�:   blacknum:����������     mode:��������
		//����:������ṹsql���
		db.execSQL("create table "+DB_NAME+"(_id integer primary key autoincrement,blacknum varchar(20),mode varchar(2))");
	}
	//�����ݿ�汾�����仯��ʱ�����
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
//	public boolean deleteDatabase(Context context){
//		return context.deleteDatabase(DB_NAME);
//	}
}
