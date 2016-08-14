package com.itbaojinmobilesafe.utils;

import android.os.Handler;

public abstract class MyAsycnTasks {
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			postTask();
		};
	};
	/**
	 * �����߳�֮ǰִ�еķ���
	 */
	public abstract void preTask();
	/**
	 * �����߳�֮��ִ�еķ���
	 */
	public abstract void doinBack();
	/**
	 * �����߳�֮��ִ�еķ���
	 */
	public abstract void postTask();
	/**
	 * ִ��
	 */
	public void execute(){
		preTask();
		new Thread(){
			public void run() {
				doinBack();
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
}
