package com.itbaojin.mobilesafe.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class ContactEngine {
	/**
	 * 获取系统联系人
	 * 
	 * @return
	 */
	public static List<HashMap<String, String>> getAllContactInfo(
			Context context) {
		ArrayList<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();
		//1,获取内容解析器对象
		ContentResolver contentResolver = context.getContentResolver();
		//2,做查询系统联系人数据库表过程(读取联系人权限)
		Cursor cursor = contentResolver.query(
				Uri.parse("content://com.android.contacts/raw_contacts"),
				new String[]{"contact_id"},
				null,null, null);
		contactList.clear();
		//3,循环游标,直到没有数据为止
		while(cursor.moveToNext()){
			String id = cursor.getString(0);
			//cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex : 
//			查询字段在cursor中索引值,一般都是用在查询字段比较多的时候
//			Log.i(tag, "id = " + id);
			//4,根据用户唯一性id值,查询data表和mimetype表生成的视图,获取data以及mimetype字段
			if (!TextUtils.isEmpty(id)) {//null   ""
				// 7.根据id查询view_data表中的数据
				// selection : 查询条件
				// selectionArgs :查询条件的参数
				// sortOrder : 排序
				// 空指针: 1.null.方法 2.参数为null
			Cursor indexCursor = contentResolver.query(
					Uri.parse("content://com.android.contacts/data"),
					new String[]{"data1","mimetype"},
					"raw_contact_id = ?",new String[]{id}, null);
			//5,循环获取每一个联系人的电话号码以及姓名,数据类型
			HashMap<String, String> hashMap = new HashMap<String,String>();
			while(indexCursor.moveToNext()){
				String data = indexCursor.getString(0);
				String type = indexCursor.getString(1);
				if (type.equals("vnd.android.cursor.item/phone_v2")) {
					if (!TextUtils.isEmpty(data)) {
						
						hashMap.put("phone", data);
					}
				}else if (type.equals("vnd.android.cursor.item/name")) {
					if (!TextUtils.isEmpty(data)) {
						
						hashMap.put("name", data);
					}
				}
				
			}
			indexCursor.close();
			contactList.add(hashMap);
			}
		}
		
		cursor.close();
		return contactList;
	}
}
