package com.brainsoon.common.util.dofile.test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager

{

private static ThreadPoolManager tpm = new ThreadPoolManager();

// 线程池维护线程的最少数量

private final static int CORE_POOL_SIZE = 4;

// 线程池维护线程的最大数量

private final static int MAX_POOL_SIZE = 10;

// 线程池维护线程所允许的空闲时间

private final static int KEEP_ALIVE_TIME = 0;

// 线程池所使用的缓冲队列大小

private final static int WORK_QUEUE_SIZE = 10;

// 消息缓冲队列

Queue msgQueue = new LinkedList();

// 访问消息缓存的调度线程

final Runnable accessBufferThread = new Runnable(){

public void run(){

// 查看是否有待定请求，如果有，则创建一个新的AccessDBThread，并添加到线程池中

if( hasMoreAcquire() ){

String msg = ( String ) msgQueue.poll();

Runnable task = new AccessDBThread( msg );

threadPool.execute( task );

}

}

};

final RejectedExecutionHandler handler = new RejectedExecutionHandler()

{

public void rejectedExecution( Runnable r, ThreadPoolExecutor executor )

{

System.out.println(((AccessDBThread )r).getMsg()+"消息放入队列中重新等待执行");

msgQueue.offer((( AccessDBThread ) r ).getMsg() );

}

};

// 管理数据库访问的线程池

final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(

CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,

new ArrayBlockingQueue( WORK_QUEUE_SIZE ), this.handler );

// 调度线程池

final ScheduledExecutorService scheduler = Executors

.newScheduledThreadPool( 1 );

final ScheduledFuture taskHandler = scheduler.scheduleAtFixedRate(

accessBufferThread, 0, 1, TimeUnit.SECONDS );

public static ThreadPoolManager newInstance()

{

return tpm;

}

private ThreadPoolManager(){}

private boolean hasMoreAcquire()

{

return !msgQueue.isEmpty();

}

public void addLogMsg( String msg )

{

Runnable task = new AccessDBThread( msg );

threadPool.execute( task );

}

}





//成功地编译Java applet之后生成响应的字节码文件HelloWorld.class的文件。用资源管理器或DIR命令列出目录列表，将会发现目录C:\ghq中多了一个名为HelloWorld.class的文件。
//
//(3)创建HTML文件
//
//在运行创建的HelloWorld.class 之前，还需创建一个HTML文件，appletviewer或浏览器将通过该文件访问创建的Applet。为运行HelloWorld.class, 需要创建包含如下HTML语句的名为HelloWorld.html的文件。
//
//<HTML>
//
//<TITLE>HelloWorld! Applet</TITLE>
//
//<APPLET
//
//CODE="JavaWorld.class"
//
//WIDTH=200
//
//HEIGHT=100>
//
//</APPLET>
//
//</HTML>
//
//本例中，<APPLET>语句指明该Applet字节码类文件名和以像素为单位的窗口的尺寸。虽然这里HTML文件使用的文件名为 HelloWorld.HTML，它对应于HelloWorld.java的名字，但这种对应关系不是必须的，可以用其他的任何名字(比如说 Ghq.HTML)命名该HTML文件。但是使文件名保持一种对应关系可给文件的管理带来方便。
//
//(4)执行 HelloWorld.html
//
//如果用appletviewer运行HelloWorld.html,需输入如下的命令行：
//
//C:\ghq\>appletviewer JavaWorld.html<ENTER>
//
//可以看出，该命令启动了appletviewer并指明了HTML文件，该HTML文件中包含对应于HelloWorld 的<APPLET>语句。
//
//如果用浏览器运行HelloWorld Applet,需在浏览器的地址栏中输入HTML文件URL地址。
//
//至此，一个Applet程序的开发运行整个过程结束了(包括java源文件、编译的class文件、html文件以及用appletviewer或用浏览器运行)。
//
//(二) Applet类
//
//Applet类是所有Applet应用的基类，所有的Java小应用程序都必须继承该类。如下所示。
//
//import java. applet.*;
//
//public class OurApplet extends Applet
//
//{
//
//......
//
//......
//
//}
//
//Applet类的构造函数只有一种，即：public Applet()
//
//Applet实现了很多基本的方法，下面列出了Applet类中常用方法和用途。
//
//public final void setStub(AppletStub stub)
//
////设置Applet的stub.stub是Java和C之间转换参数并返回值的代码位，它是由系统自动设定的。
//
//public boolean isActive();// 判断一个Applet是否处于活动状态。
//
//public URL getDocumentBase();// 检索表示该Applet运行的文件目录的对象。
//
//public URL getCodeBase();// 获取该Applet 代码的URL地址。
//
//public String getParameter(String name)；// 获取该Applet 由name指定参数的值。
//
//public AppletContext getAppletContext()；// 返回浏览器或小应用程序观察器。
//
//public void resize(int width,int height)；// 调整Applet运行的窗口尺寸。
//
//public void resize(Dimension d)；// 调整Applet运行的窗口尺寸。
//
//public void showStatus(String msg)；// 在浏览器的状态条中显示指定的信息。
//
//public Image getImage(URL url)； // 按url指定的地址装入图象。
//
//public Image getImage(URL url,String name)；// 按url指定的地址和文件名加载图像。
//
//public AudioClip getAudioClip(URL url)；// 按url指定的地址获取声音文件。
//
//public AudioClip getAudioClip(URL url, String name)；// 按url指定的地址和文件名获取声音。
//
//public String getAppletInfo()；// 返回Applet应用有关的作者、版本和版权方面的信息；
//
//public String[][] getParameterInfo()；
//
//// 返回描述Applet参数的字符串数组，该数组通常包含三个字符串： 参数名、该参数所需值的类型和该参数的说明。
//
//public void play(URL url)；// 加载并播放一个url指定的音频剪辑。
//
//public void destroy()；//撤消Applet及其所占用的资源。若该Applet是活动的，则先终止该Applet的运行。
//
//(1) Applet运行状态控制基本方法
//
//Applet类中的四种基本方法用来控制其运行状态：init()、start()、stop()、destroy()
//
//init()方法
//
//这个方法主要是为Applet的正常运行做一些初始化工作。当一个Applet被系统调用时，系统首先调用的就是该方法。通常可以在该方法中完成从网页向Applet传递参数，添加用户界面的基本组件等操作。
//
//start()方法
//
//系统在调用完init()方法之后，将自动调用start()方法。而且，每当用户离开包含该Applet的主页后又再返回时，系统又会再执行一遍start()方法。这就意味着start()方法可以被多次执行，而不像init()方法。因此，可把只希望执行一遍的代码放在init()方法中。可以在start()方法中开始一个线程，如继续一个动画、声音等。
//
//stop()方法
//
//这个方法在用户离开Applet所在页面时执行，因此，它也是可以被多次执行的。它使你可以在用户并不注意Applet的时候，停止一些耗用系统资源的工作以免影响系统的运行速度，且并不需要人为地去调用该方法。如果Applet中不包含动画、声音等程序，通常也不必实现该方法。
//
//destroy()方法
//
//与对象的finalize()方法不同，Java在浏览器关闭的时候才调用该方法。Applet是嵌在HTML文件中的，所以 destroty()方法不关心何时Applet被关闭，它在浏览器关闭的时候自动执行。在destroy()方法中一般可以要求收回占用的非内存独立资源。(如果在Applet仍在运行时浏览器被关闭，系统将先执行stop()方法，再执行destroy()方法。
//
//(2) Applet应用的有关参数说明
//
//利用Applet来接收从HTML中传递过来的参数,下面对这些参数作一简单说明：
//
//* CODE标志
//
//CODE标志指定Applet的类名；WIDTH和HEIGHT标志指定Applet窗口的像素尺寸。在APPLET语句里还可使用其他一些标志。
//
//* CODEBASE 标志
//
//CODEBASE标志指定Applet的URL地址。Applet的通用资源定位地址URL，它可以是绝对地址 ，如www.sun.com。也可以是相对于当前HTML所在目录的相对地址，如/AppletPath/Name。如果HTML文件不指定CODEBASE 标志，浏览器将使用和HTML文件相同的URL。
//
//* ALT 标志
//
//虽然Java在WWW上很受欢迎，但并非所有浏览器都对其提供支持。如果某浏览器无法运行Java Applet，那么它在遇到APPLET语句时将显示ALT标志指定的文本信息。
//
//* ALIGN 标志
//
//ALIGN标志可用来控制把Applet窗口显示在HTML文档窗口的什么位置。与HTML<LMG>语句一样，ALIGN标志指定的值可以是TOP、MIDDLE或BOTTOM。
//* VSPACE与HSPACE 标志
//
//VSPACE和HSPACE标志指定浏览器显示在Applet窗口周围的水平和竖直空白条的尺寸，单位为像素。如下例使用该标志在Applet窗口之上和之下各留出50像素的空白，在其左和其右各留出25像素的空白：
//
//* NAME 标志
//
//NAME标志把指定的名字赋予Applet的当前实例。当浏览器同时运行两个或多个Applet时，各Applet可通过名字相互引用或交换信息。如果忽略NAME标志，Applet的名字将对应于其类名。
//
//* PARAM 标志
//
//通用性是程序设计所追求的目标之一。使用户或者程序员能很方便地使用同一个Applet完成不同的任务是通用性的具体表现。从HTML文件获取信息是提高Applet通用性的一条有效途径。
//
//假设编制了一个把某公司的名字在屏幕上卷动的Applet。为了使该Applet更加通用，则可以使该Applet从HTML文件获取需要卷动的文本信息。这样，若想显示另一个公司的名字，用不着修改Java Applet本身，只需修改HTML文件即可。
//
//PARAM 标志可用来在HTML文件里指定参数，格式如下所示：
//
//PARAM Name="name" Value="Liter"
//
//Java Applet可调用getParameter方法获取HTML文件里设置的参数值。
