package com.itbaojin.mobilesafe.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Administrator
 * �����ݿ�,��ѯ���������
 *
 */
public class AddressDao {
	public static String queryAddress(String num,Context context){
		String location = "";
		//1.��ȡ���ݿ��·��
		File file = new File(context.getFilesDir(), "address.db");
		//2.�����ݿ�
		//getAbsolutePath : ��ȡ�ļ��ľ���·��
		//factory : �α깤��
		SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		//3.��ѯ���������
		//sql : sql���
		//selectionArgs :��ѯ�����Ĳ���
		//substring : ����ͷ������β
		if (num.matches("^1[34578]\\d{9}$")) {
			Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{num.substring(0, 7)});
			//4.����cursor
			//��Ϊÿ�������Ӧһ�����������,���Բ�ѯ��������һ�����������,û�б�Ҫ��while,��if�Ϳ�����
			if (cursor.moveToNext()) {
				location = cursor.getString(0);
			}
			//5.�ر����ݿ�
			cursor.close();
		}else {
			//������绰������
			switch (num.length()) {
			case 3://110  120  119  911
				location = "����绰";
				break;
			case 4://5554   5556
				location = "����绰";
				break;
			case 5://10086  10010  10000
				location ="�ͷ��绰";
				break;
			case 7://����,���ص绰
			case 8:
				location="���ص绰";
				break;

			default:// 010 1234567   10λ  	010 12345678  11λ     0372  12345678  12λ
				//��;�绰
				if (num.length() >= 10  &&  num.startsWith("0")) {
					//�������Ų�ѯ�������
					//1.��ȡ���������
					//3λ,4λ
					//3λ
					String result = num.substring(1, 3);//010   10
					//2.�������Ų�ѯ���������
					Cursor cursor = database.rawQuery("select location from data2 where area=?", new String[]{result});
					//3.����cursor
					if (cursor.moveToNext()) {
						location = cursor.getString(0);
						//��ȡ����
						location = location.substring(0, location.length()-2);
						cursor.close();
					}else{
						//3λû�в�ѯ��,ֱ�Ӳ�ѯ4λ
						//��ȡ4λ������
						result = num.substring(1, 4);//0372    372
						cursor = database.rawQuery("select location from data2 where area=?", new String[]{result});
						if (cursor.moveToNext()) {
							location = cursor.getString(0);
							location = location.substring(0, location.length()-2);
							cursor.close();
						}
					}
				}
				break;
			}
		}
		
		database.close();
		return location;
	}
}
