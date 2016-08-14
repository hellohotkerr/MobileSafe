package com.itbaojin.mobilesafe.view;

import com.itbaojin.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.itbaojin.mobilesafe";
	private static final String TAG = "SettingItemView";
	private CheckBox cb_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDesoff;
	private String mDeson;
	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SettingItemView(Context context) {
		this(context,null);
	}
	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//xml ----> view �����ý����һ����Ŀת����view����,ֱ����ӵ��˵�ǰSettingItemView��Ӧ��view��
//		View view = View.inflate(context, R.layout.setting_item_view, null);
//		this.addView(view);
		View.inflate(context, R.layout.setting_item_view, this);
		TextView tv_title =(TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		
		//��ȡ�Զ����Լ�ԭ�����ԵĲ���
		initAttrs(attrs);
		
		tv_title.setText(mDestitle);
		
	}
	private void initAttrs(AttributeSet attrs) {
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
		
	}

	public boolean isChecked(){
		return cb_box.isChecked();
		
	}
	public void setCheck(boolean isCheck){
		cb_box.setChecked(isCheck);
		if (isCheck) {
			tv_des.setText(mDeson);
		}else {
			tv_des.setText(mDesoff);
		}
	}

	

}
