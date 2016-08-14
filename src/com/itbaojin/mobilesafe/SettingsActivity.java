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
	 * ��������ʾ��λ��
	 */
	private void location() {
		scv_setting_location.setTitle("��������ʾ��λ��");
		scv_setting_location.setDes("���ù�������ʾ�����ʾλ��");
		scv_setting_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��ת������λ�õĽ���
				Intent intent = new Intent(SettingsActivity.this,DragViewActivity.class);
				startActivity(intent);
			}
		});
		
	}

	/**
	 * ���ù�������ʾ����
	 */
	private void changedbg() {
		final String[] items={"��͸��","������","��ʿ��","������","ƻ����"};
		//���ñ����������Ϣ
		scv_setting_changedbg.setTitle("��������ʾ����");
		//���ݱ����ѡ�е�ѡ�������ֵ�����Զ�����Ͽؼ�������Ϣ���Բ���
		scv_setting_changedbg.setDes(items[sp.getInt("which", 0)]);
		//�����Զ�����Ͽؼ��ĵ���¼�
		scv_setting_changedbg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//������ѡ�Ի���
				AlertDialog.Builder builder = new Builder(SettingsActivity.this);
				//����ͼ��
				builder.setIcon(R.drawable.ic_launcher);
				//���ñ���
				builder.setTitle("��������ʾ����");
				//���õ�ѡ��
				//items : ѡ����ı�������
				//checkedItem : ѡ�е�ѡ��
				//listener : ����¼�
				//���õ�ѡ��ѡ��ѡ��Ļ��Բ���
				builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener(){
					//which : ѡ�е�ѡ������ֵ
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Editor edit = sp.edit();
						edit.putInt("which", which);
						edit.commit();
						//1.�����Զ�����Ͽؼ�������Ϣ�ı�
						scv_setting_changedbg.setDes(items[which]);//����ѡ��ѡ������ֵ��items�����л�ȡ����Ӧ�ı�,���ø�������Ϣ�ؼ�
						//2.���ضԻ���
						dialog.dismiss();
					}
				});
				//����ȡ����ť
				builder.setNegativeButton("ȡ��", null);//�������ťֻ����Ҫ�������ضԻ���Ĳ����Ļ�,����2����дnull,��ʾ���ضԻ���
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
		//���Բ���
		//��̬�Ļ�ȡ�����Ƿ���
		if (AddressUtils.isRunningService("com.itbaojin.mobilesafe.service.WatchDogService", getApplicationContext())) {
			//��������
			sv_setting_lock.setCheck(true);
		}else{
			//�رշ���
			sv_setting_lock.setCheck(false);
		}
		sv_setting_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this,WatchDogService.class);
				//����checkbox��״̬����������Ϣ��״̬
				//isChecked() : ֮ǰ��״̬
				if (sv_setting_lock.isChecked()) {
					//�ر���ʾ����
					stopService(intent);
					//����checkbox��״̬
					sv_setting_lock.setCheck(false);
				}else{
					//����ʾ����
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
	 * ����������
	 */
	private void blackNum() {
				//���Բ���
				//��̬�Ļ�ȡ�����Ƿ���
				if (AddressUtils.isRunningService("com.itbaojin.mobilesafe.service.BlackNumService", getApplicationContext())) {
					//��������
					sv_setting_blacknum.setCheck(true);
				}else{
					//�رշ���
					sv_setting_blacknum.setCheck(false);
				}
				sv_setting_blacknum.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(SettingsActivity.this,BlackNumService.class);
						//����checkbox��״̬����������Ϣ��״̬
						//isChecked() : ֮ǰ��״̬
						if (sv_setting_blacknum.isChecked()) {
							//�ر���ʾ����
							stopService(intent);
							//����checkbox��״̬
							sv_setting_blacknum.setCheck(false);
						}else{
							//����ʾ����
							startService(intent);
							sv_setting_blacknum.setCheck(true);
						}
					}
				});
	}

	private void initUpdate() {
		final SettingItemView sv_update = (SettingItemView) findViewById(R.id.sv_update);
		
		//��ȡ���еĿ���״̬��������ʾ
		boolean open_update = SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		//�Ƿ�ѡ�и�����һ�δ洢�Ľ��ȥ������
		sv_update.setCheck(open_update);
		sv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isChecked = sv_update.isChecked();
				sv_update.setCheck(!isChecked);
				//��ȡ����Ľ���洢����Ӧ��sp��
				SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isChecked);
			}
		});
	}
	private void initAddress() {
		final SettingItemView sv_setting_address = (SettingItemView) findViewById(R.id.sv_setting_address);
		//���Բ���
				//��̬�Ļ�ȡ�����Ƿ���
				if (AddressUtils.isRunningService("com.itbaojin.mobilesafe.service.AddressService", getApplicationContext())) {
					//��������
					sv_setting_address.setCheck(true);
				}else{
					//�رշ���
					sv_setting_address.setCheck(false);
				}
				sv_setting_address.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(SettingsActivity.this,AddressService.class);
						//����checkbox��״̬����������Ϣ��״̬
						//isChecked() : ֮ǰ��״̬
						if (sv_setting_address.isChecked()) {
							//�ر���ʾ����
							stopService(intent);
							//����checkbox��״̬
							sv_setting_address.setCheck(false);
						}else{
							//����ʾ����
							startService(intent);
							sv_setting_address.setCheck(true);
						}
					}
				});
	}

}
