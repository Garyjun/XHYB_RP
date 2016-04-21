package com.brainsoon.common.util.dofile.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import magick.CompressionType;
import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.PixelPacket;
import magick.PreviewType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 图像处理工具类 (最好用最新版本) jar包地址 http://downloads.jmagick.org/6.3.9/ window下使用的是:
 * jmagick-win-6.3.9-Q16.zip 和 ImageMagick-6.3.9-0-Q16-windows-dll.exe
 * linux下使用的是 : JMagick-6.2.6-0.tar.gz 和 ImageMagick-x86_64-pc-linux-gnu.tar.gz
 * doc地址: http://downloads.jmagick.org/jmagick-doc/
 * 
 * windows安装方法: 一 . 安装 ImageMagick-6.3.9-0-Q16-windows-dll.exe 二 . 将 安装目录下
 * “C:/Program Files/ImageMagick-6.3.9-Q8/ ”(按自己所安装的目录找) (按自己所安装的目录找) 下的所有dll文件
 * copy 到系统盘下的 “C:/WINDOWS/system32/”文件夹里 三 . 配置环境变量 在环境变量path里添加新的值 “C:/Program
 * Files/ImageMagick-6.3.9-Q16” 四 . 解压jmagick-win-6.3.9-Q16.zip 将 jmagick.dll
 * 复制到系统盘下的 “C:/WINDOWS/system32/”文件夹里 和../xxx/resin-4.0.4/win32/下
 * 
 * 
 * linux安装方法: 一 . 安装jpeg包: 　 　 　 tar -zvxf jpegsrc.v6b.tar.gz cd jpeg-6b/
 * ./configure --enable-shared --enable-static ; make ; mkdir /usr/local/man/
 * mkdir /usr/local/man/man1 make install 二 . 安装png包: bzip2 -d
 * libpng-1.2.18.tar.bz2　 　 　 tar -xvf libpng-1.2.18.tar cd libpng-1.2.18 　　 cp
 * scripts/makefile.std makefile 　　 make 　　 make install 三 . 安装ImageMagick包 　 　
 * tar xzvf ImageMagick.tar.gz cd ImageMagick-6.3.5/ ./configure
 * --prefix=/usr/local/imgtools/ImageMagick --enable-share --enable-static make
 * make install 四 . 安装JMagick包 　 　 　tar xzvf JMagick-6.2.6-0.tar.gz autoconf
 * ./configure --prefix=/usr/local/imgtools/JMagick
 * --with-magick-home=/usr/local/imgtools/ImageMagick --enable-share
 * --enable-static make all make install 五. vi /etc/profile 　 　 加入
 * PATH=$PATH:/usr/local/imgtools/ImageMagick/bin export
 * LD_LIBRARY_PATH=:/usr/local
 * /imgtools/JMagick/lib:/usr/local/imgtools/ImageMagick/lib export
 * DYLD_LIBRARY_PATH
 * =:/usr/local/imgtools/JMagick/lib:/usr/local/imgtools/ImageMagick/lib export
 * CLASSPATH=$CLASSPATH:/usr/local/imgtools/JMagick-6.2.4-1/classes export PATH
 * 六. ln -s /usr/local/imgtools/JMagick/lib/libJMagick.so /usr/lib/libJMagick.so
 * 不做这步会报一个找不到lib.path的错误 七.java代码使用一下包
 * /usr/local/imgtools/JMagick/lib/jmagick.jar
 * 
 * 
 * @author tanghui
 * @update 2012-07-20
 * @updateDATE 2012-08-19
 * @updateDATE 2014-04-21
 * 
 */
public class ImageUtils {

	protected static final Logger logger = Logger.getLogger(ImageUtils.class);
	public static String imgConverPath;
	static {
		// 不能漏掉这个，不然jmagick.jar的路径找不到
		System.setProperty("jmagick.systemclassloader", "no");
		imgConverPath = PropertiesReader.getInstance().getProperty(ConstantsDef.imgConverPath);
		// System.out.println("-------请求的路径为：-------" + System.getProperty("java.library.path"));
	}

	public static int MARK_POSITION_LEFT_UP = 0;
	public static int MARK_POSITION_RIGHT_UP = 1;
	public static int MARK_POSITION_LEFT_DOWN = 2;
	public static int MARK_POSITION_RIGHT_DOWN = 3;
	public static final int VIDEO_IMG_W = 100; 			// 默认视频水印图片缩放尺寸-宽
	public static final int VIDEO_IMG_H = 100; 			// 默认视频水印图片缩放尺寸-高
	public static final double IMGPROP = 0.05; 			// 默认图片缩放比例 1/20
	public static final int IMGMIXSIZE = 125; 			// 默认图片最小尺寸
	public static final double VIDEO_IMG_SIZE = 1; 	// 默认视频水印图片缩放尺寸
	
	/**
	 * 压缩图片
	 * 
	 * @param filePath
	 *            源文件路径
	 * @param toPath
	 *            缩略图路径
	 */
	public static void createThumbnail(String filePath, String toPath)
			throws MagickException {
		ImageInfo info = null;
		MagickImage image = null;
		Dimension imageDim = null;
		MagickImage scaled = null;
		FileInputStream f = null;
		try {
			if (DoFileUtils.exitFile(filePath)) {
				f = new FileInputStream(filePath);
				// 数组的大小和文件的大小一致
				byte[] b = new byte[f.available()];
				f.read(b);
				info = new ImageInfo();
				image = new MagickImage(info, b);
				imageDim = image.getDimension();
				int wideth = imageDim.width;
				int height = imageDim.height;
				scaled = image.scaleImage(wideth, height);// 小图片文件的大小.
				scaled.setFileName(toPath);
				scaled.writeImage(info);
			}
		} catch (Exception e) {
			logger.error("压缩【" + filePath + "】图片失败！");
		} finally {
			if (scaled != null) {
				scaled.destroyImages();
			}
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 水印(文字)
	 * 
	 * @param filePath
	 *            源文件路径
	 * @param toImg
	 *            修改图路径
	 * @param text
	 *            名字(文字内容自己随意)
	 * @throws MagickException
	 */
	public static void initTextToImg(String filePath, String toImg, String text)
			throws MagickException {
		MagickImage aImage = null;
		ImageInfo info = null;
		FileInputStream f = null;
		try {
			if (DoFileUtils.exitFile(filePath)) {
				f = new FileInputStream(filePath);
				// 数组的大小和文件的大小一致
				byte[] bb = new byte[f.available()];
				f.read(bb);
				info = new ImageInfo();
				aImage = new MagickImage(info, bb);
				if (filePath.toUpperCase().endsWith("JPG")
						|| filePath.toUpperCase().endsWith("JPEG")) {
					info.setCompression(CompressionType.JPEGCompression); // 压缩类别为JPEG格式
					info.setPreviewType(PreviewType.JPEGPreview); // 预览格式为JPEG格式
					info.setQuality(95);
				}
				Dimension imageDim = aImage.getDimension();
				int wideth = imageDim.width;
				int height = imageDim.height;
				if (wideth > 660) {
					height = 660 * height / wideth;
					wideth = 660;
				}
				int a = 0;
				int b = 0;
				String[] as = text.split("");
				for (String string : as) {
					if (string.matches("[\u4E00-\u9FA5]")) {
						a++;
					}
					if (string.matches("[a-zA-Z0-9]")) {
						b++;
					}
				}
				int tl = a * 12 + b * 6 + 300;
				MagickImage scaled = aImage.scaleImage(wideth, height);
				if (wideth > tl && height > 5) {
					DrawInfo aInfo = new DrawInfo(info);
					aInfo.setFill(PixelPacket.queryColorDatabase("white"));
					aInfo.setUnderColor(new PixelPacket(0, 0, 0, 100));
					aInfo.setPointsize(12);
					aInfo.setTextAntialias(true);
					aInfo.setOpacity(0);
					aInfo.setText(text);
					aInfo.setGeometry("+" + (wideth - tl) + "+" + (height - 5));
					scaled.annotateImage(aInfo);
					scaled.rotateImage(80);
				}
				scaled.setFileName(toImg);
				scaled.writeImage(info);
				scaled.destroyImages();
			}
		} catch (Exception e) {
			logger.error("给【 " + filePath + "】图片添加文字水印失败！");
		} finally {
			if (aImage != null) {
				aImage.destroyImages();
			}
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 切图
	 * 
	 * @param filePath
	 *            源图路径
	 * @param toPath
	 *            修改图路径
	 * @param w
	 *            宽
	 * @param h
	 *            高
	 * @param x
	 *            x轴
	 * @param y
	 *            y轴
	 * @throws MagickException
	 */
	public static void cutImg(String filePath, String toPath, int w, int h,
			int x, int y) throws MagickException {
		ImageInfo info = null;
		MagickImage image = null;
		MagickImage cropped = null;
		Rectangle rect = null;
		FileInputStream f = null;
		try {
			if (DoFileUtils.exitFile(filePath)) {
				f = new FileInputStream(filePath);
				// 数组的大小和文件的大小一致
				byte[] b = new byte[f.available()];
				f.read(b);
				info = new ImageInfo();
				image = new MagickImage(info, b);
				rect = new Rectangle(x, y, w, h);
				cropped = image.cropImage(rect);
				cropped.setFileName(toPath);
				cropped.writeImage(info);
			}
		} catch (MagickException e) {
			logger.error("【 " + filePath + "】图片切图失败！");
		} catch (FileNotFoundException e) {
			logger.error("【 " + filePath + "】图片不存在！");
		} catch (IOException e) {
			logger.error("【 " + filePath + "】图片缩放失败！");
		} finally {
			if (cropped != null) {
				cropped.destroyImages();
			}
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 按照指定的宽高进行图片缩放
	 * 
	 * @param filePath
	 *            原图片路径
	 * @param toPath
	 *            缩放后的图片路径
	 * @param w
	 *            缩放后图片宽度
	 * @param h
	 *            缩放后图片高度
	 */

	public static void zoomImg(String filePath, String toPath, int w, int h)
			throws FileNotFoundException {
		FileOutputStream fileOutputStream = null;
		try {
			if (DoFileUtils.exitFile(filePath)) {
				// 获取原文件的宽高
				Image srcImg = ImageIO.read(new File(filePath));
				int width = srcImg.getWidth(null);
				int height = srcImg.getHeight(null);
				// 判断 如果图片的宽度和高度太小，或者为0，则采用原图片的宽和高

				if (w < 5) {
					w = width;
				}

				if (h < 5) {
					h = height;
				}

				if (w > width && h > height) { // 如果大于图片原始的宽度和高度---使用原始尺寸
					w = width;
					h = height;
				} else {
					double dd = 0; // 计算出高的压缩比例
					if (width > height) {
						dd = (double) height / width; // 计算出高的压缩比例
					} else {
						dd = (double) width / height; // 计算出高的压缩比例
					}
					if (w >= width && h < height) { // 如果大于图片原始的宽度但是小于原始图片的高度---则使用图片宽度进行缩放
						w = (int) (w * dd);
					} else if (w < width && h >= height) { // 如果小于图片原始的宽度但是大于原始图片的高度---则使用图片高度进行缩放
						h = (int) (h * dd);
					} else if (w < width && h < height) {
						if (width > height) {
							h = (int) (h * dd);
						} else {
							w = (int) (w * dd);
						}
					} else {
						w = width;
						h = height;
					}
				}

				fileOutputStream = new FileOutputStream(new File(toPath));
				BufferedImage bufferedImage = new BufferedImage(w, h,
						BufferedImage.TYPE_INT_RGB);
				bufferedImage.getGraphics().drawImage(
						srcImg.getScaledInstance(w, h, srcImg.SCALE_SMOOTH), 0,
						0, null);
				JPEGImageEncoder encoder = JPEGCodec
						.createJPEGEncoder(fileOutputStream);
				encoder.encode(bufferedImage);
				fileOutputStream.close();
			}
		} catch (Exception e) {
			logger.error("【 " + filePath + "】图片缩放失败！");
		} finally {
		}
	}

	/**
	 * 图片按照等比缩放
	 * 
	 * @param filePath
	 *            原图片路径
	 * @param toPath
	 *            缩放后的图片路径
	 */

	public static void zoomSureImg(String filePath, String toPath) {
		// MagickImage image = null;
		// MagickImage scaleImg = null;
		FileInputStream f = null;
		FileOutputStream fileOutputStream = null;
		try {
			if (DoFileUtils.exitFile(filePath)) {
				f = new FileInputStream(filePath);
				// 数组的大小和文件的大小一致
				byte[] b = new byte[f.available()];
				f.read(b);
				// ImageInfo info = new ImageInfo();
				// image = new MagickImage(info,b);
				// 获取原文件的宽高
				Image srcImg = ImageIO.read(new File(filePath));
				int width = srcImg.getWidth(null);
				int height = srcImg.getHeight(null);

				int w = IMGMIXSIZE; // 最小尺寸
				int h = IMGMIXSIZE;
				// 判断 如果图片的宽度和高度太小，或者为0，则采用原图片的宽和高
				if (width != 0 && height != 0) { // 如果大于图片原始的宽度和高度---使用原始尺寸
				// width = (int) (width* BaseCommonUtil.IMGPROP); //缩放比例
				// height = (int) (height * BaseCommonUtil.IMGPROP);
					if (w >= width && h >= height) { // 如果最小尺寸大于图片原始的宽度和高度---使用原始尺寸
						w = width;
						h = height;
					} else {
						double dd = 0; // 计算出高的压缩比例
						if (width > height) {
							dd = (double) height / width; // 计算出高的压缩比例
						} else {
							dd = (double) width / height; // 计算出高的压缩比例
						}
						if (w >= width && h < height) { // 如果大于图片原始的宽度但是小于原始图片的高度---则使用图片宽度进行缩放
							w = (int) (w * dd);
						} else if (w < width && h >= height) { // 如果小于图片原始的宽度但是大于原始图片的高度---则使用图片高度进行缩放
							h = (int) (h * dd);
						} else if (w < width && h < height) {
							if (width > height) {
								h = (int) (h * dd);
							} else {
								w = (int) (w * dd);
							}
						} else {
							w = width;
							h = height;
						}
					}
				}

				fileOutputStream = new FileOutputStream(new File(toPath));
				BufferedImage bufferedImage = new BufferedImage(w, h,
						BufferedImage.TYPE_INT_RGB);
				bufferedImage.getGraphics().drawImage(
						srcImg.getScaledInstance(w, h, srcImg.SCALE_SMOOTH), 0,
						0, null);
				JPEGImageEncoder encoder = JPEGCodec
						.createJPEGEncoder(fileOutputStream);
				encoder.encode(bufferedImage);
				fileOutputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("【 " + filePath + "】图片缩放失败！");
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					logger.error("关闭IO流失败！");
				}
			}
		}
	}

	/**
	 * 图片缩放
	 * 
	 * @param filePath
	 *            图片路径
	 * @param height
	 *            高度
	 * @param width
	 *            宽度
	 * @param bb
	 *            比例不对时是否需要补白
	 */
	public static void zoomPerImg(String filePath, String toPath, int height,
			int width, boolean bb) {
		try {
			double ratio = 0; // 缩放比例
			File f = new File(filePath);
			BufferedImage bi = ImageIO.read(f);
			Image itemp = bi.getScaledInstance(width, height,
					BufferedImage.SCALE_SMOOTH);
			// 计算比例
			if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
				if (bi.getHeight() > bi.getWidth()) {
					ratio = (new Integer(height)).doubleValue()
							/ bi.getHeight();
				} else {
					ratio = (new Integer(width)).doubleValue() / bi.getWidth();
				}
				AffineTransformOp op = new AffineTransformOp(
						AffineTransform.getScaleInstance(ratio, ratio), null);
				itemp = op.filter(bi, null);
			}
			if (bb) {
				BufferedImage image = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
							itemp.getWidth(null), itemp.getHeight(null),
							Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
							itemp.getWidth(null), itemp.getHeight(null),
							Color.white, null);
				g.dispose();
				itemp = image;
			}
			ImageIO.write((BufferedImage) itemp, "jpg", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片按照等比缩放
	 * 
	 * @param filePath
	 *            原图片路径
	 * @param toPath
	 *            缩放后的图片路径
	 * @param w
	 *            视频宽
	 * @param h
	 *            视频高
	 */
	public static void zoomPerImg(String filePath, String toPath, int w, int h) {
		BufferedImage dstImage = null;
		try {
			if (DoFileUtils.exitFile(filePath)) {
				// 获取原文件的宽高
				BufferedImage srcImg = ImageIO.read(new File(filePath));
				int width = srcImg.getWidth();
				int height = srcImg.getHeight();
				int newW = w;
				int newH = h;
				// 判断 如果图片的宽度和高度太小，或者为0，则采用原图片的宽和高
				if (width != 0 && height != 0) { // 如果大于图片原始的宽度和高度---使用原始尺寸
				// w = (int) (width* DoFileTools.VIDEO_IMG_SIZE); //缩放比例
				// h = (int) (height * DoFileTools.VIDEO_IMG_SIZE);
					if (width <= w * VIDEO_IMG_SIZE
							&& height <= h * VIDEO_IMG_SIZE) { // 如果最小尺寸大于图片原始的宽度和高度---使用原始尺寸
						newW = width;
						newH = height;
					} else {
						newW = (int) (width * VIDEO_IMG_SIZE); // 缩放比例
						newH = (int) (height * VIDEO_IMG_SIZE);
					}
				}

				double ratio = 0; // 缩放比例
				// 计算比例
				if ((height > newH) || (width > newW)) {
					if (height > width) {
						ratio = (new Integer(newH)).doubleValue() / height;
					} else {
						ratio = (new Integer(newW)).doubleValue() / width;
					}
				}

				AffineTransform transform = AffineTransform.getScaleInstance(
						ratio, ratio);// 返回表示缩放变换的变换
				AffineTransformOp op = new AffineTransformOp(transform,
						AffineTransformOp.TYPE_BILINEAR);
				dstImage = op.filter(srcImg, null);
				ImageIO.write(dstImage, "PNG", new File(toPath));
			}
		} catch (Exception e) {
			logger.error("【 " + filePath + "】图片缩放失败！");
		} finally {
		}
	}

	/**
	 * 描述：将图片转换为其他格式
	 * 
	 * @param filePath
	 *            图片的路径
	 * @param newImagePath
	 *            新图片的路径
	 * @throws MagickException
	 */
	public static boolean converToOther(String filePath, String newImagePath)
			throws MagickException, FileNotFoundException {
		MagickImage image = null;
		MagickImage scaleImg = null;
		FileInputStream f = null;
		boolean boo = false; // 转换失败
		try {
			if (DoFileUtils.exitFile(filePath)) {
				f = new FileInputStream(filePath);
				// 创建imageInfo对象
				// 数组的大小和文件的大小一致
				byte[] b = new byte[f.available()];
				f.read(b);
				ImageInfo imageInfo = new ImageInfo();
				String imgType = newImagePath.substring(
						newImagePath.lastIndexOf(".") + 1,
						newImagePath.length());
				imageInfo.setMagick(imgType);
				imageInfo.setCompression(CompressionType.ZipCompression); // 设置压缩
				// 读取imageInfo
				image = new MagickImage(imageInfo, b);
				// 设置新图片的路径
				image.setFileName(newImagePath);
				// 判断目录是否存在，不存在就创建
				DoFileUtils.mkdir(newImagePath);
				// 执行
				image.writeImage(imageInfo);
				String newGifImagePath = "";
				// 如果是gif格式的图片需改名
				if ("GIF".equals(filePath.substring(
						filePath.lastIndexOf(".") + 1, filePath.length())
						.toUpperCase())) {
					newGifImagePath = newImagePath.substring(0,
							newImagePath.lastIndexOf("."))
							+ "-0." + imgType;
					System.out.println(newGifImagePath);
					File newFile = new File(newGifImagePath);
					if (newFile.exists()) {
						newFile.renameTo(new File(newImagePath));
					}
				}
				boo = true;
				logger.info("【 " + filePath + "】图片转换成功！");
			}
		} catch (MagickException e) {
			logger.error("【 " + filePath + "】图片转换失败！");
		} catch (FileNotFoundException e) {
			logger.error("【 " + filePath + "】图片不存在！");
		} catch (IOException e) {
			logger.error("【 " + filePath + "】图片转换失败！");
		} finally {
			if (scaleImg != null) {
				scaleImg.destroyImages();
			}
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
				}
			}
		}
		return boo;
	}

	/*
	 * 给图片添加文字水印
	 * 
	 * @param pressText 水印文字
	 * 
	 * @param filePath 源图像地址
	 * 
	 * @param destImageFile 目标图像地址
	 * 
	 * @param fontName 水印的字体名称
	 * 
	 * @param fontStyle 水印的字体样式
	 * 
	 * @param color 水印的字体颜色
	 * 
	 * @param fontSize 水印的字体大小
	 * 
	 * @param x 修正值
	 * 
	 * @param y 修正值
	 * 
	 * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public static void pressText(String pressText, String filePath,
			String destImageFile, String fontName, int fontStyle, Color color,
			int fontSize, int x, int y, String alpha) throws MagickException {
		try {
			if (DoFileUtils.exitFile(filePath)) {
				File img = new File(filePath);
				Image src = ImageIO.read(img);
				int width = src.getWidth(null);
				int height = src.getHeight(null);
				BufferedImage image = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();

				g.drawImage(src, 0, 0, width, height, null);

				g.setColor(color);
				g.setFont(new Font(fontName, fontStyle, fontSize));
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_ATOP, Float.parseFloat(alpha)));
				// 在指定坐标绘制水印文字 rotateImage
				g.drawString(pressText,
						(width - (pressText.length() * fontSize)) / 2 + x,
						(height - fontSize) / 2 + y);
				g.dispose();

				ImageIO.write((BufferedImage) image, "JPEG", new File(
						destImageFile));// 输出到文件流
			}
		} catch (Exception e) {
			logger.error("给【 " + filePath + "】图片添加文字水印失败！");
		}
	}

	/**
	 * 给图片添加图片水印、可设置水印的旋转角度
	 * 
	 * @param filePath
	 *            原图片地址
	 * @param newPath
	 *            新图片地址
	 * @param logoPath
	 *            水印图片地址
	 * @param degree
	 *            旋转的度数(-180到180的整数)
	 * @param alpha
	 *            透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public static void rotateImage(String filePath, String newPath,
			String logoPath, int degree, String alpha) {
		OutputStream os = null;
		try {
			if (DoFileUtils.exitFile(filePath)) {
				Image srcImg = ImageIO.read(new File(filePath));
				BufferedImage buffImg = new BufferedImage(
						srcImg.getWidth(null), srcImg.getHeight(null),
						BufferedImage.TYPE_INT_RGB);

				// 得到画笔对象
				Graphics2D g = buffImg.createGraphics();

				// 设置对线段锯齿状边缘处理
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				g.drawImage(
						srcImg.getScaledInstance(srcImg.getWidth(null),
								srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
						0, null);

				// 设置水印图片旋转
				g.rotate(Math.toRadians(degree), (double) 0, (double) 0);

				// 水印图片的路径，水印一般格式是gif，png,这种图片可以设置透明度
				ImageIcon imgIcon = new ImageIcon(logoPath);

				// 得到Image对象
				Image img = imgIcon.getImage();

				// 原图片的宽高
				int width = srcImg.getWidth(null);
				int height = srcImg.getHeight(null);

				// 原图片的宽高
				int width1 = img.getWidth(null);
				int height1 = img.getHeight(null);

				// 透明度
				if (alpha == null || alpha.equals("")) {
					alpha = "1";
				}
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_ATOP, Float.parseFloat(alpha)));

				// System.out.println("width:" + width + "  --width1:" + width1
				// + "---height:" + height +"--height1:" + height1);

				// 表示水印图片的位置
				g.drawImage(img, (width - width1) / 2, (height - height1) / 2,
						width1, height1, null);

				// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

				g.dispose();

				os = new FileOutputStream(newPath);

				// 生成图片
				ImageIO.write(buffImg, "JPG", os);
			}
		} catch (Exception e) {
			logger.error("给【 " + filePath + "】图片添加旋转图片水印失败！");
		} finally {
			try {
				if (null != os) {
					os.close();
				}
			} catch (Exception e) {
				logger.error("给【 " + filePath + "】图片添加旋转图片水印失败！");
			}
		}
	}

	/**
	 * 根据文字生成图片
	 * 
	 * @param txt
	 *            String 文字
	 * @param fontSize
	 *            每个字的宽度和高度是一样的
	 * @param fontColor
	 *            Color 字体颜色
	 * @param isBold
	 *            字体是否加粗
	 * @param fontType
	 *            字体样式
	 * @param fontPath
	 *            字体文件
	 * @param tarImgPath
	 *            生成后的图片路径
	 * @return
	 */
	public static void createJpgByFont(String txt, int fontSize,
			Color fontColor, boolean isBold, String fontType, String fontPath,
			String tarImgPath) {
		FileOutputStream out = null;
		try {
			// 如果文字不为空
			if (StringUtils.isNotEmpty(txt)) {

				// ==========设置默认字体样式/是否加粗/大小====
				// 大小
				int defaultFontSize = 20;

				if (fontSize == 0) {
					fontSize = defaultFontSize;
				}
				// 字体样式
				Font font = null;

				if (fontType == null || "".equals(fontType)) {
					fontType = "宋体";
				}

				// BufferedImage bimage =
				// GraphicsEnvironment.getLocalGraphicsEnvironment().
				// getDefaultScreenDevice().getDefaultConfiguration().//宽度 高度
				// createCompatibleImage(getLength(txt)*fontSize+fontSize,fontSize+10,
				// Transparency.TRANSLUCENT);

				// 宽度 高度
				int imageLength = 0;
				if (getLength(txt) < 10) {
					imageLength = getLength(txt) * fontSize + fontSize;
				} else {
					if (fontSize == 20) {
						imageLength = getLength(txt) * fontSize + fontSize + 15;
					} else if (fontSize == 40) {
						imageLength = getLength(txt) * fontSize + fontSize + 25;
					} else {
						imageLength = getLength(txt) * fontSize + fontSize + 35;
					}
				}
				BufferedImage bimage = new BufferedImage(imageLength,
						fontSize + 10, Transparency.TRANSLUCENT);

				Graphics2D g = bimage.createGraphics();

				// 去除锯齿(当设置的字体过大的时候,会出现锯齿)
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				g.setColor(fontColor); // 字的颜色

				// 设置水印字体
				File file = new File(fontPath);
				// 如果是选择的字体文件
				if (file.exists()) {
					font = Font.createFont(Font.TRUETYPE_FONT, file); // 根据字体文件所在位置,创建新的字体对象(此语句在jdk1.5下面才支持)
					g.setFont(font.deriveFont((float) fontSize)); // font.deriveFont(float
																	// f)复制当前
																	// Font
																	// 对象并应用新设置字体的大小
					g.setFont(font);
				} else {
					// 是否加粗
					if (isBold) {
						g.setFont(new Font(fontType, Font.BOLD, fontSize));
					} else {
						g.setFont(new Font(fontType, Font.PLAIN, fontSize));

					}
				}
				txt = new String(txt.getBytes("UTF-8"));
				g.drawString(txt, 0, fontSize); // 在指定坐标除添加文字
				g.dispose();

				// 如果父目录不存在就创建一个
				DoFileUtils.replaceFliePathStr(tarImgPath);
				DoFileUtils.mkdir(tarImgPath); // tarImgPath
				ImageIO.write((BufferedImage) bimage, "PNG", new File(
						tarImgPath));
			}
		} catch (Exception e) {
			logger.error("文字【" + txt + "】创建【 " + tarImgPath + "】水印图片文件失败！");
			e.printStackTrace();
		} finally {
			try {
				if (null != out)
					out.close();
			} catch (Exception e) {
				logger.error("文字【" + txt + "】创建【 " + tarImgPath + "】水印图片文件失败！");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将多个小图片合成一张大jpg图 (小的jpg图片按照行列顺序平铺)
	 * 
	 * @param smallJPG
	 *            ArrayList 一组小的jpg图片
	 * @param bigWidth
	 *            int 大图宽度
	 * @param smallWidth
	 *            int 单个文字生成的小图的宽度和高度是一致的
	 * @param tarImgPath
	 *            目标文件
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private void createBigJPG(ArrayList smallJPG, int bigWidth, int smallWidth,
			Color bgColor, String tarImgPath) {
		FileOutputStream out = null;
		try {
			if (bigWidth < smallWidth) // 如果大图片的高度比小图片的高度还小 直接返回
				return;
			int colCount = bigWidth / smallWidth; // 每行放置的字数
			int leftMargin = (int) ((bigWidth - colCount * smallWidth) / 2f); // 左边距
			int rowCount = smallJPG.size(); // 小图行数
			int setWidth = bigWidth; // 每列中间不留空隙，只留左右边距
			int setHeight = smallWidth * rowCount;
			// 按照大图片宽高绘制一个背景图片
			BufferedImage bufImage = new BufferedImage(setWidth, setHeight,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bufImage.createGraphics();
			g.setColor(bgColor); // 背景的颜色
			g.fillRect(0, 0, setWidth, setHeight);
			int y = 0; // 纵坐标
			for (int i = 0; i < rowCount; i++) { // 遍历每行
				ArrayList col = (ArrayList) (smallJPG.get(i));
				int x = leftMargin; // 横坐标 可能会出现左边距
				for (int j = 0; j < col.size(); j++) {
					String jpgname = (String) (col.get(j));
					ImageIcon icon = new ImageIcon(jpgname);
					Image img = icon.getImage();
					int imgWidth = img.getHeight(null);
					g.drawImage(img, x, y, null);
					x += imgWidth;
				}
				y += (smallWidth);
			}
			g.dispose();
			out = new FileOutputStream(tarImgPath); // 指定输出文件
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out); // 设置文件格式
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufImage); // 从图片缓冲中读取
			param.setQuality(50f, true);
			encoder.encode(bufImage, param); // 存盘
			out.flush();
		} catch (Exception e) {
			logger.error("创建【 " + tarImgPath + "】图片文件失败！");
		} finally {
			try {
				if (null != out)
					out.close();
			} catch (Exception e) {
				logger.error("创建【 " + tarImgPath + "】图片文件失败！");
			}
		}
	}

	/**
	 * 彩色转为黑白
	 * 
	 * @param srcPath
	 * @param tarPath
	 */
	public static void gray(String srcPath, String tarPath) {
		try {
			BufferedImage src = ImageIO.read(new File(srcPath));
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp op = new ColorConvertOp(cs, null);
			src = op.filter(src, null);
			ImageIO.write(src, "JPEG", new File(tarPath));
		} catch (IOException e) {
			logger.error(" 转【 " + srcPath + "】彩色为黑白失败！");
		}
	}

	/**
	 * 给图片添加文字水印、可设置水印的旋转角度
	 * 
	 * @param logoText
	 *            水印文字
	 * @param srcImgPath
	 *            图片路径
	 * @param targerPath
	 *            生成后的图片路径
	 * @param degree
	 *            旋转的度数(-180到180的整数)
	 * @param fontSize
	 *            字体大小
	 * @param color
	 *            字体颜色
	 * @param fontType
	 *            字体类型
	 * @param isBold
	 *            字体是否加粗
	 * @param alpha
	 *            透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public static void imgMarkByText(String logoText, String filePath,
			String targerPath, int degree, int fontSize, Color color,
			String fontType, boolean isBold, String alpha) {

		// 主图片的路径
		InputStream is = null;
		OutputStream os = null;

		try {
			if (DoFileUtils.exitFile(filePath)) {
				Image srcImg = ImageIO.read(new File(filePath));

				BufferedImage buffImg = new BufferedImage(
						srcImg.getWidth(null), srcImg.getHeight(null),
						BufferedImage.TYPE_INT_RGB);

				// 得到画笔对象
				Graphics2D g = buffImg.createGraphics();

				// 设置对线段的锯齿状边缘处理
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				int width = srcImg.getWidth(null);
				int height = srcImg.getHeight(null);

				g.drawImage(srcImg.getScaledInstance(width, height,
						Image.SCALE_SMOOTH), 0, 0, null);

				// ======默认透明度=====================

				if (alpha == null || "".equals(alpha)) {
					alpha = "1";
				}

				// ==================================

				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_ATOP, Float.parseFloat(alpha)));

				// ==========设置默认字体样式/是否加粗/大小====

				// 大小
				int defaultFontSize = 20;

				if (fontSize == 0) {
					fontSize = defaultFontSize;
				}
				// 字体样式
				if (fontType == null || "".equals(fontType)) {
					fontType = "宋体";
				}
				// 是否加粗
				if (isBold) {
					g.setFont(new Font(fontType, Font.BOLD, fontSize));
				} else {
					g.setFont(new Font(fontType, Font.PLAIN, fontSize));

				}

				// =================================

				// 设置水印旋转
				int xValue = 0; // 设置起点坐标的x轴的数值
				int yValue = 0; // 设置起点坐标的y轴的数值
				int xtValue = 0; // 设置水印起点坐标的x轴的数值
				int ytValue = 0; // 设置水印起点坐标的y轴的数值
				// int widthTxt = fontSize * getTxtLength(logoText); //文字的长度
				int wmWidth = getWatermarkLength(logoText, g); // 文字的长度

				int hypotenuseLen = (int) Math.sqrt(width * width + height
						* height); // 图片斜边的长度

				// System.out.println(hypotenuseLen +"------" + width +
				// "-------"+ widthTxt + "-----------" +wmWidth + "-----"
				// +height );

				if (degree >= -90 && degree <= 90) {
					xValue = width / 2;
					yValue = height / 2;
					xtValue = (hypotenuseLen - wmWidth) / 4;
					ytValue = height / 2;
				} else if (degree == 0) {
					xValue = width / 2;
					yValue = height / 2;
					xtValue = (width - wmWidth) / 4;
					ytValue = height / 2;
				} else if (degree == -270) {
					xValue = width / 2;
					yValue = height / 2;
					xtValue = (height - wmWidth) / 2;
					ytValue = height / 2;
				} else {
					logger.error("角度不对");
				}

				g.rotate(Math.toRadians(degree), (double) xValue, yValue);

				// ==========设置默认颜色===============

				if (color == null) {
					g.setColor(Color.BLACK);
				} else {
					g.setColor(color);
				}
				// =================================

				AffineTransform at = new AffineTransform();

				// 使用一个translation变换为要旋转的文本设置空间
				at.setToTranslation(xtValue, ytValue);

				g.transform(at);
				logoText=new String(logoText.getBytes("UTF-8"));
				g.drawString(logoText, 0.0f, 0.0f);
				g.dispose();
				os = new FileOutputStream(targerPath);
				// 生成图片
				ImageIO.write(buffImg, "JPG", os);
				srcImg.flush();
				buffImg.flush();
				logger.info("【 " + filePath + "】图片添加文字水印成功！");
			}
		} catch (Exception e) {
			logger.error("给【 " + filePath + "】图片添加文字水印失败！");
		} finally {
			try {
				if (null != is)
					is.close();
			} catch (Exception e) {
			}
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 给图片添加水印、可设置水印的旋转角度,并调整大小
	 * 
	 * @param logoText
	 *            水印文字
	 * @param srcImgPath
	 *            图片路径
	 * @param targerPath
	 *            生成后的图片路径
	 * @param position
	 *            //位子(0,1,2,3,4) 0:左上角 1:右上角 2:左下角 3:右下角 4:居中
	 * @param fontSize
	 *            字体大小
	 * @param color
	 *            字体颜色
	 * @param fontType
	 *            字体类型
	 * @param isBold
	 *            字体是否加粗
	 * @param alpha
	 *            透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 * @param width
	 *            图片宽度
	 * @param height
	 *            图片高度
	 */
	public static void createNewImgByMark(String logoText, String srcImgPath,
			String targerPath, int position, int fontSize, Color color,
			String fontType, boolean isBold, String alpha, int width, int height) {
		if (DoFileUtils.exitFile(srcImgPath)) {
			if (width <= 0 && height <= 0) {
				pressImgText(logoText, srcImgPath, targerPath, position,
						fontSize, color, fontType, isBold, alpha);
			} else {
				try {
					zoomImg(srcImgPath, targerPath, width, height);
					if (StringUtils.isNotEmpty(targerPath))
						pressImgText(logoText, srcImgPath, targerPath, position,
								fontSize, color, fontType, isBold, alpha);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 添加图片水印---指定图片水印的4个位子(0,1,2,3,4,5)
	 * 
	 * @param wmImg
	 *            //水印文件路径
	 * @param srcImg
	 *            //原文件路径
	 * @param targetImg
	 *            //新文件路径
	 * @param position
	 *            //位子(0,1,2,3,4) 0:左上角 1:右上角 2:左下角 3:右下角 4:居中
	 * @param alpha
	 *            //透明度
	 * @param width
	 *            图片宽度
	 * @param height
	 *            图片高度
	 */
	public static void createNewImgByImg(String wmImg, String srcPath,
			String targetPath, int position, String alpha, int width, int height) {
		if (DoFileUtils.exitFile(srcPath) && DoFileUtils.exitFile(wmImg)) {
			if (width <= 0 && height <= 0) {
				pressImaPosition(wmImg, srcPath, targetPath, position, Float.parseFloat(alpha));
			} else {
				try {
					zoomImg(srcPath, targetPath, width, height);
					if (StringUtils.isNotEmpty(targetPath))
						pressImaPosition(wmImg, targetPath, targetPath,
								position, Float.parseFloat(alpha));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 添加图片水印---指定图片水印的4个位子(0,1,2,3,4,5)
	 * 
	 * @param wmImg
	 *            //水印文件路径
	 * @param srcImg
	 *            //原文件路径
	 * @param targetImg
	 *            //新文件路径
	 * @param position
	 *            //位子(0,1,2,3,4,5) 0:左上角 1:右上角 2:左下角 3:右下角 4:居中 5:平铺
	 * @param alpha
	 *            //透明度
	 */
	public final static void pressImaPosition(String wmImg, String srcPath,
			String targetPath, int position, float alpha) {
		try {
			srcPath = DoFileUtils.replaceFliePathStr(srcPath).replaceAll("//",
					"/");
			targetPath = DoFileUtils.replaceFliePathStr(targetPath).replaceAll(
					"//", "/");
			File img = null;
			File targetImg = new File(targetPath);
			if (!srcPath.equals(targetPath)) {
				img = new File(srcPath);
				FileUtil.copyFile(img, targetImg);
			}
			Image src = ImageIO.read(targetImg);
			int wideth = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(wideth, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, wideth, height, null);
			// 水印文件
			Image src_biao = ImageIO.read(new File(wmImg));
			int wideth_biao = src_biao.getWidth(null);
			int height_biao = src_biao.getHeight(null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));

			// 左下角距离边距10像素
			int leftDownWideth = 10;
			int leftDownHeight = (height - height_biao) - 10;

			// 右下角距离边距10像素
			int rightDownWideth = (wideth - wideth_biao) - 10;
			int rightDownHeight = (height - height_biao) - 10;

			// 左上角距离边距10像素
			int leftUpWideth = 10;
			int leftUpHeight = 10;

			// 右上角距离边距10像素
			int rightUpWideth = (wideth - wideth_biao) - 10;
			int rightUpHeight = 10;

			int x = 0;
			int y = 0;

			switch (position) {
			case 0:
				x = leftUpWideth;
				y = leftUpHeight;
				g.drawImage(src_biao, x, y, wideth_biao, height_biao, null);
				break;
			case 1:
				x = rightUpWideth;
				y = rightUpHeight;
				g.drawImage(src_biao, x, y, wideth_biao, height_biao, null);
				break;
			case 2:
				x = leftDownWideth;
				y = leftDownHeight;
				g.drawImage(src_biao, x, y, wideth_biao, height_biao, null);
				break;
			case 3:
				x = rightDownWideth;
				y = rightDownHeight;
				g.drawImage(src_biao, x, y, wideth_biao, height_biao, null);
				break;
			case 4:
				g.drawImage(src_biao, (wideth - wideth_biao) / 2,
						(height - height_biao) / 2, wideth_biao, height_biao,
						null);
				break;
			case 5:
				// 横向平铺水印
				for (int imageX = 0; imageX < wideth; imageX += wideth_biao) {
					int drawRow = 0;
					// 纵向平铺水印
					for (int imageY = drawRow * height_biao; imageY < height; imageY += height_biao) {
						g.drawImage(src_biao, imageX, imageY, wideth_biao,
								height_biao, null);
						drawRow++;
					}
				}
				break;
			default:
				break;
			}
			// 水印文件结束
			g.dispose();
			ImageIO.write((BufferedImage) image, "JPG", targetImg);
		} catch (Exception e) {
			logger.error("给【 " + srcPath + "】添加图片水印失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 图片加文字文字(五个位置，分别是图片的左上角，右上角，中间，左下角，右下角）对应参数 int weizhi 可以取0到4)
	 * 
	 * @param pressText
	 *            //水印文字
	 * @param srcPath
	 *            //原图片路径
	 * @param targetPath
	 *            //新图片路径
	 * @param position
	 *            //位子 0:左上角 1:右上角 2:左下角 3:右下角 4:居中
	 * @param fontSize
	 *            //字体大小
	 * @param color
	 *            //字体颜色
	 * @param fontName
	 *            //字体
	 * @param isBold
	 *            字体是否加粗
	 * @param alpha
	 *            透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public static void pressImgText(String pressText, String srcPath,
			String targetPath, int position, int fontSize, Color color,
			String fontName, boolean isBold, String alpha) {
		try {
			srcPath = DoFileUtils.replaceFliePathStr(srcPath).replaceAll("//",
					"/");
			targetPath = DoFileUtils.replaceFliePathStr(targetPath).replaceAll(
					"//", "/");
			File img = null;
			File targetImg = new File(targetPath);
			if (!srcPath.equals(targetPath)) {
				img = new File(srcPath);
				FileUtil.copyFile(img, targetImg);
			}
			Image src = ImageIO.read(targetImg);
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, width, height, null);

			// ==========设置默认字体样式/颜色/是否加粗/大小====

			if (color == null) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(color);
			}

			// 大小
			int defaultFontSize = 20;

			if (fontSize == 0) {
				fontSize = defaultFontSize;
			}

			// 字体样式
			if (fontName == null || "".equals(fontName)) {
				fontName = "宋体";
			}
			// 是否加粗
			if (isBold) {
				g.setFont(new Font(fontName, Font.BOLD, fontSize));
			} else {
				g.setFont(new Font(fontName, Font.PLAIN, fontSize));
			}

			// 设置默认的透明度
			if (StringUtils.isEmpty(alpha) || alpha.equals("0")) {
				alpha = "0.5";
			}

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					Float.parseFloat(alpha)));

			// 左上角
			int leftUpWideth = 10;
			int leftUpHeight = fontSize + 10;

			// 右上角
			int rightUpWideth = (width - (getLength(pressText) * fontSize)) - 30;
			int rightUpHeight = fontSize + 10;

			// 左下角
			int leftDownWideth = 10;
			int leftDownHeight = height - fontSize;

			// 右下角
			int rightDownWideth = (width - (getLength(pressText) * fontSize)) - 30;
			int rightDownHeight = height - fontSize;

			int x = 0;
			int y = 0;

			switch (position) {
			case 0:
				x = leftUpWideth;
				y = leftUpHeight;
				g.drawString(pressText, x, y);
				break;
			case 1:
				x = rightUpWideth;
				y = rightUpHeight;
				g.drawString(pressText, x, y);
				break;
			case 2:
				x = leftDownWideth;
				y = leftDownHeight;
				g.drawString(pressText, x, y);
				break;
			case 3:
				x = rightDownWideth;
				y = rightDownHeight;
				g.drawString(pressText, x, y);
				break;
			case 4:
				g.drawString(pressText, width / 2 - 2 * fontSize, height / 2);
				break;
			default:
				break;
			}

			g.dispose();
			ImageIO.write((BufferedImage) image, "JPG", targetImg);
		} catch (Exception e) {
			logger.error("给【 " + srcPath + "】添加文字水印失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 通过 ImageMagick 的 convert 转换成实现psd文件转换成其他图片
	 * 
	 * @param srcImagePath
	 *            psd文件(原)
	 * @param tarImagePath
	 *            转换后的新图片(新) 说明： -resize \"1024x768>\" 定义转换后的尺寸大小 -layers
	 *            flatten 合并图层
	 * @return
	 */
	public static boolean processImage2Other(String srcImagePath,
			String tarImagePath) {
		boolean boo = true;
		if (!DoFileUtils.exitFile(srcImagePath)) {
			logger.error("【" + srcImagePath + "】  不存在 !");
			return false;
		}
		Process process = null;
		try {
			if (StringUtils.isNotEmpty(imgConverPath)) {
				process = Runtime.getRuntime().exec(
						DoFileUtils.getChRealPath(imgConverPath)
								+ "  -layers flatten " + srcImagePath + " "
								+ tarImagePath);
				display(process);
				if (process != null) {
					process.destroy();
				}
			}
		} catch (IOException e) {
			boo = false;
			logger.error("转换【 " + srcImagePath + "】psd文件失败！");
		}
		return boo;
	}

	/**
	 * 等待处理的进程
	 * 
	 * @param process
	 * @throws IOException
	 */
	private static void display(Process process) throws IOException {
		String s;
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		while ((s = bufferedReader.readLine()) != null) {
			logger.debug(s);
		}
	}

	/**
	 * 取得汉字的长度
	 * 
	 * @param text
	 * @return
	 */
	public static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}

	// 获取水印文字总长度
	private static int getWatermarkLength(String str, Graphics2D g) {
		if (StringUtils.isNotEmpty(str)) {
			return g.getFontMetrics(g.getFont()).charsWidth(str.toCharArray(),
					0, str.length());
		} else {
			return 0;
		}
	}
	
	


	/**
	 * @param fontSizeType
	 *            通过水印类型来设置水印字体大小 01：大 02：中 03：小
	 * @return
	 */
	public static int getFontSize(int fontSizeType) {
		int fontSize = 0;
		if (fontSizeType == 01) {
			fontSize = 60;
		} else if (fontSizeType == 02) {
			fontSize = 40;
		} else if (fontSizeType == 03) {
			fontSize = 20;
		}
		return fontSize;
	}

	/**
	 * @param fontSizeType
	 *            通过水印方位来设置水印的角度 01：左上角 02：左下角 03 : 右上角 04：右下角
	 * @return
	 */
	public static int getWmDegreeOld(int wmPosition) {
		wmPosition = wmPosition + 1;
		int wmDegree = -45; // 默认为-45度角
		if (wmPosition == 01) {
			wmDegree = 45;
		} else if (wmPosition == 02) {
			wmDegree = -45;
		} else if (wmPosition == 03) {
			wmDegree = 0;
		} else if (wmPosition == 04) {
			wmDegree = -270;
		}
		return wmDegree;
	}

	public static void main(String[] agrs) throws MagickException {
		// processImage2Other("D:/Project素材/image/aaa1.psd","D:/Project素材/image/aaaaa.png");
		// converToOther("D:/Project素材/image/5.tif","D:/Project素材/image/11111.jpg");
		// jpgTotif("D:/Project素材/image/22222.bmp","D:/Project素材/image/5.tif");
		// String inputFile = "D:/Project素材/image/5.tif";
		// String outputFile = "D:/Project素材/image/1111.bmp";
		// tifToBmp(inputFile,outputFile);
		// rotateImage("D:/Project素材/image/1.jpg", "D:/Project素材/image/2.jpg",
		// "D:/Project素材/image/11.jpg", 30, "1");
		// initLogoImg("D:/Project素材/image/1.jpg", "D:/Project素材/image/2.jpg",
		// "D:/Project素材/image/11.jpg");
		// pressImage("D:/Project素材/image/111111.jpg",
		// "D:/Project素材/image/2.jpg", 800,600,1f);
		// pressImagePosition("D:/Project素材/image/111111.jpg",
		// "D:/Project素材/image/2.jpg","D:/Project素材/image/6.jpg",2,0.3f);
		// pressImage("D:/Project素材/image/111111.jpg",
		// "D:/Project素材/image/6.jpg", 2);
		try {
			zoomImg("C:/temp/xml/005/9787502042585/cover/cover.jpg","C:/temp/xml/005/9787502042585/cover/cover_min1.jpg",60, 80);
			//zoomSureImg("C:/temp/xml/005/9787502042585/cover/cover.jpg","C:/temp/xml/005/9787502042585/cover/cover_min2.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// zoomSureImg("D:/Project素材/image/你好.JPG",
		// "D:/Project素材/image/3333333.jpg");
		// converToOther("D:\\old\\4.gif",
		// "D:\\byyxproject\\test\\new\\1000.jpg");
		// System.out.println(getImgInfo("D:\\9.mp4"));
		// ImgYin("测试测试测试哈哈","E:/11.jpg");
		// pressImage("E:/3.jpg","E:/11.jpg",100,18,0.5f);
		// pressText("测试test.zz","E:/11.jpg","宋体",Font.BOLD,Color.red,20,20,20,1.0f);
		// pressImgText("我是水印字",
		// "D:/Project素材/image/2.jpg","D:/Project素材/image/6.jpg",4,150,Color.blue,"宋体",true,"");
		// createNewImgByMark("我是水印字",
		// "D:/Project素材/image/3.jpg","D:/Project素材/image/6.jpg",3,20,Color.blue,"宋体",true,"",0,0);
		//createJpgByFont("http://www.dajia.com.cn.jkr淡淡的trtppnn",60,Color.blue,true,"宋体","","d:/89.png");
	}

}
