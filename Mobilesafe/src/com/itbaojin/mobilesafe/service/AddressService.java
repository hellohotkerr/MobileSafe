package com.itbaojin.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.itbaojin.mobilesafe.R;
import com.itbaojin.mobilesafe.db.dao.AddressDao;

public class AddressService extends Service {
	private TelephonyManager telephonyManager;
	private MyPhoneStateListener myPhoneStateListener;
	private WindowManager windowManager;
	private View view;
	private SharedPreferences sp;
	private MyOutGoingCallReceiver myOutGoingCallReceiver;
	private WindowManager.LayoutParams params;
	private int width;
	private int height;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * �Ⲧ�绰�Ĺ㲥������
	 * @author Administrator
	 *
	 */
	private class MyOutGoingCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//��ѯ�Ⲧ�绰�ĺ��������
			//1.��ȡ�Ⲧ�绰
			String phone = getResultData();
			//2.��ѯ���������
			String queryAddress = AddressDao.queryAddress(phone, getApplicationContext());
			//3.�жϺ���������Ƿ�Ϊ��
			if (!TextUtils.isEmpty(queryAddress)) {
				//��ʾtoast
				showToast(queryAddress);
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		sp = getSharedPreferences("config", MODE_PRIVATE);
		

		//����ע������Ⲧ�绰�Ĺ㲥������
		//��Ҫ��Ԫ��:1.�㲥������,2.���ü����Ĺ㲥�¼�
		//1.���ù㲥������
		myOutGoingCallReceiver = new MyOutGoingCallReceiver();
		//2.���ý��ܵĹ㲥�¼�
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");//���ý��ܵĹ㲥�¼�
		//3.ע��㲥������
		registerReceiver(myOutGoingCallReceiver, intentFilter);
		
		// �����绰״̬
		// 1.��ȡ�绰�Ĺ�����
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 2.�����绰��״̬
		myPhoneStateListener = new MyPhoneStateListener();
		// listener : �绰״̬�Ļص�����
		// events : �����绰���¼�
		// LISTEN_NONE : ���������������
		// LISTEN_CALL_STATE : �����绰״̬
		telephonyManager.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class MyPhoneStateListener extends PhoneStateListener {
		// �����绰״̬�Ļص�����
		// state : �绰��״̬
		// incomingNumber :������绰
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// ���� ״̬,�Ҷ�״̬
				// ����toast
				 hideToast();
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				;// �����״̬
				// ��ѯ��������ز���ʾ
				String queryAddress = AddressDao.queryAddress(incomingNumber,
						getApplicationContext());
				// �жϲ�ѯ�������Ƿ�Ϊ��
				if (!TextUtils.isEmpty(queryAddress)) {
					// ��ʾ���������
//					Toast.makeText(getApplicationContext(), queryAddress, 0)
//							.show();
					 showToast(queryAddress);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// ͨ����״̬
				// ͨ����״̬,���Ⲧ�绰��״̬�ǳ�ͻ��
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	@Override
	public void onDestroy() {
		// ������رյ�ʱ��,ȡ����������
		telephonyManager.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		//ע���Ⲧ�绰�㲥������
		unregisterReceiver(myOutGoingCallReceiver);
		super.onDestroy();
	}
	
	/**
	 * ��ʾtoast
	 */
	public void showToast(String queryAddress) {
		
		int[] bgcolor = new int[]{
				R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green 
		};
		
		//1.��ȡwindowmanager
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		//�������ļ�ת����view����
		view = View.inflate(getApplicationContext(), R.layout.toast_custom, null);
		//��ʼ���ؼ�
		//view.findViewById��ʾȥtoast_custom�ҿؼ�
		TextView tv_toastcustom_address = (TextView) view.findViewById(R.id.tv_toastcustom_address);
		tv_toastcustom_address.setText(queryAddress);
		
		//���ݹ�������ʾ���������õķ������ֵ����toast��ʾ�ı������
		view.setBackgroundResource(bgcolor[sp.getInt("which", 0)]);
		
		/*textView = new TextView(getApplicationContext());
		textView.setText(queryAddress);
		textView.setTextSize(100);
		textView.setTextColor(Color.RED);*/
		
		//3.����toast������
		//layoutparams��toast������,�ؼ�Ҫ��ӵ��Ǹ����ؼ���,���ؼ���Ҫʹ���Ǹ����ؼ�������,��ʾ�ؼ������Թ�����ϸ��ؼ������Թ���
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;//�߶Ȱ�������
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; //��Ȱ�������
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  //û�н���
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE  // ���ɴ���
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; // ���ֵ�ǰ��Ļ
        params.format = PixelFormat.TRANSLUCENT; // ͸��
    	params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; // ʹ��TYPE_PHONE//
		// ִ��toast������,toast������û�л�ȡ�����ʹ����¼�

		
        //����toastλ��
        //Ч����ͻ,��Ĭ�ϵ�Ч��Ϊ��
        params.gravity = Gravity.LEFT | Gravity.TOP;
        
        params.x = sp.getInt("x", 100);//��������,��ʾ�ľ���߿�ľ���,����gravity�����õ�,���gravity��left��ʾ������߿�ľ���,�����right��ʾ�����б߿�ľ���
        params.y = sp.getInt("y", 100);//��x�ĺ���
        
		//2.��view������ӵ�windowManager��
		//params : layoutparams  �ؼ�������
		//��params�������ø�view����,����ӵ�windowManager��
		windowManager.addView(view, params);
	}
	/**
	 * toast���������¼�
	 */
	private void setTouch() {
		view.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			//v : ��ǰ�Ŀؼ�
			//event : �ؼ�ִ�е��¼�
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//event.getAction() : ��ȡ���Ƶ�ִ�е��¼�
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//���µ��¼�
					System.out.println("������....");
					//1.���¿ؼ�,��¼��ʼ��x��y������
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					//�ƶ����¼�
					System.out.println("�ƶ���....");
					//2.�ƶ����µ�λ�ü�¼�µ�λ�õ�x��y������
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					//3.�����ƶ���ƫ����
					int dX = newX-startX;
					int dY = newY-startY;
					//4.�ƶ���Ӧ��ƫ����,���»��ƿؼ�
					params.x+=dX;
					params.y+=dY;
					//���ƿؼ������겻���Ƴ��Ⲧ�绰����
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y=0;
					}
					if (params.x > width-view.getWidth()) {
						params.x = width-view.getWidth();
					}
					if (params.y > height - view.getHeight() - 25) {
						params.y = height - view.getHeight() - 25;
					}
					
					windowManager.updateViewLayout(view, params);//����windowmanager�еĿؼ�
					//5.���¿�ʼ������
					startX=newX;
					startY=newY;
					break;
				case MotionEvent.ACTION_UP:
					//̧����¼�
					System.out.println("̧����....");
					//����ؼ�������,������ǿؼ������겻����ָ����
					//��ȡ�ؼ�������
					int x = params.x;
					int y = params.y;
					
					Editor edit = sp.edit();
					edit.putInt("x", x);
					edit.putInt("y", y);
					edit.commit();
					break;
				}
				//True if the listener has consumed the event, false otherwise.
				//true:�¼�������,ִ����,false:��ʾ�¼���������
				return true;
			}
		});
	}

	
	/**
	 * ����toast
	 */
	public void hideToast(){
		if (windowManager != null && view!= null) {
			windowManager.removeView(view);//�Ƴ��ؼ�
			windowManager= null;
			view=null;
		}
	}
}
