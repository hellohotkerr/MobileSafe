package com.itbaojin.mobilesafe;

import com.itbaojinmobilesafe.utils.ConstantValue;
import com.itbaojinmobilesafe.utils.SpUtils;
import com.itbaojinmobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone_number;
	private Button bt_selcet_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		initUI();
	}

	private void initUI() {
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		//��ȡ��ϵ�˵绰������Թ���
		String phone = SpUtils.getString(this, ConstantValue.CONTACT_PHONE, "");
		et_phone_number.setText(phone);
		bt_selcet_number = (Button) findViewById(R.id.bt_selcet_number);
		bt_selcet_number.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(getApplicationContext(),ContactListActivity.class);
//				startActivityForResult(intent, 0);
				Intent intent = new Intent();
				intent.setAction("android.intent.action.PICK");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setType("vnd.android.cursor.dir/phone_v2");
				startActivityForResult(intent, 1);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (data != null) {
//			//1,���ص���ǰ�����ʱ��,���ܽ���ķ���
//			String phone = data.getStringExtra("phone");
//			//2,�������ַ�����(�л���ת���ɿ��ַ���)
//			phone = phone.replace("-", "").replace(" ","").trim();
//			et_phone_number.setText(phone);
//			//3,�洢��ϵ����sp��
//			SpUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
//		}
		if(data !=null){
			//	String num = data.getStringExtra("num");
			Uri uri = data.getData();
			String num = null;
			// �������ݽ�����
					ContentResolver contentResolver = getContentResolver();
					Cursor cursor = contentResolver.query(uri,
							null, null, null, null);
					while(cursor.moveToNext()){
						num = cursor.getString(cursor.getColumnIndex("data1"));
						
					}
					cursor.close();
					num = num.replaceAll("-", "");//�滻�Ĳ���,555-6 -> 5556
					et_phone_number.setText(num);	
					SpUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE,num);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	protected void showNextPage() {
		//�����ť�Ժ�,��Ҫ��ȡ������е���ϵ��,������һҳ����
				String phone = et_phone_number.getText().toString();
				
				//��sp�洢�������ϵ���Ժ�ſ�����ת����һ������
//				String contact_phone = SpUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
				if (!TextUtils.isEmpty(phone)) {
					Intent intent = new Intent(getApplicationContext(),Setup4Activity.class);
					startActivity(intent);
					finish();
					//�������������绰����,����Ҫȥ����
					SpUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
					overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
				}else {
					ToastUtil.show(getApplicationContext(), "������绰����");
				}
		
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
		
	}

}
