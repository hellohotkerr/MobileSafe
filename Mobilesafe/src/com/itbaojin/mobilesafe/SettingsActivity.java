package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itbaojin.mobilesafe.service.AddressService;
import com.itbaojin.mobilesafe.service.BlackNumService;
import com.itbaojin.mobilesafe.service.WatchDogService;
import com.itbaojin.mobilesafe.view.SettingClickView;
import com.itbaojin.mobilesafe.view.SettingItemView;
import com.itbaojinmobilesafe.utils.AddressUtils;
import com.itbaojinmobilesafe.utils.ConstantValue;
import com.itbaojinmobilesafe.utils.SpUtils;

public class SettingsActivity extends Activity {
	private SettingClickView scv_setting_changedbg;
	private SharedPreferences sp;
	private SettingClickView scv_setting_location;
	private SettingItemView sv_setting_blacknum;
	private SettingItemView sv_setting_lock;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		scv_setting_changedbg = (SettingClickView) findViewById(R.id.scv_setting_changedbg);
		scv_setting_location = (SettingClickView) findViewById(R.id.scv_setting_location);
		sv_setting_blacknum = (SettingItemView) findViewById(R.id.sv_setting_blacknum);
		sv_setting_lock = (SettingItemView) findViewById(R.id.sv_setting_lock);
		
		initUpdate();
		changedbg();
		location();
		
	}

	/**
	 * 归属地提示框位置
	 */
	private void location() {
		scv_setting_location.setTitle("归属地提示框位置");
		scv_setting_location.setDes("设置归属地提示框的显示位置");
		scv_setting_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到设置位置的界面
				Intent intent = new Intent(SettingsActivity.this,DragViewActivity.class);
				startActivity(intent);
			}
		});
		
	}

	/**
	 * 设置归属地提示框风格
	 */
	private void changedbg() {
		final String[] items={"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		//设置标题和描述信息
		scv_setting_changedbg.setTitle("归属地提示框风格");
		//根据保存的选中的选项的索引值设置自定义组合控件描述信息回显操作
		scv_setting_changedbg.setDes(items[sp.getInt("which", 0)]);
		//设置自定义组合控件的点击事件
		scv_setting_changedbg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//弹出单选对话框
				AlertDialog.Builder builder = new Builder(SettingsActivity.this);
				//设置图标
				builder.setIcon(R.drawable.ic_launcher);
				//设置标题
				builder.setTitle("归属地提示框风格");
				//设置单选框
				//items : 选项的文本的数组
				//checkedItem : 选中的选项
				//listener : 点击事件
				//设置单选框选中选项的回显操作
				builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener(){
					//which : 选中的选项索引值
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Editor edit = sp.edit();
						edit.putInt("which", which);
						edit.commit();
						//1.设置自定义组合控件描述信息文本
						scv_setting_changedbg.setDes(items[which]);//根据选中选项索引值从items数组中获取出相应文本,设置给描述信息控件
						//2.隐藏对话框
						dialog.dismiss();
					}
				});
				//设置取消按钮
				builder.setNegativeButton("取消", null);//当点击按钮只是需要进行隐藏对话框的操作的话,参数2可以写null,表示隐藏对话框
				builder.show();
			}
		});
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initAddress();
		blackNum();
		lock();
	}
	private void lock() {
		//回显操作
		//动态的获取服务是否开启
		if (AddressUtils.isRunningService("com.itbaojin.mobilesafe.service.WatchDogService", getApplicationContext())) {
			//开启服务
			sv_setting_lock.setCheck(true);
		}else{
			//关闭服务
			sv_setting_lock.setCheck(false);
		}
		sv_setting_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this,WatchDogService.class);
				//根据checkbox的状态设置描述信息的状态
				//isChecked() : 之前的状态
				if (sv_setting_lock.isChecked()) {
					//关闭提示更新
					stopService(intent);
					//更新checkbox的状态
					sv_setting_lock.setCheck(false);
				}else{
					//打开提示更新
					startService(intent);
					sv_setting_lock.setCheck(true);
				}
			}
		});
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/**
	 * 黑名单拦截
	 */
	private void blackNum() {
				//回显操作
				//动态的获取服务是否开启
				if (AddressUtils.isRunningService("com.itbaojin.mobilesafe.service.BlackNumService", getApplicationContext())) {
					//开启服务
					sv_setting_blacknum.setCheck(true);
				}else{
					//关闭服务
					sv_setting_blacknum.setCheck(false);
				}
				sv_setting_blacknum.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(SettingsActivity.this,BlackNumService.class);
						//根据checkbox的状态设置描述信息的状态
						//isChecked() : 之前的状态
						if (sv_setting_blacknum.isChecked()) {
							//关闭提示更新
							stopService(intent);
							//更新checkbox的状态
							sv_setting_blacknum.setCheck(false);
						}else{
							//打开提示更新
							startService(intent);
							sv_setting_blacknum.setCheck(true);
						}
					}
				});
	}

	private void initUpdate() {
		final SettingItemView sv_update = (SettingItemView) findViewById(R.id.sv_update);
		
		//获取已有的开关状态，用作显示
		boolean open_update = SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		//是否选中根据上一次存储的结果去做决定
		sv_update.setCheck(open_update);
		sv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isChecked = sv_update.isChecked();
				sv_update.setCheck(!isChecked);
				//将取反后的结果存储到相应的sp中
				SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isChecked);
			}
		});
	}
	private void initAddress() {
		final SettingItemView sv_setting_address = (SettingItemView) findViewById(R.id.sv_setting_address);
		//回显操作
				//动态的获取服务是否开启
				if (AddressUtils.isRunningService("com.itbaojin.mobilesafe.service.AddressService", getApplicationContext())) {
					//开启服务
					sv_setting_address.setCheck(true);
				}else{
					//关闭服务
					sv_setting_address.setCheck(false);
				}
				sv_setting_address.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(SettingsActivity.this,AddressService.class);
						//根据checkbox的状态设置描述信息的状态
						//isChecked() : 之前的状态
						if (sv_setting_address.isChecked()) {
							//关闭提示更新
							stopService(intent);
							//更新checkbox的状态
							sv_setting_address.setCheck(false);
						}else{
							//打开提示更新
							startService(intent);
							sv_setting_address.setCheck(true);
						}
					}
				});
	}

}
