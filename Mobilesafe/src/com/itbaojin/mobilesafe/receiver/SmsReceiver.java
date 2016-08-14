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

	//广播接受者在每个接收到一个广播事件,重新new广播接受者
	@Override
	public void onReceive(Context context, Intent intent) {
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName componentName = new ComponentName(context, Admin.class);

		// 1,判断是否开启了防盗保护
		boolean open_security = SpUtils.getBoolean(context,
				ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			// 2,获取短信内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			// 3,循环遍历短信过程
			for (Object object : objects) {
				// 4,获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				// 5,获取短信对象的基本信息
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();

				// 6,判断是否包含播放音乐的关键字
				if (messageBody.contains("#*alarm*#")) {
					// 播放报警音乐
					// 在播放报警音乐之前,将系统音量设置成最大
					// 声音的管理者
					AudioManager audioManager = (AudioManager) context
							.getSystemService(Context.AUDIO_SERVICE);
					// 设置系统音量的大小
					// streamType : 声音的类型
					// index : 声音的大小 0最小 15最大
					// flags : 指定信息的标签
					// getStreamMaxVolume : 获取系统最大音量,streamType:声音的类型
					audioManager
							.setStreamVolume(
									AudioManager.STREAM_MUSIC,
									audioManager
											.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
									0);

					// 判断是否在播放报警音乐
					if (mediaPlayer != null) {
						mediaPlayer.release();// 释放资源
					}
					mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
					abortBroadcast();// 拦截操作,原生android系统,国产深度定制系统中屏蔽,比如小米
				} else if (messageBody.contains("#*location*#")) {
					// 8,开启获取位置服务
					Intent intent_gps = new Intent(context, GPSService.class);
					context.startService(intent_gps);

					abortBroadcast();// 拦截操作,原生android系统,国产深度定制系统中屏蔽,比如小米
					context.stopService(intent_gps);
				} else if (messageBody.contains("#*lockscrenn*#")) {
					//远程锁屏
					System.out.println("远程锁屏");
					//判断超级管理员是否激活
					if (devicePolicyManager.isAdminActive(componentName)) {
						devicePolicyManager.lockNow();
					}
					abortBroadcast();// 拦截操作,原生android系统,国产深度定制系统中屏蔽,比如小米
				} else if (messageBody.contains("#*wipedate*#")) {
					//远程删除数据,类似于恢复出厂设置
					System.out.println("远程删除数据");
					if (devicePolicyManager.isAdminActive(componentName)) {
						devicePolicyManager.wipeData(0);//远程删除数据
					}
					abortBroadcast();// 拦截操作,原生android系统,国产深度定制系统中屏蔽,比如小米
				}

			}
		}
	}

}
