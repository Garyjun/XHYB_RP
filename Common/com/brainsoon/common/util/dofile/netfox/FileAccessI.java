/* 
 **FileAccess.java 
 */
package com.brainsoon.common.util.dofile.netfox;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * 
 * @ClassName: FileAccessI 
 * @Description:  负责文件的存储。
 * @author tanghui 
 * @date 2013-8-14 下午5:22:38 
 *
 */
public class FileAccessI implements Serializable {
	RandomAccessFile oSavedFile;
	long nPos;

	public FileAccessI() throws IOException {
		this("", 0);
	}

	public FileAccessI(String sName, long nPos) throws IOException {
		oSavedFile = new RandomAccessFile(sName, "rw");
		this.nPos = nPos;
		oSavedFile.seek(nPos);
	}

	public synchronized int write(byte[] b, int nStart, int nLen) {
		int n = -1;
		try {
			oSavedFile.write(b, nStart, nLen);
			n = nLen;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}
}
