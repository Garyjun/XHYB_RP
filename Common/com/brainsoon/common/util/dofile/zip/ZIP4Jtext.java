package com.brainsoon.common.util.dofile.zip;

import java.io.*;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZIP4Jtext
{

	public static void main(String args[]){
		ZipFile zipFile;
		try {
			zipFile = new ZipFile("d://交接文档.zip");
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setFileNameInZip("文件2.doc");
			parameters.setSourceExternalStream(true);

			InputStream is = new FileInputStream("d://公司文档/2012年BIBF客户信息记录表-tanghui.xls");

			zipFile.addStream(is, parameters);

			is.close();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		

}

