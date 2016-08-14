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
	//�����Ƿ���ʾϵͳ���̵ı�ʶ
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
		tv_taskmanage_processes.setText("�����н���:\n" + processCount + "��");
		// ��ȡʣ��,���ڴ�'
		long availableRam = TaskUtil.getAvailableRam(getApplicationContext());
		// ����ת��
		String availaRam = Formatter.formatFileSize(getApplicationContext(),
				availableRam);
		// ��ȡ���ڴ�
		// ���ݲ�ͬ��sdk��ȥ���ò�ͬ�ķ���
		// 1.��ȡ��ǰ��sdk�汾
		int sdk = android.os.Build.VERSION.SDK_INT;
		long totalRam;
		if (sdk >= 16) {
			totalRam = TaskUtil.getTotalRam(getApplicationContext());
		} else {
			totalRam = TaskUtil.getTotalRam();
		}
		// ����ת��
		String totRam = Formatter.formatFileSize(getApplicationContext(),
				totalRam);
		tv_taskmanage_freeandtotalram.setText("ʣ��/���ڴ�:\n" + availaRam + "/"
				+ totRam);
		
		fillData();
		listviewItemClick();
	}
	/**
	 * listview��Ŀ����¼�
	 */
	private void listviewItemClick() {
		lv_taskmanager_process.setOnItemClickListener(new OnItemClickListener() {
			//view : ��Ŀ��view����
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//��̬�ı�checkbox״̬
				//1.�����û������ϵͳ����(...��)��������
				if (position == 0 || position == userappinfo.size()+1) {
					return;
				}
				//2.��ȡ��Ŀ����Ӧ��Ӧ�ó������Ϣ
				//���ݾ�Ҫ��userappinfo��systemappinfo�л�ȡ
				if (position <= userappinfo.size()) {
					//�û�����
					taskInfo = userappinfo.get(position-1);
				}else{
					//ϵͳ����
					taskInfo = systemappinfo.get(position - userappinfo.size() - 2);
				}
				//3.����֮ǰ�����checkbox��״̬���õ��֮���״̬,ԭ��ѡ��,���֮��ѡ��
				if (taskInfo.isChecked()) {
					taskInfo.setChecked(false);
				}else{
					//����ǵ�ǰӦ�ò������ó�true
					if (!taskInfo.getPackageName().equals(getPackageName())) {
						taskInfo.setChecked(true);
					}
				}
				//4.���½���
				//myadapter.notifyDataSetChanged();
				//ֻ���µ������Ŀ
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				viewHolder.cb_itemtaskmanager_ischecked.setChecked(taskInfo.isChecked());
			}
		});
	}

	/**
	 * ��������
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
//			��ȡ����
			//1.��ȡӦ�ó������Ϣ
			TaskInfo taskInfo = null;
			//���ݾ�Ҫ��userappinfo��systemappinfo�л�ȡ
			if (position <= userappinfo.size()) {
				//�û�����
				taskInfo = userappinfo.get(position-1);
			}else{
				//ϵͳ����
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
			 //����ת��
			 String formatFileSize = Formatter.formatFileSize(getApplicationContext(), ramSize);
			 
			 viewHolder.tv_itemtaskmanager_rom .setText("�ڴ�ռ�ã�"+formatFileSize);
			//��Ϊcheckbox��״̬�����һ����,����һ��Ҫ��̬�޸ĵĿؼ���״̬,�������ȥ����,���ǽ�״̬���浽bean����,��ÿ�θ���ʹ�ÿؼ���ʱ��
			//����ÿ����Ŀ��Ӧ��bean���󱣴��״̬,�����ÿؼ���ʾ����Ӧ��״̬
			 if (taskInfo.isChecked()) {
					viewHolder.cb_itemtaskmanager_ischecked.setChecked(true);
				}else{
					viewHolder.cb_itemtaskmanager_ischecked.setChecked(false);
				}
			//�ж���������ǵ�Ӧ�ó���,�Ͱ�checkbox����,���ǵĻ���ʾ,��getview����if������else
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
	 * ȫѡ
	 * @param v
	 */
	public void all(View v){
		//�û�����
		for (int i = 0; i < userappinfo.size(); i++) {
				if (!userappinfo.get(i).getPackageName().equals(getPackageName())) {
					userappinfo.get(i).setChecked(true);
				}
		}
		//ϵͳ����
		for (int i = 0; i < systemappinfo.size(); i++) {
			systemappinfo.get(i).setChecked(true);
		}
		//���½���
		myAdapter.notifyDataSetChanged();
	}
	/**
	 * ȡ��
	 * @param v
	 */
	public void cancel(View v){
		//�û�����
		for (int i = 0; i < userappinfo.size(); i++) {
			userappinfo.get(i).setChecked(false);
		}
		//ϵͳ����
		for (int i = 0; i < systemappinfo.size(); i++) {
			systemappinfo.get(i).setChecked(false);
		}
		//���½���
		myAdapter.notifyDataSetChanged();
	}
	/**
	 * ����
	 * @param v
	 */
	public void clear(View v){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//����ɱ��������Ϣ�ļ���
		List<TaskInfo> deleteTaskInfos = new ArrayList<TaskInfo>();
		
		for (int i = 0; i < userappinfo.size(); i++) {
			if (userappinfo.get(i).isChecked()) {
				//ɱ������
				//packageName : ���̵İ���
				//ɱ����̨����
				am.killBackgroundProcesses(userappinfo.get(i).getPackageName());
				deleteTaskInfos.add(userappinfo.get(i));//��ɱ���Ľ�����Ϣ����ļ�����
			}
		}
		//ϵͳ����
		for (int i = 0; i < systemappinfo.size(); i++) {
			if (systemappinfo.get(i).isChecked()) {
				//ɱ������
				//packageName : ���̵İ���
				//ɱ����̨����
				am.killBackgroundProcesses(systemappinfo.get(i).getPackageName());
				deleteTaskInfos.add(systemappinfo.get(i));//��ɱ���Ľ�����Ϣ����ļ�����
			}
		}
		long memory = 0;
		//����deleteTaskInfos,�ֱ��userappinfo��systemappinfo��ɾ��deleteTaskInfos�е�����
		for (TaskInfo taskInfo : deleteTaskInfos) {
			if (taskInfo.isUser()) {
				userappinfo.remove(taskInfo);
			}else{
				systemappinfo.remove(taskInfo);
				memory+=taskInfo.getRamSize();
			}
		}
		//����ת��
		String deleteSize = Formatter.formatFileSize(getApplicationContext(), memory);
		ToastUtil.show(getApplicationContext(), "������"+deleteTaskInfos.size()+"�����̣��ͷ�"+deleteSize+"�ڴ�ռ�");
		// ���������еĽ��̸����Լ�ʣ�����ڴ�
		processCount = processCount - deleteTaskInfos.size();
		tv_taskmanage_processes.setText("�����н���:\n" + processCount + "��");
		
		// ����ʣ�����ڴ�,���»�ȡʣ�����ڴ�
		// ��ȡʣ��,���ڴ�'
		long availableRam = TaskUtil.getAvailableRam(getApplicationContext());
		// ����ת��
		String availaRam = Formatter.formatFileSize(getApplicationContext(),
				availableRam);
		// ��ȡ���ڴ�
		// ���ݲ�ͬ��sdk��ȥ���ò�ͬ�ķ���
		// 1.��ȡ��ǰ��sdk�汾
		int sdk = android.os.Build.VERSION.SDK_INT;
		long totalRam;
		if (sdk >= 16) {
			totalRam = TaskUtil.getTotalRam(getApplicationContext());
		} else {
			totalRam = TaskUtil.getTotalRam();
		}
		// ����ת��
		String totRam = Formatter.formatFileSize(getApplicationContext(),
				totalRam);
		tv_taskmanage_freeandtotalram.setText("ʣ��/���ڴ�:\n" + availaRam + "/"
				+ totRam);

		//Ϊ�´����������׼��
		deleteTaskInfos.clear();
		deleteTaskInfos=null;
		//���½���
		myAdapter.notifyDataSetChanged();
	}
	/**
	 * ����
	 * @param v
	 */
	public void setting(View v){
		//true ��Ϊfalse  false��Ϊtrue
		isshowSystem = !isshowSystem;
		//���½���
		myAdapter.notifyDataSetChanged();
	}


}
