package com.itbaojin.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itbaojin.mobilesafe.bean.BlackNumInfo;
import com.itbaojin.mobilesafe.db.dao.BlackNumDao;
import com.itbaojinmobilesafe.utils.MyAsycnTasks;
import com.itbaojinmobilesafe.utils.ToastUtil;

public class CallSmSafeActivity extends Activity {
	private ListView lv_callsmssafe_blacknums;
	private ProgressBar loading;
	private BlackNumDao blackNumDao;
	private List<BlackNumInfo> list;
	private MyAdapter myAdapter;
	private AlertDialog dialog;
	private final int MaxNum = 20;
	private int startIndex = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsmssafe);
		blackNumDao = new BlackNumDao(getApplicationContext());
	
		lv_callsmssafe_blacknums = (ListView) findViewById(R.id.lv_callsmssafe_blacknums);
		loading = (ProgressBar) findViewById(R.id.loading);
		fillData();
		//listview滑动监听事件
		lv_callsmssafe_blacknums.setOnScrollListener(new OnScrollListener() {
			// 滑动状态改变时调用的方法
			//view:listview
			//scrollState:滑动状态
			//SCROLL_STATE_IDLE : 空闲的状态
			//SCROLL_STATE_TOUCH_SCROLL : 缓慢滑动的状态
			//SCROLL_STATE_FLING : 快速滑动
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//当listview静止的时候判断界面显示的最后一个条目是否是查询数据的最后一个条目,是加载下一波数据,
//				不是用户进行其他操作
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					//获取界面显示最后一个条目
					int position = lv_callsmssafe_blacknums.getLastVisiblePosition();//获取界面显示最后一个条目,返回的时候条目的位置
					//判断是否是查询数据的最后一个数据  20   0-19
					if (position == list.size()-1) {
						//加载下一波数据
						//更新查询的其实位置   0-19    20-39
						startIndex+=MaxNum;
						fillData();
					}
				}	
			}
			// 滑动时调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				
			}
		});
		
	}

	private void fillData() {
		new MyAsycnTasks() {
			@Override
			public void preTask() {
				loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				if (myAdapter == null) {
					myAdapter = new MyAdapter();
					lv_callsmssafe_blacknums.setAdapter(myAdapter);
				}else{
					myAdapter.notifyDataSetChanged();
				}
				loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doinBack() {
				if (list == null) {
					//1.2.3    4.5.6
					list = blackNumDao.getPartBlackNum(MaxNum,startIndex);
				}else{
					//addAll : 将一个集合整合到另一个集合
					//A [1.2.3] B[4.5.6]
					//A.addAll(B)  A [1.2.3.4.5.6]
					list.addAll(blackNumDao.getPartBlackNum(MaxNum,startIndex));
				}
			}
		}.execute();
	}
	public class MyAdapter extends BaseAdapter{
		// 设置条目的个数
		@Override
		public int getCount() {
			
			return list.size();
		}
		// 获取条目对应的数据
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}
		// 获取条目的id
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		// 设置条目显示的样式
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			//根据条目的位置获取对应的bean对象
			final BlackNumInfo blackNumInfo = list.get(position);
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(), R.layout.item_callsmssafe, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_callsmssafe_blacknum = 
						(TextView) view.findViewById(R.id.tv_callsmssafe_blacknum);
				viewHolder.tv_callsmssafe_mode = 
						(TextView) view.findViewById(R.id.tv_callsmssafe_mode);
				viewHolder.iv_callsmssafe_delete =
						(ImageView) view.findViewById(R.id.iv_callsmssafe_delete);
				view.setTag(viewHolder);
						
			}else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
//			if (convertView == null) {
//				convertView = View.inflate(getApplicationContext(), R.layout.item_callsmssafe, null);
//			}
//			
//			TextView tv_callsmssafe_blacknum =
//					(TextView) view.findViewById(R.id.tv_callsmssafe_blacknum);
//			TextView tv_callsmssafe_mode =
//					(TextView) view.findViewById(R.id.tv_callsmssafe_mode);
//			ImageView iv_callsmssafe_delete =
//					(ImageView) view.findViewById(R.id.iv_callsmssafe_delete);
			viewHolder.tv_callsmssafe_blacknum.setText(blackNumInfo.getBlackNum());
			int mode = blackNumInfo.getMode();
			switch (mode) {
			case BlackNumDao.CALL:
				viewHolder.tv_callsmssafe_mode.setText("电话拦截");
				break;
			case BlackNumDao.SMS:
				viewHolder.tv_callsmssafe_mode.setText("短信拦截");
				break;
			case BlackNumDao.ALL:
				viewHolder.tv_callsmssafe_mode.setText("全部拦截");
				break;
			}
			viewHolder.iv_callsmssafe_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmSafeActivity.this);
					builder.setMessage("您确认要删除黑名单号码"+blackNumInfo.getBlackNum()+"吗?");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							blackNumDao.deleteBlackNum(blackNumInfo.getBlackNum());
							list.remove(position);
							myAdapter.notifyDataSetChanged();//更新界面
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
			return view;
		}
	}
	static class ViewHolder{
		TextView tv_callsmssafe_blacknum,tv_callsmssafe_mode;
		ImageView iv_callsmssafe_delete;
	}
	public void addBlackNum(View view){
		AlertDialog.Builder builder = new Builder(this);
	
		View view2 = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknum, null);
		final EditText et_addblacknum_blacknum = (EditText) view2.findViewById(R.id.et_addblacknum_blacknum);
		final RadioGroup rg_addblacknum_modes = (RadioGroup) view2.findViewById(R.id.rg_addblacknum_modes);
		Button btn_ok = (Button) view2.findViewById(R.id.btn_ok);
		Button btn_cancle = (Button) view2.findViewById(R.id.btn_cancle);
		
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String blacknum = et_addblacknum_blacknum.getText().toString().trim();
				if (TextUtils.isEmpty(blacknum)) {
					ToastUtil.show(getApplicationContext(), "请输入黑名单号码");
					return;
				}
				int mode = -1;
				int checkedRadioButtonId = rg_addblacknum_modes
						.getCheckedRadioButtonId();
				switch (checkedRadioButtonId) {
				case R.id.rb_addblacknum_tel:
					mode = BlackNumDao.CALL;
					break;
				case R.id.rb_addblacknum_sms:
					mode = BlackNumDao.SMS;
					break;
				case R.id.rb_addblacknum_all:
					mode = BlackNumDao.ALL;
					break;
				}
				blackNumDao.addBlackNum(blacknum, mode);
				
//				list.add(new BlackNumInfo(blacknum, mode));
				list.add(0, new BlackNumInfo(blacknum, mode));
				
				myAdapter.notifyDataSetChanged();
				
				dialog.dismiss();
				
				
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		builder.setView(view2);
        dialog = builder.create();
		dialog.show();
	}
}
