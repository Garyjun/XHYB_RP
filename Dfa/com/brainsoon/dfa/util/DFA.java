package com.brainsoon.dfa.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * @ClassName: DFA 
 * @Description:  DFA算法类
 * @author tanghui 
 * @date 2014-1-7 下午2:45:00 
 *
 */
public class DFA {
	private static final Log logger = LogFactory.getLog(DFA.class);
	
	/**
	 * 关键词编码
	 */
	public String charset = "UTF-8";
	
	public DFA() {}
	
	
	//构造函数设置编码
	public DFA(String charset) {
		if(StringUtils.isNotBlank(charset)){
			this.charset = charset;
		}
	}

	/**
	 * 根节点
	 */
	private TreeNode rootNode = new TreeNode();
	

	/**
	 * 关键词缓存
	 */
	private ByteBuffer keywordBuffer = ByteBuffer.allocate(1024);	
	
	

	/**
	 * 将敏感词的列表循环，将关键词转换成字节码，一个汉字对应三个数字字符
	 * 将转换的字节码放到树的节点上
	 * @param keywordList  要过滤的敏感词
	 * @throws UnsupportedEncodingException
	 */
	public void createKeywordTree(List<String> keywordList) throws UnsupportedEncodingException{
		
		//循环每个敏感词，将每个敏感词按照规则转换成字节码
		for (String keyword : keywordList) {
			if(keyword == null) continue;
			keyword = keyword.trim();
			byte[] bytes = keyword.getBytes(charset);
			
			//获取初始化的树
			TreeNode tempNode = rootNode;
			//循环每个字节
			for (int i = 0; i < bytes.length; i++) {
				int index = bytes[i] & 0xff; //字符转换成数字
				
				//根据每个敏感词转换的字节码数字，获取树上面的节点
				TreeNode node = tempNode.getSubNode(index);
				
				//若为空，说明该位置还没有放置敏感词转换的字节码
				if(node == null){ //没初始化
					node = new TreeNode();
					tempNode.setSubNode(index, node);
					//logger.debug("DFA:{}"+ tempNode.toString()+"关键关键+++++++++++++============"); 
				}
				
				tempNode = node;
				
				//每个敏感词转换成的字节码循环完成后，在结束位置进行标记
				if(i == bytes.length - 1){
					tempNode.setKeywordEnd(true);	 //关键词结束， 设置结束标志
				}
			}//end for
		}//end for
		
	}
	
	/**
	 * 搜索关键字
	 */
	public String searchKeyword(String text) throws UnsupportedEncodingException{
		return searchKeyword(text.getBytes(charset));
	}
	
	/**
	 * 搜索关键字
	 */
	public String searchKeyword(byte[] bytes){
		StringBuilder words = new StringBuilder();
		if(bytes == null || bytes.length == 0){
			return words.toString();
		}
		
		//获取敏感词生成的树
		TreeNode tempNode = rootNode;
		
		int rollback = 0;	//回滚数
		int position = 0; //当前比较的位置
		
		while (position < bytes.length) {
			int index = bytes[position] & 0xFF; //字符转换成数字
			//logger.info(index+"+++++++++++++++---------"+bytes[position]+"===============");
			keywordBuffer.put(bytes[position]);	//写关键词缓存
			
			tempNode = tempNode.getSubNode(index);
			
			//当前位置的匹配结束
			if(tempNode == null){ 
				position = position - rollback; //回退 并测试下一个字节
				rollback = 0;
				tempNode = rootNode;  	//状态机复位
				keywordBuffer.clear();	//清空
			}else if(tempNode.isKeywordEnd()){  //是结束点 记录关键词
				keywordBuffer.flip();
				String keyword = Charset.forName(charset).decode(keywordBuffer).toString();
				logger.info("+++++++++++++++++++++++++++++次次次次"+keyword+"你您你你==========++++++++++++++++++++");
				keywordBuffer.limit(keywordBuffer.capacity());
				
				if( words.length() == 0 ) words.append(keyword);
				else words.append(":").append(keyword);
				
				rollback = 1;	//遇到结束点  rollback 置为1
			}else{	
				rollback++;	//非结束点 回退数加1
			}
			
			position++;
		}
		return words.toString();
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}


	public TreeNode getRootNode() {
		return rootNode;
	}


	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}


	public ByteBuffer getKeywordBuffer() {
		return keywordBuffer;
	}


	public void setKeywordBuffer(ByteBuffer keywordBuffer) {
		this.keywordBuffer = keywordBuffer;
	}


	public String getCharset() {
		return charset;
	}
	
	
	
}