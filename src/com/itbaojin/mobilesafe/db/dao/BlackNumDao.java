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
	 * 在构造函数中获取blackNameOpenHelper
	 */
	public BlackNumDao(Context context){
		blackNumOpenHelper = new BlackNameOpenHelper(context);
	}
	//增删改查
	/**
	 * 添加黑名单
	 * @param blacknum
	 * @param mode
	 */
	public void addBlackNum(String blacknum,int mode){
		// 1.获取数据库
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
		// 2.添加操作
		// ContentValues :　添加的数据
		ContentValues values = new ContentValues();
		values.put("blacknum", blacknum);
		values.put("mode", mode);
		database.insert(BlackNameOpenHelper.DB_NAME, null, values);
		// 3.关闭数据库
		database.close();
	}
	/**
	 * 更新黑名单的拦截模式
	 */
	public void updateBlackNum(String blacknum,int mode){
		//1.获取数据库
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
		//2.更新操作
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		//table : 表名
		//values : 要更新数据
		//whereClause : 查询条件  where blacknum=blacknum
		//whereArgs : 查询条件的参数
		database.update(BlackNameOpenHelper.DB_NAME, values, "blacknum=?", new String[]{blacknum});
		//3.关闭数据库
		database.close();
	}
	/**
	 * 通过黑名单号码,查询黑名单号码的拦截模式
	 */
	public int queryBlackNumMode(String blacknum){
		int mode=-1;
		//1.获取数据库
		SQLiteDatabase database = blackNumOpenHelper.getReadableDatabase();
		//2.查询数据库
		//table : 表名
		//columns : 查询的字段  mode
		//selection : 查询条件  where xxxx = xxxx
		//selectionArgs : 查询条件的参数
		//groupBy : 分组
		//having : 去重
		//orderBy : 排序
		Cursor cursor = database.query(BlackNameOpenHelper.DB_NAME, new String[]{"mode"}, "blacknum=?", new String[]{blacknum}, null, null, null);
		//3.解析cursor
		if (cursor.moveToNext()) {
			//获取查询出来的数据
			mode = cursor.getInt(0);
		}
		//4.关闭数据库
		cursor.close();
		database.close();
		return mode;
	}
	/**
	 * 根据黑名单号码,删除相应的数据
	 * @param blacknum
	 */
	public void deleteBlackNum(String blacknum){
		//1.获取数据库
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
		//2.删除
		//table : 表名
		//whereClause : 查询的条件
		//whereArgs : 查询条件的参数
		database.delete(BlackNameOpenHelper.DB_NAME, "blacknum=?", new String[]{blacknum});
		//3.关闭数据库
		database.close();
	}
	
	/**
	 * 查询全部黑名单
	 * @return
	 */
	public List<BlackNumInfo> queryAllBlack(){
		List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
		//1.获取数据库
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
		Cursor cursor = database.query(BlackNameOpenHelper.DB_NAME, new String[]{"blacknum","mode"}, null, null, null, null, "_id desc");	//desc倒序查询	asc正序查寻
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
	 * @param MaxNum 查询的总个数
	 * @param startIndex 起始位置
	 * @return
	 */
	public List<BlackNumInfo> getPartBlackNum(int MaxNum,int startIndex){
		List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
		//1.获取数据库
		SQLiteDatabase database = blackNumOpenHelper.getWritableDatabase();
//		Cursor cursor = database.query(BlackNameOpenHelper.DB_NAME, new String[]{"blacknum","mode"}, null, null, null, null, "_id desc");	//desc倒序查询	asc正序查寻
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
