package com.nio;

import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

import sun.nio.ch.DirectBuffer;

/**
 * Buffer  NIO缓冲区，封装了除了boolean的7种基本类型的数组，提供了可操作数组的API
 * Buffer4个属性：0 <= mark <= position <= limit <= capacity
 * 		capacity	容量，即数组长度
 * 		limit		限制，数组仅前limit个元素可读写，已存在的>limit的mark被丢弃，已存在的>limit的postion=limit
 * 		position	即将读写的位置，如果>limit则limit = postion，已存在的>position的mark被丢弃
 * @author Nerv
 *
 */
public class Buffer_ {
	
	byte[] bs = {1,2,3,4,5,6};
	short[] s = {1, 2, 3};
	int[] is = {1, 2, 3, 4};
	long[] ls = {1, 2, 3, 4, 5};
	float[] fs = {1, 2, 3, 4, 5, 6};
	double[] ds = {1, 2, 3, 4, 5, 6, 7};
	char[] cs = {'李', '智', '大', '帝', '万', '岁', '万', '万', '岁', '\n'};

	
	/**
	 * 创建Buffer对象
	 * 		1.堆内存创建HeapXXXBuffer实例，重写了父类大部分方法
	 * 			wrap()：用已存在的数组创建HeapXXXBuffer实例，重写了父类大部分方法
	 * 			allocate()：用指定长度的空数组创建HeapXXXBuffter实例
	 * 		2.堆外内存创建DirectByteBuffer
	 * 			allocateDirect()：创建直接缓冲区，仅ByteBuffer有此方法
	 */
	@Test
	public void test1() {
		// wrap()
		IntBuffer intBuffer = IntBuffer.wrap(is);
		// allocate()
		ShortBuffer shortBuffer = ShortBuffer.allocate(3);
		// allocateDirect()
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
		LongBuffer longBuffer = LongBuffer.wrap(ls);
		// asReadOnlyBuffer()：根据已有的buffter复制一个只读的buffer对象
		FloatBuffer floatBuffer = FloatBuffer.wrap(fs).asReadOnlyBuffer(); 
		DoubleBuffer doubleBuffer = DoubleBuffer.wrap(ds);
		CharBuffer charBuffer = CharBuffer.wrap(cs);
		
		for(Buffer buffer : Arrays.asList(byteBuffer, shortBuffer, intBuffer, longBuffer, floatBuffer, doubleBuffer, charBuffer)) {
			System.out.println(buffer.getClass().getName() + "：capacity=" + buffer.capacity() +
					"，limit=" + buffer.limit() +
					"，postion="+buffer.position() +
					"，有剩余可用=" + buffer.hasRemaining() +
					"，剩余可用=" + buffer.remaining()	+ // remaining = limit - position
					"，只读缓冲区=" + buffer.isReadOnly() + 
					"，直接内存= " + buffer.isDirect() +
					"，堆内可写数组=" + buffer.hasArray() +	// 直接内存不使用堆内数组hb，故false
					""
			);
		}
		((DirectBuffer)byteBuffer).cleaner().clean();	//释放直接内存
	}
	
	/**
	 * warp(char[] array)：buffer存的是对数组的引用，所以数组值改变，则buffer取值也会变化
	 * 		mark不确定，position = 0，limit = capacity = array.length
	 * wrap(char[] array, int offset, int length)	// capacity = array.length，position = offset， limit = offset + length
	 */
	@Test
	public void test8() {
		CharBuffer buffer = CharBuffer.wrap(cs, 0, 10);
		cs[0] = 'a';	// 数组值改变
		System.out.println(buffer.get(0));
		buffer.put(1, 'b');	// buffer改变值
		System.out.println(cs[1]);
	}
	
	/**
	 * limit作用：反复向缓冲区读写数据时
	 * 		第一次缓存9个数据，读取完毕；第二次缓存4个数据，此时只有前4个元素有效，所以设置limit=4
	 */
	@Test
	public void test2() {
		CharBuffer buffer = CharBuffer.wrap(cs);
		buffer.limit(2);	// 如果postion > 2，则postion = 2
		// 以下两行代码都会报错
		System.out.println(buffer.get(2));
		buffer.put(2, 'c');
	}
	
	/**
	 * position：即将进行读写的元素索引
	 * put(value)：在position处赋值，并position++
	 * put(index,value)：在index处赋值
	 * get()：取position处的值，并position++
	 * get(index)：取index处的值
	 */
	@Test
	public void test3() {
		CharBuffer buffer = CharBuffer.wrap(cs);
		buffer.position(3);
		buffer.put('b');
		System.out.println(buffer.get(3));
		System.out.println(buffer.position());
		buffer.limit(2);	// 如果postion > 2，则postion = 2
		System.out.println(buffer.position());
	}
	
	/**
	 * put(srcBuffer)：取srcBuffer的remaining放入buffer，从position处开始放置，超过limit则报错，
	 * 两个buffer的position+=srcBuffer.remaining
	 * 	用途：可利用此方法扩容
	 */
	@Test
	public void test3_1() {
		CharBuffer buffer = CharBuffer.wrap(cs);
		buffer.position(4);
		CharBuffer srcBuffer = CharBuffer.wrap(new char[] {'道', '法', '自', '然'});
		buffer.put(srcBuffer);
		IntStream.range(buffer.position(), buffer.limit()).forEach(i -> System.out.println(i + "：" + buffer.get()));
	}
	/**
	 * ByteBuffer专属API
	 * 	putInt(value)：int转4字节byte后按当前order放入position，position+=4
	 * 	putInt(index,value)：int转4字节byte后放入index
	 * 	其余float、double、long、char、short同理
	 */
	@Test
	public void test3_2() {
		ByteBuffer buffer = ByteBuffer.wrap(bs);
		buffer.putInt(256);
		System.out.println("position = " + buffer.position());
		IntStream.range(0, buffer.limit()).forEach(i -> System.out.println(buffer.get(i)));
	}
	
	/**
	 * order()：获得缓冲区的多字节值的字节存储顺序（左高右低）
	 * 		BIG_ENDIAN：	高到低，默认
	 * 		LITTLE_ENDIAN：	低到高
	 */
	@Test
	public void test12() {
		ByteBuffer buffer1 = ByteBuffer.allocate(4), buffer2 = ByteBuffer.allocate(4);
		// buffer1默认BIG_ENDIAN
		buffer1.putInt(256);
		System.out.print(buffer1.order());
		IntStream.range(0, buffer1.limit()).forEach(i -> System.out.print(buffer1.get(i) + " "));
		// buffer2设为LITTLE_ENDIAN
		buffer2.order(ByteOrder.LITTLE_ENDIAN);
		buffer2.putInt(256);
		System.out.print("\n" + buffer2.order());
		IntStream.range(0, buffer2.limit()).forEach(i -> System.out.print(buffer2.get(i) + " "));
	}

	
	/**
	 * mark：标记当前position，reset()时position重置到mark处
	 * 		当mark==-1时，reset()报错
	 * 		mark默认-1
	 */
	@Test
	public void test4() {
		CharBuffer buffer = CharBuffer.wrap(cs);
		buffer.position(2);
		buffer.mark();	// 标记当前position
		buffer.position(3);
		buffer.reset();	// position重置为mark
		buffer.position(1);	// position < 已存在的mark，则mark = -1；limit同
		buffer.reset();	// mark == -1，报错
	}
	
	/**
	 * clear()：恢复默认，但不清除数据。用于重新写入之前
	 * 		limit = capacity，position = 0，mark = -1
	 * flip()：用在写入后，读取前
	 * 		limit = position, postion = 0，remark = -1
	 * rewind()：用于重新读取之前
	 * 		postion = 0，remark = -1
	 */
	@Test
	public void test5() {
		CharBuffer buffer = CharBuffer.allocate(20);
		IntStream.range(0, buffer.limit()).forEach(i -> buffer.put((char)i));	//写入数据
		buffer.clear();		// 重新写入前
		new String(cs).chars().forEach(i -> buffer.put((char)i));
		buffer.flip();		// 重新写入后，读取前
		IntStream.range(0, buffer.limit()).forEach(i -> System.out.print(buffer.get()));
		buffer.rewind();	// 重新读取前
		IntStream.range(0, buffer.limit()).forEach(i -> System.out.print(buffer.get()));
	}
	
	/**
	 * compact()：紧凑排列，将remaining内容复制，并从数组起始元素依次覆盖
	 * 		排列后，调用flip()重新遍历
	 */
	@Test
	public void test13() {
		CharBuffer buffer = CharBuffer.wrap(cs);
		IntStream.range(0, buffer.limit()).forEach(i -> System.out.print(buffer.get(i)));
		buffer.position(3);
		buffer.compact();
		buffer.flip();
		IntStream.range(0, buffer.limit()).forEach(i -> System.out.print(buffer.get(i)));
	}
	
	/**
	 * allocateDirect()适用于易受操作系统I/O影响的、大量的、长时间保存的数据
	 * 		分配直接内存慢于分配堆内存，处理数据高于堆内存
	 * 		底层用Unsafe分配、释放内存
	 * 
	 * 
	 * 测试分配内存：创建10w ByteBuffer对象
	 * 		堆内存：	3次平均59ms
	 * 		直接内存：	4次平均654ms
	 * 测试处理数据：赋值100w数据
	 * 
	 * 释放内存方式
	 * 		1.手动：((DirectBuffer)byteBuffer).cleaner().clean();
	 * 		2.自动：JVM执行full GC时回收
	 */
	@Test
	public void test7() {
		int type = 2;
		// 1. 测试分配内存
		if (type == 1) {
			long start = System.currentTimeMillis();
			IntStream.range(0, 1000000).forEach(i -> {
				//ByteBuffer.allocate(200);	// 59 60 58 
				ByteBuffer.allocateDirect(200);	// 650 687 655
			});
			System.out.println(System.currentTimeMillis() - start + "ms");
		} else {
		// 2. 测试处理数据
			int capacity = 1000000000;
			//ByteBuffer buffer = ByteBuffer.allocateDirect(capacity); // 410 392 400
			ByteBuffer buffer = ByteBuffer.allocate(capacity);	// 335 317 319
			long start = System.currentTimeMillis();
			IntStream.range(0, buffer.limit()).forEach(i -> buffer.put((byte)123));
			System.out.println(System.currentTimeMillis() - start + "ms");
		}
	}
	
	/**
	 * put(char[] src, int offset, int length)：从src的offset开始取length个元素，在buffer底层数组的position依次放入
	 * 		当remaining < length，报错
	 * put(char[] src)：相当于put(src, 0, src.length)
	 * get(char[] dst, int offset, int length)：从buffer底层数组的position开始取length个元素，在dst的offset处依次放入
	 * 		当remaining < length，报错
	 * get(char[] src)：相当于get(src, 0, src.length)
	 */
	@Test
	public void test9() {
		CharBuffer buffer = CharBuffer.wrap(cs);
		char[] src = {'a', 'b', 'c'};
		buffer.position(8);
		buffer.put(src, 1, 2);
		buffer.rewind();
		IntStream.range(0, buffer.limit()).forEach(i -> System.out.print(buffer.get()));
		
		buffer.position(2);
		buffer.get(src, 1, 2);
		System.out.println("\n" + src[1] + src[2]);
	}
	
	/**
	 * slice()：基于同一底层数组复制一个共享缓冲区
	 * 		是否直接内存、是否只读属性相同，capacity、position、limit、mark 独立
	 * 		newBuffer默认：position = 0, capacity = limit = buffer.remaining， mark = -1
	 * arrayOffset()：当前buffer的首元素在底层数组的索引（即距离数组首元素的偏移量）
	 */
	@Test
	public void test10() {
		CharBuffer buffer = CharBuffer.wrap(cs);
		buffer.position(3);
		buffer.limit(8);
		CharBuffer buffer2 = buffer.slice();
		buffer.put(7, '火');
		System.out.println(buffer.get(7) + "，offset="+ buffer.arrayOffset() +"，position = " + buffer.position() + "，capacity = " + buffer.capacity() + "，limit = " + buffer.limit());
		System.out.println(buffer2.get(4) + "，offset="+buffer2.arrayOffset() +"，position = " + buffer2.position() + "，capacity = " + buffer2.capacity()+ "，limit = "+ buffer2.limit());
	}
	
	/**
	 * byteBuffer.asCharBuffer()等
	 * 		asCharBuffer()：基于同一底层数组生成CharBuffer，remaining每2个字节转为1个字符（utf-16），不足2个字节则丢弃
	 * 		CharBuffer的capacity、limit为原来1/2
	 * 		是否直接内存、是否只读属性相同，capacity、position、limit、mark 独立
	 * 	
	 * 	中文乱码
	 * 		字符串转byte默认以utf-8编码，char本身使用utf-16字符集，避免乱码方法：
	 * 		1.字符串转byte用utf-16
	 * 			String.getBytes("utf-16")
	 * 		2.不用asCharBuffer()，直接用Charset以utf-8解码
	 * 			CharBuffer charBuffer = Charset.forName("utf-8").decode(byteBuffer);
	 * 		
	 * 	ByteBuffer转其它类型缓冲区同理
	 * 
	 * asReadOnlyBuffer()：基于同一数组创建新的只读的缓冲区
	 * 		limit、mark、position、capacity相同
	 * 		但两个缓冲区的limit、mark、position、capacity互相独立
	 * 
	 * duplicate()：基于同一数组创建新的缓冲区
	 * 		是否只读、是否直接内存 相同
	 * 		limit、mark、position、capacity相同
	 * 		但两个缓冲区的limit、mark、position、capacity互相独立
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void test11() throws UnsupportedEncodingException {
		// 默认utf-8，但char是utf-16编码
		ByteBuffer bufferB = ByteBuffer.wrap("李智大帝万岁万万岁！".getBytes("utf-16"));
		CharBuffer bufferC = bufferB.asCharBuffer();
		CharBuffer bufferC2 = Charset.forName("utf-8").decode(bufferB);
		System.out.println(bufferB.getClass().getName() + "：position=" +bufferB.position() + "，capacity="+bufferB.capacity()+"，limit=" + bufferB.limit());
		System.out.println(bufferC.getClass().getName() + "：position=" +bufferC.position() + "，capacity="+bufferC.capacity()+"，limit=" + bufferC.limit());
		System.out.println(bufferC2.getClass().getName() + "：position=" +bufferC2.position() + "，capacity="+bufferC2.capacity()+"，limit=" + bufferC2.limit());
		IntStream.range(bufferC.position(), bufferC.limit()).forEach(i -> System.out.print(bufferC.get()));
	}
	
	/**
	 * charBuffer专属API
	 * 		append()：从position处开始放入字符，同时position后移
	 * 		charAt()：距离position处的偏移量处的字符
	 * 		length()：== remaining
	 * 		subSequence(start,end)：基于同一底层数组创建新CharBuffer
	 * 			是否只读、是否直接内存相同，position= src.position+start，limit=src.position+end
	 * 			start <= end <= src.remaining
	 * 
	 */
	@Test
	public void test14() {
		CharBuffer buffer = CharBuffer.allocate(4);
		buffer.append('李');
		buffer.append('智');
		buffer.append("大帝");	// 等价于 buffer.append(s, 0, s.length)
		IntStream.range(0, buffer.limit()).forEach(i -> System.out.print(buffer.get(i)));
		System.out.println("\n" + buffer.position());
		buffer.position(2);
		System.out.println(buffer.charAt(0));
	}
}
