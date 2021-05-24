package com.java.awt.image;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Test;

import sun.font.FontDesignMetrics;
 
/**
 * BufferedImage
 * 		具有可访问图像数据缓冲区的 Image，由图像数据的 ColorModel 和 Raster，分别用于存储图像的颜色数据与像素数据
 * 		通过ImageIO读写图片
 * 	实例方法
 * 		getWidth()		图像宽度
 * 		getHeight()		图像高度
 * 		getMinX()		最大横坐标
 * 		getMinY()		最大纵坐标
 * 		getRGB(x, y)	返回默认 RGB的ColorModel (TYPE_INT_ARGB) 和默认 sRGB 颜色空间中的整数像素。
 * 						返回的int值前3个字节分别表示rgb的三个颜色（1字节=8位，表示0-255，即每个颜色分量只有 8 位精度）。最高字节用来表示透明度
 * 						如果此默认模型与该图像的 ColorModel 不匹配则发生颜色转换，若图像具有比每个样本8比特和/或每像素4个样本更高的精度,则发生精度损失
 * 		getType()		返回图像类型。如果不是已知的类型（即不是BufferedImage的静态常量），则返回 TYPE_CUSTOM
 * 	示例
 * 		图像加载/输出
 * 		图像合成
 * 		图像缩放
 * @author sjl
 * @date 2019-04-23 17:58
 */
public class BufferedImage_ {

    private static final String PATH_PNG = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1591778889564&di=b464204d75d603f83bed66e0170ca3fc&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F9vo3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2F3801213fb80e7bec5d8543262e2eb9389a506b9d.jpg";
    private static final String PATH_BASE = "d:\\nerv\\Pictures\\永恒之心\\「ZERO动漫」ef - a fairy tale of the two_230P.jpg";
    private BufferedImage image;
    
    /**
     * 读取全部像素数据
     * @param image
     * @param x
     * @param y
     * @param width
     * @param height
     * @param pixels
     * @return
     */
    public int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            return (int[]) image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }
    
    /**
     * 写入像素数据
     * @param image
     * @param x
     * @param y
     * @param width
     * @param height
     * @param pixels
     */
    public void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        } else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }
    
    /**
     * 加载远程图片
     * @return
     * @throws IOException
     */
    public BufferedImage loadImageUrl(String path) {
    	//url 为图片的URL 地址
		try {
			URL url = new URL(path);
			return ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * 获取坐标点处的颜色
     * @param x
     * @param y
     */
    public Color getColor(int x, int y) {
    	// 获取图片的每个像素点的像素值
    	int pixel = image.getRGB(x, y);
    	int[] rgb = new int[3];
    	rgb[0] = (pixel & 0xff0000) >> 16;
        rgb[1] = (pixel & 0xff00) >> 8;
        rgb[2] = (pixel & 0xff);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }
    
    /**
     * 测试：合成图片并添加文字
     * @param args
     */
    public static void main(String[] args) {
    	BufferedImage_ o = new BufferedImage_();
    	BufferedImage nerv = o.loadImageLocal(PATH_BASE);
		ImageObserve imgO = new ImageObserve(o.loadImageUrl(PATH_PNG), 0, 0);
		StringObserve strO = new StringObserve("李智大帝万岁！", 40);
		o.save("d:\\t.png", o.modify(nerv, imgO, strO));
	}
    
    /**
     * 导入本地图片到缓冲区
     */
    public BufferedImage loadImageLocal(String path) {
        try {
        	return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(e.getMessage() + "----" + path);
        }
        return null;
    }
 
    /**
     * 生成图片到本地
     */
    public void save(String path, BufferedImage img) {
        try {
            File outputfile = new File(path);
            ImageIO.write(img, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 图片合成
     * @param img		基底图像
     * @param imgObs	素材图像
     * @param strObs	素材文字
     * @return
     */
    public BufferedImage modify(BufferedImage img, ImageObserve imgObs, StringObserve strObs) {
        try {
        	int w = img.getWidth();
            int h = img.getHeight();
            Graphics2D g = img.createGraphics();
 
            // 遮盖原本模板
            if (imgObs != null) {
                ImageObserver observer = new Checkbox();
                g.drawImage(zoom(imgObs.o, 100, 100), imgObs.x, imgObs.y, observer);
            }
 
            //设置背景颜色
            g.setBackground(Color.WHITE);
            //设置字体颜色
            g.setColor(Color.RED);
            
            // 图片右下角添加文字
            if (strObs != null) {
            	// 字体
                Font font = new Font("黑体", Font.ITALIC|Font.HANGING_BASELINE, strObs.fontSize);
                g.setFont(font);
                FontDesignMetrics fontMetrics = FontDesignMetrics.getMetrics(font);
            	// 最右： 图像宽度 - 字符串宽度
            	int x = w - fontMetrics.charsWidth(strObs.o.toCharArray(), 0, strObs.o.length());
            	// 最下：字体基准线y坐标 = 图像高度 - Descent
            	int y = h - fontMetrics.getDescent() ;
                g.drawString(strObs.o, x, y);
            }
            g.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return img;
    }
    
    /**
     * 按比例对图片进行缩放.
     * @param scale 缩放比例
     * @throws IOException
     */
    public BufferedImage zoom(BufferedImage img, double scale) throws IOException {
        //获取缩放后的长和宽
        int _width = (int) (scale * img.getWidth());
        int _height = (int) (scale * img.getHeight());
        //获取缩放后的Image对象
        Image _img = img.getScaledInstance(_width, _height, Image.SCALE_DEFAULT);
        //新建一个和Image对象相同大小的画布
        BufferedImage image = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
        //获取画笔
        Graphics2D graphics = image.createGraphics();
        //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image 信息通知的异步更新接口,没用到直接传空
        graphics.drawImage(_img, 0, 0, null);
        //释放资源
        graphics.dispose();
        return image;
    }
 
    /**
     * 指定长和宽对图片进行缩放
     * @param width 长
     * @param height 宽
     * @throws IOException
     */
    public BufferedImage zoom(BufferedImage img, int width, int height) throws IOException {
        //与按比例缩放的不同只在于,不需要获取新的长和宽,其余相同.
        Image _img = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(_img, 0, 0, null);
        graphics.dispose();
        return image;
    }
    
    //================================实体类===============================
    
    /**
     * 封装字符串及起始坐标
     * @author nerv
     *
     */
    private static class StringObserve {
    	String o;
    	int fontSize;
    	public StringObserve(String o, int fontSize) {
			this.o = o;
			this.fontSize = fontSize;
		}
    }
    
    private static class ImageObserve {
    	BufferedImage o;
    	int x;
    	int y;
    	public ImageObserve(BufferedImage o, int x, int y) {
			this.o = o;
			this.x = x;
			this.y = y;
		}
    }
    
    @Test
    public void test3() {
    	System.out.println(tt(-31));
    }
    
    private int tt(int n) {
    	return n >> 31 & 1;
    }
}
