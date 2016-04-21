package com.brainsoon.common.util.dofile.cnmarc;

import java.util.LinkedHashMap;
import java.util.Map;

public class CNMarcFieldsConstants {
	/**
	 * 切割字段说明的
	 */
	public static final String FILEDSPLITSTR = "------";
	/**
	 * CNMarc现用到的解析字段
	 */
	public static Map<String,String> CNFILEDS = new LinkedHashMap<String,String>();
	static{
		//组成为                 key：三位字段标识符+描述     		value ：子字段标识符 + 字符开始位置 + 字符数  + 翻译串[{'a':'现期出版的连续出版物'},{...}]
		//如果字符开始位置为-1，则全部截取此子字段标识符内容，最后一个JSON用做字段的翻译
		/*----------------------------------------010国际标准书号(ISBN)------------------------------------------------------*/
		CNFILEDS.put("010ISBN" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("010装订方式" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("010获得方式和／或定价" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("010错误的ISBN号" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------035源数据ID------------------------------------------------------*/
		CNFILEDS.put("035源数据ID" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------100一般处理数据--必备------------------------------------------------------*/
		CNFILEDS.put("100入档日期" , "a" + FILEDSPLITSTR + "0" + FILEDSPLITSTR + "8");
		CNFILEDS.put("100出版日期类型" , "a" + FILEDSPLITSTR + "8" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{'a':'现期出版的连续出版物'},{'b':'已停刊的连续出版物'},{'c':'刊行状态不明的连续出版物 '},{'d':'一次或一年内出全的专着'},{'e':'复制本(如重印本，摹写本，再版本)'}]");
		CNFILEDS.put("100出版日期1" , "a" + FILEDSPLITSTR + "9" + FILEDSPLITSTR + "4");
		CNFILEDS.put("100出版日期2" , "a" + FILEDSPLITSTR + "13" + FILEDSPLITSTR + "4");
		CNFILEDS.put("100阅读对象代码" , "a" + FILEDSPLITSTR + "17" + FILEDSPLITSTR + "3" + FILEDSPLITSTR + "[{'a':'普通青少年'},{'b':'学龄前儿童(0-5岁)'},{'c':'小学生(5-10岁)'},{'d':'少年(9-14岁)'},{'e':'青年(14-20岁)'},{'k':'研究人员'},{'m':'普通成人'},{'u':'不详'},{'z':'特殊读者(如盲人)'}]");
		CNFILEDS.put("100政府出版物代码" , "a" + FILEDSPLITSTR + "20" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{a:'中央政府，各部委'},{b:'直辖市、省、自治区'},{c:'省直辖市、县级'},{d:'市、镇、乡机构'},{f:'政府间组织机构'},{h:'层次未定 '},{u:'不能确定是否是政府出版物'},{y:'非政府出版物'},{z:'其他政府层次'}]");
		CNFILEDS.put("100修改记录代码" , "a" + FILEDSPLITSTR + "21" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{'0':'未修改的记录'},{'1':'修改的记录'}]");
		CNFILEDS.put("100编目语种" , "a" + FILEDSPLITSTR + "22" + FILEDSPLITSTR + "3" + FILEDSPLITSTR + "[{chi:'汉语'},{eng:'英语'},{fre:'法语'},{ger:'德语'},{jpn:'日语'},{rus:'俄语'},{mon:'蒙古语'}]");
		CNFILEDS.put("100音译代码" , "a" + FILEDSPLITSTR + "25" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{a:'ISO音译体系'},{b:'其它'},{c:'多种音译体系；ISO或其他体系'},{y:'未使用音译'}]");
		CNFILEDS.put("100字符集G0集" , "a" + FILEDSPLITSTR + "26" + FILEDSPLITSTR + "2" + FILEDSPLITSTR + "[{'01':'ISO 646, IRV version (基本拉丁集)'},{'02':'ISO Registration #37 (基本基里尔集)'},{'03':'ISO 5426 (扩充拉丁集)'},{'04':'ISO DIS 5427 (扩充基里尔集)'},{'05':'ISO 5428 (西腊集)'},{'06':'ISO 6438 (非洲编码字符集)'},{'10':'GB2312-80信息交换用汉字编码字符集资本集(双7位)'},{'11':'信息交换用汉字编码字符集基本集第一辅助集(双7位)'},{'20':'信息交换用汉字编码字符集基本集(双8位表示。基本、辅3、5集为一个集合)'},{'21':'信息交换用汉字编码字符集基本集(双8位表示的辅1、3、5集所构成的集合)'}]");
		CNFILEDS.put("100字符集G1集" , "a" + FILEDSPLITSTR + "28" + FILEDSPLITSTR + "2" + FILEDSPLITSTR + "[{'01':'ISO 646, IRV version (基本拉丁集)'},{'02':'ISO Registration #37 (基本基里尔集)'},{'03':'ISO 5426 (扩充拉丁集)'},{'04':'ISO DIS 5427 (扩充基里尔集)'},{'05':'ISO 5428 (西腊集)'},{'06':'ISO 6438 (非洲编码字符集)'},{'10':'GB2312-80信息交换用汉字编码字符集资本集(双7位)'},{'11':'信息交换用汉字编码字符集基本集第一辅助集(双7位)'},{'20':'信息交换用汉字编码字符集基本集(双8位表示。基本、辅3、5集为一个集合)'},{'21':'信息交换用汉字编码字符集基本集(双8位表示的辅1、3、5集所构成的集合)'}]");
		CNFILEDS.put("100补充字符集G2集" , "a" + FILEDSPLITSTR + "30" + FILEDSPLITSTR + "2" + FILEDSPLITSTR + "[{'12':'信息交换试用汉字编码字符集辅助集3(双7位)'},{'13':'信息交换试用汉字编码字符集辅助集5(双7位)'}]");
		CNFILEDS.put("100补充字符集G3集" , "a" + FILEDSPLITSTR + "32" + FILEDSPLITSTR + "2" + FILEDSPLITSTR + "[{'12':'信息交换试用汉字编码字符集辅助集3(双7位)'},{'13':'信息交换试用汉字编码字符集辅助集5(双7位)'}]");
		CNFILEDS.put("100题名语系代码" , "a" + FILEDSPLITSTR + "34" + FILEDSPLITSTR + "2" + FILEDSPLITSTR + "[{'ea':'汉语-文字类型未指明'},{'eb':'汉语-汉字'},{'ec':'汉语-汉语拼音'},{'ba':'拉丁语'},{'ca':'基里尔语'},{'da':'日语-文字类型未指明'},{'db':'日语-汉字'},{'dc':'日语-假名'},{'zz':'其它'}]");
		
		/*----------------------------------------101作品语种------------------------------------------------------*/
		CNFILEDS.put("101正文、声道等语种" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101中间语种" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101原着语种" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101提要语种" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101与正文语种不同的目次页语种" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101与正文语种不同的题名页语种" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101与正文、声道的第一语种不同的正题名语种" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101歌词等语种" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101附件语种" , "i" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("101字幕语种" , "j" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------102出版国别------------------------------------------------------*/
		CNFILEDS.put("102出版国代码" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("102出版地区代码" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("102非国际标准出版地区代码来源" , "2" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------105代码数据字段: 图书------------------------------------------------------*/
		CNFILEDS.put("105图表代码" , "a" + FILEDSPLITSTR + "0" + FILEDSPLITSTR + "4" + FILEDSPLITSTR + "[{'a':'图表(包括画册)'},{'b':'地图'},{'c':'肖像'},{'d':'海图'},{'e':'设计图'},{'f':'单页插图'},{'g':'乐谱'},{'h':'摹真本'},{'i':'徽章图'},{'j':'世系表'},{'k':'表格'},{'l':'样品'},{'m':'录音资料'},{'n':'透明图片'},{'o':'彩饰'},{'y':'无图表'}]");
		CNFILEDS.put("105内容特征代码" , "a" + FILEDSPLITSTR + "4" + FILEDSPLITSTR + "4" + FILEDSPLITSTR + "[{'a':'书目'},{'b':'目录'},{'c':'索引'},{'d':'文摘'},{'e':'字、词典'},{'f':'百科全书'},{'g':'人名录、指南'},{'i':'统计资料'},{'j':'成套教科书'},{'k':'专利文献书'},{'l':'标准或规定'},{'m':'学位论文'},{'n':'法律和法规'},{'o':'数字表格'},{'p':'技术报告'},{'q':'试题'},{'r':'文学评论'},{'s':'条约'},{'t':'卡通或连环画'},{'z':'其它'}]");
		CNFILEDS.put("105会议代码" , "a" + FILEDSPLITSTR + "8" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{'0':'非会议出版物'},{'1':'会议出版物'}]");
		CNFILEDS.put("105纪念文集指示符" , "a" + FILEDSPLITSTR + "9" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{'0':'非纪念文集'},{'1':'纪念文集'}]");
		CNFILEDS.put("105索引指示符" , "a" + FILEDSPLITSTR + "10" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{'0':'无索引'},{'1':'有索引'}]");
		CNFILEDS.put("105文学体裁代码" , "a" + FILEDSPLITSTR + "11" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{'a':'小说'},{'b':'戏剧'},{'c':'散文'},{'d':'幽默、讽刺小品'},{'e':'书信'},{'f':'短篇小说'},{'g':'诗词'},{'h':'演说集'},{'y':'非文学作品'},{'z':'其它或多种文学体裁'}]");
		CNFILEDS.put("105传记代码" , "a" + FILEDSPLITSTR + "12" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{'a':'自传'},{'b':'个人传记 '},{'c':'集体传记'},{'d':'包含传记资料'},{'y':'非传记作品'}]");
		
		/*----------------------------------------106编码数据字段：文字资料形态特征------------------------------------------------------*/
		CNFILEDS.put("106物理介质标识" , "a" + FILEDSPLITSTR + "0" + FILEDSPLITSTR + "1" + FILEDSPLITSTR + "[{'d':'大型印刷品'},{'e':'报纸'},{'f':'盲文'},{'g':'微型印刷品'},{'h':'手写稿'},{'i':'多种载体'},{'j':'小型印刷品'},{'r':'一般印刷品'},{'z':'其它形式的文字资料'}]");
		
		
		/*----------------------------------------200题名与责任者项--必备------------------------------------------------------*/
		CNFILEDS.put("200正题名" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200一般资料标识" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200另一作者的正题名" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200并列正题名" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200其他题名信息" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200第一责任者" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200其它责任者" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200分册名" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200分册号" , "i" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200卷册号标识" , "v" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200并列题名语种" , "z" + FILEDSPLITSTR + "-1");
		//注意：国图拼音字段使用$9,CALIS使用$A
		CNFILEDS.put("200正题名汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200副题名、其他说明题名文字的汉语拼音" , "E" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200第一责任者的汉语拼音" , "F" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("200分册(辑)名的汉语拼音" , "I" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------205版本项 ------------------------------------------------------*/
		CNFILEDS.put("205版本说明" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("205其它版本形式说明" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("205并列版本说明" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("205与本版有关的第一责任者说明" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("205与本版有关的其它责任说明" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("205印次" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------210出版发行项------------------------------------------------------*/
		CNFILEDS.put("210出版(发行)地" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("210出版(发行)者地址" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("210出版(发行)者" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("210出版(发行)时间" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("210制作地" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("210制作者地址" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("210制作者名称" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("210制作时间" , "h" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------215载体形态项------------------------------------------------------*/
		CNFILEDS.put("215页数或卷册数" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("215图及其它细节" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("215尺寸或开本" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("215附件" , "e" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------225丛编项-----------------------------------------------------*/
		CNFILEDS.put("225丛编题名" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("225并列从编题名" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("225其它题名信息" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("225责任说明" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("225附属丛编号" , "h" + FILEDSPLITSTR + "-1");//注意200,key重复
		CNFILEDS.put("225附属丛编名" , "i" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("225卷识号" , "v" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("225ISSN" , "x" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("225并列丛编题名语种" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------300一般性附注------------------------------------------------------*/
		CNFILEDS.put("300一般性附注" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------314责任者附注------------------------------------------------------*/
		CNFILEDS.put("314责任者附注" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------327内容附注------------------------------------------------------*/
		CNFILEDS.put("327附注内容" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------330提要或文摘------------------------------------------------------*/
		CNFILEDS.put("330提要或文摘" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------333读者对象(注意：标准CNMarc未找到此字段)------------------------------------------------------*/
		CNFILEDS.put("333读者对象" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------410总集 (CALIS的丛书字段)------------------------------------------------------*/
		CNFILEDS.put("410丛书连接字段" , "1" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("410丛书名称" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------411附属丛编 (CALIS的丛书字段)------------------------------------------------------*/
		CNFILEDS.put("411附属丛书连接字段" , "1" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("411附属丛书名称" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------461总集 (国图的丛书字段)------------------------------------------------------*/
		CNFILEDS.put("461丛书连接字段" , "1" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("461丛书名称" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------462附属 丛编(国图的丛书字段)------------------------------------------------------*/
		CNFILEDS.put("462附属丛书连接字段" , "1" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("462附属丛书名称" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------510并列题名------------------------------------------------------*/
		CNFILEDS.put("510并列题名" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510其它题名信息" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510分册号" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510分册名" , "i" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510有关卷号或日期" , "j" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510其他信息" , "n" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510并列题名语种" , "z" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510并列题名汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510其他题名信息汉语拼音" , "E" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("510分册名汉语拼音" , "I" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------517交换题名------------------------------------------------------*/
		CNFILEDS.put("517交换题名" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------601团体名称主题------------------------------------------------------*/
		CNFILEDS.put("601系统代码" , "2" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601正题名" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601次级部分(或按地名着录的名称)" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601名称附加或限定" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601会议届次" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601会议地点" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601会议日期" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601倒置部分" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601款目要素和倒置部分之外的名称部分" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601形式复分" , "j" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601题名" , "t" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601论题复分" , "x" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601地理复分" , "y" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("601年代复分" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------602家族名称主题------------------------------------------------------*/
		CNFILEDS.put("602系统代码" , "2" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("602规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("602正题名" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("602年代" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("602形式复分" , "j" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("602题名" , "t" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("602论题复分" , "x" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("602地理复分" , "y" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("602年代复分" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------604名称和题名主题------------------------------------------------------*/
		CNFILEDS.put("604正题名" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("604第一责任说明" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("604分辑(册)号" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("604分辑(册)名" , "i" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("604卷标识" , "v" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------605题名主题------------------------------------------------------*/
		CNFILEDS.put("605系统代码" , "2" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605正题名" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605分辑(册)、章节号" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605分辑(册)、章节名" , "i" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605形式复分" , "j" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605出版日期" , "k" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605形式副标目" , "l" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605语种(用作标目的组成部分时)" , "m" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605其他信息" , "n" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605版本(或版本日期)" , "q" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605演奏媒体(音乐用)" , "r" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605数字标识(音乐用)" , "s" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605调名(音乐用)" , "u" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605改编乐曲说明(音乐用)" , "w" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605论题复分" , "x" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605地理复分" , "y" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("605年代复分" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------606论题名称主题------------------------------------------------------*/
		CNFILEDS.put("606系统代码" , "2" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("606规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("606主标目" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("606形式复分" , "j" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("606论题复分" , "x" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("606地理复分" , "y" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("606年代复分" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------607地理名称主题------------------------------------------------------*/
		CNFILEDS.put("607系统代码" , "2" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("607规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("607正题名" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("607形式复分" , "j" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("607论题复分" , "x" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("607地理复分" , "y" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("607年代复分" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------608形式、体裁或物理特性目标------------------------------------------------------*/
		CNFILEDS.put("608系统代码" , "2" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("608规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("608使用本字段的机构" , "5" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("608款目要素" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("608形式复分" , "j" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("608论题复分" , "x" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("608地理复分" , "y" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("608年代复分" , "z" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------610非控主题词------------------------------------------------------*/
		CNFILEDS.put("610非控主题词" , "a" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------690中国图书馆图书分类法分类号(CLC)------------------------------------------------------*/
		CNFILEDS.put("690分类号" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("690系统自动生成" , "v" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------700人名--主要责任者 ------------------------------------------------------*/
		CNFILEDS.put("700款目要素" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700名称的其余部分" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700人名修饰语" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700罗马数字" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700年代" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700名字首字母的展开形式" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700任职机构/地址" , "p" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700责任方式" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("700款目要素汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------701人名--等同责任者------------------------------------------------------*/
		CNFILEDS.put("701款目要素" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701名称的其余部分" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701人名修饰语" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701罗马数字" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701年代" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701名字首字母的展开形式" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701任职机构/地址" , "p" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701责任方式" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("701款目要素汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------702人名--次要责任者------------------------------------------------------*/
		CNFILEDS.put("702款目要素" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702名称的其余部分" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702人名修饰语" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702罗马数字" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702年代" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702名字首字母的展开形式" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702任职机构/地址" , "p" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702责任方式" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("702款目要素汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------710团体名称--主要责任者 ------------------------------------------------------*/
		CNFILEDS.put("710主标目" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710副标目" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710名称修饰语" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710会议届次" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710会议地点" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710会议日期" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710名称倒装部分" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710其余部分" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710着作责任" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("710主标目汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------711团体名称--等同责任者 ------------------------------------------------------*/
		CNFILEDS.put("711主标目" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711副标目" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711名称修饰语" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711会议届次" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711会议地点" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711会议日期" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711名称倒装部分" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711其余部分" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711着作责任" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("711主标目汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------712团体名称--次要责任者 ------------------------------------------------------*/
		CNFILEDS.put("712主标目" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712副标目" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712名称修饰语" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712会议届次" , "d" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712会议地点" , "e" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712会议日期" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712名称倒装部分" , "g" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712其余部分" , "h" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712着作责任" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("712主标目汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------720家族名称--主要责任者 ------------------------------------------------------*/
		CNFILEDS.put("720主标目" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("720年代" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("720规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("720着作责任" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("720主标目汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------721家族名称--等同责任者 ------------------------------------------------------*/
		CNFILEDS.put("721主标目" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("721年代" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("721规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("721着作责任" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("721主标目汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------722家族名称--次要责任者  ------------------------------------------------------*/
		CNFILEDS.put("722主标目" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("722年代" , "f" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("722规范记录号" , "3" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("722着作责任" , "4" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("722主标目汉语拼音" , "9" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------801记录来源字段------------------------------------------------------*/
		CNFILEDS.put("801国家代码" , "a" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("801机构名称" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("801处理日期" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("801编目条例代码" , "g" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------856链接地址(注意：标准CNMarc未找到此字段)------------------------------------------------------*/
		CNFILEDS.put("856样本地址" , "u" + FILEDSPLITSTR + "-1");
		
		/*----------------------------------------980卖场分类(注意：标准CNMarc未找到此字段)------------------------------------------------------*/
		CNFILEDS.put("980出版物类别" , "b" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("980卖场分类" , "c" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("980获奖情况" , "p" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("980学科类型" , "s" + FILEDSPLITSTR + "-1");
		CNFILEDS.put("980院校类型" , "u" + FILEDSPLITSTR + "-1");
	}
}
