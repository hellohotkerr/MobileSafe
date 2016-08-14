package com.itbaojin.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;


/**
 * @author Administrator
 *�ܹ���ȡ������Զ����TextView
 */
public class FocusTextView extends TextView {
	//ͨ��java���봴���ؼ�
	public FocusTextView(Context context) {
		super(context);
	}
	//��ϵͳ����(������+�����Ļ����Ĺ��췽��)
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//��ϵͳ����(������+�����Ļ����Ĺ��췽��+�����ļ��ж�����ʽ�ļ����췽��)
	
	public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	//��д��ȡ����ķ���,��ϵͳ���ã�����ʱĬ�ϻ�ȡ����
	@Override
	public boolean isFocused() {
		return true;
	}

}
