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
		/*TrafficStats.getMobileRxBytes();//获取手机下载的流量
		TrafficStats.getMobileTxBytes();//获取手机上传的流量
		
		TrafficStats.getTotalRxBytes();//获取下载总流量
		TrafficStats.getTotalTxBytes();//获取上传总流量
		
		TrafficStats.getUidRxBytes(uid);//获取某个应用程序下载流量,uid:应用程序userid
		TrafficStats.getUidTxBytes(uid);//获取某个应用程序上传流量*/
		DrawerLayout dl = (DrawerLayout) findViewById(R.id.dl);
//		dl.openDrawer(Gravity.RIGHT);//表示默认打开哪个方式布局
		dl.openDrawer(Gravity.LEFT);
	}

}
