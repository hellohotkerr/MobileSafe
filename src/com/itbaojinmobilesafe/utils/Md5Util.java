package com.itbaojinmobilesafe.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;


public class Md5Util {

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		//����
//		String psd = "123"+"uagcsgcscbvfcYTQcxjIxcbv";
//		encode(psd);
//	}

	/**��ָ���ַ�������md5�㷨ȥ����
	 * @param psd	��Ҫ���ܵ�����
	 */
	public static String encode(String psd) {
		//ָ�������㷨����
		try {
			//����
//			psd = psd + "mobilesafe";
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//����Ҫ���ܵ��ַ���ת����byte���͵����飬Ȼ���������Ĺ�ϣ����
			byte[] bs = digest.digest(psd.getBytes());
//			System.out.println(bs.length);
			//ѭ������bs��Ȼ������32λ�ַ������̶�д��
			//ƴ���ַ����Ĺ���
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;
				//int���͵�i��Ҫת����16�����ַ�
				String hexString = Integer.toHexString(i);
//				System.out.println(hexString);
				if (hexString.length() < 2) {
					hexString = "0"+hexString;
				}
				stringBuffer.append(hexString);
			}
//			System.out.println(stringBuffer.toString());
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}
