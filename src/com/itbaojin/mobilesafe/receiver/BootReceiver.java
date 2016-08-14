package com.itbaojin.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.itbaojinmobilesafe.utils.ConstantValue;
import com.itbaojinmobilesafe.utils.SpUtils;

public class BootReceiver extends BroadcastReceiver {
	private static final String tag = "BootReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(tag, "重启手机成功了,并且监听到了相应的广播......");
		if (SpUtils.getBoolean(context, ConstantValue.OPEN_SECURITY, false)) {
			//1,获取开机后手机的sim卡的序列号
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			//String simSerialNumber = tm.getSimSerialNumber()+"xxx";	刻意的，测试用
			String simSerialNumber = tm.getSimSerialNumber();
			//2,sp中存储的序列卡号
			String sim_number = SpUtils.getString(context,ConstantValue.SIM_NUMBER, "");
			//3,比对不一致
			if (!TextUtils.isEmpty(simSerialNumber) && !TextUtils.isEmpty(sim_number)) {
				if(!simSerialNumber.equals(sim_number)){
					//4,发送短信给选中联系人号码
					SmsManager sms = SmsManager.getDefault();
					//destinationAddress : 收件人
					//scAddress :　短信中心的地址　　一般null
					//text : 短信的内容
					//sentIntent : 是否发送成功
					//deliveryIntent : 短信的协议  一般null
					//sms.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
					String phone = SpUtils.getString(context, ConstantValue.CONTACT_PHONE, "");
					sms.sendTextMessage(phone, null, "sim change!!!", null, null);
				}
			}
		}
	
	}

}
