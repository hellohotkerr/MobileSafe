package com.itbaojin.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itbaojinmobilesafe.utils.ConstantValue;
import com.itbaojinmobilesafe.utils.SpUtils;
import com.itbaojinmobilesafe.utils.StreamTools;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.IOUtils;

public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 0;
	protected static final int UPDATE_VERSION = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NEWWORK_ERROE = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String mVersionDes;
	private String mDownloadUrl;
	private int mLocalVersionCode;
	private RelativeLayout rl_root;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		initUI();
		initData();
		//��ʼ������
		initAnimation();
		shortCut();
		copyDb("address.db");
		copyDb("antivirus.db");

	}
	private void copyDb	(final String dbname) {
		// �������ݿ�
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(getFilesDir(), dbname);
				// �ж��ļ��Ƿ����
				if (!file.exists()) {
					// ��assetsĿ¼�н����ݿ��ȡ����
					// 1.��ȡassets�Ĺ�����
					AssetManager am = getAssets();
					InputStream in = null;
					FileOutputStream out = null;
					try {
						// 2.��ȡ���ݿ�
						in = am.open(dbname);
						// д����
						// getCacheDir : ��ȡ�����·��
						// getFilesDir : ��ȡ�ļ���·��
						out = new FileOutputStream(file);
						// 3.��д����s
						// ���û�����
						byte[] b = new byte[1024];
						int len = -1;
						while ((len = in.read(b)) != -1) {
							out.write(b, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						// in.close();
						// out.close();
						IOUtils.closeQuietly(in);
						IOUtils.closeQuietly(out);
					}
				}

			}
		}).start();
	}
	private void shortCut() {
		if (sp.getBoolean("firstshortcut", true)) {
			// �����淢��һ���㲥
			Intent intent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			// ��������
			// ���ÿ�ݷ�ʽ����
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ���ʿ");
			// ���ÿ�ݷ�ʽ��ͼ��
			Bitmap value = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, value);
			// ���ÿ�ݷ�ʽִ�еĲ���
			Intent intent2 = new Intent();
			intent2.setAction("com.itbaojin.mobliesafe.home");
			intent2.addCategory("android.intent.category.DEFAULT");
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
			sendBroadcast(intent);
			
			//�����Ѿ�������ݷ�ʽ��״̬
			Editor edit = sp.edit();
			edit.putBoolean("firstshortcut", false);
			edit.commit();
		}
	}

	

	

	private void initAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.startAnimation(alphaAnimation);
		
	}

	/**
	 * ��ʼ��UI���� 	alt+shift+j
	 */
	public void initUI(){
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}
	
	/**
	 * ��ȡ���ݷ���
	 */
	/**
	 * 
	 */
	private void initData() {
		//1,Ӧ�ð汾����
		tv_splash_version.setText("�汾�ţ�" + getVersionName());
		//���(���ذ汾�źͷ������汾�űȶ�)�Ƿ��и���,����и���,��ʾ�û�����(member)
		//2,��ȡ���ذ汾��
		mLocalVersionCode = getVersionCode();
		//3,��ȡ�������汾��(�ͻ��˷�����,����˸���Ӧ,(json,xml))
				//http://www.oxxx.com/update74.json?key=value  ����200 ����ɹ�,���ķ�ʽ�����ݶ�ȡ����
				//json�����ݰ���:
				/* ���°汾�İ汾����
				 * �°汾��������Ϣ
				 * �������汾��
				 * �°汾apk���ص�ַ*/
		if (SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
			
			checkUpdate();
		}else {
//			enterHome();
			//��Ϣ����
//			handler.sendMessageDelayed(msg, 4000);
			//�ڷ�����Ϣ4��󣬴������״̬���Ӧ�ĳ���
			handler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
	
		
		
		
	}
	
	
	
	private int getVersionCode() {
		//���������ֻ���APK
		PackageManager pm = getPackageManager();
		
		try {
			//�õ�APK�Ĺ����嵥�ļ�,��0�����ȡ������Ϣ
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private void enterHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		//�رյ�ǰҳ��
		finish();
	}
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_VERSION://��ʾ�����ĶԻ���
				Log.i(TAG,"��ʾ�����ĶԻ���");
				showUodateDialog();
				
				break;
			case ENTER_HOME:
				enterHome();
				break;
			case URL_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(),"URL����",0).show();
				break;
			case NEWWORK_ERROE:

				enterHome();
				Toast.makeText(getApplicationContext(),"NETWORK����",0).show();
				break;
			case JSON_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(),"JSON����",0).show();
				break;

			default:
				break;
			}
		}

	};
	

	private void checkUpdate() {
		new Thread(new Runnable(){		
			
			@Override
			public void run() {
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL(getString(R.string.serveurl));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					//Ĭ�Ͼ���get����ʽ,
					conn.setConnectTimeout(4000);
					conn.setReadTimeout(4000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if (code == 200) {
						//�����ɹ� 
						InputStream is = conn.getInputStream();
						//����ת����String����
						String result = StreamTools.readFromStream(is);
						Log.i(TAG,"�����ɹ���"+result);
						//json����
						JSONObject jsonObject = new JSONObject(result);
						//�õ��������汾��Ϣ
						String versionName = (String) jsonObject.get("versionName");
						 mVersionDes = (String) jsonObject.get("versionDes");
						 String versionCode = jsonObject.getString("versionCode");
						 mDownloadUrl = (String) jsonObject.get("downloadUrl");
						 
						//��־��ӡ	
							Log.i(TAG, versionName);
							Log.i(TAG, mVersionDes);
							Log.i(TAG, versionCode);
							Log.i(TAG, mDownloadUrl);
							
						 //У���Ƿ����°汾
						 if (mLocalVersionCode<Integer.parseInt(versionCode)) {
							//��ʾ�û�����,�����Ի���(UI),��Ϣ����
								msg.what = UPDATE_VERSION;
							
						}else{
							//����Ӧ�ó���������
							msg.what = ENTER_HOME;
						}
						
					}
				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
				
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = NEWWORK_ERROE;
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
				}finally{
					long endTime = System.currentTimeMillis();
					long dtime = endTime - startTime;
					if (dtime < 2000) {
						try {
							Thread.sleep(2000 - dtime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			}
			
		}).start();
	}

	/**
	 * �����Ի�����ʾ�û�����
	 */
	protected void showUodateDialog() {
		//�Ի���,��������activity���ڵ�
		Builder builder = new AlertDialog.Builder(this);
		//�������Ͻ�ͼ��
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("�汾����");
		//������������
		builder.setMessage(mVersionDes);
		//������ť����������
		builder.setPositiveButton("��������", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ����apk,apk���ص�ַdownloadUrl
				downloadApk();
				
			}
		});
		//������ť���Ժ���˵
		builder.setNegativeButton("�Ժ���˵", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ȡ���Ի��򣬽�����ҳ��
				enterHome();
			}
		});
		//���ȡ���¼�����
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
				dialog.dismiss();
				
			}
		});
		builder.show();
	}

	protected void downloadApk() {
		// apk���ص�ַ������aok����·��
		//�ж�sd���Ƿ���ã��Ƿ������
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		//��ȡsd��·��     ��File.separator�鿴/
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+
					File.separator+"MobileSafe1.apk";
		//3,��������,��ȡapk,���ҷ��õ�ָ��·��
			HttpUtils httpUtils = new HttpUtils();
		//4,��������,���ݲ���(���ص�ַ,����Ӧ�÷���λ��)
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					//���سɹ�(���ع���ķ�����sd����apk)
					Log.i(TAG, "���سɹ�");
					File file = responseInfo.result;
					//��ʾ�û���װ
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// ����ʧ��
					Log.i(TAG, "����ʧ��");
					
				}
				//�տ�ʼ���صķ���
				@Override
				public void onStart() {
					Log.i(TAG, "�ոտ�ʼ����");
					super.onStart();
				}
				//���ع����еķ���(����apk�ܴ�С,��ǰ������λ��,�Ƿ���������)
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					Log.i(TAG, "������........");
					Log.i(TAG, "total = "+total);
					Log.i(TAG, "current = "+current);
					super.onLoading(total, current, isUploading);
				}
			});
			
		}
		
	}

	/**��װ��Ӧapk
	 * @param file	��װ�ļ�
	 */
	protected void installApk(File file) {
		//ϵͳӦ�ý��棬Դ�룬��װapk���
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
//		//�ļ���Ϊ����Դ
//		intent.setData(Uri.fromFile(file));
//		//���ð�װ����
//		intent.setType();
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//		startActivity(intent);
		startActivityForResult(intent, 0);
	}
	//����һ��activity��,���ؽ�����õķ���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}
	private String getVersionName(){
		//���������ֻ���APK
		PackageManager pm = getPackageManager();
		
		try {
			//�õ�APK�Ĺ����嵥�ļ�,��0�����ȡ������Ϣ
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
