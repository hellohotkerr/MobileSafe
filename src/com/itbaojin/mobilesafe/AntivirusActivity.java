package com.itbaojin.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itbaojin.mobilesafe.db.dao.AntiVirusDao;
import com.itbaojinmobilesafe.utils.Md5Util;

public class AntivirusActivity extends Activity {
	private ImageView iv_antivirus_scanner;
	private ProgressBar pb_antivirus_progressbar;
	private TextView tv_antivirus_text;
	private LinearLayout ll_antivirus_safeapks;
	//存放病毒应用的包名
	private List<String> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		list = new ArrayList<String>();
		iv_antivirus_scanner = (ImageView) findViewById(R.id.iv_antivirus_scanner);
		pb_antivirus_progressbar = (ProgressBar) findViewById(R.id.pb_antivirus_progressbar);
		tv_antivirus_text = (TextView) findViewById(R.id.tv_antivirus_text);
		ll_antivirus_safeapks = (LinearLayout) findViewById(R.id.ll_antivirus_safeapks);
		
		//设置旋转动画
		//fromDegrees : 旋转开始的角度
		//toDegrees : 结束的角度
		//后面四个:是以自身变化还是以父控件变化
		//Animation.RELATIVE_TO_SELF : 以自身旋转
		//Animation.RELATIVE_TO_PARENT : 以父控件旋转
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);//持续时间
		rotateAnimation.setRepeatCount(Animation.INFINITE);//设置旋转的次数,INFINITE:一直旋转
		
		//设置动画插补器
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		rotateAnimation.setInterpolator(linearInterpolator);
		iv_antivirus_scanner.startAnimation(rotateAnimation);
		
		//扫描程序,设置进度条进度
		scanner();
	}
	/**
	 * 扫描程序
	 */
	private void scanner() {
		//1.获取包的管理者
		final PackageManager pm = getPackageManager();
		tv_antivirus_text.setText("正在初始化64核杀毒引擎....");
		new Thread(){
			public void run() {
				//延迟一秒中扫描程序
				SystemClock.sleep(100);
				//2.获取所有安装应用程序信息
				List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
				
				//3.1设置进度条最大进度
				pb_antivirus_progressbar.setMax(installedPackages.size());
				//3.2设置当前进度
				int count=0;
				for (final PackageInfo packageInfo : installedPackages) {
					SystemClock.sleep(100);
					//3.设置进度条的最大进度和当前进度
					count++;
					pb_antivirus_progressbar.setProgress(count);
					
					//4.设置扫描显示软件名称
					final String name = packageInfo.applicationInfo.loadLabel(pm).toString();
					//在ui线程(主线程)进行操作
					runOnUiThread(new Runnable() {//封装了handler
						
						@Override
						public void run() {
							tv_antivirus_text.setText("正在扫描:"+name);
							
							//7.获取应用程序的签名,并进行md5加密
							Signature[] signatures= packageInfo.signatures;//获取应用程序的签名信息,获取签名的数组
							String charsString = signatures[0].toCharsString();
							//对签名信息进行MD5加密
							String signature = Md5Util.encode(charsString);
							System.out.println(name+"  : "+signature);
							
							//8.查询MD5值是否在病毒数据库中
							boolean b = AntiVirusDao.queryAntiVirus(getApplicationContext(), signature);
							//6.展示扫描软件的名称信息
							TextView textView = new TextView(getApplicationContext());
							//9.根据返回的值将病毒标示出来
							if (b) {
								//有病毒
								textView.setTextColor(Color.RED);
								//将病毒的包名添加到list集合
								list.add(packageInfo.packageName);
							}else{
								//没有病毒
								textView.setTextColor(Color.BLACK);
							}
							textView.setText(name);
							//textview添加到线性布局中
							//ll_antivirus_safeapks.addView(textView);
							ll_antivirus_safeapks.addView(textView, 0);//index:将textview添加到线性布局的哪个位置
						}
					});
				};
				//5.扫描完成,显示扫描完成信息,同时旋转动画停止
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						//10.根据是否有病毒进行显示操作
						if (list.size() > 0) {
							//有病毒
							tv_antivirus_text.setText("扫描完成,发现"+list.size()+"个病毒");
							//卸载病毒应用
							AlertDialog.Builder builder = new Builder(AntivirusActivity.this);
							builder.setTitle("提醒!发现"+list.size()+"个病毒");
							builder.setIcon(R.drawable.ic_launcher);
							builder.setMessage("是否卸载病毒应用!");
							builder.setPositiveButton("卸载", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//卸载操作
									for (String packagname : list) {
										Intent intent = new Intent();
										intent.setAction("android.intent.action.DELETE");
										intent.addCategory("android.intent.category.DEFAULT");
										intent.setData(Uri.parse("package:"+packagname));
										startActivity(intent);
									}
									dialog.dismiss();
								}
							});
							builder.setNegativeButton("取消", null);
							builder.show();
						}else{
							//设置显示信息以及停止动画
							tv_antivirus_text.setText("扫描完成,未发现病毒");
						}
						iv_antivirus_scanner.clearAnimation();//移出动画
					}
				});
			};
		}.start();
	}
	
	
	
	
}
