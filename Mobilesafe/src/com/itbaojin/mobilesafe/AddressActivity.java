package com.itbaojin.mobilesafe;



import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itbaojin.mobilesafe.db.dao.AddressDao;

public class AddressActivity extends Activity {
	private EditText et_address_queryphone;
	private TextView tv_address_queryaddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		et_address_queryphone = (EditText) findViewById(R.id.et_address_queryphone);
		tv_address_queryaddress = (TextView) findViewById(R.id.tv_address_queryaddress);
		//����������ı��仯
		et_address_queryphone.addTextChangedListener(new TextWatcher() {
			//���ı��仯��ɵĵ�ʱ�����
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//1.��ȡ��������������
				String phone = s.toString();
				//2.���ݺ����ѯ���������
				String queryAddress = AddressDao.queryAddress(phone, getApplicationContext());
				//3.�жϲ�ѯ�ĺ���������Ƿ�Ϊ��
				if (!TextUtils.isEmpty(queryAddress)) {
					//����ѯ�ĺ�����������ø�textveiw��ʾ
					tv_address_queryaddress.setText(queryAddress);
					
				}
			}
			//���ı��仯֮ǰ����
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			//�ı��仯֮�����
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * ��ѯ��������ز���
	 * @param v
	 */
	public void query(View v){
		//1.��ȡ����ĺ���
		String phone = et_address_queryphone.getText().toString().trim();
		//2.�жϺ����Ƿ�Ϊ��
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(getApplicationContext(), "������Ҫ��ѯ����", 0).show();
			//ʵ�ֶ�����Ч��
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			/*shake.setInterpolator(new Interpolator() {
				
				@Override
				public float getInterpolation(float x) {
					return 0;//����x��ֵ��ȡy��ֵ  y=x*x  y=x-k
				}
			});*/
			et_address_queryphone.startAnimation(shake);//��������
			//��
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(100);//��100����,0.1��
			
		}else {
			//3.���ݺ����ѯ���������
			String queryAddress = AddressDao.queryAddress(phone, getApplicationContext());
			//4.�жϲ�ѯ�ĺ���������Ƿ�Ϊ��
			if (!TextUtils.isEmpty(queryAddress)) {
				tv_address_queryaddress.setText(queryAddress);
			}
		}
		
	}
}
