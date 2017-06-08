package com.ucg.util.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.ucg.util.excel.FileUtils;

/**
 * description : 图片操作类(剪切,缩小等图片相关操作)
 *
 * @version 1.0
 * @author xiaqiang
 * @createtime : 2014年10月21日 下午2:46:50
 * 
 * 修改历史:
 * 修改人                                          修改时间                                                  修改内容
 * --------------- ------------------- -----------------------------------
 * xiaqiang        2014年10月21日 下午2:46:50
 *
*/

public class ImageUtils {

	/**
	 * @author xiaqiang
	 * @createtime 2014年9月1日 下午3:30:44
	 * @Decription BufferedImage转InputStream
	 *
	 * @param bi BufferedImage对象
	 * @param imageType 图片类型,例如"png"、"jpg"
	 * @return
	 */
	public static InputStream getImageStream(BufferedImage bi, String imageType) {
		InputStream is = null;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut;
		try {
			imOut = ImageIO.createImageOutputStream(bs);
			ImageIO.write(bi, imageType, imOut);
			is = new ByteArrayInputStream(bs.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

	/**  
	 * 图像切割(直接传入源文件路径和目标文件路径)
	 * @param srcImageFile            源图像地址 
	 * @param result            新图像地址 
	 * @param x                       目标切片起点x坐标 
	 * @param y                      目标切片起点y坐标 
	 * @param destWidth              目标切片宽度 
	 * @param destHeight             目标切片高度 
	 * @throws Exception 
	 */
	public static void abscut(String srcImageFile, String result, int x, int y, int destWidth, int destHeight,
			int maxWidth, int maxHeight) throws Exception {
		String imageType = srcImageFile.substring(srcImageFile.lastIndexOf(".") + 1);
		try {
			Image img;
			ImageFilter cropFilter;

			double widthRatio = 0.0; // 缩放比例
			double heightRatio = 0.0; // 缩放比例
			// 读取源图像  
			File f = new File(srcImageFile);
			BufferedImage bi = ImageIO.read(f);

			// 计算比例
			heightRatio = (new Integer(maxHeight)).doubleValue() / bi.getHeight();
			widthRatio = (new Integer(maxWidth)).doubleValue() / bi.getWidth();

			double ratio = heightRatio > widthRatio ? widthRatio : heightRatio;
			int finalWidth = (int) (bi.getWidth() * ratio); //改变后的宽度
			int finalHeight = (int) (bi.getHeight() * ratio);//改变后的高度

			int srcWidth = bi.getWidth(); // 源图宽度  
			int srcHeight = bi.getHeight(); // 源图高度            
			if (srcWidth >= destWidth && srcHeight >= destHeight) {
				Image image = bi.getScaledInstance(finalWidth, finalHeight, bi.SCALE_SMOOTH);
				// 改进的想法:是否可用多线程加快切割速度  
				// 四个参数分别为图像起点坐标和宽高  
				// 即: CropImageFilter(int x,int y,int width,int height)  
				cropFilter = new CropImageFilter(x, y, destWidth, destHeight);
				img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
				BufferedImage bufImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
				Graphics g = bufImg.getGraphics();
				g.drawImage(img, 0, 0, null); // 绘制缩小后的图  
				g.dispose();
				// 输出为文件  

				File resultFile = new File(result);
				if (!FileUtils.isFileExist(result)) {
					resultFile.mkdirs();
				}
				ImageIO.write(bufImg, imageType, resultFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**  
	 * 图像切割 (直接传入输入流,返回输入流)
	 * @param is 输入流
	 * @param imageType 新图像地址 
	 * @param x 目标切片起点x坐标 
	 * @param y 目标切片起点y坐标 
	 * @param destWidth 目标切片宽度 
	 * @param destHeight 目标切片高度 
	 * @throws Exception 
	 */
	public static InputStream abscut(InputStream is, String imageType, int x, int y, int destWidth, int destHeight,
			int maxWidth, int maxHeight) throws Exception {
		try {
			Image img;
			ImageFilter cropFilter;

			double widthRatio = 0.0; // 缩放比例
			double heightRatio = 0.0; // 缩放比例
			// 读取源图像  
			BufferedImage bi = ImageIO.read(is);

			// 计算比例
			heightRatio = (new Integer(maxHeight)).doubleValue() / bi.getHeight();
			widthRatio = (new Integer(maxWidth)).doubleValue() / bi.getWidth();

			double ratio = heightRatio > widthRatio ? widthRatio : heightRatio;
			int finalWidth = (int) (bi.getWidth() * ratio); //改变后的宽度
			int finalHeight = (int) (bi.getHeight() * ratio);//改变后的高度

			int srcWidth = bi.getWidth(); // 源图宽度  
			int srcHeight = bi.getHeight(); // 源图高度            
			if (srcWidth >= destWidth && srcHeight >= destHeight) {
				Image image = bi.getScaledInstance(finalWidth, finalHeight, bi.SCALE_SMOOTH);
				// 改进的想法:是否可用多线程加快切割速度  
				// 四个参数分别为图像起点坐标和宽高  
				// 即: CropImageFilter(int x,int y,int width,int height)  
				cropFilter = new CropImageFilter(x, y, destWidth, destHeight);
				img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
				BufferedImage bufImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
				Graphics g = bufImg.createGraphics();
				g.drawImage(img, 0, 0, null); // 绘制缩小后的图  
				g.dispose();
				return getImageStream(bufImg, imageType);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/** 
	 * 图像类型转换 GIF->JPG GIF->PNG PNG->JPG PNG->GIF(X) 
	 */
	public static void convert(String source, String result) {
		try {
			File f = new File(source);
			f.canRead();
			f.canWrite();
			BufferedImage src = ImageIO.read(f);
			ImageIO.write(src, "JPG", new File(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 * 彩色转为黑白 
	 *  
	 * @param source 
	 * @param result 
	 */
	public static void gray(String source, String result) {
		try {
			BufferedImage src = ImageIO.read(new File(source));
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp op = new ColorConvertOp(cs, null);
			src = op.filter(src, null);
			ImageIO.write(src, "JPEG", new File(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能描述：缩放图像（按高度和宽度缩放）
	 * 
	 * @param srcImageFile
	 *            源图像文件地址
	 * @param result
	 *            缩放后的图像地址
	 * @param height
	 *            缩放后的高度(若为0,则以宽度计算比例)
	 * @param width
	 *            缩放后的宽度(若为0,则以高度计算比例)
	 * @param flag
	 *            比例不对时是否需要补白：true为补白; false为不补白;
	 * 
	 */
	public static void scale(String srcImageFile, String result, int height, int width, boolean flag) throws Exception {
		String imageType = srcImageFile.substring(srcImageFile.lastIndexOf(".") + 1);
		if (imageType.equalsIgnoreCase("png")) {
			flag = true;
		}
		try {
			double widthRatio = 0.0; // 缩放比例
			double heightRatio = 0.0; // 缩放比例
			File f = new File(srcImageFile);
			BufferedImage bi = ImageIO.read(f);

			// 计算比例

			heightRatio = (new Integer(height)).doubleValue() / bi.getHeight();
			widthRatio = (new Integer(width)).doubleValue() / bi.getWidth();

			if (height != 0 && width != 0) {

			} else {
				double ratio = heightRatio > widthRatio ? heightRatio : widthRatio;
				heightRatio = ratio;
				widthRatio = ratio;
				width = (int) (bi.getWidth() * ratio); //改变后的宽度
				height = (int) (bi.getHeight() * ratio);//改变后的高度
			}

			Image itemp = bi.getScaledInstance(width, height, bi.SCALE_SMOOTH);
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(heightRatio, widthRatio),
					null);
			itemp = op.filter(bi, null);
			if (flag) {// 补白
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				g.dispose();
				itemp = image;
			}
			BufferedImage bufImg = new BufferedImage(itemp.getWidth(null), itemp.getHeight(null),
					BufferedImage.TYPE_INT_RGB);
			Graphics g = bufImg.createGraphics();
			g.drawImage(itemp, 0, 0, null);
			g.dispose();

			File resultFile = new File(result);
			if (!FileUtils.isFileExist(result)) {
				resultFile.mkdirs();
			}
			ImageIO.write(bufImg, imageType, resultFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 功能描述：缩放图像（按高度和宽度缩放）传入输入流,返回的也是输入流
	 * 
	 * @param is
	 *            源图像文件地址
	 * @param height
	 *            缩放后的高度(若为0,则以宽度计算比例)
	 * @param width
	 *            缩放后的宽度(若为0,则以高度计算比例)
	 * @param flag
	 *            比例不对时是否需要补白：true为补白; false为不补白;
	 * 
	 */
	public static InputStream scale(InputStream is, String imageType, int height, int width, boolean flag)
			throws Exception {
		if (imageType.equalsIgnoreCase("png")) {
			flag = true;
		}
		try {
			double widthRatio = 0.0; // 缩放比例
			double heightRatio = 0.0; // 缩放比例
			BufferedImage bi = ImageIO.read(is);

			// 计算比例

			heightRatio = (new Integer(height)).doubleValue() / bi.getHeight();
			widthRatio = (new Integer(width)).doubleValue() / bi.getWidth();

			if (height != 0 && width != 0) {

			} else {
				double ratio = heightRatio > widthRatio ? heightRatio : widthRatio;
				heightRatio = ratio;
				widthRatio = ratio;
				width = (int) (bi.getWidth() * ratio); //改变后的宽度
				height = (int) (bi.getHeight() * ratio);//改变后的高度
			}

			Image itemp = bi.getScaledInstance(width, height, bi.SCALE_SMOOTH);
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(heightRatio, widthRatio),
					null);
			itemp = op.filter(bi, null);
			if (flag) {// 补白
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				g.dispose();
				itemp = image;
			}
			BufferedImage bufImg = new BufferedImage(itemp.getWidth(null), itemp.getHeight(null),
					BufferedImage.TYPE_INT_RGB);
			Graphics g = bufImg.createGraphics();
			g.drawImage(itemp, 0, 0, null);
			g.dispose();

			return getImageStream(bufImg, imageType);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
