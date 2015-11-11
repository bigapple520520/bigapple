package com.xuan.bigapple.lib.media.helper;

import android.os.Environment;

/**
 * media操作的参数类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-5-2 下午8:03:27 $
 */
public class MediaConfig {
	/** 文件存放路径 */
	private String filePath = Environment.getExternalStorageDirectory()
			.getPath() + "/bigapple/voice/";
	/** 文件后缀名 */
	private String fileExt = "amr";// 文件后缀名

	private int volumeInteral = 1000;// 分呗取样间隔，单位ms

	public MediaConfig(String filePath, String fileExt) {
		this.filePath = filePath;
		this.fileExt = fileExt;
	}

	public MediaConfig configFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}

	public MediaConfig configFileExt(String fileExt) {
		this.fileExt = fileExt;
		return this;
	}

	public MediaConfig configVolumeInteral(int volumeInteral) {
		this.volumeInteral = volumeInteral;
		return this;
	}

	public int getVolumeInteral() {
		return volumeInteral;
	}

	public String getFileExt() {
		return fileExt;
	}

	public String getFilePath() {
		return filePath;
	}

}
