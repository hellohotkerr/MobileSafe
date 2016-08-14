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
	 * ��ȡϵͳ��ϵ��
	 * 
	 * @return
	 */
	public static List<HashMap<String, String>> getAllContactInfo(
			Context context) {
		ArrayList<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();
		//1,��ȡ���ݽ���������
		ContentResolver contentResolver = context.getContentResolver();
		//2,����ѯϵͳ��ϵ�����ݿ�����(��ȡ��ϵ��Ȩ��)
		Cursor cursor = contentResolver.query(
				Uri.parse("content://com.android.contacts/raw_contacts"),
				new String[]{"contact_id"},
				null,null, null);
		contactList.clear();
		//3,ѭ���α�,ֱ��û������Ϊֹ
		while(cursor.moveToNext()){
			String id = cursor.getString(0);
			//cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex : 
//			��ѯ�ֶ���cursor������ֵ,һ�㶼�����ڲ�ѯ�ֶαȽ϶��ʱ��
//			Log.i(tag, "id = " + id);
			//4,�����û�Ψһ��idֵ,��ѯdata���mimetype�����ɵ���ͼ,��ȡdata�Լ�mimetype�ֶ�
			if (!TextUtils.isEmpty(id)) {//null   ""
				// 7.����id��ѯview_data���е�����
				// selection : ��ѯ����
				// selectionArgs :��ѯ�����Ĳ���
				// sortOrder : ����
				// ��ָ��: 1.null.���� 2.����Ϊnull
			Cursor indexCursor = contentResolver.query(
					Uri.parse("content://com.android.contacts/data"),
					new String[]{"data1","mimetype"},
					"raw_contact_id = ?",new String[]{id}, null);
			//5,ѭ����ȡÿһ����ϵ�˵ĵ绰�����Լ�����,��������
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
