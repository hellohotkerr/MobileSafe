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
	//1.创建刷子
		public interface ShowProgress{
			//设置最大进度
			public void setMax(int max);
			//设置当前进度
			public void setProgress(int progerss);
		}
		
		//2.媳妇儿给你刷子
		/**
		 * 获取短信
		 */
		public static void getAllSMS(Context context,ShowProgress showProgress){
			//1.获取短信
			//1.1获取内容解析者
			ContentResolver resolver = context.getContentResolver();
			//1.2获取内容提供者地址   sms,sms表的地址:null  不写
			//1.3获取查询路径
			Uri uri = Uri.parse("content://sms");
			//1.4.查询操作
			//projection : 查询的字段
			//selection : 查询的条件
			//selectionArgs : 查询条件的参数
			//sortOrder : 排序
			Cursor cursor = resolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
			//设置最大进度
			int count = cursor.getCount();//获取短信的个数
			showProgress.setMax(count);
			
			//设置当前进度
			int progress = 0;
			
			//2.备份短信
			//2.1获取xml序列器
			XmlSerializer xmlSerializer = Xml.newSerializer();
			try {
				//2.2设置xml文件保存的路径
				//os : 保存的位置
				//encoding : 编码格式
				xmlSerializer.setOutput(new FileOutputStream(new File("/mnt/sdcard/backupsms.xml")), "utf-8");
				//2.3设置头信息
				//standalone : 是否独立保存
				xmlSerializer.startDocument("utf-8", true);
				//2.4设置根标签
				xmlSerializer.startTag(null, "smss");
				//1.5.解析cursor
				while(cursor.moveToNext()){
					SystemClock.sleep(1000);
					//2.5设置短信的标签
					xmlSerializer.startTag(null, "sms");
					//2.6设置文本内容的标签
					xmlSerializer.startTag(null, "address");
					String address = cursor.getString(0);
					//2.7设置文本内容
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
					
					//设置当前进度
					progress++;
					showProgress.setProgress(progress);
					
				}
				xmlSerializer.endTag(null, "smss");
				xmlSerializer.endDocument();
				//2.8将数据刷新到文件中
				xmlSerializer.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
}
