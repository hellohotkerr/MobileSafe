package com.itbaojin.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itbaojin.mobilesafe.db.WatchDogOpenHelper;

public class WatchDogDao {
	private WatchDogOpenHelper watchDogOpenHelper;
	private byte[] b = new byte[1024];
	private Context context;
	//�ڹ��캯���л�ȡBlackNumOpenHlper
	public WatchDogDao(Context context){
		watchDogOpenHelper = new WatchDogOpenHelper(context);
		this.context = context;
	}
	//��ɾ�Ĳ�
	//����:����ͬһʱ�̶����ݿ�Ƚ��ж�����Ҳ����д����,��ô���������������ͬʱ�������ݿ�,ͬ����+��WatchDogOpenHelper���óɵ���ģʽ
	/**
	 * ���Ӧ�ó������
	 * @param blacknum
	 * @param mode
	 */
	public void addLockApp(String packageName){
//		synchronized (b) {
			//1.��ȡ���ݿ�
			SQLiteDatabase database = watchDogOpenHelper.getWritableDatabase();
			//2.��Ӳ���
			//ContentValues :����ӵ�����
			ContentValues values = new ContentValues();
			values.put("packagename", packageName);
			database.insert(WatchDogOpenHelper.DB_NAME, null, values);
			//֪ͨ���ݹ۲������ݿ�仯��
			ContentResolver contentResolver = context.getContentResolver();
			//��Ϊ�������Լ������ݷ����仯��,��������Ҫ�Զ���һ��uri���в���
			Uri uri = Uri.parse("content://com.itbaojin.mobliesafe.lock.changed");
			//֪ͨ���ݹ۲������ݷ����仯��
			contentResolver.notifyChange(uri, null);
			//3.�ر����ݿ�
			database.close();
//		}
	}
	/**
	 * ��ѯ���ݿ��Ƿ��а���,��return true  ,û��return false
	 */
	public boolean queryLockApp(String packagName){
		boolean isLock = false;
		//1.��ȡ���ݿ�
		SQLiteDatabase database = watchDogOpenHelper.getReadableDatabase();
		//2.��ѯ���ݿ�
		//table : ����
		//columns : ��ѯ���ֶ�  mode
		//selection : ��ѯ����  where xxxx = xxxx
		//selectionArgs : ��ѯ�����Ĳ���
		//groupBy : ����
		//having : ȥ��
		//orderBy : ����
		Cursor cursor = database.query(WatchDogOpenHelper.DB_NAME, null, "packagename=?", new String[]{packagName}, null, null, null);
		//3.����cursor
		if (cursor.moveToNext()) {
			//��ȡ��ѯ����������
			isLock = true;
		}
		//4.�ر����ݿ�
		cursor.close();
		database.close();
		return isLock;
	}
	/**
	 * ɾ������
	 * @param blacknum
	 */
	public void deleteLockApp(String packagename){
		//1.��ȡ���ݿ�
		SQLiteDatabase database = watchDogOpenHelper.getWritableDatabase();
		//2.ɾ��
		//table : ����
		//whereClause : ��ѯ������
		//whereArgs : ��ѯ�����Ĳ���
		database.delete(WatchDogOpenHelper.DB_NAME, "packagename=?", new String[]{packagename});
		

		//֪ͨ���ݹ۲������ݿ�仯��
		ContentResolver contentResolver = context.getContentResolver();
		//��Ϊ�������Լ������ݷ����仯��,��������Ҫ�Զ���һ��uri���в���
		Uri uri = Uri.parse("content://com.itbaojin.mobliesafe.lock.changed");
		//֪ͨ���ݹ۲������ݷ����仯��
		contentResolver.notifyChange(uri, null);
		//3.�ر����ݿ�
		database.close();
	}
	/**
	 * ��ѯȫ������
	 */
	public List<String> queryAllLockApp(){
		List<String> list = new ArrayList<String>();
		//1.��ȡ���ݿ�
		SQLiteDatabase database = watchDogOpenHelper.getReadableDatabase();
		//2.��ѯ����
		Cursor cursor = database.query(WatchDogOpenHelper.DB_NAME, new String[]{"packagename"}, null, null, null, null, null);//desc�����ѯ,asc:�����ѯ,Ĭ�������ѯ
		//3.����cursor
		while(cursor.moveToNext()){
			//��ȡ��ѯ����������]
			String packagename = cursor.getString(0);
			list.add(packagename);
		}
		//4.�ر����ݿ�
		cursor.close();
		database.close();
		return list;
	}
	
}
