package com.itbaojin.mobilesafe;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itbaojin.mobilesafe.engine.ContactEngine;
import com.itbaojinmobilesafe.utils.MyAsycnTasks;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ContactListActivity extends Activity {
	protected static final String tag = "ContactListActivity";
	private ListView lv_contact;
	private MyAdapter mAdapter;
	//ע���ʼ���ؼ�,����Spring,ע�����ʽ����javabean,�ڲ�:ͨ������ķ�ʽִ����findviewById
	@ViewInject(R.id.loading)
	private ProgressBar loading;
	private List<HashMap<String, String>> contactList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);
		initUI();
		initDate();
	}
	

	private void initDate() {
		ViewUtils.inject(this);
		new MyAsycnTasks() {
			
			@Override
			public void preTask() {
				//��������֮ǰ����ʾ������
				loading.setVisibility(View.VISIBLE);
				
			}
			
			@Override
			public void postTask() {
				mAdapter = new MyAdapter();
				lv_contact.setAdapter(mAdapter);
				loading.setVisibility(View.INVISIBLE);
				
			}
			
			@Override
			public void doinBack() {
				//��ȡ��ϵ��
				contactList = ContactEngine.getAllContactInfo(getApplicationContext());
			}
		}.execute();
		
		
	}
	/*//����:�����չ��
	//����1:���߳�ִ������Ĳ���
	//����2:ִ�еĽ���
	//����3:���߳�ִ�еĽ��
	//�첽���ؿ��:���Ա���,��д�첽���ؿ��,�ٶ�������:����ִ�е����ٸ�������ʱ��ͺ�newһ���߳�û������,5��
	new AsyncTask<String, Integer, String>(){
		
		//���߳�֮ǰִ�еķ���
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		//�����߳�֮��ִ�еķ���
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}
		//�����߳�֮��ִ�еķ���
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		//��ʾ��ǰ���صĽ���
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
	};*/

	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//1,��ȡ������Ŀ������ָ�򼯺��еĶ���
				if (mAdapter != null) {
					HashMap<String, String> hashMap = mAdapter.getItem(position);
					//2,��ȡ��ǰ��Ŀָ�򼯺϶�Ӧ�ĵ绰����
					String phone = hashMap.get("phone");
					//3,�˵绰������Ҫ����������������ʹ��
					
					//4,�ڽ����˽���ص�ǰһ�����������ʱ��,��Ҫ�����ݷ��ع�ȥ
					Intent intent = new Intent();
					intent.putExtra("phone", phone);
					setResult(0, intent);
					
					finish();
				}
			}
		});
	}
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return contactList.size();
		}

		@Override
		public HashMap<String,String> getItem(int position) {
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("phone"));
			return view;
		}
		
	}
}
