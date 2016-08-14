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
		//接收数据
		Intent intent = getIntent();
		packageName = intent.getStringExtra("packageName");
		//设置显示加锁的应用程序的图片和名称
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
			//跳转到主界面
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	public void lock(View v){
		//解锁
		//1.获取输入的密码
		String password = ed_watchdog_password.getText().toString().trim();
		//2.判断密码是否输入正确
		if ("123".equals(password)) {
			//解锁
			//一般通过广播的形式将信息发送给服务
			Intent intent = new Intent();
			intent.setAction("com.itbaojin.mobliesafe.unlock");//自定义发送广播事件
			intent.putExtra("packageName", packageName);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(getApplicationContext(), "密码错误", 0).show();
		}
	}
}
