package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DragViewActivity extends Activity {
	private LinearLayout ll_dragview_toast;
	private SharedPreferences sp;
	private int width;
	private int height;
	private TextView tv_dragview_bottom;
	private TextView tv_dragview_top;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dragview);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		ll_dragview_toast = (LinearLayout) findViewById(R.id.ll_dragview_toast);
		tv_dragview_bottom = (TextView) findViewById(R.id.tv_dragview_bottom);
		tv_dragview_top = (TextView) findViewById(R.id.tv_dragview_top);

		setTouch();
		setDoubleClick();
		// ���ÿؼ����Բ���
		// 1.��ȡ���������
		int x = sp.getInt("x", 0);
		int y = sp.getInt("y", 0);
		System.out.println("x:" + x + " y:" + y);
		// 2.���»��ƿؼ�
		/*
		 * int width = ll_dragview_toast.getWidth(); int height =
		 * ll_dragview_toast.getHeight();
		 * System.out.println("width:"+width+" height:"+height);
		 * ll_dragview_toast.layout(x, y, x+width, y+height);
		 */
		// �ڳ�ʼ���ؼ�֮ǰ�������ÿؼ�������
		// ��ȡ���ؼ������Թ���,���ؼ���layoutparams
		RelativeLayout.LayoutParams params = (LayoutParams) ll_dragview_toast
				.getLayoutParams();
		// ������Ӧ������
		// leftMargin : ���븸�ؼ���ߵľ���,���ݲ����ļ��пؼ���layout_marginLeft����Ч������
		params.leftMargin = x;
		params.topMargin = y;
		// ���ؼ���������
		ll_dragview_toast.setLayoutParams(params);

		// ��ȡ��Ļ�Ŀ��
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		// windowManager.getDefaultDisplay().getWidth();
		DisplayMetrics outMetrics = new DisplayMetrics();// ������һ�Ű�ֽ
		windowManager.getDefaultDisplay().getMetrics(outMetrics);// ����ֽ���ÿ��
		width = outMetrics.widthPixels;
		height = outMetrics.heightPixels;

		if (y >= height / 2) {
			// �����·���ʾ�Ϸ�
			tv_dragview_bottom.setVisibility(View.INVISIBLE);
			tv_dragview_top.setVisibility(View.VISIBLE);
		} else {
			// �����Ϸ���ʾ�·�
			tv_dragview_top.setVisibility(View.INVISIBLE);
			tv_dragview_bottom.setVisibility(View.VISIBLE);
		}
	}
	
	long[] mHits = new long[2];

	/**
	 * ˫������
	 */
	private void setDoubleClick() {
		ll_dragview_toast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * src the source array to copy the content. ������ԭ���� srcPos the
				 * starting index of the content in src. �Ǵ�Դ�����Ǹ�λ�ÿ�ʼ���� dst the
				 * destination array to copy the data into. ������Ŀ������ dstPos the
				 * starting index for the copied content in dst. �Ǵ�Ŀ�������Ǹ�λ�ÿ�ʼȥд
				 * length the number of elements to be copied. �����ĳ���
				 */
				// �����������
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis(); // ���뿪����ʱ�����ø�����ĵڶ���Ԫ��,�뿪��ʱ��
																		// :����ֵ,�ֻ����߲���
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) { // �ж��Ƿ�������
					System.out.println("˫����...");
					// ˫������
					int l = (width - ll_dragview_toast.getWidth()) / 2;
					int t = (height - 25 - ll_dragview_toast.getHeight()) / 2;
					ll_dragview_toast.layout(l, t,
							l + ll_dragview_toast.getWidth(), t
									+ ll_dragview_toast.getHeight());
					// ����ؼ�������
					Editor edit = sp.edit();
					edit.putInt("x", l);
					edit.putInt("y", t);
					edit.commit();
				}
			}
		});
	}

	/**
	 * ���ô�������
	 */
	private void setTouch() {
		ll_dragview_toast.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			// v : ��ǰ�Ŀؼ�
			// event : �ؼ�ִ�е��¼�
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// event.getAction() : ��ȡ���Ƶ�ִ�е��¼�
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// ���µ��¼�
					// System.out.println("������....");
					// 1.���¿ؼ�,��¼��ʼ��x��y������
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// �ƶ����¼�
					// System.out.println("�ƶ���....");
					// 2.�ƶ����µ�λ�ü�¼�µ�λ�õ�x��y������
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					// 3.�����ƶ���ƫ����
					int dX = newX - startX;
					int dY = newY - startY;
					// 4.�ƶ���Ӧ��ƫ����,���»��ƿؼ�
					// ��ȡ��ʱ��ԭ�ؼ����븸�ؼ���ߺͶ����ľ���
					int l = ll_dragview_toast.getLeft();
					int t = ll_dragview_toast.getTop();
					// ��ȡ�µĿؼ��ľ��븸�ؼ���ߺͶ����ľ���
					l += dX;
					t += dY;
					int r = l + ll_dragview_toast.getWidth();
					int b = t + ll_dragview_toast.getHeight();

					// �ڻ��ƿؼ�֮ǰ,�ж�ltrb��ֵ�Ƿ񳬳���Ļ�Ĵ�С,����ǾͲ��ڽ��л��ƿؼ��Ĳ���
					if (l < 0 || r > width || t < 0 || b > height - 25) {
						break;
					}
					ll_dragview_toast.layout(l, t, r, b);// ���»��ƿؼ�

					// �ж�textview��������ʾ
					int top = ll_dragview_toast.getTop();
					if (top >= height / 2) {
						// �����·���ʾ�Ϸ�
						tv_dragview_bottom.setVisibility(View.INVISIBLE);
						tv_dragview_top.setVisibility(View.VISIBLE);
					} else {
						// �����Ϸ���ʾ�·�
						tv_dragview_top.setVisibility(View.INVISIBLE);
						tv_dragview_bottom.setVisibility(View.VISIBLE);
					}
					// 5.���¿�ʼ������
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP:
					// ̧����¼�
					// System.out.println("̧����....");
					// ����ؼ�������,������ǿؼ������겻����ָ����
					// ��ȡ�ؼ�������
					int x = ll_dragview_toast.getLeft();
					int y = ll_dragview_toast.getTop();

					Editor edit = sp.edit();
					edit.putInt("x", x);
					edit.putInt("y", y);
					edit.commit();
					break;
				}
				// True if the listener has consumed the event, false otherwise.
				// true:�¼�������,ִ����,false:��ʾ�¼���������
				return false;
			}
		});
	}

}
