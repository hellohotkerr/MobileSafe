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
		//1.获取手势识别器
		//要想让手势是识别器生效,必须将手势识别器注册到屏幕的触摸事件中
		gestureDetector = new GestureDetector(this, new MyOnGestureListener());
	}
	//界面的触摸事件
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			gestureDetector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
	//base simple
	private class MyOnGestureListener extends SimpleOnGestureListener{
		//e1 : 按下的事件,保存有按下的坐标
		//e2 : 抬起的事件,保存有抬起的坐标
		//velocityX : velocity 速度    在x轴上移动的速率
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			//得到按下的x坐标
			float startX = e1.getRawX();
			//得到抬起的x坐标
			float endX = e2.getRawX();
			//得到按下的Y坐标
			float startY = e1.getRawY();
			//得到抬起的y坐标
			float endY = e2.getRawY();
			//判断是否是斜滑
			if ((Math.abs(startY-endY)) > 300) {
//				Toast.makeText(getApplicationContext(), "你小子又乱滑了,别闹了....", 0).show();
				return false;
			}
			//下一步
			if ((startX-endX) > 0) {
				showNextPage();
			}
			//上一步
			if ((endX-startX) > 0) {
				showPrePage();
			}
			//true if the event is consumed, else false
			//true : 事件执行     false:拦截事件,事件不执行
//			return super.onFling(e1, e2, velocityX, velocityY);
			return true;
	}

	}
//	因为父类不知道子类上一步,下一步具体实现,所以创建两个抽象方法,让子类去实现,根据自己的特性去实现响应的操作
	//下一页的抽象方法,由子类决定具体跳转到那个界面
		protected abstract void showNextPage();
		//上一页的抽象方法,由子类决定具体跳转到那个界面
		protected abstract void showPrePage();
	//点击下一页按钮的时候,根据子类的showNextPage方法做相应跳转
	public void nextPage(View view){
		showNextPage();
	}
	
	//点击上一页按钮的时候,根据子类的showPrePage方法做相应跳转
	public void prePage(View view){
		showPrePage();
	}
//	第二个抽取:因为在进行第二个界面的时候,点击返回键会直接回到主界面,因为每个界面中都有返回键操作,所以向上抽取到父类进行统一的处理,子类就不去单独处理返回键的操作
	//监听手机物理按钮的点击事件
	//keyCode :　物理按钮的标示
	//event : 按键的处理事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//判断keycode是否是返回键的标示
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//true:是可以屏蔽按键的事件
			//return true;
			showPrePage();
		}
		return super.onKeyDown(keyCode, event);
	}
}
