package com.itbaojin.mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.itbaojin.mobilesafe.R;
import com.itbaojin.mobilesafe.receiver.MyWidget;
import com.itbaojinmobilesafe.utils.TaskUtil;


public class WidgetService extends Service {
	private Timer timer;
	private AppWidgetManager appWidgetManager;
	private WidgetReceiver widgetReceiver;
	private ScreenOffReceiver screenOffReceiver;
	private ScreenOnReceiver screenOnReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * ������̵Ĺ㲥������
	 * @author Administrator
	 *
	 */
	private class WidgetReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//�������
			killProcess();
		}
	}
	/**
	 * �����Ĺ㲥������
	 * @author Administrator
	 *
	 */
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("������.....");
			//�������
			killProcess();
			//ֹͣ����
			stopUpdates();
		}
	}
	/**
	 * �����Ĺ㲥������
	 * @author Administrator
	 *
	 */
	private class ScreenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			updateWidgets();
		}
	}
	
	/**
	 * �������
	 */
	public void killProcess() {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//��ȡ�������н���
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			//�ж����ǵ�Ӧ�ý��̲��ܱ�����
			if (!runningAppProcessInfo.processName.equals(getPackageName())) {
				am.killBackgroundProcesses(runningAppProcessInfo.processName);
			}
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		super.onCreate();
		//��������С�ؼ�
		//ע��������̹㲥������
		//1.�㲥������
		widgetReceiver = new WidgetReceiver();
		//2.���ý��ܵĹ㲥�¼�
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("aa.bb.cc");
		// 3.ע��㲥������
		registerReceiver(widgetReceiver, intentFilter);
		//ע�������Ĺ㲥������
		screenOffReceiver = new ScreenOffReceiver();
		//���ý��ܹ㲥�¼�
		IntentFilter screenoffIntentfilter = new IntentFilter();
		screenoffIntentfilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenOffReceiver, screenoffIntentfilter);
		//ע������Ĺ㲥������
		screenOnReceiver = new ScreenOnReceiver();
		IntentFilter screenOnIntentFilter = new IntentFilter();
		screenOnIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(screenOnReceiver, screenOnIntentFilter);
		
		
		//widget�Ĺ�����
		appWidgetManager = AppWidgetManager.getInstance(this);
		//���²���
		updateWidgets();
	}
	/**
	 * ����widget
	 */
	private void updateWidgets() {
		/*new Thread(){
			public void run() {
				while(true){
					SystemClock.sleep(2000);
				}
			};
		}.start();*/
		//������
		timer = new Timer();
		//ִ�в���
		//task : Ҫִ�в���
		//when : �ӳٵ�ʱ��
		//period : ÿ��ִ�еļ��ʱ��
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("widget������......");
				//���²���
				//��ȡ����ı�ʾ
				ComponentName provider = new ComponentName(WidgetService.this, MyWidget.class);
				//��ȡԶ�̲���
				//packageName : Ӧ�õİ���
				//layoutId :widget�����ļ�
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				//Զ�̲��ֲ���ͨ��findviewbyid��ȡ��ʼ���ؼ�
				//���²����ļ�����Ӧ�ؼ���ֵ
				//viewId :���¿ؼ���id
				//text : ���µ�����
				views.setTextViewText(R.id.process_count, "�����������:"+TaskUtil.getProcessCount(WidgetService.this));
				views.setTextViewText(R.id.process_memory, "�����ڴ�:"+Formatter.formatFileSize(WidgetService.this, TaskUtil.getAvailableRam(WidgetService.this)));
				
				//��ť����¼�
				Intent intent = new Intent();
				intent.setAction("aa.bb.cc");//����Ҫ���͵Ĺ㲥,aa.bb.cc:�Զ���Ĺ㲥�¼�
				//sendBroadcast(intent);
				//ͨ������һ���㲥ȥ��ʾҪִ���������,ͨ�����ܷ��͵Ĺ㲥ִ���������
				//flags : ָ����Ϣ�ı�ǩ
				PendingIntent pendingIntent = PendingIntent.getBroadcast(WidgetService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				//viewId : ����Ŀؼ���id
				//pendingIntent : �ӳ���ͼ  ����һ��intent��ͼ,������Ĳ�ȥִ�������ͼ,������Ͳ�ִ����ͼ
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				//���²���
				appWidgetManager.updateAppWidget(provider, views);
			}
		}, 2000, 2000);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		//ֹͣ����widget
		stopUpdates();
		//ע��������̵Ĺ㲥������
		if (widgetReceiver != null) {
			unregisterReceiver(widgetReceiver);
			widgetReceiver = null;
		}
		if (screenOffReceiver != null) {
			unregisterReceiver(screenOffReceiver);
			screenOffReceiver = null;
		}
		if (screenOnReceiver != null) {
			unregisterReceiver(screenOnReceiver);
			screenOnReceiver = null;
		}
	}
	/**
	 * ֹͣ����
	 */
	private void stopUpdates() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
}
