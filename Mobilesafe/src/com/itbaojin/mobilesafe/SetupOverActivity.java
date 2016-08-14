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
			//��������ɹ��������ĸ����������������-->ͣ����������ɺ�Ĺ����б����
			setContentView(R.layout.activity_setup_over);
			initUI();
		}else {
			//��������ɹ��������ĸ���������û���������-->��ת����һ����������
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
		//���÷��������Ƿ���״̬
		//��ȡ����ķ�������״̬
		boolean b = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
		if (b) {
			imageView.setImageResource(R.drawable.lock);
		}else {
			imageView.setImageResource(R.drawable.unlock);
		}
		
	}

	/**
	 * @param view���½��������򵼵���¼�
	 */
	public void resetup(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
	}
	

}
