package com.java.lang.ref;
/**
 * Finalizer
 * 	作用：jvm无法管理native资源(jvm通过jni暴漏出来的功能)：例如直接内存DirectByteBuffer，网络连接SocksSocketImpl，文件流FileInputStream等与操作系统
 * 		有交互的资源，需要需要通过代码来释放，一般会有close()。但为了避免程序员忘记手动释放这些资源，java提供了finalizer机制通过重写finalizer()
 * 		作为最后一道保险
 * 	原理-finalize()
 * 		1）类加载时，实现了finalize()且方法内容非空的类会标记为finalize类（以下简称为标记类）
 * 		2）标记类实例化时，根据VM参数RegisterFinalizersAtInit值
 * 			· 默认true，在调用构造函数返回之前调用Finalizer.register(obj)将对象封装为Finalizer对象并将其注册进Finalizer对象链
 * 			· -XX:-RegisterFinalizersAtInit设为false，则将在对象空间分配好之后执行Finalizer.register(obj)
 * 		3）回收时，gc算法会判断标记类对象是不是只被Finalizer类引用，是则将此Finalizer对象放到Finalizer类的ReferenceQueue里，因为Finalizer类还持有对象的
 * 		引用（强引用）故本次GC无法回收，在gc完成之前，jvm会调用ReferenceQueue里的lock对象的notify()（当ReferenceQueue为空的时候，FinalizerThread线程会
 * 		调用ReferenceQueue的lock对象的wait()直到被jvm唤醒）唤醒FinalizerThread执行finalize()
 * 		4）下一次GC时，回收标记类对象（执行finilize()期间可能经过了多次GC）
 * 	原理-FinalizeThread
 * 		内部类FinalizerThread最多启动2个线程（Finalizer和Secondary finalizer）执行finalize()
 * 	应用
 * 		SocksSocketImpl	FileInputStream
 * 参考：
 * 	http://lovestblog.cn/blog/2015/07/09/final-reference/
 * 	https://blog.csdn.net/zqz_zqz/article/details/79225245
 * -----------------------------------------------------------------------------------------------------------
 * Cleaner
 * 		基于 PhantomReference 的，不会像finalizer有复活对象的机会。其它同Finalizer
 * 		JDK1.9的java.lang.ref.Cleaner其实是以前的 sun.misc.Cleaner 的公有API移植版
 * 	应用：DirectByteBuffer
 * @author nerv
 *
 */
public class Finalizer_ {

}
