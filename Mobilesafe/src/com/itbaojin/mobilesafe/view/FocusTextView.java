package com.itbaojin.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;


/**
 * @author Administrator
 *能够获取焦点的自定义的TextView
 */
public class FocusTextView extends TextView {
	//通过java代码创建控件
	public FocusTextView(Context context) {
		super(context);
	}
	//由系统调用(带属性+上下文环境的构造方法)
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//由系统调用(带属性+上下文环境的构造方法+布局文件中定义样式文件构造方法)
	
	public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	//重写获取焦点的方法,有系统调用，调用时默认获取焦点
	@Override
	public boolean isFocused() {
		return true;
	}

}
