package com.itbaojin.mobilesafe.receiver;

import com.itbaojin.mobilesafe.R;
import com.itbaojin.mobilesafe.service.GPSService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.itbaojinmobilesafe.utils.ConstantValue;
import com.itbaojinmobilesafe.utils.SpUtils;


public class SmsReceiver extends BroadcastReceiver {
	private MediaPlayer mediaPlayer;

	//�㲥��������ÿ�����յ�һ���㲥�¼�,����new�㲥������
	@Override
	public void onReceive(Context context, Intent intent) {
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName componentName = new ComponentName(context, Admin.class);

		// 1,�ж��Ƿ����˷�������
		boolean open_security = SpUtils.getBoolean(context,
				ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			// 2,��ȡ��������
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			// 3,ѭ���������Ź���
			for (Object object : objects) {
				// 4,��ȡ���Ŷ���
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				// 5,��ȡ���Ŷ���Ļ�����Ϣ
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();

				// 6,�ж��Ƿ�����������ֵĹؼ���
				if (messageBody.contains("#*alarm*#")) {
					// ���ű�������
					// �ڲ��ű�������֮ǰ,��ϵͳ�������ó����
					// �����Ĺ�����
					AudioManager audioManager = (AudioManager) context
							.getSystemService(Context.AUDIO_SERVICE);
					// ����ϵͳ�����Ĵ�С
					// streamType : ����������
					// index : �����Ĵ�С 0��С 15���
					// flags : ָ����Ϣ�ı�ǩ
					// getStreamMaxVolume : ��ȡϵͳ�������,streamType:����������
					audioManager
							.setStreamVolume(
									AudioManager.STREAM_MUSIC,
									audioManager
											.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
									0);

					// �ж��Ƿ��ڲ��ű�������
					if (mediaPlayer != null) {
						mediaPlayer.release();// �ͷ���Դ
					}
					mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
					abortBroadcast();// ���ز���,ԭ��androidϵͳ,������ȶ���ϵͳ������,����С��
				} else if (messageBody.contains("#*location*#")) {
					// 8,������ȡλ�÷���
					Intent intent_gps = new Intent(context, GPSService.class);
					context.startService(intent_gps);

					abortBroadcast();// ���ز���,ԭ��androidϵͳ,������ȶ���ϵͳ������,����С��
					context.stopService(intent_gps);
				} else if (messageBody.contains("#*lockscrenn*#")) {
					//Զ������
					System.out.println("Զ������");
					//�жϳ�������Ա�Ƿ񼤻�
					if (devicePolicyManager.isAdminActive(componentName)) {
						devicePolicyManager.lockNow();
					}
					abortBroadcast();// ���ز���,ԭ��androidϵͳ,������ȶ���ϵͳ������,����С��
				} else if (messageBody.contains("#*wipedate*#")) {
					//Զ��ɾ������,�����ڻָ���������
					System.out.println("Զ��ɾ������");
					if (devicePolicyManager.isAdminActive(componentName)) {
						devicePolicyManager.wipeData(0);//Զ��ɾ������
					}
					abortBroadcast();// ���ز���,ԭ��androidϵͳ,������ȶ���ϵͳ������,����С��
				}

			}
		}
	}

}
