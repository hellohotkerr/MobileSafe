package com.itbaojin.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itbaojin.mobilesafe.bean.BlackNumInfo;
import com.itbaojin.mobilesafe.db.BlackNameOpenHelper;

public class BlackNumDao {
	public static final int CALL = 0;
	public static final int SMS =1;
	public static final int ALL=2;
	private BlackNameOpenHelper blackNumOpenHelper;
	

	/**
	 * �ڹ��캯���л�ȡblackNameOpenHelper
	 */
	public BlackNumDao(Context context){
		blackNumOpenHelper = new BlackNameOpenHelper(context);
	}
	//��ɾ�Ĳ�
	/**
	 * ��Ӻ�����
	 * @param blacknum
	 * @param mode
	 */
	public void addBlackNum(String blacknum,int mode){
		// 1.��ȡ���ݿ�
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
		// 2.��Ӳ���
		// ContentValues :����ӵ�����
		ContentValues values = new ContentValues();
		values.put("blacknum", blacknum);
		values.put("mode", mode);
		database.insert(BlackNameOpenHelper.DB_NAME, null, values);
		// 3.�ر����ݿ�
		database.close();
	}
	/**
	 * ���º�����������ģʽ
	 */
	public void updateBlackNum(String blacknum,int mode){
		//1.��ȡ���ݿ�
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
		//2.���²���
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		//table : ����
		//values : Ҫ��������
		//whereClause : ��ѯ����  where blacknum=blacknum
		//whereArgs : ��ѯ�����Ĳ���
		database.update(BlackNameOpenHelper.DB_NAME, values, "blacknum=?", new String[]{blacknum});
		//3.�ر����ݿ�
		database.close();
	}
	/**
	 * ͨ������������,��ѯ���������������ģʽ
	 */
	public int queryBlackNumMode(String blacknum){
		int mode=-1;
		//1.��ȡ���ݿ�
		SQLiteDatabase database = blackNumOpenHelper.getReadableDatabase();
		//2.��ѯ���ݿ�
		//table : ����
		//columns : ��ѯ���ֶ�  mode
		//selection : ��ѯ����  where xxxx = xxxx
		//selectionArgs : ��ѯ�����Ĳ���
		//groupBy : ����
		//having : ȥ��
		//orderBy : ����
		Cursor cursor = database.query(BlackNameOpenHelper.DB_NAME, new String[]{"mode"}, "blacknum=?", new String[]{blacknum}, null, null, null);
		//3.����cursor
		if (cursor.moveToNext()) {
			//��ȡ��ѯ����������
			mode = cursor.getInt(0);
		}
		//4.�ر����ݿ�
		cursor.close();
		database.close();
		return mode;
	}
	/**
	 * ���ݺ���������,ɾ����Ӧ������
	 * @param blacknum
	 */
	public void deleteBlackNum(String blacknum){
		//1.��ȡ���ݿ�
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
		//2.ɾ��
		//table : ����
		//whereClause : ��ѯ������
		//whereArgs : ��ѯ�����Ĳ���
		database.delete(BlackNameOpenHelper.DB_NAME, "blacknum=?", new String[]{blacknum});
		//3.�ر����ݿ�
		database.close();
	}
	
	/**
	 * ��ѯȫ��������
	 * @return
	 */
	public List<BlackNumInfo> queryAllBlack(){
		List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
		//1.��ȡ���ݿ�
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
		Cursor cursor = database.query(BlackNameOpenHelper.DB_NAME, new String[]{"blacknum","mode"}, null, null, null, null, "_id desc");	//desc�����ѯ	asc�����Ѱ
		while(cursor.moveToNext()){
			String blackNum = cursor.getString(0);
			int mode = cursor.getInt(1);
			BlackNumInfo blackNumInfo = new BlackNumInfo(blackNum, mode);
			list.add(blackNumInfo);
		}
		cursor.close();
		database.close();
		return list;
	}
	
	/**
	 * @param MaxNum ��ѯ���ܸ���
	 * @param startIndex ��ʼλ��
	 * @return
	 */
	public List<BlackNumInfo> getPartBlackNum(int MaxNum,int startIndex){
		List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
		//1.��ȡ���ݿ�
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
//		Cursor cursor = database.query(BlackNameOpenHelper.DB_NAME, new String[]{"blacknum","mode"}, null, null, null, null, "_id desc");	//desc�����ѯ	asc�����Ѱ
		Cursor cursor = database.rawQuery("select blacknum,mode from info order by _id desc limit ? offset ?", new String[]{MaxNum +"",startIndex+""});
		while(cursor.moveToNext()){
			String blackNum = cursor.getString(0);
			int mode = cursor.getInt(1);
			BlackNumInfo blackNumInfo = new BlackNumInfo(blackNum, mode);
			list.add(blackNumInfo);
		}
		cursor.close();
		database.close();
		return list;
	}
}
