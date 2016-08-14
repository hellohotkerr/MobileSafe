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
				// 4,��ȡ���Ŷ���
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				// 5,��ȡ���Ŷ���Ļ�����Ϣ
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
					//�Ҷϵ绰
					endCall();
					//ɾ��ͨ����¼
					//1.��ȡ���ݽ�����
					final ContentResolver resolver = getContentResolver();
					//2.��ȡ�����ṩ�ߵ�ַ  call_log   calls��ĵ�ַ:calls
					//3.��ȡִ�в���·��
					final Uri uri = Uri.parse("content://call_log/calls");
					//4.ɾ������
					//resolver.delete(uri, "number=?", new String[]{incomingNumber});
					//ͨ�����ݹ۲��߹۲������ṩ������,����仯,��ȥִ��ɾ������
					//notifyForDescendents : ƥ�����,true : ��ȷƥ��   false:ģ��ƥ��
					resolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
						//�����ṩ�����ݱ仯��ʱ�����
						@Override
						public void onChange(boolean selfChange) {
							super.onChange(selfChange);
							//ɾ��ͨ����¼
							resolver.delete(uri, "number=?", new String[]{incomingNumber});
							//ע�����ݹ۲���
							resolver.unregisterContentObserver(this);
						}
					});
				}
				
			}
		}

		private void endCall() {
			//ͨ���������ʵ��
			try {
				//1.ͨ���������������Ӧ���class�ļ�
				//Class<?> forName = Class.forName("android.os.ServiceManager");
				Class<?> loadClass = BlackNumService.class.getClassLoader().loadClass("android.os.ServiceManager");
				//2.��ȡ������Ӧ�ķ���
				//name : ������
				//parameterTypes : ��������
				Method method = loadClass.getDeclaredMethod("getService", String.class);
				//3.ִ�з���,��ȡ����ֵ
				//receiver : ���ʵ��
				//args : ����Ĳ���
				IBinder invoke = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
				//aidl
				ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);
				//�Ҷϵ绰
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
