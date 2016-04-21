package com.brainsoon.docviewer.support;

/**
 * 
 * @ClassName: ViewStatus 
 * @Description: 在线预览文件状态 枚举类
 * @author tanghui 
 * @date 2014-9-29 下午4:13:49 
 *
 */
public enum ViewStatus {
	$0("待转换", 0), //待转换 
	$1("转换中", 1), //转换中
	$2("转换成功", 2), //转换成功 
	$3("转换失败", 3), //转换失败
	$4("无需转换", 4), //无需转换
	$5("文件未进入待转换队列，写入出错", 5), //文件未进入待转换队列，写入出错
	$6("传入的文件路径为空", 6), //传入的文件路径为空
	$7("文件不存在", 7), //文件不存在
	$8("无法识别文件扩展名", 8);  //无法识别文件扩展名
	
	// 成员变量
    private String name;
    private int index;

    // 构造方法
    private ViewStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (ViewStatus c : ViewStatus.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
