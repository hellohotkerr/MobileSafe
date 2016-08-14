package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector gestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//1.��ȡ����ʶ����
		//Ҫ����������ʶ������Ч,���뽫����ʶ����ע�ᵽ��Ļ�Ĵ����¼���
		gestureDetector = new GestureDetector(this, new MyOnGestureListener());
	}
	//����Ĵ����¼�
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			gestureDetector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
	//base simple
	private class MyOnGestureListener extends SimpleOnGestureListener{
		//e1 : ���µ��¼�,�����а��µ�����
		//e2 : ̧����¼�,������̧�������
		//velocityX : velocity �ٶ�    ��x�����ƶ�������
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			//�õ����µ�x����
			float startX = e1.getRawX();
			//�õ�̧���x����
			float endX = e2.getRawX();
			//�õ����µ�Y����
			float startY = e1.getRawY();
			//�õ�̧���y����
			float endY = e2.getRawY();
			//�ж��Ƿ���б��
			if ((Math.abs(startY-endY)) > 300) {
//				Toast.makeText(getApplicationContext(), "��С�����һ���,������....", 0).show();
				return false;
			}
			//��һ��
			if ((startX-endX) > 0) {
				showNextPage();
			}
			//��һ��
			if ((endX-startX) > 0) {
				showPrePage();
			}
			//true if the event is consumed, else false
			//true : �¼�ִ��     false:�����¼�,�¼���ִ��
//			return super.onFling(e1, e2, velocityX, velocityY);
			return true;
	}

	}
//	��Ϊ���಻֪��������һ��,��һ������ʵ��,���Դ����������󷽷�,������ȥʵ��,�����Լ�������ȥʵ����Ӧ�Ĳ���
	//��һҳ�ĳ��󷽷�,���������������ת���Ǹ�����
		protected abstract void showNextPage();
		//��һҳ�ĳ��󷽷�,���������������ת���Ǹ�����
		protected abstract void showPrePage();
	//�����һҳ��ť��ʱ��,���������showNextPage��������Ӧ��ת
	public void nextPage(View view){
		showNextPage();
	}
	
	//�����һҳ��ť��ʱ��,���������showPrePage��������Ӧ��ת
	public void prePage(View view){
		showPrePage();
	}
//	�ڶ�����ȡ:��Ϊ�ڽ��еڶ��������ʱ��,������ؼ���ֱ�ӻص�������,��Ϊÿ�������ж��з��ؼ�����,�������ϳ�ȡ���������ͳһ�Ĵ���,����Ͳ�ȥ���������ؼ��Ĳ���
	//�����ֻ�����ť�ĵ���¼�
	//keyCode :������ť�ı�ʾ
	//event : �����Ĵ����¼�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//�ж�keycode�Ƿ��Ƿ��ؼ��ı�ʾ
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//true:�ǿ������ΰ������¼�
			//return true;
			showPrePage();
		}
		return super.onKeyDown(keyCode, event);
	}
}
