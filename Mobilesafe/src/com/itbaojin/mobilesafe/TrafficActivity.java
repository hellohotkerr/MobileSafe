package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

public class TrafficActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		/*TrafficStats.getMobileRxBytes();//��ȡ�ֻ����ص�����
		TrafficStats.getMobileTxBytes();//��ȡ�ֻ��ϴ�������
		
		TrafficStats.getTotalRxBytes();//��ȡ����������
		TrafficStats.getTotalTxBytes();//��ȡ�ϴ�������
		
		TrafficStats.getUidRxBytes(uid);//��ȡĳ��Ӧ�ó�����������,uid:Ӧ�ó���userid
		TrafficStats.getUidTxBytes(uid);//��ȡĳ��Ӧ�ó����ϴ�����*/
		DrawerLayout dl = (DrawerLayout) findViewById(R.id.dl);
//		dl.openDrawer(Gravity.RIGHT);//��ʾĬ�ϴ��ĸ���ʽ����
		dl.openDrawer(Gravity.LEFT);
	}

}
