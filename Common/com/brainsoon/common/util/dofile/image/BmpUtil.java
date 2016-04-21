package com.brainsoon.common.util.dofile.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 
 * @ClassName: BmpUtil
 * @Description: BMP图片处理类
 * @author tanghui
 * @date 2014-12-18 上午10:26:44
 * 
 */
public class BmpUtil {
	static {
		System.setProperty("java.awt.headless", "true");
	}

	/**
	 * <p>
	 * Discription:[bmpTojpg]
	 * </p>
	 * 
	 * @param file
	 * @param dstFile
	 */
	public static boolean bmp2jpg(String srcFile, String dstFile) {
		FileOutputStream out = null;
		boolean b = true;
		try {
			DoFileUtils.mkdir(dstFile);
			FileInputStream in = new FileInputStream(srcFile);
			Image TheImage = read(in);
			int wideth = TheImage.getWidth(null);
			int height = TheImage.getHeight(null);
			BufferedImage tag = new BufferedImage(wideth, height,
					BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(TheImage, 0, 0, wideth, height, null);
			out = new FileOutputStream(dstFile);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(tag);
			tag.flush();
			TheImage.flush();
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		} finally {
			if (!new File(dstFile).exists() || new File(dstFile).length() <= 0) {
				b = ImgCoverUtil.converToOther(srcFile, dstFile);
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return b;
	}

	public static int constructInt(byte[] in, int offset) {
		int ret = ((int) in[offset + 3] & 0xff);
		ret = (ret << 8) | ((int) in[offset + 2] & 0xff);
		ret = (ret << 8) | ((int) in[offset + 1] & 0xff);
		ret = (ret << 8) | ((int) in[offset + 0] & 0xff);
		return (ret);
	}

	public static int constructInt3(byte[] in, int offset) {
		int ret = 0xff;
		ret = (ret << 8) | ((int) in[offset + 2] & 0xff);
		ret = (ret << 8) | ((int) in[offset + 1] & 0xff);
		ret = (ret << 8) | ((int) in[offset + 0] & 0xff);
		return (ret);
	}

	public static long constructLong(byte[] in, int offset) {
		long ret = ((long) in[offset + 7] & 0xff);
		ret |= (ret << 8) | ((long) in[offset + 6] & 0xff);
		ret |= (ret << 8) | ((long) in[offset + 5] & 0xff);
		ret |= (ret << 8) | ((long) in[offset + 4] & 0xff);
		ret |= (ret << 8) | ((long) in[offset + 3] & 0xff);
		ret |= (ret << 8) | ((long) in[offset + 2] & 0xff);
		ret |= (ret << 8) | ((long) in[offset + 1] & 0xff);
		ret |= (ret << 8) | ((long) in[offset + 0] & 0xff);
		return (ret);
	}

	public static double constructDouble(byte[] in, int offset) {
		long ret = constructLong(in, offset);
		return (Double.longBitsToDouble(ret));
	}

	public static short constructShort(byte[] in, int offset) {
		short ret = (short) ((short) in[offset + 1] & 0xff);
		ret = (short) ((ret << 8) | (short) ((short) in[offset + 0] & 0xff));
		return (ret);
	}

	static class BitmapHeader {
		public int iSize, ibiSize, iWidth, iHeight, iPlanes, iBitcount,
				iCompression, iSizeimage, iXpm, iYpm, iClrused, iClrimp;

		// 读取bmp文件头信息
		public void read(FileInputStream fs){
			try {
				final int bflen = 14;
				byte bf[] = new byte[bflen];
				fs.read(bf, 0, bflen);
				final int bilen = 40;
				byte bi[] = new byte[bilen];
				fs.read(bi, 0, bilen);
				iSize = constructInt(bf, 2);
				ibiSize = constructInt(bi, 2);
				iWidth = constructInt(bi, 4);
				iHeight = constructInt(bi, 8);
				iPlanes = constructShort(bi, 12);
				iBitcount = constructShort(bi, 14);
				iCompression = constructInt(bi, 16);
				iSizeimage = constructInt(bi, 20);
				iXpm = constructInt(bi, 24);
				iYpm = constructInt(bi, 28);
				iClrused = constructInt(bi, 32);
				iClrimp = constructInt(bi, 36);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Image read(FileInputStream fs) {
		try {
			BitmapHeader bh = new BitmapHeader();
			bh.read(fs);
			if (bh.iBitcount == 24) {
				return (readImage24(fs, bh));
			}
			if (bh.iBitcount == 32) {
				return (readImage32(fs, bh));
			}
			fs.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return (null);
	}

	// 24位
	protected static Image readImage24(FileInputStream fs, BitmapHeader bh) {
		Image image = null;
		try {
			if (bh.iSizeimage == 0) {
				bh.iSizeimage = ((((bh.iWidth * bh.iBitcount) + 31) & ~31) >> 3);
				bh.iSizeimage *= bh.iHeight;
			}
			int npad = (bh.iSizeimage / bh.iHeight) - bh.iWidth * 3;
			int ndata[] = new int[bh.iHeight * bh.iWidth];
			byte brgb[] = new byte[(bh.iWidth + npad) * 3 * bh.iHeight];
			fs.read(brgb, 0, (bh.iWidth + npad) * 3 * bh.iHeight);
			int nindex = 0;
			for (int j = 0; j < bh.iHeight; j++) {
				for (int i = 0; i < bh.iWidth; i++) {
					ndata[bh.iWidth * (bh.iHeight - j - 1) + i] = constructInt3(
							brgb, nindex);
					nindex += 3;
				}
				nindex += npad;
			}
			image = Toolkit.getDefaultToolkit().createImage(
					new MemoryImageSource(bh.iWidth, bh.iHeight, ndata, 0,
							bh.iWidth));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return image;
	}

	// 32位
	protected static Image readImage32(FileInputStream fs, BitmapHeader bh) {
		Image image = null;
		try {
			int ndata[] = new int[bh.iHeight * bh.iWidth];
			byte brgb[] = new byte[bh.iWidth * 4 * bh.iHeight];
			fs.read(brgb, 0, bh.iWidth * 4 * bh.iHeight);
			int nindex = 0;
			for (int j = 0; j < bh.iHeight; j++) {
				for (int i = 0; i < bh.iWidth; i++) {
					ndata[bh.iWidth * (bh.iHeight - j - 1) + i] = constructInt3(
							brgb, nindex);
					nindex += 4;
				}
			}
			image = Toolkit.getDefaultToolkit().createImage(
					new MemoryImageSource(bh.iWidth, bh.iHeight, ndata, 0,
							bh.iWidth));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return (image);
	}

	public static void main(String[] args) {
		String srcfile = "D:/Project素材/image/搞怪爱新觉罗-玄烨.bmp";
		String dstFile = "D:/Project素材/image/搞怪爱新觉罗-玄烨.jpg";
		bmp2jpg(srcfile, dstFile);
	}

}