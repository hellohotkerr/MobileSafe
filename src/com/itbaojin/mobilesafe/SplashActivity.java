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
		//初始化动画
		initAnimation();
		shortCut();
		copyDb("address.db");
		copyDb("antivirus.db");

	}
	private void copyDb	(final String dbname) {
		// 拷贝数据库
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(getFilesDir(), dbname);
				// 判断文件是否存在
				if (!file.exists()) {
					// 从assets目录中将数据库读取出来
					// 1.获取assets的管理者
					AssetManager am = getAssets();
					InputStream in = null;
					FileOutputStream out = null;
					try {
						// 2.读取数据库
						in = am.open(dbname);
						// 写入流
						// getCacheDir : 获取缓存的路径
						// getFilesDir : 获取文件的路径
						out = new FileOutputStream(file);
						// 3.读写操作s
						// 设置缓冲区
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
			// 给桌面发送一个广播
			Intent intent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			// 设置属性
			// 设置快捷方式名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
			// 设置快捷方式的图标
			Bitmap value = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, value);
			// 设置快捷方式执行的操作
			Intent intent2 = new Intent();
			intent2.setAction("com.itbaojin.mobliesafe.home");
			intent2.addCategory("android.intent.category.DEFAULT");
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
			sendBroadcast(intent);
			
			//保存已经创建快捷方式的状态
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
	 * 初始化UI方法 	alt+shift+j
	 */
	public void initUI(){
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}
	
	/**
	 * 获取数据方法
	 */
	/**
	 * 
	 */
	private void initData() {
		//1,应用版本名称
		tv_splash_version.setText("版本号：" + getVersionName());
		//检测(本地版本号和服务器版本号比对)是否有更新,如果有更新,提示用户下载(member)
		//2,获取本地版本号
		mLocalVersionCode = getVersionCode();
		//3,获取服务器版本号(客户端发请求,服务端给响应,(json,xml))
				//http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
				//json中内容包含:
				/* 更新版本的版本名称
				 * 新版本的描述信息
				 * 服务器版本号
				 * 新版本apk下载地址*/
		if (SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
			
			checkUpdate();
		}else {
//			enterHome();
			//消息机制
//			handler.sendMessageDelayed(msg, 4000);
			//在发送消息4秒后，处理这个状态码对应的程序
			handler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
	
		
		
		
	}
	
	
	
	private int getVersionCode() {
		//用来管理手机的APK
		PackageManager pm = getPackageManager();
		
		try {
			//得到APK的功能清单文件,传0代表获取基本信息
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
		//关闭当前页面
		finish();
	}
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_VERSION://显示升级的对话框
				Log.i(TAG,"显示升级的对话框");
				showUodateDialog();
				
				break;
			case ENTER_HOME:
				enterHome();
				break;
			case URL_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(),"URL错误",0).show();
				break;
			case NEWWORK_ERROE:

				enterHome();
				Toast.makeText(getApplicationContext(),"NETWORK错误",0).show();
				break;
			case JSON_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(),"JSON错误",0).show();
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
					//默认就是get请求方式,
					conn.setConnectTimeout(4000);
					conn.setReadTimeout(4000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if (code == 200) {
						//联网成功 
						InputStream is = conn.getInputStream();
						//把流转换成String类型
						String result = StreamTools.readFromStream(is);
						Log.i(TAG,"联网成功了"+result);
						//json解析
						JSONObject jsonObject = new JSONObject(result);
						//得到服务器版本信息
						String versionName = (String) jsonObject.get("versionName");
						 mVersionDes = (String) jsonObject.get("versionDes");
						 String versionCode = jsonObject.getString("versionCode");
						 mDownloadUrl = (String) jsonObject.get("downloadUrl");
						 
						//日志打印	
							Log.i(TAG, versionName);
							Log.i(TAG, mVersionDes);
							Log.i(TAG, versionCode);
							Log.i(TAG, mDownloadUrl);
							
						 //校验是否有新版本
						 if (mLocalVersionCode<Integer.parseInt(versionCode)) {
							//提示用户更新,弹出对话框(UI),消息机制
								msg.what = UPDATE_VERSION;
							
						}else{
							//进入应用程序主界面
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
	 * 弹出对话框，提示用户更新
	 */
	protected void showUodateDialog() {
		//对话框,是依赖于activity存在的
		Builder builder = new AlertDialog.Builder(this);
		//设置左上角图标
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("版本更新");
		//设置描述内容
		builder.setMessage(mVersionDes);
		//积极按钮，立即更新
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载apk,apk下载地址downloadUrl
				downloadApk();
				
			}
		});
		//消极按钮，稍后再说
		builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 取消对话框，进入主页面
				enterHome();
			}
		});
		//点击取消事件监听
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
		// apk下载地址，放置aok所在路径
		//判断sd卡是否可用，是否挂载上
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		//获取sd卡路径     调File.separator查看/
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+
					File.separator+"MobileSafe1.apk";
		//3,发送请求,获取apk,并且放置到指定路径
			HttpUtils httpUtils = new HttpUtils();
		//4,发送请求,传递参数(下载地址,下载应用放置位置)
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					//下载成功(下载过后的放置在sd卡中apk)
					Log.i(TAG, "下载成功");
					File file = responseInfo.result;
					//提示用户安装
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// 下载失败
					Log.i(TAG, "下载失败");
					
				}
				//刚开始下载的方法
				@Override
				public void onStart() {
					Log.i(TAG, "刚刚开始下载");
					super.onStart();
				}
				//下载过程中的方法(下载apk总大小,当前的下载位置,是否正在下载)
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					Log.i(TAG, "下载中........");
					Log.i(TAG, "total = "+total);
					Log.i(TAG, "current = "+current);
					super.onLoading(total, current, isUploading);
				}
			});
			
		}
		
	}

	/**安装对应apk
	 * @param file	安装文件
	 */
	protected void installApk(File file) {
		//系统应用界面，源码，安装apk入口
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
//		//文件作为数据源
//		intent.setData(Uri.fromFile(file));
//		//设置安装类型
//		intent.setType();
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//		startActivity(intent);
		startActivityForResult(intent, 0);
	}
	//开启一个activity后,返回结果调用的方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}
	private String getVersionName(){
		//用来管理手机的APK
		PackageManager pm = getPackageManager();
		
		try {
			//得到APK的功能清单文件,传0代表获取基本信息
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
