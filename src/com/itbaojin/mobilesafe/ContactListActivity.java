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
	//注解初始化控件,类似Spring,注解的形式生成javabean,内部:通过反射的方式执行了findviewById
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
				//加载数据之前，显示进度条
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
				//获取联系人
				contactList = ContactEngine.getAllContactInfo(getApplicationContext());
			}
		}.execute();
		
		
	}
	/*//参数:提高扩展性
	//参数1:子线程执行所需的参数
	//参数2:执行的进度
	//参数3:子线程执行的结果
	//异步加载框架:面试必问,手写异步加载框架,百度面试题:当他执行到多少个操作的时候就和new一个线程没区别了,5个
	new AsyncTask<String, Integer, String>(){
		
		//子线程之前执行的方法
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		//在子线程之中执行的方法
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}
		//在子线程之后执行的方法
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		//显示当前加载的进度
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
				//1,获取点中条目的索引指向集合中的对象
				if (mAdapter != null) {
					HashMap<String, String> hashMap = mAdapter.getItem(position);
					//2,获取当前条目指向集合对应的电话号码
					String phone = hashMap.get("phone");
					//3,此电话号码需要给第三个导航界面使用
					
					//4,在结束此界面回到前一个导航界面的时候,需要将数据返回过去
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
