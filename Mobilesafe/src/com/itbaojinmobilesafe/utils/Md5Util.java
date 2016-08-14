package com.itbaojinmobilesafe.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;


public class Md5Util {

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		//加盐
//		String psd = "123"+"uagcsgcscbvfcYTQcxjIxcbv";
//		encode(psd);
//	}

	/**给指定字符串按照md5算法去加密
	 * @param psd	需要加密的密码
	 */
	public static String encode(String psd) {
		//指定加密算法类型
		try {
			//加盐
//			psd = psd + "mobilesafe";
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//将需要加密的字符串转换成byte类型的数组，然后进行随机的哈希过程
			byte[] bs = digest.digest(psd.getBytes());
//			System.out.println(bs.length);
			//循环遍历bs，然后生成32位字符串，固定写法
			//拼接字符串的过程
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;
				//int类型的i需要转换成16进制字符
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
