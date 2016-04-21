package com.brainsoon.common.util.dofile.cnmarc;

import com.brainsoon.common.util.dofile.code.Epub2Html;
import com.itextpdf.text.DocumentException;

public class Test {

	/**
	 * @param args
	 * @throws DocumentException 
	 */
	public static void main(String[] args){
//		String marcStr = "01103nam0 2200313   450 001001100000005001700011010003800028100004100066101001300107102001500120105001900135106000600154200006600160205000800226210003000234215003700264225006000301307003500361330013100396461002600527510005300553606002900606690001800635701003300653702002700686702002700713702002700740801002200767011200000120120530155544.4  a978-7-04-010937-5b精装dCNY37.00  a20120530d2010    em y0chiy0110    ea0 achiajpn  aCNb100011  aao   z   000yy  ar1 a大学英语教程Ada xue ying yu jiao chengf张东军g孙国名、达克  a2版  a北京cXXXX出版社d2010.09  a412页c图d185 * 260e1DVD，2VCD2 a研究生数学丛书Ayan jiu sheng shu xue cong shuf张三h1  a附光盘：ISBN 978-7-89469-418-8  a本书共分3册。第1册内容包括: 数列极限、函数极限与连续、一元函数的导数与微分中值定理、Taylor公式、不定积分、Riemann积分6章内容。 012001 a研究生数学丛书1 aEnglish in Mind Student's Pack Starterachiajpn0 a英语x程序设计j社会用书  aTN911.6v2010 0a张东军Azhang dong jun4编译 0a苏丙29su bing 24编译 0a苏丙39su bing 34编译 0a苏丙49su bing 34编译 0aCNbHEPc20120530";
//		String marcStr1 = "01177nam0 2200277   450 001001100000005001700011010003200028100004100060101000800101102002300109105001800132106000600150200010400156210003400260215002200294304002900316330023500345333014300580606002700723690001300750690001300763701003100776801002600807856005300833980001300886001155307320120517000000.0  a978-7-115-25024-7dCNY50.00  a20120517d2012    em y0chiy0110    ea0 achi  aCNb1100002GB2260  aa   z   000yy  ar1 a信息通信网络建设安全管理概要9xin xi tong xin wang luo jian she an quan guan li gai yaof林幼槐主编  a北京c人民邮电出版社d2012.05  a264页c图表d24cm  a湖北电信工程有限公司组编  a本书根据通信行业的特点，结合其他行业的安全管理理论，全方位地介绍了信息网络项目建设安全管理理论、安全管理策划与控制、安全管理流程与体系建设、通用安全生产技术、专业安全生产技术及国际工程安全管理等安全管理理论及全过程控制与管理方法。  a本书适合信息通信行业建设单位工程建设管理人员、项目经理、运营商维护人员以及机房作业人员、监理单位相关人员、市场推广人员以及研究者参考使用。0 a计算机通信网x安全管理  aTN915v4  aTP393v4 0a林幼槐9lin you huai4主编 0aCNbCBIP.CNc201205174 uhttp://www.cbip.cn/BookInfo.aspx?BookId=11553073  c电子通信";
//		//校对数据
//		String marcStr2 = "01504oam2 2200337   450 001001100000005001700011010003200028100004100060101001300101102002300114105001800137106000600155200020400161210003400365215002200399225002100421314002900442330025700471333003700728461002800765510010300793606001300896606001300909690001500922701005900937701005000996702003001046801002601076856005301102980001101155001155307520120517000000.0  a978-7-115-27075-7dCNY99.00  a20120517d2012    em y0chiy0110    ea1 achiceng  aCNb1100002GB2260  aak  z   000yy  ar1 a软件加密与解密9ruan jian jia mi yu jie mid = Surreptitious software: obfuscation, watermarking, and tamperproofing for software protectionf(美)Christian Collberg, (美)Jasvir Nagra着g崔孝晨译zeng  a北京c人民邮电出版社d2012.05  a601页c图表d24cm2 a图灵程序设计丛书  a责任者中文姓名取自版权页  a本书介绍了如何利用混淆、水印和防篡改等技术，来保护软件免受盗版、篡改和恶意逆向工程的危害，主要内容包括攻击者和防御者用来分析程序的各种主流方法，如何使用代码混淆技术使程序更难以被分析和理解，如何在软件中添加水印和指纹以标识软件的开发者和购买用户，等等。  a本书适合各层次软件开发人员阅读。 012001 a图灵程序设计丛书1 aSurreptitious software: obfuscation, watermarking, and tamperproofing for software protectionzeng0 a软件加密0 a解密软件  aTP309.7v4 0c(美)a科尔伯格9ke er bo gec(Collberg, Christian)4着 0c(美)a纳盖雷9na gai leic(Nagra, Jasvir)4着 0a崔孝晨9cui xiao chen4译 0aCNbCBIP.CNc201205174 uhttp://www.cbip.cn/BookInfo.aspx?BookId=11553075  c计算机";
//		String marcStr3 = "01626nam0 2200301   450 001001100000005001700011010003200028100004100060101001300101102002300114105001800137106000600155200016300161210003400324215003400358306006200392314002900454330037700483333017500860510005501035606003101090690001601121701006601137702002901203801002601232856005301258980001301311001155315020120518000000.0  a978-7-115-27955-2dCNY69.00  a20120518d2012    em y0chiy0110    ea1 achiceng  aCNb1100002GB2260  aacf z   000yy  ar1 aCisco OSPF命令与配置手册9Cisco OSPF ming ling yu pei zhi shou ced = Cisco OSPF command and configuration handbookf(美)William R. Parkhurst着g孙余强译zeng  a北京c人民邮电出版社d2012.05  a430页c图表，肖像，图版d24cm  a本书中文简体字版由美国Cisco Press授权人民邮电出版社出版。  a责任者中文姓名取自版权页  a本书是一本简洁而又完整的OSPF命令手册。书中提供了许多配置示例，在只用几台路由器的情况下，演示了每条OSPF命令的正确用法。读者可借此学习每条OSPF命令，而无需搭建庞大而又昂贵的实验室网络环境。本书涵盖了OSPF配置的诸多主题，包括：接口配置、OSPF区域配置、路由过滤、OSPF进程配置、路由开销、默认路由的生成、路由重分发、管理距离、OSPF邻接关系、路由汇总，以及show、debug和clear命令等。  a本书层次分明、阐述清晰、分析透彻、理论与实践并重，不仅适合准备CCNA、CCNP或CCIE认证考试的人员阅读，也是从事计算机网络设计、管理和运维工作的工程技术人员必不可少的参考资料。1 aCisco OSPF command and configuration handbookzeng0 a互联网络x路由器x基本知识  aTN915.05v4 0c(美)a帕克赫斯特9pa ke he si tec(Parkhurst, William R.)4着 0a孙余强9sun yu qiang4译 0aCNbCBIP.CNc201205184 uhttp://www.cbip.cn/BookInfo.aspx?BookId=11553150  c电子通信";
//		//校验
//		String marcStr4 = "01824oam2 2200313   450 001001100000005001700011010003200028100004100060101000800101102001500109105001800124106000600142200011400148205000800262210003400270215004100304225003100345330073100376333018001107461002001287462002801307606001701335610001801352690001601370701003201386801002601418856005301444980001301497001155309120120518000000.0  a978-7-115-27627-8dCNY98.00  a20120517d2012    em y0chiy0110    ea0 achi  aCNb110000  aacf z   000yy  ar1 aAfter effects影视合成与特效火星风暴9After effects ying shi he cheng yu te xiao huo xing feng baof张天骐编着  a2版  a北京c人民邮电出版社d2012.05  a340页c图表，肖像，图版d26cme2光盘2 a火星风暴i影视后期系列图书  a本书是“火星风暴·影视后期”系列中的一本。通过近40个案例系统地介绍了After Effects影视合成与特效制作技术。全书共9章，第1章介绍了After Effects影视制作基础与影片输出的方法；第2章介绍了软件配置方法、基础动画、Mask抠像等技术；第3章介绍了影视后期色彩校正与处理等知识；第4章介绍了色彩差异或亮度差异抠像技术；第5章介绍了四点追踪与两点追踪技术在影视后期中的实际应用；第6章介绍了几种文字动画与特效的制作；第7章介绍了如何将2D空间转化为3D空间、摄影机动画与摄影机对焦效果、灯光和投影等；第8章介绍了如何使用软件自带的特效来制作光效；第9章介绍了插件在After Effects影视后期与特效制作中的应用。随书附带两张DVD多媒体教学光盘，盘中的视频内容包括书中绝大部分案例的教学视频和 After Effects基础部分的教学视频，以及书中所有实例的工程文件和素材文件。  a本书内容丰富、结构严谨、文字精炼，不仅适合 After Effects 的初中级用户学习，而且对从事影视后期制作的人员也有较高的参考价值，同时也可作为各大专院校和社会培训机构相关专业的教材。 012001 a火星风暴 012001 a影视后期系列图书0 a图像处理软件0 aAfter Effects  aTP391.41v4 0a张天骐9zhang tian qi4编着 0aCNbCBIP.CNc201205174 uhttp://www.cbip.cn/BookInfo.aspx?BookId=11553091  c图形图像";
//		CNMarc marc = new CNMarc();
//		marc.load(marcStr);
////		System.out.println(marc.getHeadArea());
////		System.out.println(marc.getCatalog().getColumnCatalogs().size());
//		
////		marc.getFieldDesc("ISBN");
////		marc.getFieldDesc("装订方式");
////		marc.getFieldDesc("获得方式和／或定价");
//////		marc.getFieldDesc("错误的ISBN号");
////		
////		
////		marc.getFieldDesc("入档日期(必备)");
////		marc.getFieldDesc("出版日期类型");
////		marc.getFieldDesc("出版日期1");
////		marc.getFieldDesc("出版日期2");
////		marc.getFieldDesc("阅读对象代码",true);
////		marc.getFieldDesc("政府出版物代码",true);
////		marc.getFieldDesc("编目语种");
////		marc.getFieldDesc("字符集G0集");
////		marc.getFieldDesc("题名语系代码");
////		
////		//200
////		marc.getFieldDesc("正题名");
////		marc.getFieldDesc("正题名汉语拼音");
////		
////		//205
////		marc.getFieldDesc("版次说明");
////		
////		//210
////		marc.getFieldDesc("出版、发行地");
////		marc.getFieldDesc("出版、发行者名称");
////		marc.getFieldDesc("出版、发行日期");
////		
////		//215
////		marc.getFieldDesc("页数或卷册数(数量及其单位)");
////		marc.getFieldDesc("尺寸或开本");
////		marc.getFieldDesc("附件");
////		
////		//225
////		marc.getFieldDesc("正丛编题名"); 
////		
////		//314
////		marc.getFieldDesc("责任者附注"); 
////		
////		//330
////		marc.getFieldDesc("提要或文摘"); 
////		
////		//333
////		marc.getFieldDesc("读者对象"); 
////		
////		//510
////		marc.getFieldDesc("并列题名");
////		
////		//606
////		marc.getFieldDesc("主标目"); 
////		
////		//610
////		marc.getFieldDesc("非控制主题词"); 
////		
////		//690
////		marc.getFieldDesc("分类号"); 
////		
////		//701
////		marc.getFieldDesc("人名修饰语1"); 
////		marc.getFieldDesc("着作责任1"); 
////		marc.getFieldDesc("主标目1"); 
////		
////		//702
////		marc.getFieldDesc("主标目2"); 
////		marc.getFieldDesc("着作责任2"); 
////		
////		//801
////		marc.getFieldDesc("国家代码"); 
////		
////		//856
////		marc.getFieldDesc("样本地址"); 
////		
////		//980
////		marc.getFieldDesc("卖场分类"); 
//		
//		
////		Map column = marc.getColumns();
////		//开始
////		for (Iterator ite = column.entrySet().iterator(); ite.hasNext();) {
////			Map.Entry entry = (Map.Entry) ite.next();
////			String colkey = (String) entry.getKey();
////			CNMarcColumnBase colValue = (CNMarcColumnBase) entry.getValue();
////			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~start");
////			System.out.println(colkey);
////			System.out.println("标识:"+colValue.getCharacter());
////			System.out.println("分隔符1:"+colValue.getIdenticator1());
////			System.out.println("分隔符2:"+colValue.getIdenticator2());
////			if(null != colValue.getContent()){
////				System.out.println("主字段内容:"+colValue.getContent());
////			}
////			List<CNMarcSubColumn> subs = colValue.getSubColumns();
////			for (CNMarcSubColumn cnMarcSubColumn : subs) {
////				System.out.println("子字段标识:"+cnMarcSubColumn.getSubField());
////				System.out.println("子字段内容:"+cnMarcSubColumn.getSubContent());
////			}
////			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~end");
////		}
////		
////		
////		System.out.println("获取");
////		try {
////			System.out.println(marc.getMarcData());
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////		System.out.println("描述------------------");
////		try {
////			System.out.println(marc.getDescription(true,false));
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//////		String filePath = "E:\\work\\CNMARK\\样本数据国图test.iso";
//////		List<CNMarc> cc = ICNMarcService.loadCNMarcFromISO(filePath);
//////		if(null != cc)
//////			System.out.println(cc.size());
//////		
//////		ICNMarcService.createCNMarcFile("E:\\work\\CNMARK\\样本数据国图test1111.iso",cc);
//////		ICNMarcService.createCNMarcFile("D:\\样本数据国图1111.iso",cc.get(0));
////
////		//生成
//		Map<String,CNMarcColumnBase> columns = new LinkedHashMap<String, CNMarcColumnBase>();
//		CNMarcColumnBase col_x = new CNMarcColumnBase();
//		col_x.setCharacter("001");
//		col_x.setContent("0112000001");
//		columns.put("001", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("005");
//		col_x.setContent("20120529145849.4");
//		columns.put("005", col_x);
//		
//		
//		CNMarcColumnBase col_010 = new CNMarcColumnBase();
//		col_010.setCharacter("010");
//		col_010.setIdenticator1(" ");
//		col_010.setIdenticator2(" ");
//		List<CNMarcSubColumn> subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "978-7-04-010937-5"));
//		subColumns.add(new CNMarcSubColumn("b", "精装"));
//		subColumns.add(new CNMarcSubColumn("d", "CNY37.00"));
//		col_010.setSubColumns(subColumns);
//		columns.put("010", col_010);
////		
//		CNMarcColumnBase col_100 = new CNMarcColumnBase();
//		col_100.setCharacter("100");
//		col_100.setIdenticator1(" ");
//		col_100.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "20120529d2010    em y0chiy0110    ea"));
//		col_100.setSubColumns(subColumns);
//		columns.put("100", col_100);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("101");
//		col_x.setIdenticator1("0");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "chi"));
//		subColumns.add(new CNMarcSubColumn("a", "jpn"));
//		col_x.setSubColumns(subColumns);
//		columns.put("101", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("102");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "CN"));
//		subColumns.add(new CNMarcSubColumn("b", "100011"));
//		col_x.setSubColumns(subColumns);
//		columns.put("102", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("105");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "ao   z   000yy"));
//		col_x.setSubColumns(subColumns);
//		columns.put("105", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("106");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "r"));
//		col_x.setSubColumns(subColumns);
//		columns.put("106", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("200");
//		col_x.setIdenticator1("1");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "大学英语教程"));
//		subColumns.add(new CNMarcSubColumn("A", "da xue ying yu jiao cheng"));
//		subColumns.add(new CNMarcSubColumn("f", "张东军"));
//		subColumns.add(new CNMarcSubColumn("g", "孙国名、达克"));
//		col_x.setSubColumns(subColumns);
//		columns.put("200", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("205");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "2版"));
//		col_x.setSubColumns(subColumns);
//		columns.put("205", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("210");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "北京"));
//		subColumns.add(new CNMarcSubColumn("c", "XXXX出版社"));
//		subColumns.add(new CNMarcSubColumn("d", "2010.09"));
//		col_x.setSubColumns(subColumns);
//		columns.put("210", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("215");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "412页"));
//		subColumns.add(new CNMarcSubColumn("c", "图"));
//		subColumns.add(new CNMarcSubColumn("d", "185 * 260"));
//		subColumns.add(new CNMarcSubColumn("e", "1DVD，2VCD"));
//		col_x.setSubColumns(subColumns);
//		columns.put("215", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("225");
//		col_x.setIdenticator1("2");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "研究生数学丛书"));
//		subColumns.add(new CNMarcSubColumn("A", "yan jiu sheng shu xue cong shu"));
//		subColumns.add(new CNMarcSubColumn("f", "张三"));
//		subColumns.add(new CNMarcSubColumn("h", "1"));
//		col_x.setSubColumns(subColumns);
//		columns.put("225", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("307");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "附光盘：ISBN 978-7-89469-418-8"));
//		col_x.setSubColumns(subColumns);
//		columns.put("307", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("330");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "本书共分3册。第1册内容包括: 数列极限、函数极限与连续、一元函数的导数与微分中值定理、Taylor公式、不定积分、Riemann积分6章内容。"));
//		col_x.setSubColumns(subColumns);
//		columns.put("330", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("461");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2("0");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("1", "2001 "));
//		subColumns.add(new CNMarcSubColumn("a", "研究生数学丛书"));
//		col_x.setSubColumns(subColumns);
//		columns.put("461", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("510");
//		col_x.setIdenticator1("1");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "English in Mind Student's Pack Starter"));
//		subColumns.add(new CNMarcSubColumn("a", "chi"));
//		subColumns.add(new CNMarcSubColumn("a", "jpn"));
//		col_x.setSubColumns(subColumns);
//		columns.put("510", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("606");
//		col_x.setIdenticator1("0");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "英语"));
//		subColumns.add(new CNMarcSubColumn("x", "程序设计"));
//		subColumns.add(new CNMarcSubColumn("j", "社会用书"));
//		col_x.setSubColumns(subColumns);
//		columns.put("606", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("690");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2(" ");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "TN911.6"));
//		subColumns.add(new CNMarcSubColumn("v", "2010"));
//		col_x.setSubColumns(subColumns);
//		columns.put("690", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("701");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2("0");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "张东军"));
//		subColumns.add(new CNMarcSubColumn("A", "zhang dong jun"));
//		subColumns.add(new CNMarcSubColumn("4", "编译"));
//		col_x.setSubColumns(subColumns);
//		columns.put("701", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("801");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2("0");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "CN"));
//		subColumns.add(new CNMarcSubColumn("b", "HEP"));
//		subColumns.add(new CNMarcSubColumn("c", "20120529"));
//		col_x.setSubColumns(subColumns);
//		columns.put("801", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("702");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2("0");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "苏丙2"));
//		subColumns.add(new CNMarcSubColumn("9", "su bing 2"));
//		subColumns.add(new CNMarcSubColumn("4", "编译"));
//		col_x.setSubColumns(subColumns);
//		columns.put("702", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("702");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2("0");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "苏丙3"));
//		subColumns.add(new CNMarcSubColumn("9", "su bing 3"));
//		subColumns.add(new CNMarcSubColumn("4", "编译"));
//		col_x.setSubColumns(subColumns);
//		columns.put("702-1", col_x);
//		
//		col_x = new CNMarcColumnBase();
//		col_x.setCharacter("702");
//		col_x.setIdenticator1(" ");
//		col_x.setIdenticator2("0");
//		subColumns = new ArrayList<CNMarcSubColumn>();
//		subColumns.add(new CNMarcSubColumn("a", "苏丙4"));
//		subColumns.add(new CNMarcSubColumn("9", "su bing 4"));
//		subColumns.add(new CNMarcSubColumn("4", "编译"));
//		col_x.setSubColumns(subColumns);
//		columns.put("702-2", col_x);
////		
////	
//		try {
//			CNMarc nn = ICNMarcService.createCNMarcByMaps(columns);
//			System.out.println(nn.getDescription(true,true));
//			System.out.println(nn.getFieldDesc("702主标目")+"========================");
//			System.out.println(marc.getMarcData());
////			ICNMarcService.createCNMarcFile("d:\\mmm.iso", nn);
//			
//			
////			System.out.println("========================================================阅读================================================================================================================");
////			Map<String,String> rtn = ICNMarcService.getTranslateDesc(marc);
////			for(Iterator itr = rtn.entrySet().iterator(); itr.hasNext();){
////				Map.Entry entry = (Map.Entry)itr.next();
////				System.out.println(entry.getKey()+"---------------"+entry.getValue());
////			}
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		///excel 导出 用例
////		JSONArray jsonArr = JSONArray.fromObject(titleJson);
////		for (int i = 0; i < jsonArr.size(); i++) {
////			JSONObject jsonObject = (JSONObject) jsonArr.get(i);
////			    String key = (String)jsonObject.keys().next();
////			    System.out.println(key + "#" + jsonObject.getString(key));
////
////			
////		}
////		List<CNMarc> data = new ArrayList<CNMarc>();
////		List<CNMarc> data = ICNMarcService.loadCNMarcFromISO("E:\\work\\CNMARK\\样本数据国图 - 副本.iso");
//////		String ccc = "01504oam2 2200337   450 001001100000005001700011010003200028100004100060101001300101102002300114105001800137106000600155200020400161210003400365215002200399225002100421314002900442330025700471333003700728461002800765510010300793606001300896606001300909690001500922701005900937701005000996702003001046801002601076856005301102980001101155001155307520120517000000.0  a978-7-115-27075-7dCNY99.00  a20120517d2012    em y0chiy0110    ea1 achiceng  aCNb1100002GB2260  aak  z   000yy  ar1 a软件加密与解密9ruan jian jia mi yu jie mid = Surreptitious software: obfuscation, watermarking, and tamperproofing for software protectionf(美)Christian Collberg, (美)Jasvir Nagra着g崔孝晨译zeng  a北京c人民邮电出版社d2012.05  a601页c图表d24cm2 a图灵程序设计丛书  a责任者中文姓名取自版权页  a本书介绍了如何利用混淆、水印和防篡改等技术，来保护软件免受盗版、篡改和恶意逆向工程的危害，主要内容包括攻击者和防御者用来分析程序的各种主流方法，如何使用代码混淆技术使程序更难以被分析和理解，如何在软件中添加水印和指纹以标识软件的开发者和购买用户，等等。  a本书适合各层次软件开发人员阅读。 012001 a图灵程序设计丛书1 aSurreptitious software: obfuscation, watermarking, and tamperproofing for software protectionzeng0 a软件加密0 a解密软件  aTP309.7v4 0c(美)a科尔伯格9ke er bo gec(Collberg, Christian)4着 0c(美)a纳盖雷9na gai leic(Nagra, Jasvir)4着 0a崔孝晨9cui xiao chen4译 0aCNbCBIP.CNc201205174 uhttp://www.cbip.cn/BookInfo.aspx?BookId=11553075  c计算机";
////		///excel 导出 用例
////		ICNMarcService.createCNMarcFile("d:\\cnmarc.xls", true, data);
////		
////		CNMarc mm = ICNMarcService.loadCNMarcFromString("01917oam2 2200337   450 001001100000005001700011010003200028100004100060101000800101102002300109105001800132106000600150200013900156210003400295215002200329225004300351305002500394306011400419314002900533320002600562330061700588333005701205410005001262510004001312606003701352690001101389701006501400801002601465856005301491980003501544001155312520120517000000.0  a978-7-115-27794-7dCNY59.00  a20120517e2012    em y0chiy0110    ea0 aeng  aCNb1100002GB2260  aa   z   001yy  ar1 a计算机科学概论（本科）Aji suan ji ke xue gai lun（ben ke）d = Computer science: and overviewe英文版f(美)J. Glenn Brookshear着zeng  a北京c人民邮电出版社d2012.05  a609页c图表d24cm2 a国外着名高等院校信息科学与技术优秀教材  a根据原书第十一版影印  aChina edition published by PEARSON EDUCATION ASIA LTD., and POSTS ＆TELECOMMUNICATIONS PRESS Copyright ?2012.  a责任者中文姓名取自版权页  a有索引（第597-609页）  a本书是计算机科学概论课程的经典教材，全书对计算机科学做了百科全书式的精彩阐述，充分展现了计算机科学的历史背景、发展历程和新的技术趋势。本书首先介绍的是信息编码及计算机体系结构的基本原理(第1章和第2章)，进而讲述操作系统(第3章)和组网及因特网(第4章)，接着探讨了算法、程序设计语言及软件工程(第5章至第7章)，然后讨论数据抽象和数据库(第8章和第9章)方面的问题，第10章通过图形学讲述计算机技术的一些主要应用，第11章涉及人工智能，第12章通过对计算理论的介绍来结束全书。本书在内容编排上由具体到抽象逐步推进，很适合教学安排，每一个主题自然而然地引导出下一个主题。此外，书中还包含大量的图、表和示例，有助于读者对知识的了解与把握。  a本书适合用作高等院校计算机以及相关专业本科生的教材。 012001 a国外着名高等院校信息科学与技术优秀教材1 aComputer science: and overviewzeng0 a计算机科学x高等学校j教材x英文  aTP3v4 0c(美)a布鲁克希尔Abu lu ke xi erc(Brookshear, J. Glenn)4着 0aCNbCBIP.CNc201205174 uhttp://www.cbip.cn/BookInfo.aspx?BookId=11553125  c计算机b教材s计算机类u理工类");
////		try {
////			System.out.println("国图:"+mm.getDescription(true,false));
////			System.out.println("国图:"+mm.getCatalog().getMarcData());
////			//转为calis
////			mm = CNMarcUtils.cn2Calis(mm);
////			System.out.println("marc::::"+mm.getMarcData());
////			System.out.println("calis:"+mm.getDescription(true,false));
////			System.out.println("calis:"+mm.getCatalog().getMarcData());
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		System.out.println("==========="+mm.getFieldDesc("200并列正题名"));
////		data.add(mm);
////		ICNMarcService.createCNMarcFile("d:\\test.xls",true, data);
		String filePath = "D:\\fedoraSpace\\fedoraTempDir\\bres\\type23\\126768\\1b8ad47d-c486-4cea-9dd6-9dd6a887b318\\iso\\2013717.iso";
		String encode = Epub2Html.getFileCharsetByPath(filePath);
		System.out.println("---"+encode);
	}
}
