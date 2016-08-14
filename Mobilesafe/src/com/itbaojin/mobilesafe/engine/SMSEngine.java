package com.itbaojin.mobilesafe.engine;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Xml;

public class SMSEngine {
	//1.����ˢ��
		public interface ShowProgress{
			//����������
			public void setMax(int max);
			//���õ�ǰ����
			public void setProgress(int progerss);
		}
		
		//2.ϱ��������ˢ��
		/**
		 * ��ȡ����
		 */
		public static void getAllSMS(Context context,ShowProgress showProgress){
			//1.��ȡ����
			//1.1��ȡ���ݽ�����
			ContentResolver resolver = context.getContentResolver();
			//1.2��ȡ�����ṩ�ߵ�ַ   sms,sms��ĵ�ַ:null  ��д
			//1.3��ȡ��ѯ·��
			Uri uri = Uri.parse("content://sms");
			//1.4.��ѯ����
			//projection : ��ѯ���ֶ�
			//selection : ��ѯ������
			//selectionArgs : ��ѯ�����Ĳ���
			//sortOrder : ����
			Cursor cursor = resolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
			//����������
			int count = cursor.getCount();//��ȡ���ŵĸ���
			showProgress.setMax(count);
			
			//���õ�ǰ����
			int progress = 0;
			
			//2.���ݶ���
			//2.1��ȡxml������
			XmlSerializer xmlSerializer = Xml.newSerializer();
			try {
				//2.2����xml�ļ������·��
				//os : �����λ��
				//encoding : �����ʽ
				xmlSerializer.setOutput(new FileOutputStream(new File("/mnt/sdcard/backupsms.xml")), "utf-8");
				//2.3����ͷ��Ϣ
				//standalone : �Ƿ��������
				xmlSerializer.startDocument("utf-8", true);
				//2.4���ø���ǩ
				xmlSerializer.startTag(null, "smss");
				//1.5.����cursor
				while(cursor.moveToNext()){
					SystemClock.sleep(1000);
					//2.5���ö��ŵı�ǩ
					xmlSerializer.startTag(null, "sms");
					//2.6�����ı����ݵı�ǩ
					xmlSerializer.startTag(null, "address");
					String address = cursor.getString(0);
					//2.7�����ı�����
					xmlSerializer.text(address);
					xmlSerializer.endTag(null, "address");
					
					xmlSerializer.startTag(null, "date");
					String date = cursor.getString(1);
					xmlSerializer.text(date);
					xmlSerializer.endTag(null, "date");
					
					xmlSerializer.startTag(null, "type");
					String type = cursor.getString(2);
					xmlSerializer.text(type);
					xmlSerializer.endTag(null, "type");
					
					xmlSerializer.startTag(null, "body");
					String body = cursor.getString(3);
					xmlSerializer.text(body);
					xmlSerializer.endTag(null, "body");
					
					xmlSerializer.endTag(null, "sms");
					System.out.println("address:"+address+"   date:"+date+"  type:"+type+"  body:"+body);
					
					//���õ�ǰ����
					progress++;
					showProgress.setProgress(progress);
					
				}
				xmlSerializer.endTag(null, "smss");
				xmlSerializer.endDocument();
				//2.8������ˢ�µ��ļ���
				xmlSerializer.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
}
