package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WatchDogActivity extends Activity {
	private ImageView iv_watchdog_icon;
	private TextView tv_watchdog_name;
	private String packageName;
	private EditText ed_watchdog_password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchdog);
		iv_watchdog_icon = (ImageView) findViewById(R.id.iv_watchdog_icon);
		tv_watchdog_name = (TextView) findViewById(R.id.tv_watchdog_name);
		ed_watchdog_password = (EditText) findViewById(R.id.ed_watchdog_password);
		//��������
		Intent intent = getIntent();
		packageName = intent.getStringExtra("packageName");
		//������ʾ������Ӧ�ó����ͼƬ������
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
			Drawable icon = applicationInfo.loadIcon(pm);
			String name = applicationInfo.loadLabel(pm).toString();
			iv_watchdog_icon.setImageDrawable(icon);
			tv_watchdog_name.setText(name);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/**
			 * Starting: Intent { 
			   act=android.intent.action.MAIN 
			   cat=[android.intent.category.HOME
			   ] cmp=com.android.launcher/com.android.launcher2.Launcher } from pid 208
			 */
			//��ת��������
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	public void lock(View v){
		//����
		//1.��ȡ���������
		String password = ed_watchdog_password.getText().toString().trim();
		//2.�ж������Ƿ�������ȷ
		if ("123".equals(password)) {
			//����
			//һ��ͨ���㲥����ʽ����Ϣ���͸�����
			Intent intent = new Intent();
			intent.setAction("com.itbaojin.mobliesafe.unlock");//�Զ��巢�͹㲥�¼�
			intent.putExtra("packageName", packageName);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(getApplicationContext(), "�������", 0).show();
		}
	}
}
