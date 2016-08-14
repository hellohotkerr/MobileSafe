package com.itbaojin.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itbaojin.mobilesafe.R;

public class SettingClickView extends RelativeLayout {
	private TextView tv_setting_title;
	private TextView tv_setting_des;
	//�ڴ�����ʹ�õ�ʱ�����
	public SettingClickView(Context context) {
		super(context);
		//�����ڳ�ʼ���Զ���ؼ���ʱ�����ӿؼ�
		init();
	}
	//�ڲ����ļ���ʹ�õ�ʱ�����,�������������˸���ʽ
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	//�ڲ����ļ���ʹ�õ�ʱ�����
	//AttributeSet : �����пؼ�����������
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	/**
	 * ��ӿؼ�
	 */
	private void init(){
		//��Ӳ����ļ�
//		TextView textView = new TextView(getContext());
//		textView.setText("�����Զ�����Ͽؼ���textview");
		//��һ�ַ�ʽ
		//�������ļ�ת����view����
//		View view = View.inflate(getContext(), R.layout.settingview, null);//������,ȥ�Һ���,����
//		//��Ӳ���
//		this.addView(view);//���Զ�����Ͽؼ������һ��textview
		//�ڶ��ַ�ʽ
		//��ȡview����,ͬʱ��veiw�������ø��ؼ�,�൱���ȴ���һ��view����,�ڰѿؼ��ŵ��Զ���ؼ���
		View view = View.inflate(getContext(), R.layout.setting_click_view, this);//��������,ȥ�ҵ�,ϲ����
		//��ʼ������,������Ϣ,checkbox�ؼ�
		//view.findViewById : ��ʾ��settingview�����ļ��л�ȡ�ؼ�
		tv_setting_title = (TextView) view.findViewById(R.id.tv_setting_title);
		tv_setting_des = (TextView) view.findViewById(R.id.tv_setting_des);
	}
	//��Ҫ���һЩ����,ʹ����Ա�ܷ����ȥ�ı��Զ���ؼ��еĿؼ���ֵ
	/**
	 * ���ñ���ķ���
	 */
	public void setTitle(String title){
		tv_setting_title.setText(title);
	}
	/**
	 * ����������Ϣ�ķ���
	 */
	public void setDes(String des){
		tv_setting_des.setText(des);
	}

	

}
