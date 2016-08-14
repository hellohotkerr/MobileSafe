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
		Log.i(tag, "�����ֻ��ɹ���,���Ҽ���������Ӧ�Ĺ㲥......");
		if (SpUtils.getBoolean(context, ConstantValue.OPEN_SECURITY, false)) {
			//1,��ȡ�������ֻ���sim�������к�
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			//String simSerialNumber = tm.getSimSerialNumber()+"xxx";	����ģ�������
			String simSerialNumber = tm.getSimSerialNumber();
			//2,sp�д洢�����п���
			String sim_number = SpUtils.getString(context,ConstantValue.SIM_NUMBER, "");
			//3,�ȶԲ�һ��
			if (!TextUtils.isEmpty(simSerialNumber) && !TextUtils.isEmpty(sim_number)) {
				if(!simSerialNumber.equals(sim_number)){
					//4,���Ͷ��Ÿ�ѡ����ϵ�˺���
					SmsManager sms = SmsManager.getDefault();
					//destinationAddress : �ռ���
					//scAddress :���������ĵĵ�ַ����һ��null
					//text : ���ŵ�����
					//sentIntent : �Ƿ��ͳɹ�
					//deliveryIntent : ���ŵ�Э��  һ��null
					//sms.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
					String phone = SpUtils.getString(context, ConstantValue.CONTACT_PHONE, "");
					sms.sendTextMessage(phone, null, "sim change!!!", null, null);
				}
			}
		}
	
	}

}
