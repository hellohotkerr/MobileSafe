package com.itbaojin.mobilesafe.service;

import java.util.List;
import java.util.Timer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;

import com.itbaojin.mobilesafe.WatchDogActivity;
import com.itbaojin.mobilesafe.db.dao.WatchDogDao;

public class WatchDogService extends Service {

	private WatchDogDao watchDogDao;
	private boolean isLock;
	private UnlockCurrentReceiver unlockCurrentReceiver;
	private String unlockcurrentPackagname;
	private ScreenOffReceiver screenOffReceiver;
	private List<String> list;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	private class UnlockCurrentReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//��������
			unlockcurrentPackagname = intent.getStringExtra("packageName");
		}
	}
	private class  ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//�����Ĳ���
			unlockcurrentPackagname = null;
		}
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		watchDogDao = new WatchDogDao(getApplicationContext());
		//ע������Ĺ㲥������
				//1.�㲥������
				unlockCurrentReceiver = new UnlockCurrentReceiver();
				//2.���ý��ܵĹ㲥�¼�
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("com.itbaojin.mobliesafe.unlock");
				//3.ע��㲥������
				registerReceiver(unlockCurrentReceiver, intentFilter);
				//ע�������Ĺ㲥������
				screenOffReceiver = new ScreenOffReceiver();
				IntentFilter screenOffIntentFilter = new IntentFilter();
				screenOffIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
				registerReceiver(screenOffReceiver, screenOffIntentFilter);
		
		//ʱʱ�̼̿����û��򿪵ĳ���
		//activity���Ǵ��������ջ�е�,һ��Ӧ��ֻ��һ������ջ
		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		isLock = true;
		new Thread(){

			public void run() {
				
				//�Ƚ����ݿ��е�����,��ѯ��ŵ��ڴ�,Ȼ���ٰ����ݴ��ڴ��л�ȡ�������в���
				
				//�����ݿ�仯��ʱ�����¸����ڴ��е�����,�����ݿ�仯��ʱ��֪ͨ���ݹ۲������ݿ�仯��,Ȼ�������ݹ۲�����ȥ�������µ�����
				Uri uri = Uri.parse("content://com.itbaojin.mobliesafe.lock.changed");
				//notifyForDescendents:ƥ�����,true:��ȷƥ��  false:ģ��ƥ��
				getContentResolver().registerContentObserver(uri, true, new ContentObserver(null) {
					public void onChange(boolean selfChange) {
						//��������
						list = watchDogDao.queryAllLockApp();
					};
				});
				list = watchDogDao.queryAllLockApp();
				while(isLock){
					//��������
					//�����û�������Щ����ջ,����ЩӦ��
					//��ȡ�������е�����ջ,�������ջ����,�ͱ�ʾӦ�ô򿪹�
					//maxNum : ��ȡ�������е�����ջ�ĸ���
					//���ڴ򿪵�Ӧ�õ�����ջ,��Զ�ڵ�һ��,��֮ǰ(���home��С��,û���˳�)��Ӧ�õ�����ջ������������
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
//					System.out.println("----------------------");
					for (RunningTaskInfo runningTaskInfo : runningTasks) {
						//��ȡ����ջ,ջ�׵�activity
						ComponentName baseactivity = runningTaskInfo.baseActivity;
						/*//��ȡ����ջջ����activity
						runningTaskInfo.topActivity;*/
						String packageName = baseactivity.getPackageName();
//						System.out.println(packageName);
						//�ж�list�������Ƿ��������
						boolean b = list.contains(packageName);
						//ͨ����ѯ���ݿ�,������ݿ�����,������������,û�в�������
						if (b) {
							if (!packageName.equals(unlockcurrentPackagname)) {
								//������������
								Intent intent = new Intent(WatchDogService.this,WatchDogActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra("packageName", packageName);
								startActivity(intent);
							}
						}
					}
//					System.out.println("----------------------");
					SystemClock.sleep(300);
				}
			};
		}.start();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		//������رյĽ�ֹ�����û��򿪵�Ӧ�ó���
		isLock = false;
		//ע�������Ĺ㲥������
		if (unlockCurrentReceiver != null) {
			unregisterReceiver(unlockCurrentReceiver);
			unlockCurrentReceiver = null;
		}
		//ע�������Ĺ㲥������
		if (screenOffReceiver != null) {
			unregisterReceiver(screenOffReceiver);
			screenOffReceiver = null;
		}
	}
            
        
}
