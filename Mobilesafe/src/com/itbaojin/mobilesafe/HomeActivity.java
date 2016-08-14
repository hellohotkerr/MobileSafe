package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.itbaojinmobilesafe.utils.ConstantValue;
import com.itbaojinmobilesafe.utils.Md5Util;
import com.itbaojinmobilesafe.utils.SpUtils;
import com.itbaojinmobilesafe.utils.ToastUtil;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private String[] mTitleStrings;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
		//初始化数据
		initData();
		
	}

	private void initData() {
		mTitleStrings = new String[]{"手机防盗","通信卫士","软件管理","进程管理",
				"流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
		mDrawableIds = new int[]{
				R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
				R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
				R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
		};
		//九宫格控件设置数据适配器（等同于ListView设置数据适配器）
		gv_home.setAdapter(new MyAdapter());
		//注册九宫格单个条目的点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			//点中列表条目的索引position
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					//开启对话框
					showDialog(id);
					break;
				case 1:
					// 通讯卫士
					Intent intent1 = new Intent(HomeActivity.this,CallSmSafeActivity.class);
					startActivity(intent1);
					break;
				case 2:
					// 软件管理
					Intent intent2 = new Intent(HomeActivity.this,SoftManageActivity.class);
					startActivity(intent2);
					break;
				case 3:
					// 进程管理
					Intent intent3 = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent3);
					break;
				case 4:
					// 流量统计
					Intent intent4 = new Intent(HomeActivity.this,TrafficActivity.class);
					startActivity(intent4);
					break;
				case 5:
					// 手机杀毒
					Intent intent5 = new Intent(HomeActivity.this,AntivirusActivity.class);
					startActivity(intent5);
					break;
				case 6:
					// 手机杀毒
					Intent intent6 = new Intent(HomeActivity.this,ClearCacheActivity.class);
					startActivity(intent6);
					break;
				case 7:
					Intent intent7 = new Intent(HomeActivity.this,AToolsActicity.class);
					startActivity(intent7);
					break;
				case 8:
					Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
				
			}
		});
	}

	protected void showDialog(long id) {
		//判断本地是否存储密码（sp 字符串）
		String psd = SpUtils.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
		if (TextUtils.isEmpty(psd)) {
			// 初始设置密码对话框
			showSetPsdDialod();
		}else {
			// 确认密码对话框
			showConfirmPsdDialod();
		}
	}

	

	private void showSetPsdDialod() {
		// 需要自己定义对话框的展示样式
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_set_psd, null);
		dialog.setView(view);
		dialog.show();
		Button bt_submit = (Button)view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button)view.findViewById(R.id.bt_cancel);
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_set_psd =(EditText) view.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
				String psd  = et_set_psd.getText().toString();
				String confirmPsd = et_confirm_psd.getText().toString();
				if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
					if (psd.equals(confirmPsd)) {
						//进入手机防盗模块
						Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
						startActivity(intent);
						//跳转到新的界面以后，需要隐藏对话框
						dialog.dismiss();
						
						SpUtils.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD,
								Md5Util.encode(confirmPsd));
						
					}else {
						ToastUtil.show(getApplicationContext(), "确认密码错误");
					}
					
				}else {
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
				
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
	}
	
	private void showConfirmPsdDialod() {
		// 需要自己定义对话框的展示样式
				Builder builder = new AlertDialog.Builder(this);
				final AlertDialog dialog = builder.create();
				final View view = View.inflate(this, R.layout.dialog_confirm_pad, null);
//				dialog.setView(view);
				dialog.setView(view, 0, 0, 0, 0);
				
				dialog.show();
				Button bt_submit = (Button)view.findViewById(R.id.bt_submit);
				Button bt_cancel = (Button)view.findViewById(R.id.bt_cancel);
				bt_submit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
						String confirmPsd = et_confirm_psd.getText().toString();
						if ( !TextUtils.isEmpty(confirmPsd)) {
							String  psd  = SpUtils.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
							if (psd.equals(Md5Util.encode(confirmPsd))) {
								//进入手机防盗模块
								Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
								startActivity(intent);
								//跳转到新的界面以后，需要隐藏对话框
								dialog.dismiss();
								
							}else {
								ToastUtil.show(getApplicationContext(), "确认密码错误");
							}
							
						}else {
							ToastUtil.show(getApplicationContext(), "请输入密码");
						}
						
					}
				});
				bt_cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	}

	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
	}
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// 条目总数 文字组数 = 图片张数
			
			return mTitleStrings.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrings[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		//Holder
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
			TextView tv_title =  (TextView) view.findViewById(R.id.tv_title);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			tv_title.setText(mTitleStrings[position]);
			iv_icon.setBackgroundResource(mDrawableIds[position]);
			return view;
		}
		
	}

}
