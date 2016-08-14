package com.itbaojin.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itbaojin.mobilesafe.db.dao.BlackNumDao;

public class BlackNumService extends Service {

	private SmsReceiver smsReceiver;
	private BlackNumDao blackNumDao;
	private TelephonyManager telephonyManager;
	private MyPhoneStateListener myPhoneStateListener;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	private class SmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				// 4,获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				// 5,获取短信对象的基本信息
				String sender = sms.getOriginatingAddress();
				String body = sms.getMessageBody();
				int mode = blackNumDao.queryBlackNumMode(sender);
				if (mode == BlackNumDao.SMS || mode == BlackNumDao.ALL) {
					abortBroadcast();
				}
			}
			
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
		blackNumDao = new BlackNumDao(getApplicationContext());
		
		smsReceiver = new SmsReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(1000);
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, intentFilter);
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		
	}
	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				int mode = blackNumDao.queryBlackNumMode(incomingNumber);
				if (mode == BlackNumDao.SMS || mode == BlackNumDao.ALL) {
					//挂断电话
					endCall();
					//删除通话记录
					//1.获取内容解析者
					final ContentResolver resolver = getContentResolver();
					//2.获取内容提供者地址  call_log   calls表的地址:calls
					//3.获取执行操作路径
					final Uri uri = Uri.parse("content://call_log/calls");
					//4.删除操作
					//resolver.delete(uri, "number=?", new String[]{incomingNumber});
					//通过内容观察者观察内容提供者内容,如果变化,就去执行删除操作
					//notifyForDescendents : 匹配规则,true : 精确匹配   false:模糊匹配
					resolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
						//内容提供者内容变化的时候调用
						@Override
						public void onChange(boolean selfChange) {
							super.onChange(selfChange);
							//删除通话记录
							resolver.delete(uri, "number=?", new String[]{incomingNumber});
							//注销内容观察者
							resolver.unregisterContentObserver(this);
						}
					});
				}
				
			}
		}

		private void endCall() {
			//通过反射进行实现
			try {
				//1.通过类加载器加载相应类的class文件
				//Class<?> forName = Class.forName("android.os.ServiceManager");
				Class<?> loadClass = BlackNumService.class.getClassLoader().loadClass("android.os.ServiceManager");
				//2.获取类中相应的方法
				//name : 方法名
				//parameterTypes : 参数类型
				Method method = loadClass.getDeclaredMethod("getService", String.class);
				//3.执行方法,获取返回值
				//receiver : 类的实例
				//args : 具体的参数
				IBinder invoke = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
				//aidl
				ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);
				//挂断电话
				iTelephony.endCall();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(smsReceiver);
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}

}
