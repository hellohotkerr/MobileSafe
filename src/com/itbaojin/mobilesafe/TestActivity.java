package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(this);
		textView.setText("���¼�ʱ�У��Ѿ������졣��֪���Ϲ���,��Ϧ�Ǻ��ꣿ"
				+ "�����˷��ȥ��Ω����¥����ߴ���ʤ��������Ū��Ӱ���������˼䣿 "
				+ "ת��󣬵�粻��������ߡ���Ӧ�кޡ����³����ʱԲ��"
				+ "���б�����ϣ���������Բȱ�����¹���ȫ����Ը�˳��ã�ǧ�ﹲ���ꡣ");
		setContentView(textView);
		
	}
}
