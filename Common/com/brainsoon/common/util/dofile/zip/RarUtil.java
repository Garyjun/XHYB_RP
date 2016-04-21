package com.brainsoon.common.util.dofile.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.common.util.dofile.image.ImageUtils;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.util.MD5Util;
import com.brainsoon.semantic.ontology.model.Ca;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

/**
 * 
 * @ClassName: RarUtil 
 * @Description:  rar文件解压工具类
 * @author tanghui 
 * @date 2013-10-28 下午5:44:44 
 *
 */
public class RarUtil {
	private static Log logger = LogFactory.getLog(RarUtil.class);
			
	public static void unZip(String sourceFile,String destDir) {
		ZipInputStream zipinputstream = null;
		ZipEntry zipentry = null;
		try {
			// destination folder to extract the contents
			byte[] buf = new byte[1024];
			zipinputstream = new ZipInputStream(new FileInputStream(sourceFile));
			zipentry =  zipinputstream.getNextEntry();
			while (zipentry != null) {

				// for each entry to be extracted
				String entryName = zipentry.getName();

				System.out.println(entryName);

				int n;
				FileOutputStream fileoutputstream;
				File newFile = new File(entryName);

				String directory = newFile.getParent();

				// to creating the parent directories
				if (directory == null) {
					if (newFile.isDirectory()) {
						break;
					}
				} else {
					new File(destDir + directory).mkdirs();
				}

				if (!zipentry.isDirectory()) {
					System.out.println("File to be extracted....." + entryName);
					fileoutputstream = new FileOutputStream(destDir + entryName);
					while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
						fileoutputstream.write(buf, 0, n);
					}
					fileoutputstream.close();
				}

				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != zipinputstream) {
				try {
					zipinputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void unRar(String sourceFile,String destDir) {
		File archive = new File(sourceFile);
		File destination = new File(destDir);
		Archive arch = null;
		FileHeader fh = null;
		try {
			arch = new Archive(archive);
			if (arch != null) {
				if (arch.isEncrypted()) {
					System.out.println("archive is encrypted cannot extreact");
					return;
				}
				while (true) {
					fh = arch.nextFileHeader();
					if (fh == null) {
						break;
					}
					if (fh.isEncrypted()) {
						System.out.println("file is encrypted cannot extract: "
								+ fh.getFileNameString());
						continue;
					}
					try {
						if (fh.isDirectory()) {
							createDirectory(fh, destination);
						} else {
							File f = createFile(fh, destination);
							OutputStream stream = new FileOutputStream(f);
							arch.extractFile(fh, stream);
							stream.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fh = null;
			if (null != arch) {
				try {
					arch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Ca unRarCa(String sourceFile,String destDir,Ca ca,String fileDoi) {
		File archive = new File(sourceFile);
		Archive arch = null;
		FileHeader fh = null;
		try {
			arch = new Archive(archive);
			if (arch != null) {
				if (arch.isEncrypted()) {
					System.out.println("archive is encrypted cannot extreact");
					return null;
				}
				List<com.brainsoon.semantic.ontology.model.File> fileList=new ArrayList<com.brainsoon.semantic.ontology.model.File>();
                com.brainsoon.semantic.ontology.model.File uploadFile = null;
        //        Map<Integer,Map<String,String>> tempMap = new HashMap<Integer,Map<String,String>>();
                Map<String,String> dirMap = new HashMap<String,String>();
                String middlePath = destDir.replaceAll(BresAction.FILE_ROOT, "")+File.separator;
                int j=0;
				while (true) {
					fh = arch.nextFileHeader();
					if (fh == null) {
						break;
					}
					if (fh.isEncrypted()) {
						System.out.println("file is encrypted cannot extract: "
								+ fh.getFileNameString());
						continue;
					}
					try {
					    String time = new Date().getTime()+"";
	                    String path = "";
	        			String tempNum ="";
						if (fh.isDirectory()) {
						} else {
							String name = null;
							if (fh.isFileHeader() && fh.isUnicode()) {
								name = fh.getFileNameW().trim();
							} else {
								name = fh.getFileNameString().trim();
							}
							name = name.replaceAll("\\\\", "/");
							if(name.indexOf("/")>0){
								String[] dirs = name.split("/");
								String dirName = "";
								for(int k=0;k<dirs.length;k++){
									String realName = dirs[k];
									dirName += dirs[k]+"/";
									String pid = "-1";
									if(k == dirs.length-1){
										j++;
										String absPath = dirs[k];
										String fileType = absPath.substring(absPath.lastIndexOf(".")+1,absPath.length());
										String tempDir = dirName.substring(0,dirName.length()-1);
										if(tempDir.indexOf("/")>0){
											String lastPath = tempDir.substring(0,tempDir.lastIndexOf("/")+1);
											if(dirMap.get(lastPath)!=null){
												path = dirMap.get(lastPath) +(k+1)+""+j+"."+fileType;
												RarUtil.createCaFile(arch, fh, destDir + File.separator + path);
												File newFile = new File(destDir + File.separator + path);
												uploadFile = new com.brainsoon.semantic.ontology.model.File();
												uploadFile.setAliasName((k+1)+""+j+"."+fileType);
												uploadFile.setName(realName);
												uploadFile.setPath(middlePath+path);
												uploadFile.setFileType(fileType);
												uploadFile.setFileByte(newFile.length() + "");
												uploadFile.setCreate_time(time);
												uploadFile.setCreator(ca.getCreator());
												uploadFile.setMd5(MD5Util.getFileMD5String(newFile));
												String serialNumber = "";
												String serialNumberTemp=  j+"";
												for(int n=0;n<4-serialNumberTemp.length();n++){
													serialNumber += "0";
												}
												serialNumber = serialNumber+serialNumberTemp;
												uploadFile.setIdentifier(fileDoi+"."+serialNumber);
												uploadFile.setModified_time(time);
												uploadFile.setVersion("1");
												uploadFile.setIsDir("2");
												uploadFile.setId(path);
												uploadFile.setPid(dirMap.get(lastPath));
												fileList.add(uploadFile);
											}
										}else{
											path = (k+1)+""+j+"."+fileType;
											RarUtil.createCaFile(arch, fh, destDir + File.separator + path);
											File newFile = new File(destDir + File.separator + path);
											uploadFile = new com.brainsoon.semantic.ontology.model.File();
											uploadFile.setAliasName((k+1)+""+j+"."+fileType);
											uploadFile.setName(realName);
											uploadFile.setPath(middlePath+path);
											uploadFile.setFileType(fileType);
											uploadFile.setFileByte(newFile.length() + "");
											uploadFile.setCreate_time(time);
											uploadFile.setCreator(ca.getCreator());
											uploadFile.setMd5(MD5Util.getFileMD5String(newFile));
											uploadFile.setModified_time(time);
											uploadFile.setVersion("1");
											String serialNumber = "";
											String serialNumberTemp=  j+"";
											for(int n=0;n<4-serialNumberTemp.length();n++){
												serialNumber += "0";
											}
											serialNumber = serialNumber+serialNumberTemp;
											uploadFile.setIdentifier(fileDoi+"."+serialNumber);
											uploadFile.setIsDir("2");
											uploadFile.setId(path);
											uploadFile.setPid(pid);
											fileList.add(uploadFile);
										}
									}else{
										if(dirMap.get(dirName) == null){
											j++;
											String tempDir = dirName.substring(0,dirName.length()-1);
											if(tempDir.indexOf("/")>0){
												String lastPath = tempDir.substring(0,tempDir.lastIndexOf("/")+1);
												if(dirMap.get(lastPath)!=null){
													path = dirMap.get(lastPath) +(k+1)+""+j+File.separator;
													pid = dirMap.get(lastPath);
												}
											}else{
												if(dirMap.get(tempDir)!=null){
													path = dirMap.get(tempDir) +(k+1)+""+j+File.separator;
													pid = dirMap.get(tempDir);
												}else{
													path = (k+1)+""+j+File.separator;
												}
											}
											dirMap.put(dirName,path);
											File tempFile= new File(destDir + File.separator + path);
	    									if(!tempFile.exists()){
	    										FileUtils.forceMkdir(tempFile);
	    										uploadFile = new com.brainsoon.semantic.ontology.model.File();
	    		        						uploadFile.setPath(middlePath+path);
	    		        						uploadFile.setCreate_time(time);
	    		        						uploadFile.setIsDir("1");
	    		        						uploadFile.setCreator(ca.getCreator());
	    		        						uploadFile.setCaId(ca.getObjectId());
	    		        						uploadFile.setName(realName);
	    		        						uploadFile.setAliasName((k+1)+""+j);
	    		        						uploadFile.setId(path);
	    		        						uploadFile.setPid(pid);
	    		        						fileList.add(uploadFile);
	    									}
										}
									}
								}
							}else{
								int k=0;
								j++;
								String fileType = name.substring(name.lastIndexOf(".")+1,name.length());
								RarUtil.createCaFile(arch, fh, destDir + File.separator + name);
								File newFile = new File(destDir + File.separator + name);
								uploadFile = new com.brainsoon.semantic.ontology.model.File();
								uploadFile.setAliasName((k+1)+""+j+"."+fileType);
								uploadFile.setName(name);
								uploadFile.setPath(middlePath+name);
								uploadFile.setFileType(fileType);
								uploadFile.setFileByte(newFile.length() + "");
								uploadFile.setCreate_time(time);
								uploadFile.setCreator(ca.getCreator());
								uploadFile.setMd5(MD5Util.getFileMD5String(newFile));
								uploadFile.setModified_time(time);
								uploadFile.setVersion("1");
								String serialNumber = "";
								String serialNumberTemp=  j+"";
								for(int n=0;n<4-serialNumberTemp.length();n++){
									serialNumber += "0";
								}
								serialNumber = serialNumber+serialNumberTemp;
								uploadFile.setIdentifier(fileDoi+"."+serialNumber);
								uploadFile.setIsDir("2");
								uploadFile.setId(name);
								uploadFile.setPid("-1");
								fileList.add(uploadFile);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				ca.setRealFiles(fileList);
			}
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fh = null;
			if (null != arch) {
				try {
					arch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ca;
	}
	public static void unRar(String sourceFile) {
		File archive = new File(sourceFile);
		String destDir = archive.getParent();
		File destination = new File(destDir);
		//logger.info("解压后的路径为：" + destDir);
		Archive arch = null;
		FileHeader fh = null;
		try {
			arch = new Archive(archive);
			if (arch != null) {
				if (arch.isEncrypted()) {
					System.out.println("archive is encrypted cannot extreact");
					return;
				}
				while (true) {
					fh = arch.nextFileHeader();
					if (fh == null) {
						break;
					}
					if (fh.isEncrypted()) {
						System.out.println("file is encrypted cannot extract: "
								+ fh.getFileNameString());
						continue;
					}
					try {
						if (fh.isDirectory()) {
							createDirectory(fh, destination);
						} else {
							File f = createFile(fh, destination);
							OutputStream stream = new FileOutputStream(f);
							arch.extractFile(fh, stream);
							stream.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			}
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fh = null;
			if (null != arch) {
				try {
					arch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private static void createCaFile(Archive arch,FileHeader fh,String destinationPath){
		File tempFile= new File(destinationPath);
		if(!tempFile.exists()){
			OutputStream stream;
			try {
				stream = new FileOutputStream(tempFile);
				arch.extractFile(fh, stream);
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static File createFile(FileHeader fh, File destination) {
		File f = null;
		String name = null;
		if (fh.isFileHeader() && fh.isUnicode()) {
			name = fh.getFileNameW().trim();
//		if(!existZH(name)){
//			name = fh.getFileNameString().trim();
		} else {
			name = fh.getFileNameString().trim();
		}
		f = new File(destination, name);
		if (!f.exists()) {
			try {
				f = makeFile(destination, name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return f;
	}

	public static boolean existZH(String str) {
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		while (m.find()) {
		return true;
		}
		return false;
	}
	
	private static File makeFile(File destination, String name) throws IOException {
		String[] dirs = name.split("\\\\");
		if (dirs == null) {
			return null;
		}
		String path = "";
		int size = dirs.length;
		if (size == 1) {
			return new File(destination, name);
		} else if (size > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				path = path + File.separator + dirs[i];
				new File(destination, path).mkdir();
			}
			path = path + File.separator + dirs[dirs.length - 1];
			File f = new File(destination, path);
			f.createNewFile();
			return f;
		} else {
			return null;
		}
	}

	private static void createDirectory(FileHeader fh, File destination) {
		File f = null;
		String fileName = "";
		if (fh.isDirectory() && fh.isUnicode()) {
			fileName = fh.getFileNameW();
		} else if (fh.isDirectory() && !fh.isUnicode()) {
			fileName = fh.getFileNameString();
		}
		System.out.println(fileName+"--------");
		f = new File(destination, fileName);
		if (!f.exists()) {
			makeDirectory(destination, fileName);
		}
	}

	private static void makeDirectory(File destination, String fileName) {
		String[] dirs = fileName.split("\\\\");
		if (dirs == null) {
			return;
		}
		String path = "";
		for (String dir : dirs) {
			path = path + File.separator + dir;
			new File(destination, path).mkdir();
		}
	}

	/**
	 * 
	* @Title: unRarCaNew
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param sourceFile	rar压缩包路径
	* @param destDir	保存资源的路径（目标路径）
	* @param ca	要存储的Ca资源
	* @param fileDoi
	* @param importCoverType 导入封面类型
	* @return    参数
	* @return Ca    返回类型
	* @throws
	 */
	public static Ca unRarCaNew(String sourceFile,String destDir,Ca ca,String fileDoi) {
		File archive = new File(sourceFile);
		Archive arch = null;
		FileHeader fh = null;
		String zipName = archive.getName().substring(0,archive.getName().lastIndexOf("."));
		boolean isFirst = true;//是否执行过一次封面改名操作，开始默认为true 执行过之后 改为false
		try {
			arch = new Archive(archive);
			if (arch != null) {
				if (arch.isEncrypted()) {
					System.out.println("archive is encrypted cannot extreact");
					logger.info("【RarUtil】unRarCaNew 报错：archive is encrypted cannot extreact");
					return null;
				}
				List<com.brainsoon.semantic.ontology.model.File> fileList=new ArrayList<com.brainsoon.semantic.ontology.model.File>();
                com.brainsoon.semantic.ontology.model.File uploadFile = null;
               // Map<String,String> dirMap = new HashMap<String,String>();//创建过的文件夹
                Set<String> dirSet = new HashSet<String>();
                String middlePath = destDir.replaceAll(BresAction.FILE_ROOT, "")+File.separator;
				while (true) {
					fh = arch.nextFileHeader();
					if (fh == null) {
						break;
					}
					if (fh.isEncrypted()) {
						System.out.println("file is encrypted cannot extract: " + fh.getFileNameString());
						logger.info("【RarUtil】unRarCaNew 报错：file is encrypted cannot extract: " + fh.getFileNameString());
						continue;
					}
					try {
					    String time = new Date().getTime()+"";
	                    String path = "";
	        			String tempNum ="";
						if (fh.isDirectory()) {//不会走这里
						} else {//直接循环文件
							String name = null;
							if (fh.isFileHeader() && fh.isUnicode()) {
								name = fh.getFileNameW().trim();
							} else {
								name = fh.getFileNameString().trim();
							}
							name = name.replaceAll("\\\\", "/");
							if(name.indexOf("/")>0){	//test/cover/123.jpg
								String[] dirs = name.split("/");
								String dirName = "";
								for(int k=0;k<dirs.length;k++){	//k=0	k=1		k=2
									String realName = dirs[k];	//test	cover	123.jpg
									dirName += dirs[k]+"/";		//test/	test/cover/	test/cover123.jpg
									String pid = "-1";
									if(k == dirs.length-1){//是不是到文件了  即123.jpg
										String absPath = dirs[k];
										String fileType = absPath.substring(absPath.lastIndexOf(".")+1,absPath.length());
										String tempDir = dirName.substring(0,dirName.length()-1);
										String lastPath = tempDir.substring(0,tempDir.lastIndexOf("/")+1);
										if(dirSet.contains(lastPath)){
											path = lastPath +realName;
											File newFile = null;
											File minFile = null;
											
											//importCoverType=2 有cover目录但是没有cover.jpg文件  将cover目录下的第一个文件改名为cover.jpg
											if (isFirst) {//没有执行过改名
												if (lastPath.contains("cover/") || lastPath.contains("Cover/")) {
					                    			if ("jpg".equals(fileType) || "png".equals(fileType)) {
					                    				newFile = new File(destDir + File.separator+ lastPath + "cover.jpg");
					                    				minFile = new File(destDir.replace("fileRoot", "viewer") + File.separator+ lastPath + "cover_min.jpg");
					                    				if (!newFile.exists()) {
					                    					RarUtil.createCaFile(arch, fh, destDir  + File.separator+ lastPath + "cover.jpg");
					                    					logger.info("【RarUtil】处理封面图片 ->>>封面原路径："+destDir+File.separator+path+" 改名后路径："+newFile.getAbsolutePath());
					                    					uploadFile = new com.brainsoon.semantic.ontology.model.File();
					                    					uploadFile.setAliasName("cover.jpg");
					                    					uploadFile.setName("cover.jpg");
					                    					uploadFile.setPath(middlePath + File.separator+ lastPath + "cover.jpg");
					                    					uploadFile.setFileType(fileType);
					                    					uploadFile.setFileByte(newFile.length() + "");
					                    					uploadFile.setCreate_time(time);
					                    					uploadFile.setCreator(ca.getCreator());
					                    					uploadFile.setMd5(MD5Util.getFileMD5String(newFile));
					                    					uploadFile.setModified_time(time);
					                    					uploadFile.setVersion("1");
					                    					uploadFile.setIsDir("2");
					                    					uploadFile.setId(lastPath+"cover.jpg");
					                    					uploadFile.setPid(lastPath);
					                    					fileList.add(uploadFile);
					                    					ca.getMetadataMap().put("cover", (middlePath+lastPath + "cover.jpg").replaceAll("\\\\", "/").replaceAll("//", "/"));
					                    					isFirst = false;//执行过一次之后  之后的文件就不改名的
					                    					
					                    					
					                    					//缩略图
					                    					if (!minFile.getParentFile().exists()) {
																FileUtils.forceMkdir(minFile.getParentFile());
															}
					                    					ImageUtils.zoomImg(newFile.getAbsolutePath(), minFile.getAbsolutePath(),60, 80);
					    									logger.info("【ResUtils】 处理封面图片 ->>>封面原路径：->>>封面缩略图路径："+minFile.getAbsolutePath());
					                    					continue;
														}
					                    			}
					                    		}
											}
											
											//importCoverType=3  没有cover目录但是有cover.jpg文件（但是在资源下 和main。xml同级）  创建cover目录 并将cover.jpg挪到创建的cover目录下
											if (zipName.equals(lastPath.substring(0,lastPath.length()-1)) && "cover.jpg".equals(realName)) {//  没有cover目录，在根目录下就是Cover.jpg
												//创建cover目录
												File cover= new File( destDir + File.separator + lastPath + "cover");
												if (!cover.exists()) {
													FileUtils.forceMkdir(cover);
		    										logger.info("【RarUtil】unRarCaNew 创建cover目录信息："+ cover.getAbsolutePath());
		    										uploadFile = new com.brainsoon.semantic.ontology.model.File();
		    		        						uploadFile.setPath(middlePath+ lastPath + "cover");
		    		        						uploadFile.setCreate_time(time);
		    		        						uploadFile.setIsDir("1");
		    		        						uploadFile.setCreator(ca.getCreator());
		    		        						uploadFile.setCaId(ca.getObjectId());
		    		        						uploadFile.setName("cover");
		    		        						uploadFile.setAliasName("cover");
		    		        						uploadFile.setId(lastPath + "cover" + File.separator);
		    		        						uploadFile.setPid(lastPath);
		    		        						fileList.add(uploadFile);
												}
												//拷贝cover.jpg
												RarUtil.createCaFile(arch, fh, destDir + File.separator + lastPath + File.separator + "cover" + File.separator + realName);
												newFile = new File(destDir + File.separator + lastPath + File.separator + "cover" + File.separator + realName);
												logger.info("【RarUtil】处理封面图片 ->>>封面原路径："+destDir + File.separator + lastPath+ File.separator + realName+" 改名后路径："+newFile.getAbsolutePath());
												uploadFile = new com.brainsoon.semantic.ontology.model.File();
												uploadFile.setAliasName(realName);
												uploadFile.setName(realName);
												uploadFile.setPath(middlePath+ File.separator + lastPath + "cover" + File.separator +realName);
												uploadFile.setFileType(fileType);
												uploadFile.setFileByte(newFile.length() + "");
												uploadFile.setCreate_time(time);
												uploadFile.setCreator(ca.getCreator());
												uploadFile.setMd5(MD5Util.getFileMD5String(newFile));
												uploadFile.setModified_time(time);
												uploadFile.setIsDir("2");
												uploadFile.setId(lastPath + "cover" + File.separator +realName);
												uploadFile.setPid(lastPath + "cover" + File.separator);
												fileList.add(uploadFile);
												ca.getMetadataMap().put("cover", (middlePath+ File.separator + lastPath + "cover" + File.separator +realName).replaceAll("\\\\", "/").replaceAll("//", "/"));
												
												//缩略图
												minFile = new File(destDir.replace("fileRoot", "viewer") + File.separator + lastPath + File.separator + "cover" + File.separator + "cover_min.jpg");
												if (!minFile.getParentFile().exists()) {
													FileUtils.forceMkdir(minFile.getParentFile());
												}
												ImageUtils.zoomImg(newFile.getAbsolutePath(), minFile.getAbsolutePath(),60, 80);
		    									logger.info("【ResUtils】 处理封面图片 ->>>封面原路径：->>>封面缩略图路径："+minFile.getAbsolutePath());
												continue;
											}
											
											//正常走的逻辑
											newFile = new File(destDir + File.separator + path);
											RarUtil.createCaFile(arch, fh, destDir + File.separator + path);
											logger.info("【RarUtil】unRarCaNew 正常逻辑 获取file信息 拷贝文件路径:" + newFile.getAbsolutePath());
											uploadFile = new com.brainsoon.semantic.ontology.model.File();
											uploadFile.setAliasName(realName);
											uploadFile.setName(realName);
											uploadFile.setPath(middlePath+path);
											uploadFile.setFileType(fileType);
											uploadFile.setFileByte(newFile.length() + "");
											uploadFile.setCreate_time(time);
											uploadFile.setCreator(ca.getCreator());
											uploadFile.setMd5(MD5Util.getFileMD5String(newFile));
											uploadFile.setModified_time(time);
											uploadFile.setVersion("1");
											uploadFile.setIsDir("2");
											uploadFile.setId(path);
											uploadFile.setPid(lastPath);
											fileList.add(uploadFile);
											if ("cover.jpg".equals(realName)) {
												ca.getMetadataMap().put("cover", (middlePath+path).replaceAll("\\\\", "/").replaceAll("//", "/"));
												//缩略图
												minFile = new File((destDir.replace("fileRoot", "viewer") + File.separator + path).replace("cover.jpg", "cover_min.jpg"));
												if (!minFile.getParentFile().exists()) {
													FileUtils.forceMkdir(minFile.getParentFile());
												}
												ImageUtils.zoomImg(newFile.getAbsolutePath(), minFile.getAbsolutePath(),60, 80);
		    									logger.info("【ResUtils】 处理封面图片 ->>>封面原路径：->>>封面缩略图路径："+minFile.getAbsolutePath());
											}
										}
									}else{//到文件夹
										if(!dirSet.contains(dirName)){//这个目录还没有创建
											String tempDir = dirName.substring(0,dirName.length()-1);
											if(tempDir.indexOf("/")>0){//不是第一级目录
												String lastPath = tempDir.substring(0,tempDir.lastIndexOf("/")+1);//上一级目录
												path = dirName;
												pid = lastPath;
											}else{//是第一级文件夹
												path = dirName;
												pid = "-1";
											}
 											dirSet.add(dirName);
											File tempFile= new File(destDir + File.separator + path);
	    									if(!tempFile.exists()){
	    										FileUtils.forceMkdir(tempFile);
	    										logger.info("【RarUtil】unRarCaNew 正常逻辑 获取file文件夹信息 拷贝文件夹路径:" + tempFile.getAbsolutePath());
	    										uploadFile = new com.brainsoon.semantic.ontology.model.File();
	    		        						uploadFile.setPath(middlePath+path);
	    		        						uploadFile.setCreate_time(time);
	    		        						uploadFile.setIsDir("1");
	    		        						uploadFile.setCreator(ca.getCreator());
	    		        						uploadFile.setCaId(ca.getObjectId());
	    		        						uploadFile.setName(realName);
	    		        						uploadFile.setAliasName(realName);
	    		        						uploadFile.setId(path);
	    		        						uploadFile.setPid(pid);
	    		        						fileList.add(uploadFile);
	    									}
										}
									}
								}
							}else{//解压下面就是文件  不在文件夹里面
								logger.info("【RarUtil】unRarCaNew 和资源目录同级  一般情况不会发生");
								System.out.println("12222222222222222222222和资源目录同级  一般情况不会发生");
								String fileType = name.substring(name.lastIndexOf(".")+1,name.length());
								RarUtil.createCaFile(arch, fh, destDir + File.separator + name);
								File newFile = new File(destDir + File.separator + name);
								uploadFile = new com.brainsoon.semantic.ontology.model.File();
								uploadFile.setAliasName(name);
								uploadFile.setName(name);
								uploadFile.setPath(middlePath+name);
								uploadFile.setFileType(fileType);
								uploadFile.setFileByte(newFile.length() + "");
								uploadFile.setCreate_time(time);
								uploadFile.setCreator(ca.getCreator());
								uploadFile.setMd5(MD5Util.getFileMD5String(newFile));
								uploadFile.setModified_time(time);
								uploadFile.setIsDir("2");
								uploadFile.setId(name);
								uploadFile.setPid("-1");
								fileList.add(uploadFile);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				ca.setRealFiles(fileList);
			}
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fh = null;
			if (null != arch) {
				try {
					arch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ca;
	}    

	public static void main(String[] args) throws Exception {
		// extract zip
		Ca ca = new Ca();
		//String zipPath  = "C:/工作/项目/测试数据/9787502040260.rar".replaceAll("\\\\", "/");
		String zipPath  = "C:/temp/9787502042585.rar".replaceAll("\\\\", "/");
		//String destPath= "C:/temp/test".replaceAll("\\\\", "/");
		String destPath= "C:/workhj/workspace/bsrcm_cciph/WebRoot/fileDir/fileRoot/1/G2/0024b9bf-1db6-4185-85fd-5f623481e57c".replaceAll("\\\\", "/");
		//RarUtil.unRarCaNew(zipPath, destPath,ca,"","3");
		RarUtil.unRarCaNew(zipPath, destPath,ca,"");
		
		
		//RarUtil.unrar(new File(zipPath), new File(destPath));
		
		// extract rar
//		RarUtil.unRar("D:/Project素材/anime/anime.rar", "d:/javazip");
	//	RarUtil.unRar("D:/第三方版权合同附件.rar","d://1");
		//new File("D:/北欧文学.rar").delete();
	}
	
	
	public static void unrar(File sourceRar, File destDir) throws Exception {  
        Archive archive = null;  
        FileOutputStream fos = null;  
        System.out.println("Starting...");  
        try {  
            archive = new Archive(sourceRar);  
            FileHeader fh = archive.nextFileHeader();  
            int count = 0;  
            File destFileName = null;  
            while (fh != null) {  
                System.out.println((++count) + ") " + fh.getFileNameString());  
                String compressFileName = fh.getFileNameString().trim();  
                destFileName = new File(destDir.getAbsolutePath() + "/" + compressFileName);  
                if (fh.isDirectory()) {  
                    if (!destFileName.exists()) {  
                        destFileName.mkdirs();  
                    }  
                    fh = archive.nextFileHeader();  
                    continue;  
                }   
                if (!destFileName.getParentFile().exists()) {  
                    destFileName.getParentFile().mkdirs();  
                }  
                fos = new FileOutputStream(destFileName);  
                archive.extractFile(fh, fos);  
                fos.close();  
                fos = null;  
                fh = archive.nextFileHeader();  
            }  
  
            archive.close();  
            archive = null;  
            System.out.println("Finished !");  
        } catch (Exception e) {  
            throw e;  
        } finally {  
            if (fos != null) {  
                try {  
                    fos.close();  
                    fos = null;  
                } catch (Exception e) {  
                    //ignore  
                }  
            }  
            if (archive != null) {  
                try {  
                    archive.close();  
                    archive = null;  
                } catch (Exception e) {  
                    //ignore  
                }  
            }  
        }  
    }  
}
