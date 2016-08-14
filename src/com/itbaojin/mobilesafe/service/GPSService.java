package com.itbaojin.mobilesafe.service;

import java.util.List;

import com.itbaojinmobilesafe.utils.ConstantValue;
import com.itbaojinmobilesafe.utils.SpUtils;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class GPSService extends Service {
	public static final String tag = "MainActivity";
	private LocationManager locationManager;
	private MyLocationListener myLocationListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		//1.��ȡλ�õĹ�����
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//2.��ȡ��λ��ʽ
		//2.1��ȡ���еĶ�λ��ʽ
		//enabledOnly : true : �������п��õĶ�λ��ʽ
		List<String> providers = locationManager.getProviders(true);
		for (String string : providers) {
			System.out.println(string);
		}
		//2.2��ȡ��ѵĶ�λ��ʽ
		Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(true);//�����Ƿ���Զ�λ����,true:���Զ�λ����,һ������gps��λ
		//criteria : ���ö�λ������,����ʹ��ʲô��λ��ʽ��
		//enabledOnly : true : ��λ���õľͷ���
		String bestProvider = locationManager.getBestProvider(criteria, true);
		System.out.println("��ѵĶ�λ��ʽ:"+bestProvider);
		//3.��λ
		myLocationListener = new MyLocationListener();
		//provider : ��λ��ʽ
		//minTime : ��λ����Сʱ����
		//minDistance : ��λ����С������
		//listener : LocationListener
		locationManager.requestLocationUpdates(bestProvider, 30, 0, myLocationListener);
		
			
	}
	private class MyLocationListener implements LocationListener{
		//����λλ�øı��ʱ�����
		//location : ��ǰ��λ��
		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();//��ȡγ��,ƽ��
			double longitude = location.getLongitude();//��ȡ����
			Log.i(tag, "longitude:"+longitude+"   latitude:"+latitude);
			//����ȫ���뷢��һ��������γ������Ķ���
			SmsManager smsManager = SmsManager.getDefault();
			String safeNumber = SpUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
			smsManager.sendTextMessage(safeNumber, null, "longitude:"+longitude+"  latitude:"+latitude, null, null);
			//ֹͣ����
			stopSelf();
		}
		//����λ״̬�ı��ʱ�����
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		//����λ���õ�ʱ�����
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		//����λ�����õ�ʱ�����
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		//�ر�gps��λ,�߰汾���Ѿ�������ô����,�߰汾�й涨�رպͿ���gps���뽻���û��Լ�ȥʵ��
		locationManager.removeUpdates(myLocationListener);
	}
}
