package com.itbaojin.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itbaojin.mobilesafe.bean.TaskInfo;
import com.itbaojin.mobilesafe.engine.TaskEngine;
import com.itbaojinmobilesafe.utils.MyAsycnTasks;
import com.itbaojinmobilesafe.utils.TaskUtil;
import com.itbaojinmobilesafe.utils.ToastUtil;

public class TaskManagerActivity extends Activity {
	private ListView lv_taskmanager_process;
	private ProgressBar loading;
	private List<TaskInfo> list;
	private List<TaskInfo> userappinfo;
	private List<TaskInfo> systemappinfo;
	private MyAdapter myAdapter;
	private TaskInfo taskInfo;
	private TextView tv_taskmanage_processes;
	private TextView tv_taskmanage_freeandtotalram;
	private int processCount;
	//设置是否显示系统进程的标识
	private boolean isshowSystem = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager);
		lv_taskmanager_process = (ListView) findViewById(R.id.lv_taskmanager_process);
		loading = (ProgressBar) findViewById(R.id.loading);
		tv_taskmanage_processes = (TextView) findViewById(R.id.tv_taskmanage_processes);
		tv_taskmanage_freeandtotalram = (TextView) findViewById(R.id.tv_taskmanage_freeandtotalram);

		processCount = TaskUtil.getProcessCount(getApplicationContext());
		tv_taskmanage_processes.setText("运行中进程:\n" + processCount + "个");
		// 获取剩余,总内存'
		long availableRam = TaskUtil.getAvailableRam(getApplicationContext());
		// 数据转化
		String availaRam = Formatter.formatFileSize(getApplicationContext(),
				availableRam);
		// 获取总内存
		// 根据不同的sdk版去调用不同的方法
		// 1.获取当前的sdk版本
		int sdk = android.os.Build.VERSION.SDK_INT;
		long totalRam;
		if (sdk >= 16) {
			totalRam = TaskUtil.getTotalRam(getApplicationContext());
		} else {
			totalRam = TaskUtil.getTotalRam();
		}
		// 数据转化
		String totRam = Formatter.formatFileSize(getApplicationContext(),
				totalRam);
		tv_taskmanage_freeandtotalram.setText("剩余/总内存:\n" + availaRam + "/"
				+ totRam);
		
		fillData();
		listviewItemClick();
	}
	/**
	 * listview条目点击事件
	 */
	private void listviewItemClick() {
		lv_taskmanager_process.setOnItemClickListener(new OnItemClickListener() {
			//view : 条目的view对象
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//动态改变checkbox状态
				//1.屏蔽用户程序和系统程序(...个)弹出气泡
				if (position == 0 || position == userappinfo.size()+1) {
					return;
				}
				//2.获取条目所对应的应用程序的信息
				//数据就要从userappinfo和systemappinfo中获取
				if (position <= userappinfo.size()) {
					//用户程序
					taskInfo = userappinfo.get(position-1);
				}else{
					//系统程序
					taskInfo = systemappinfo.get(position - userappinfo.size() - 2);
				}
				//3.根据之前保存的checkbox的状态设置点击之后的状态,原先选中,点击之后不选中
				if (taskInfo.isChecked()) {
					taskInfo.setChecked(false);
				}else{
					//如果是当前应用不能设置成true
					if (!taskInfo.getPackageName().equals(getPackageName())) {
						taskInfo.setChecked(true);
					}
				}
				//4.更新界面
				//myadapter.notifyDataSetChanged();
				//只更新点击的条目
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				viewHolder.cb_itemtaskmanager_ischecked.setChecked(taskInfo.isChecked());
			}
		});
	}

	/**
	 * 加载数据
	 */
	private void fillData() {
		new MyAsycnTasks() {
			@Override
			public void preTask() {
				// TODO Auto-generated method stub
				loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				if (myAdapter == null) {
					myAdapter = new MyAdapter();
					lv_taskmanager_process.setAdapter(myAdapter);
				}else {
					myAdapter.notifyDataSetChanged();
				}
				loading.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void doinBack() {
				list = TaskEngine.getTaskAllInfo(getApplicationContext());
				userappinfo = new ArrayList<TaskInfo>();
				systemappinfo = new ArrayList<TaskInfo>();
			for (TaskInfo taskInfo : list) {
				if (taskInfo.isUser()) {
					userappinfo.add(taskInfo);
				}else {
					systemappinfo.add(taskInfo);
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
			return isshowSystem == true ? userappinfo.size() + 1 + systemappinfo.size() +1 : userappinfo.size() +1;
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
				//添加用户程序(...个)textview
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("用户进程("+userappinfo.size()+")");
				return textView;
			}else if(position == userappinfo.size()+1){
				//添加系统程序(....个)textview
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.GRAY);
				textView.setTextColor(Color.WHITE);
				textView.setText("系统进程("+systemappinfo.size()+")");
				return textView;
			}
			View view;
			ViewHolder viewHolder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			 
			}else {
				view = View.inflate(getApplicationContext(), R.layout.item_taskmanager, null);
				 viewHolder =new ViewHolder();
				 viewHolder.iv_itemtaskmanager_icon = 
						 (ImageView) view.findViewById(R.id.iv_itemtaskmanager_icon);
				 viewHolder.tv_itemtaskmanagerr_name = (TextView) view.findViewById(R.id.tv_itemtaskmanagerr_name);
				 viewHolder.tv_itemtaskmanager_rom = (TextView) view.findViewById(R.id.tv_itemtaskmanager_rom);
				 viewHolder.cb_itemtaskmanager_ischecked = (CheckBox) view.findViewById(R.id.cb_itemtaskmanager_ischecked);
				 view.setTag(viewHolder);
			}
//			AppInfo appInfo = list.get(position);
//			获取数据
			//1.获取应用程序的信息
			TaskInfo taskInfo = null;
			//数据就要从userappinfo和systemappinfo中获取
			if (position <= userappinfo.size()) {
				//用户程序
				taskInfo = userappinfo.get(position-1);
			}else{
				//系统程序
				taskInfo = systemappinfo.get(position - userappinfo.size() - 2);
			}
			if (taskInfo.getIcon() == null) {
				 viewHolder.iv_itemtaskmanager_icon.setImageResource(R.drawable.ic_default);
			}else {
				viewHolder.iv_itemtaskmanager_icon.setImageDrawable(taskInfo.getIcon());
			}
			if (TextUtils.isEmpty(taskInfo.getName())) {
				viewHolder.tv_itemtaskmanagerr_name.setText(taskInfo.getPackageName());
			}else {
				viewHolder.tv_itemtaskmanagerr_name.setText(taskInfo.getName());
			}
			 long ramSize = taskInfo.getRamSize();
			 //数据转化
			 String formatFileSize = Formatter.formatFileSize(getApplicationContext(), ramSize);
			 
			 viewHolder.tv_itemtaskmanager_rom .setText("内存占用："+formatFileSize);
			//因为checkbox的状态会跟着一起复用,所以一般要动态修改的控件的状态,不会跟着去复用,而是将状态保存到bean对象,在每次复用使用控件的时候
			//根据每个条目对应的bean对象保存的状态,来设置控件显示的相应的状态
			 if (taskInfo.isChecked()) {
					viewHolder.cb_itemtaskmanager_ischecked.setChecked(true);
				}else{
					viewHolder.cb_itemtaskmanager_ischecked.setChecked(false);
				}
			//判断如果是我们的应用程序,就把checkbox隐藏,不是的话显示,在getview中有if必须有else
				if (taskInfo.getPackageName().equals(getPackageName())) {
					viewHolder.cb_itemtaskmanager_ischecked.setVisibility(View.INVISIBLE);
				}else{
					viewHolder.cb_itemtaskmanager_ischecked.setVisibility(View.VISIBLE);
				}
			return view;
		}
		
	}
	static class ViewHolder{
		ImageView iv_itemtaskmanager_icon;
		TextView tv_itemtaskmanagerr_name,tv_itemtaskmanager_rom;
		CheckBox cb_itemtaskmanager_ischecked;
		
	}
	/**
	 * 全选
	 * @param v
	 */
	public void all(View v){
		//用户进程
		for (int i = 0; i < userappinfo.size(); i++) {
				if (!userappinfo.get(i).getPackageName().equals(getPackageName())) {
					userappinfo.get(i).setChecked(true);
				}
		}
		//系统进程
		for (int i = 0; i < systemappinfo.size(); i++) {
			systemappinfo.get(i).setChecked(true);
		}
		//更新界面
		myAdapter.notifyDataSetChanged();
	}
	/**
	 * 取消
	 * @param v
	 */
	public void cancel(View v){
		//用户进程
		for (int i = 0; i < userappinfo.size(); i++) {
			userappinfo.get(i).setChecked(false);
		}
		//系统进程
		for (int i = 0; i < systemappinfo.size(); i++) {
			systemappinfo.get(i).setChecked(false);
		}
		//更新界面
		myAdapter.notifyDataSetChanged();
	}
	/**
	 * 清理
	 * @param v
	 */
	public void clear(View v){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//保存杀死进程信息的集合
		List<TaskInfo> deleteTaskInfos = new ArrayList<TaskInfo>();
		
		for (int i = 0; i < userappinfo.size(); i++) {
			if (userappinfo.get(i).isChecked()) {
				//杀死进程
				//packageName : 进程的包名
				//杀死后台进程
				am.killBackgroundProcesses(userappinfo.get(i).getPackageName());
				deleteTaskInfos.add(userappinfo.get(i));//将杀死的进程信息保存的集合中
			}
		}
		//系统进程
		for (int i = 0; i < systemappinfo.size(); i++) {
			if (systemappinfo.get(i).isChecked()) {
				//杀死进程
				//packageName : 进程的包名
				//杀死后台进程
				am.killBackgroundProcesses(systemappinfo.get(i).getPackageName());
				deleteTaskInfos.add(systemappinfo.get(i));//将杀死的进程信息保存的集合中
			}
		}
		long memory = 0;
		//遍历deleteTaskInfos,分别从userappinfo和systemappinfo中删除deleteTaskInfos中的数据
		for (TaskInfo taskInfo : deleteTaskInfos) {
			if (taskInfo.isUser()) {
				userappinfo.remove(taskInfo);
			}else{
				systemappinfo.remove(taskInfo);
				memory+=taskInfo.getRamSize();
			}
		}
		//数据转化
		String deleteSize = Formatter.formatFileSize(getApplicationContext(), memory);
		ToastUtil.show(getApplicationContext(), "共清理"+deleteTaskInfos.size()+"个进程，释放"+deleteSize+"内存空间");
		// 更改运行中的进程个数以及剩余总内存
		processCount = processCount - deleteTaskInfos.size();
		tv_taskmanage_processes.setText("运行中进程:\n" + processCount + "个");
		
		// 更改剩余总内存,重新获取剩余总内存
		// 获取剩余,总内存'
		long availableRam = TaskUtil.getAvailableRam(getApplicationContext());
		// 数据转化
		String availaRam = Formatter.formatFileSize(getApplicationContext(),
				availableRam);
		// 获取总内存
		// 根据不同的sdk版去调用不同的方法
		// 1.获取当前的sdk版本
		int sdk = android.os.Build.VERSION.SDK_INT;
		long totalRam;
		if (sdk >= 16) {
			totalRam = TaskUtil.getTotalRam(getApplicationContext());
		} else {
			totalRam = TaskUtil.getTotalRam();
		}
		// 数据转化
		String totRam = Formatter.formatFileSize(getApplicationContext(),
				totalRam);
		tv_taskmanage_freeandtotalram.setText("剩余/总内存:\n" + availaRam + "/"
				+ totRam);

		//为下次清理进程做准备
		deleteTaskInfos.clear();
		deleteTaskInfos=null;
		//更新界面
		myAdapter.notifyDataSetChanged();
	}
	/**
	 * 设置
	 * @param v
	 */
	public void setting(View v){
		//true 改为false  false改为true
		isshowSystem = !isshowSystem;
		//更新界面
		myAdapter.notifyDataSetChanged();
	}


}
