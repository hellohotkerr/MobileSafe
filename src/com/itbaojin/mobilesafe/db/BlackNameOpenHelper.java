package com.itbaojin.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNameOpenHelper extends SQLiteOpenHelper {
	//方便后期我们在实现数据库操作的时候能方便去使用表名,同时也方便后期去修改表名
	public static final String DB_NAME="info";

	/**	context : 上下文
	* name :　数据库名称
	* factory：游标工厂
	* version : 数据库的版本号
	*/
	public BlackNameOpenHelper(Context context) {
		super(context, "blacknum.db", null, 1);
	}
	
	//第一次创建数据库的调用,创建表结构
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建表结构   字段:   blacknum:黑名单号码     mode:拦截类型
		//参数:创建表结构sql语句
		db.execSQL("create table "+DB_NAME+"(_id integer primary key autoincrement,blacknum varchar(20),mode varchar(2))");
	}
	//当数据库版本发生变化的时候调用
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
//	public boolean deleteDatabase(Context context){
//		return context.deleteDatabase(DB_NAME);
//	}
}
