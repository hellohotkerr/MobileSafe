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
	//��Ų���Ӧ�õİ���
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
		
		//������ת����
		//fromDegrees : ��ת��ʼ�ĽǶ�
		//toDegrees : �����ĽǶ�
		//�����ĸ�:��������仯�����Ը��ؼ��仯
		//Animation.RELATIVE_TO_SELF : ��������ת
		//Animation.RELATIVE_TO_PARENT : �Ը��ؼ���ת
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);//����ʱ��
		rotateAnimation.setRepeatCount(Animation.INFINITE);//������ת�Ĵ���,INFINITE:һֱ��ת
		
		//���ö����岹��
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		rotateAnimation.setInterpolator(linearInterpolator);
		iv_antivirus_scanner.startAnimation(rotateAnimation);
		
		//ɨ�����,���ý���������
		scanner();
	}
	/**
	 * ɨ�����
	 */
	private void scanner() {
		//1.��ȡ���Ĺ�����
		final PackageManager pm = getPackageManager();
		tv_antivirus_text.setText("���ڳ�ʼ��64��ɱ������....");
		new Thread(){
			public void run() {
				//�ӳ�һ����ɨ�����
				SystemClock.sleep(100);
				//2.��ȡ���а�װӦ�ó�����Ϣ
				List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
				
				//3.1���ý�����������
				pb_antivirus_progressbar.setMax(installedPackages.size());
				//3.2���õ�ǰ����
				int count=0;
				for (final PackageInfo packageInfo : installedPackages) {
					SystemClock.sleep(100);
					//3.���ý������������Ⱥ͵�ǰ����
					count++;
					pb_antivirus_progressbar.setProgress(count);
					
					//4.����ɨ����ʾ�������
					final String name = packageInfo.applicationInfo.loadLabel(pm).toString();
					//��ui�߳�(���߳�)���в���
					runOnUiThread(new Runnable() {//��װ��handler
						
						@Override
						public void run() {
							tv_antivirus_text.setText("����ɨ��:"+name);
							
							//7.��ȡӦ�ó����ǩ��,������md5����
							Signature[] signatures= packageInfo.signatures;//��ȡӦ�ó����ǩ����Ϣ,��ȡǩ��������
							String charsString = signatures[0].toCharsString();
							//��ǩ����Ϣ����MD5����
							String signature = Md5Util.encode(charsString);
							System.out.println(name+"  : "+signature);
							
							//8.��ѯMD5ֵ�Ƿ��ڲ������ݿ���
							boolean b = AntiVirusDao.queryAntiVirus(getApplicationContext(), signature);
							//6.չʾɨ�������������Ϣ
							TextView textView = new TextView(getApplicationContext());
							//9.���ݷ��ص�ֵ��������ʾ����
							if (b) {
								//�в���
								textView.setTextColor(Color.RED);
								//�������İ�����ӵ�list����
								list.add(packageInfo.packageName);
							}else{
								//û�в���
								textView.setTextColor(Color.BLACK);
							}
							textView.setText(name);
							//textview��ӵ����Բ�����
							//ll_antivirus_safeapks.addView(textView);
							ll_antivirus_safeapks.addView(textView, 0);//index:��textview��ӵ����Բ��ֵ��ĸ�λ��
						}
					});
				};
				//5.ɨ�����,��ʾɨ�������Ϣ,ͬʱ��ת����ֹͣ
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						//10.�����Ƿ��в���������ʾ����
						if (list.size() > 0) {
							//�в���
							tv_antivirus_text.setText("ɨ�����,����"+list.size()+"������");
							//ж�ز���Ӧ��
							AlertDialog.Builder builder = new Builder(AntivirusActivity.this);
							builder.setTitle("����!����"+list.size()+"������");
							builder.setIcon(R.drawable.ic_launcher);
							builder.setMessage("�Ƿ�ж�ز���Ӧ��!");
							builder.setPositiveButton("ж��", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//ж�ز���
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
							builder.setNegativeButton("ȡ��", null);
							builder.show();
						}else{
							//������ʾ��Ϣ�Լ�ֹͣ����
							tv_antivirus_text.setText("ɨ�����,δ���ֲ���");
						}
						iv_antivirus_scanner.clearAnimation();//�Ƴ�����
					}
				});
			};
		}.start();
	}
	
	
	
	
}
