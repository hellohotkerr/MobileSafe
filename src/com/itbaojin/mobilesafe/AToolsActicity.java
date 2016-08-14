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
		//��ת����ѯ���������ҳ��
		Intent intent = new Intent(this,AddressActivity.class);
		startActivity(intent);
	}
	/**
	 * ���ݶ���
	 * @param v
	 */
	public void backupsms(View v){
		//progressdialog�ǿ��������߳��и���
		progressDialog = new ProgressDialog(this);
//		progressDialog.setCancelable(false);//����ȡ��
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		/*progressDialog.setMax(max);//����������
//		progressDialog.setProgress(value);//���õ�ǰ����*/
		progressDialog.show();
		//��ȡ����
		//���ݶ���
		new Thread(){
			public void run() {
				//3.���ǽ���ˢ��,����ˢ��,ˢЬ,ˢ��Ͱ.....
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
		
		//�ص�����,�Ϳ��Խ�Ҫ���Ĳ����ŵ����������ִ��,ҵ���ṩһ������,�����������֪��Ҫ��ô��,������������������������,����Ĳ��������Ǿ���
	}
	/**
	 * ���Ż�ԭ
	 * @param v
	 */
	public void restoresms(View v){
		//����xml
		//XmlPullParser xmlPullParser = Xml.newPullParser();
		//�������
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
