package com.itbaojin.mobilesafe.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itbaojin.mobilesafe.R;

public class SDFragment extends Fragment {
	//��ʼ������
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	//����fragment�Ĳ���
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//����1:�����ļ�
		//����2:����
		//����3:�Զ�����  ,һ��false
		View view= inflater.inflate(R.layout.fragment_sd, container, false);
		return view;
	}
	//���������ʾ����
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
}
