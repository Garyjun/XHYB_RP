package com.brainsoon.common.util.dofile.md5;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileRead {
	
	public static void main(String[] args) throws Exception {
	    int bufSize = 1024;
	    byte[] bs = new byte[bufSize];
	    // 这里是分配缓存大小。也就是用来存放从硬盘中度出来的文件
	    // 什么叫一次把文件读出来？其实就是当缓存大小和在硬盘中文件大小一样，
	    // 只通过一个read指令把整个文件都扔到缓存里面。例如要一次读一个2G的文件，把缓存设为2G就能一次读出来。
	    // 不过当分配空间的时候，这个缓存根本是分配不出来的，因为内存不足。
	    ByteBuffer byteBuf = ByteBuffer.allocate(bufSize);
	    FileChannel channel = new RandomAccessFile("d:\\filename","r").getChannel();
	    int size;
	    // 因为这里缓存大小是1K，所以每个channel.read()指令最多只会读到文件的1K的内容。
	    // 如果文件有1M大小，这里for会循环1024次，把文件分开1024次读取出来
	    while((size = channel.read(byteBuf)) != -1) {
	      byteBuf.rewind();
	      byteBuf.get(bs);
	      // 把文件当字符串处理，直接打印做为一个例子。
	      System.out.print(new String(bs, 0, size));
	      byteBuf.clear();
	    }
	    channel.close();
	  }
}
