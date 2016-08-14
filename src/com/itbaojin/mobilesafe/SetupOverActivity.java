package com.itbaojin.mobilesafe;

import com.itbaojinmobilesafe.utils.ConstantValue;
import com.itbaojinmobilesafe.utils.SpUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupOverActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if (setup_over) {
			//密码输入成功，并且四个导航界面设置完成-->停留在设置完成后的功能列表界面
			setContentView(R.layout.activity_setup_over);
			initUI();
		}else {
			//密码输入成功，并且四个导航界面没有设置完成-->跳转到第一个导航界面
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			
			finish();
		}
	}
	
	private void initUI() {
		TextView tv_phone =(TextView) findViewById(R.id.tv_phone);
		ImageView imageView = (ImageView) findViewById(R.id.iv_lock);
		String phone = SpUtils.getString(this, ConstantValue.CONTACT_PHONE, "");
		tv_phone.setText(phone);
		//设置防盗保护是否开启状态
		//获取保存的防盗保护状态
		boolean b = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
		if (b) {
			imageView.setImageResource(R.drawable.lock);
		}else {
			imageView.setImageResource(R.drawable.unlock);
		}
		
	}

	/**
	 * @param view重新进入设置向导点击事件
	 */
	public void resetup(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
	}
	

}
