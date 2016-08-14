package com.itbaojin.mobilesafe.fragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itbaojin.mobilesafe.R;

public class CacheFragment extends Fragment {
	private View view;
	private TextView tv_cachefragment_text;
	private ProgressBar pb_cachefragment_progressbar;
	private List<CachInfo> list;
	private Myadapter myadapter;
	//��ʼ������
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	//����fragment�Ĳ���
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		list = new ArrayList<CacheFragment.CachInfo>();
		list.clear();
		//����1:�����ļ�
		//����2:����
		//����3:�Զ�����  ,һ��false
		view = inflater.inflate(R.layout.fragment_cache, container, false);
		tv_cachefragment_text = (TextView) view.findViewById(R.id.tv_cachefragment_text);
		pb_cachefragment_progressbar = (ProgressBar) view.findViewById(R.id.pb_cachefragment_progressbar);
		lv_cachefragment_caches = (ListView) view.findViewById(R.id.lv_cachefragment_caches);
		btn_cachefragment_clear = (Button) view.findViewById(R.id.btn_cachefragment_clear);
		lv_cachefragment_caches.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//��ת������ҳ��
				Intent intent = new Intent();
				intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				intent.setData(Uri.parse("package:"+list.get(position).getPackageName()));
				startActivity(intent);
			}
		});
		return view;
	}
	//���������ʾ����
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		scanner();
	}
	/**
	 * ɨ��
	 */
	private void scanner() {
		pm = getActivity().getPackageManager();
		tv_cachefragment_text.setText("���ڳ�ʼ��128��ɨ������.....");
		new Thread(){
			public void run() {
				SystemClock.sleep(100);
				List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
				//���ý�����������
				pb_cachefragment_progressbar.setMax(installedPackages.size());
				int count=0;
				for (PackageInfo packageInfo : installedPackages) {
					SystemClock.sleep(100);
					//���ý����������Ⱥ͵�ǰ����
					count++;
					pb_cachefragment_progressbar.setProgress(count);
					
					//��ȡ�����С
					
					//�����ȡ����
					try {
						Class<?> loadClass = getActivity().getClass().getClassLoader().loadClass("android.content.pm.PackageManager");
						Method method = loadClass.getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
						//receiver : ���ʵ��,���ز���,�������Ǿ�̬�ı���ָ��
						method.invoke(pm, packageInfo.packageName,mStatsObserver);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//����ɨ����ʾ��Ӧ�õ�����
					final String name = packageInfo.applicationInfo.loadLabel(pm).toString();
					if (getActivity() != null) {
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								tv_cachefragment_text.setText("����ɨ��:"+name);
							}
						});	
					}
				}
				//ɨ�����
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tv_cachefragment_text.setVisibility(View.GONE);
							pb_cachefragment_progressbar.setVisibility(View.GONE);
							myadapter = new Myadapter();
							//listview����adapter
							lv_cachefragment_caches.setAdapter(myadapter);
							if (list.size() > 0) {
								btn_cachefragment_clear.setVisibility(View.VISIBLE);
								//������
								btn_cachefragment_clear.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										//������ʵ��������
										try {
											Class<?> loadClass = getActivity().getClass().getClassLoader().loadClass("android.content.pm.PackageManager");
											//Long.class  Long     TYPE  long
											Method method = loadClass.getDeclaredMethod("freeStorageAndNotify", Long.TYPE,IPackageDataObserver.class);
											method.invoke(pm, Long.MAX_VALUE,new MyIPackageDataObserver());
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										//������
										list.clear();
										//���½���
										myadapter.notifyDataSetChanged();
										//����button��ť
										btn_cachefragment_clear.setVisibility(View.GONE);
									}
								});
							}
						}
					});
				}
			};
		}.start();
	}
	//��ȡ�����С
	IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
        	long cachesize = stats.cacheSize;//�����С
        	/*long codesize = stats.codeSize;//Ӧ�ó���Ĵ�С
        	long datasize = stats.dataSize;//���ݴ�С
*/        	if (cachesize > 0) {
					String cache = Formatter.formatFileSize(getActivity(), cachesize);
					list.add(new CachInfo(stats.packageName, cache));
			}
        	/*String code = Formatter.formatFileSize(getActivity(), codesize);
        	String data = Formatter.formatFileSize(getActivity(), datasize);*/
//        	System.out.println(stats.packageName+"cachesize:"+cache +" codesize:"+code+" datasize:"+data);
        }
	};
	
	private class MyIPackageDataObserver extends IPackageDataObserver.Stub{
		//�������������֮�����
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			
		}
	}
	
	private ListView lv_cachefragment_caches;
	private PackageManager pm;
	private Button btn_cachefragment_clear;
	
	class CachInfo{
		private String packageName;
		private String cachesize;
		public String getPackageName() {
			return packageName;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		public String getCachesize() {
			return cachesize;
		}
		public void setCachesize(String cachesize) {
			this.cachesize = cachesize;
		}
		public CachInfo(String packageName, String cachesize) {
			super();
			this.packageName = packageName;
			this.cachesize = cachesize;
		}
		
	}
	private class Myadapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
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
			
			View cachView;
			ViewHolder viewHolder;
			if (convertView == null) {
				 cachView = View.inflate(getActivity(), R.layout.item_cache, null);
				 viewHolder = new ViewHolder();
				 viewHolder.iv_itemcache_icon = (ImageView) cachView.findViewById(R.id.iv_itemcache_icon);
				 viewHolder.tv_itemcache_name = (TextView) cachView.findViewById(R.id.tv_itemcache_name);
				 viewHolder.tv_itemcache_size = (TextView) cachView.findViewById(R.id.tv_itemcache_size);
				 cachView.setTag(viewHolder);
			}else{
				cachView = convertView;
				viewHolder = (ViewHolder) cachView.getTag();
			}
			//������ʾ����
			CachInfo cachInfo = list.get(position);
			//���ݰ�����ȡapplication��Ϣ
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(cachInfo.getPackageName(), 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				String name = applicationInfo.loadLabel(pm).toString();
				//������ʾ
				 viewHolder.iv_itemcache_icon.setImageDrawable(icon);
				 viewHolder.tv_itemcache_name.setText(name);
				 viewHolder.tv_itemcache_size.setText(cachInfo.getCachesize());
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return cachView;
		}
		
	}
	static class ViewHolder{
		ImageView iv_itemcache_icon;
		TextView tv_itemcache_name,tv_itemcache_size;
	}
	
}
