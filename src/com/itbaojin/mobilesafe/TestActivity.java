package com.itbaojin.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(this);
		textView.setText("明月几时有？把酒问青天。不知天上宫阙,今夕是何年？"
				+ "我欲乘风归去，惟恐琼楼玉宇，高处不胜寒．起舞弄清影，何似在人间？ "
				+ "转朱阁，低绮户，照无眠。不应有恨、何事长向别时圆？"
				+ "人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共蝉娟。");
		setContentView(textView);
		
	}
}
