package com.itbaojin.mobilesafe;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.itbaojin.mobilesafe.fragment.CacheFragment;
import com.itbaojin.mobilesafe.fragment.SDFragment;

public class ClearCacheActivity extends FragmentActivity {
	private CacheFragment cacheFragment;
	private SDFragment sdFragment;
	private FragmentManager supportFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clearcache);
		// ����fragment
		// 1.��ȡfragment
		cacheFragment = new CacheFragment();
		sdFragment = new SDFragment();
		// 2.����fragment
		// 2.1��ȡfragment�Ĺ�����
		supportFragmentManager = getSupportFragmentManager();
		// 2.2��������
		FragmentTransaction beginTransaction = supportFragmentManager
				.beginTransaction();
		// 2.3���fragment����
		// ���ݲ�������ˢ��
		// ����1:fragmentҪ�滻�Ŀؼ���id
		// ����2:Ҫ�滻fragment
		beginTransaction.add(R.id.fram_clearcache_fragment, cacheFragment);
		// ����չʾ�����һ����ӵ�Ϊ׼
		beginTransaction.add(R.id.fram_clearcache_fragment, sdFragment);
		// ����fragment����
		beginTransaction.hide(sdFragment);
		// Ҳ����ʵ���滻����,���ݻ�����ˢ��
		// beginTransaction.replace(arg0, arg1)
		// 2.4�ύ����
		beginTransaction.commit();
	}

	// ��������
	public void cache(View v) {
		// ����sdfragment,��ʾcacheFragment
		// 1.��ȡ����
		FragmentTransaction beginTransaction = supportFragmentManager
				.beginTransaction();
		// 2.������ʾ����
		beginTransaction.hide(sdFragment);// ����fragmnt
		beginTransaction.show(cacheFragment);// ��ʾfragment����
		// 3.�ύ
		beginTransaction.commit();
	}

	// SD������
	public void sd(View v) {
		// ����cachefragment,��ʾsdfragment
		// 1.��ȡ����
		FragmentTransaction beginTransaction = supportFragmentManager
				.beginTransaction();
		// 2.������ʾ����
		beginTransaction.hide(cacheFragment);// ����fragmnt
		beginTransaction.show(sdFragment);// ��ʾfragment����
		// 3.�ύ
		beginTransaction.commit();
	}
	
}
