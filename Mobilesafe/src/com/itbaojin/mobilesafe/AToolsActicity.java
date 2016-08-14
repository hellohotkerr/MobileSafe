package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.itbaojin.mobilesafe.engine.SMSEngine;
import com.itbaojin.mobilesafe.engine.SMSEngine.ShowProgress;

public class AToolsActicity extends Activity {
	private ProgressDialog progressDialog;
	private ProgressBar pb_atools_sms;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		pb_atools_sms = (ProgressBar) findViewById(R.id.pb_atools_sms);
	}
	public void queryAddress(View view){
		//跳转到查询号码归属地页面
		Intent intent = new Intent(this,AddressActivity.class);
		startActivity(intent);
	}
	/**
	 * 备份短信
	 * @param v
	 */
	public void backupsms(View v){
		//progressdialog是可以在子线程中更新
		progressDialog = new ProgressDialog(this);
//		progressDialog.setCancelable(false);//不能取消
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		/*progressDialog.setMax(max);//设置最大进度
//		progressDialog.setProgress(value);//设置当前进度*/
		progressDialog.show();
		//读取短信
		//备份短信
		new Thread(){
			public void run() {
				//3.我们接受刷子,进行刷牙,刷鞋,刷马桶.....
				SMSEngine.getAllSMS(getApplicationContext(),new ShowProgress() {
					
					@Override
					public void setProgress(int progerss) {
						progressDialog.setProgress(progerss);
//						pb_atools_sms.setProgress(progerss);
					}
					
					@Override
					public void setMax(int max) {
						progressDialog.setMax(max);
//						pb_atools_sms.setMax(max);
					}
				});	
				progressDialog.dismiss();
			};
		}.start();
		
		//回调函数,就可以将要做的操作放到我们这边来执行,业务提供一个操作,但是这个他不知道要怎么做,他会把这个操作交给我们来做,具体的操作由我们决定
	}
	/**
	 * 短信还原
	 * @param v
	 */
	public void restoresms(View v){
		//解析xml
		//XmlPullParser xmlPullParser = Xml.newPullParser();
		//插入短信
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://sms");
		ContentValues values = new ContentValues();
		values.put("address", "95588");
		values.put("date", System.currentTimeMillis());
		values.put("type", 1);
		values.put("body", "zhuan zhang le $10000000000000000000");
		resolver.insert(uri, values);
	}
}
