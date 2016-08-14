package com.itbaojin.mobilesafe.bean;


public class BlackNumInfo {
	private String blackNum;
	private int mode;
	
	public String getBlackNum() {
		return blackNum;
	}
	public void setBlackNum(String blackNum) {
		this.blackNum = blackNum;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		if (mode >= 0 && mode <= 2) {
			
			this.mode = mode;
		}else {
			this.mode =0;
		}
	}
	public BlackNumInfo() {
		super();
	}
	public BlackNumInfo(String blackNum, int mode) {
		super();
		this.blackNum = blackNum;
if (mode >= 0 && mode <= 2) {
			
			this.mode = mode;
		}else {
			this.mode =0;
		}
	}
	@Override
	public String toString() {
		return "BlackNumInfo [blackNum=" + blackNum + ", mode=" + mode + "]";
	}
	

}
