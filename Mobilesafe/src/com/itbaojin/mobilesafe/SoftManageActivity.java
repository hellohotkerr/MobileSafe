package com.itbaojin.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itbaojin.mobilesafe.bean.AppInfo;
import com.itbaojin.mobilesafe.db.dao.WatchDogDao;
import com.itbaojin.mobilesafe.engine.AppEngine;
import com.itbaojinmobilesafe.utils.AppUtil;
import com.itbaojinmobilesafe.utils.DensityUtil;
import com.itbaojinmobilesafe.utils.MyAsycnTasks;

public class SoftManageActivity extends Activity implements OnClickListener {
	private ListView lv_softmanager_application;
	private ProgressBar loading;
	private List<AppInfo> list;
	private List<AppInfo> userappinfo;
	private List<AppInfo> systemappinfo;
	private TextView tv_softmanager_userorsystem;
	private AppInfo appInfo;
	private PopupWindow popupWindow;
	private TextView tv_softmanage_room;
	private TextView tv_softmanage_sd;
	private WatchDogDao watchDogDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_softmanager);
		watchDogDao = new WatchDogDao(getApplicationContext());
		lv_softmanager_application = (ListView) findViewById(R.id.lv_softmanager_application);
		loading = (ProgressBar) findViewById(R.id.loading);
		tv_softmanager_userorsystem = (TextView) findViewById(R.id.tv_softmanager_userorsystem);
		tv_softmanage_room = (TextView) findViewById(R.id.tv_softmanage_room);
		tv_softmanage_sd = (TextView) findViewById(R.id.tv_softmanage_sd);
		//��ȡ�����ڴ�,��ȡ����kb
		long availableSD = AppUtil.getAvailableSD();
		long availableROM = AppUtil.getAvailableROM();
		// ����ת��
		String sdsize = Formatter.formatFileSize(getApplicationContext(),
				availableSD);
		String romsize = Formatter.formatFileSize(getApplicationContext(),
				availableROM);
		//������ʾ
		tv_softmanage_sd.setText("SD������:"+sdsize);
		tv_softmanage_room.setText("�ڴ����:"+romsize);

		fillData();
		listviewOnscroll();
		listviewItemClick();
		listviewItemLongClick();
	}


	/**
	 * ��������¼�
	 */
	private void listviewItemLongClick() {
		lv_softmanager_application.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//true if the callback consumed the long click, false otherwise
				//true:��ʾִ��  false:����
				//���������Ĳ���
				//�����û���ϵͳ����(..��)���ܼ�����������
				if (position == 0 || position == userappinfo.size()+1) {
					return true;
				}
				//��ȡ����
				if (position <= userappinfo.size()) {
					//�û�����
					appInfo = userappinfo.get(position-1);
				}else{
					//ϵͳ����
					appInfo = systemappinfo.get(position - userappinfo.size() - 2);
				}
				//��������
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				//�ж�Ӧ����û�м���,�еĽ���,û�еļ���
				if (watchDogDao.queryLockApp(appInfo.getPackageName())) {
					//��������
					watchDogDao.deleteLockApp(appInfo.getPackageName());
					viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.unlock);
				}else{
					//��������
					//�ж�����ǵ�ǰӦ�ó���,�Ͳ�Ҫ����
					if (!appInfo.getPackageName().equals(getPackageName())) {
						watchDogDao.addLockApp(appInfo.getPackageName());
						viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.lock);
					}else{
						Toast.makeText(getApplicationContext(), "��ǰ��Ӧ�ó����ܼ���", 0).show();
					}
				}
				//myadapter.notifyDataSetChanged();
				return true;
			}
		});
	}

	private void listviewItemClick() {
		lv_softmanager_application.setOnItemClickListener(new OnItemClickListener() {

			//view : ��Ŀ��view����
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//��������
				//1.�����û������ϵͳ����(...��)��������
				if (position == 0 || position == userappinfo.size()+1) {
					return;
				}
				//2.��ȡ��Ŀ����Ӧ��Ӧ�ó������Ϣ
				//���ݾ�Ҫ��userappinfo��systemappinfo�л�ȡ
				if (position <= userappinfo.size()) {
					//�û�����
					appInfo = userappinfo.get(position-1);
				}else{
					//ϵͳ����
					appInfo = systemappinfo.get(position - userappinfo.size() - 2);
				}
				//5.�����µ�����֮ǰ,ɾ���� ������
				hidePopuwindow();
				//3.��������
				/*TextView contentView = new TextView(getApplicationContext());
				contentView.setText("����popuwindow��textview�ؼ�");
				contentView.setBackgroundColor(Color.RED);*/
				View contentView = View.inflate(getApplicationContext(), R.layout.popu_window, null);
				//��ʼ���ؼ�
				LinearLayout ll_popuwindow_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_popuwindow_uninstall);
				LinearLayout ll_popuwindow_start = (LinearLayout) contentView.findViewById(R.id.ll_popuwindow_start);
				LinearLayout ll_popuwindow_share = (LinearLayout) contentView.findViewById(R.id.ll_popuwindow_share);
				LinearLayout ll_popuwindow_detail = (LinearLayout) contentView.findViewById(R.id.ll_popuwindow_detail);
				
				//���ؼ����õ���¼�
				ll_popuwindow_uninstall.setOnClickListener(SoftManageActivity.this);
				ll_popuwindow_start.setOnClickListener(SoftManageActivity.this);
				ll_popuwindow_share.setOnClickListener(SoftManageActivity.this);
				ll_popuwindow_detail.setOnClickListener(SoftManageActivity.this);
				
				//contentView : ��ʾview����
				//width,height : view���
				popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//4.��ȡ��Ŀ��λ��,��������ʾ����Ӧ����Ŀ
				int[] location = new int[2];//����x��y���������
				view.getLocationInWindow(location);//��ȡ��Ŀx��y������,ͬʱ���浽int[]
				//��ȡx��y������
				int x = location[0];
				int y = location[1];
				//parent : Ҫ�������Ǹ��ؼ���
				//gravity,x,y : ����popuwindow��ʾ��λ��
				//50 px ,dp  dp -->px
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x+DensityUtil.dip2qx(getApplicationContext(), 50), y);
//				��popuwindow���ñ���
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

				
//				����Ҫ��ִ��,ִ�еĿؼ������б���,�������ǻ��ڱ���������һЩ����,û�б����������޷�ִ��,popuwindowĬ����û�����ñ���

						// 6.���ö���
						// ���Ŷ���
						// ǰ�ĸ� :�����ƿؼ���û�б䵽�� ���� 0:û�� 1:�����ؼ�
						// ���ĸ�:���ƿؼ��ǰ��������Ǹ��ؼ����б仯
						// RELATIVE_TO_SELF : ������仯
						// RELATIVE_TO_PARENT : �Ը��ؼ��仯
						ScaleAnimation scaleAnimation = new ScaleAnimation(0,
								1, 0, 1, Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0.5f);
						scaleAnimation.setDuration(500);
						// ���䶯��
						AlphaAnimation alphaAnimation = new AlphaAnimation(
								0.4f, 1.0f);// �ɰ�͸����ɲ�͸��
						alphaAnimation.setDuration(500);
						//��϶���
						//shareInterpolator : �Ƿ�ʹ����ͬ�Ķ����岹��  true:����    false:����ʹ�ø��Ե�
						AnimationSet animationSet = new AnimationSet(true);
						//��Ӷ���
						animationSet.addAnimation(scaleAnimation);
						animationSet.addAnimation(alphaAnimation);
						//ִ�ж���
						contentView.startAnimation(animationSet);
							
			}
			
		});
			
		
	}
	/**
	��������
	 * ��������
	 */
	private void hidePopuwindow() {
		if (popupWindow != null) {
			popupWindow.dismiss();//��������
			popupWindow = null;
		}
	}


	/**
	 * listview���������¼�
	 */
	private void listviewOnscroll() {
		lv_softmanager_application.setOnScrollListener(new OnScrollListener() {
			//����״̬�ı��ʱ�����
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			//������ʱ�����
			//view : listview
			//firstVisibleItem : �����һ����ʾ��Ŀ
			//visibleItemCount : ��ʾ��Ŀ�ܸ���
			//totalItemCount : ��Ŀ���ܸ���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				hidePopuwindow();
				//Ϊnull��ԭ��:listview�ڳ�ʼ����ʱ��ͻ����onScroll����
				if (userappinfo != null && systemappinfo != null) {
					if (firstVisibleItem >= userappinfo.size()+1) {
						tv_softmanager_userorsystem.setText("ϵͳ����("+systemappinfo.size()+")");	
					}else{
						tv_softmanager_userorsystem.setText("�û�����("+userappinfo.size()+")");	
					}
				}
			}
		});
	}

	/**
	 * ��������
	 */
	private void fillData() {
		new MyAsycnTasks() {

			private MyAdapter myAdapter;

			@Override
			public void preTask() {
				// TODO Auto-generated method stub
				loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				if (myAdapter == null) {
					myAdapter = new MyAdapter();
					lv_softmanager_application.setAdapter(myAdapter);
				}else {
					myAdapter.notifyDataSetChanged();
				}
				loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doinBack() {
				list = AppEngine.getAppInfo(getApplicationContext());
				userappinfo = new ArrayList<AppInfo>();
				systemappinfo = new ArrayList<AppInfo>();
			for (AppInfo appInfo : list) {
				if (appInfo.isUser()) {
					userappinfo.add(appInfo);
				}else {
					systemappinfo.add(appInfo);
				}
			}
			}
		}.execute();
	}
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return list.size();
			return userappinfo.size() + systemappinfo.size() +2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				//����û�����(...��)textview
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("�û�����("+userappinfo.size()+")");
				return textView;
			}else if(position == userappinfo.size()+1){
				//���ϵͳ����(....��)textview
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("ϵͳ����("+systemappinfo.size()+")");
				return textView;
			}
			View view;
			ViewHolder viewHolder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			 
			}else {
				view = View.inflate(getApplicationContext(), R.layout.item_softmanager, null);
				 viewHolder =new ViewHolder();
				 viewHolder.iv_itemsoftmanager_icon = 
						 (ImageView) view.findViewById(R.id.iv_itemsoftmanager_icon);
				 viewHolder.tv_softmanager_name = (TextView) view.findViewById(R.id.tv_softmanager_name);
				 viewHolder.tv_softmanager_issd = (TextView) view.findViewById(R.id.tv_softmanager_issd);
				 viewHolder.tv_softmanager_version = (TextView) view.findViewById(R.id.tv_softmanager_version);
				 viewHolder.iv_itemsoftmanager_islock = 
						 (ImageView) view.findViewById(R.id.iv_itemsoftmanager_islock);
				 view.setTag(viewHolder);
			}
//			AppInfo appInfo = list.get(position);
//			��ȡ����
			//1.��ȡӦ�ó������Ϣ
			AppInfo appInfo = null;
			//���ݾ�Ҫ��userappinfo��systemappinfo�л�ȡ
			if (position <= userappinfo.size()) {
				//�û�����
				appInfo = userappinfo.get(position-1);
			}else{
				//ϵͳ����
				appInfo = systemappinfo.get(position - userappinfo.size() - 2);
			}
			
			
			 viewHolder.iv_itemsoftmanager_icon.setImageDrawable(appInfo.getIcon());
			 viewHolder.tv_softmanager_name.setText(appInfo.getName());
			 boolean sd = appInfo.isSD(); 
			 if (sd) {
				 viewHolder.tv_softmanager_issd .setText("SD��");
			}else {
				viewHolder.tv_softmanager_issd .setText("�ֻ��ڴ�");
			}
			 viewHolder.tv_softmanager_version.setText(appInfo.getVersionName());
				//�ж�Ӧ�ó����Ǽ������ǽ���
				if (watchDogDao.queryLockApp(appInfo.getPackageName())) {
					//����
					viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.lock);
				}else{
					//����
					viewHolder.iv_itemsoftmanager_islock.setImageResource(R.drawable.unlock);
				}
			return view;
		}
		
	}
	static class ViewHolder{
		ImageView iv_itemsoftmanager_icon,iv_itemsoftmanager_islock;
		TextView tv_softmanager_name,tv_softmanager_issd,tv_softmanager_version;
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		hidePopuwindow();
	}


	@Override
	public void onClick(View v) {
		//�жϵ���ǰ�����ť
		//getId() : ��ȡ�����ť��id
		switch (v.getId()) {
		case R.id.ll_popuwindow_uninstall:
			System.out.println("ж��");
			uninstall();
			break;
		case R.id.ll_popuwindow_start:
			System.out.println("����");
			start();
			break;
		case R.id.ll_popuwindow_share:
			System.out.println("����");
			share();
			break;
		case R.id.ll_popuwindow_detail:
			System.out.println("����");
			detail();
			break;
		}
		hidePopuwindow();
	}


	/**
	 * share
	 */
	private void share() {
		/**
		 *  Intent 
			{ 
				act=android.intent.action.SEND 
				typ=text/plain 
				flg=0x3000000 
				cmp=com.android.mms/.ui.ComposeMessageActivity (has extras)   intent�а�����Ϣ
			} from pid 228
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "����һ����ţx���"+appInfo.getName()+",���ص�ַ:www.baidu.com,�Լ�ȥ��");
		startActivity(intent);
	}


	/**
	 * ж��
	 */
	private void uninstall() {
		/**
		 * <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
            </intent-filter>
		 */
		//�ж��Ƿ���ϵͳ����
		if (appInfo.isUser()) {
			//�ж��Ƿ��������Լ���Ӧ��,�ǲ���ж��
			if (!appInfo.getPackageName().equals(getPackageName())) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+appInfo.getPackageName()));//tel:110
				startActivityForResult(intent,0);
			}else{
				Toast.makeText(getApplicationContext(), "�������,�ž���ɱ", 0).show();
			}
		}else{
			Toast.makeText(getApplicationContext(), "Ҫ��ж��ϵͳ����,��root��", 0).show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}
	/**
	 * ����
	 */
	private void start() {
		PackageManager pm = getPackageManager();
		//��ȡӦ�ó����������ͼ
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if (intent!=null) {
			startActivity(intent);
		}else{
			Toast.makeText(getApplicationContext(), "ϵͳ���ĳ���,�޷�����", 0).show();
		}
	}

	/**
	 *����
	 */
	private void detail() {
		/**
		 *  Intent 
			{ 
			act=android.settings.APPLICATION_DETAILS_SETTINGS    action
			dat=package:com.example.android.apis   data
			cmp=com.android.settings/.applications.InstalledAppDetails 
			} from pid 228
		 */
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
		startActivity(intent);
	}

}
