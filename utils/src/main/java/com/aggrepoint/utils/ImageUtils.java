/*
 * 创建日期 2006-1-14
 */
package com.aggrepoint.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.imgscalr.Scalr;

/**
 * @author Yang Jiang Ming
 * 
 *         图片处理功能集合
 */
public class ImageUtils {
	private static char[] m_chars = { '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'E', 'F', 'G', 'H', 'J', 'K',
			'L', 'M', 'N', 'P', 'R', 'S', 'T', 'V', 'W', 'X', 'Y' };

	private static double PI2 = java.lang.Math.PI * 2;

	private static Random m_ran = new Random();

	public static double getRandomDouble(double lo, double hi) {
		return lo + ((hi - lo + 1) * m_ran.nextFloat());
	}

	public static int getRandomInt(int lo, int hi) {
		return lo + (int) ((hi - lo + 1) * m_ran.nextFloat());
	}

	public static String getRandomNumber(int len) {
		StringBuffer sb = new StringBuffer();
		int p;
		for (int i = 0; i < len; i++) {
			p = getRandomInt(0, m_chars.length);
			if (p < 0)
				p = 0;
			if (p >= m_chars.length)
				p = m_chars.length - 1;
			sb.append(m_chars[p]);
		}
		return sb.toString();
	}

	public static BufferedImage readImage(File file) throws IOException {
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			String name = file.getName().toUpperCase();
			if (name.endsWith(".JPG") || name.endsWith(".JPEG")) { // 可能是CMYK，转换为RGB
				// 使用这个简单的方案，但是转换结果颜色不对：
				// http://stackoverflow.com/questions/2408613/problem-reading-jpeg-image-using-imageio-readfile-file
				// 这个方案看起来更完整，有点复杂暂时不用：
				// http://stackoverflow.com/questions/3123574/how-to-convert-from-cmyk-to-rgb-in-java-correctly

				// Find a suitable ImageReader
				Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
				ImageReader reader = null;
				while (readers.hasNext()) {
					reader = (ImageReader) readers.next();
					if (reader.canReadRaster()) {
						break;
					}
				}

				// Stream the image file (the original CMYK image)
				ImageInputStream input = ImageIO.createImageInputStream(file);
				reader.setInput(input);

				// Read the image raster
				Raster raster = reader.readRaster(0, null);

				// Create a new RGB image
				BufferedImage bi = new BufferedImage(raster.getWidth(), raster.getHeight(),
						BufferedImage.TYPE_4BYTE_ABGR);

				// Fill the new image with the old raster
				bi.getRaster().setRect(raster);

				return bi;
			}

			throw e;
		}
	}

	public static int writeImage(String dest, BufferedImage bufferedImage, float quality) throws IOException {
		// extracts extension of output file
		String formatName = dest.substring(dest.lastIndexOf(".") + 1);

		File output = new File(dest);

		if (formatName.equalsIgnoreCase("jpg") || formatName.equalsIgnoreCase("jpeg")) {
			FileOutputStream fos = new FileOutputStream(output);

			Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName(formatName);
			ImageWriter imageWriter = iterator.next();
			ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
			imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			imageWriteParam.setCompressionQuality(quality);
			MemoryCacheImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(fos);
			imageWriter.setOutput(imageOutputStream);
			IIOImage iioimage = new IIOImage(bufferedImage, null, (IIOMetadata) null);
			imageWriter.write((IIOMetadata) null, iioimage, imageWriteParam);
			imageOutputStream.flush();
			fos.close();
			imageOutputStream.close();
		} else {
			ImageIO.write(bufferedImage, formatName, output);
		}

		return (int) output.length();
	}

	/**
	 * 将源图片缩放为指定的尺寸大小
	 * 
	 * @param src
	 * @param dest
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static int resizeImage(BufferedImage inputImage, String dest, int width, int height, float quality)
			throws IOException {
		BufferedImage outputImage = Scalr.resize(inputImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, width, height,
				Scalr.OP_ANTIALIAS);
		return writeImage(dest, outputImage, 0.9f);
	}

	public static int resizeImage(BufferedImage inputImage, String dest, int width, int height) throws IOException {
		return resizeImage(inputImage, dest, width, height, 0.9f);
	}

	public static int resizeImage(String src, String dest, int width, int height) throws Exception {
		return resizeImage(readImage(new File(src)), dest, width, height);
	}

	/**
	 * 将源图片按比例缩放为指定的尺寸大小
	 * 
	 * @param srcImg
	 * @param dest
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static int[] shrinkImage(BufferedImage srcImg, String dest, int width, int height) throws IOException {
		int w = srcImg.getWidth(null);
		int h = srcImg.getHeight(null);

		if (w <= width && h <= height) {
			width = w;
			height = h;
		} else {
			double r1 = (double) width / (double) w;
			double r2 = (double) height / (double) h;

			if (r1 > r2) {
				width = (int) (w * r2);
				height = (int) (h * r2);
			} else {
				width = (int) (w * r1);
				height = (int) (h * r1);
			}

			if (width <= 0)
				width = 1;
			if (height <= 0)
				height = 1;
		}

		int size = resizeImage(srcImg, dest, width, height);
		return new int[] { width, height, size };
	}

	public static int[] shrinkImage(String src, String dest, int width, int height) throws IOException {
		return shrinkImage(readImage(new File(src)), dest, width, height);
	}

	/**
	 * 扭曲图片
	 * 
	 * @param srcBmp
	 * @param bXDir
	 * @return
	 */
	public static BufferedImage twistImage(BufferedImage srcBmp, int width, int height, boolean bXDir) {
		double dMultValue = getRandomDouble(1, 3);
		double dPhase = getRandomDouble(0, PI2);

		if (width > srcBmp.getWidth())
			width = srcBmp.getWidth();
		if (height > srcBmp.getHeight())
			height = srcBmp.getHeight();

		BufferedImage destBmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics g = destBmp.getGraphics();

		// 设定背景色
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);

		// 画边框
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);

		g.dispose();

		double dBaseAxisLen = bXDir ? (double) height : (double) width;

		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				double dx = 0;
				dx = bXDir ? (PI2 * (double) j) / dBaseAxisLen : (PI2 * (double) i) / dBaseAxisLen;
				dx += dPhase;
				double dy = Math.sin(dx);

				// 取得当前点的颜色
				int nOldX = 0, nOldY = 0;
				nOldX = bXDir ? i + (int) (dy * dMultValue) : i;
				nOldY = bXDir ? j : j + (int) (dy * dMultValue);

				int color = srcBmp.getRGB(i, j);
				if (nOldX >= 1 && nOldX < width - 1 && nOldY >= 1 && nOldY < height - 1) {
					destBmp.setRGB(nOldX, nOldY, color);
				}
			}
		}

		return destBmp;
	}

	/**
	 * 生成验证码图片
	 * 
	 * @param rand
	 * @param size
	 * @return
	 */
	public static BufferedImage genRandomImage(String rand, int size) {
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Font font = new Font("Courier New", Font.BOLD, size);

		// 获取图形上下文
		Graphics2D g = image.createGraphics();
		Rectangle2D rect = font.getStringBounds(rand, g.getFontRenderContext());

		// 设定背景色
		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());

		// 将验证码显示到图象中
		g.setColor(Color.black);
		g.setFont(font);
		g.drawString(rand, 2, (int) rect.getHeight() - 2);

		// 图象生效
		g.dispose();

		// 随机产生干扰点
		for (int i = 0; i < 500; i++)
			image.setRGB(m_ran.nextInt(image.getWidth()), m_ran.nextInt(image.getWidth()), 0);

		return twistImage(image, (int) rect.getWidth() + 4, (int) rect.getHeight() + 4, true);
	}

	/**
	 * 将图片顺时针旋转90度
	 * 
	 * @param src
	 * @return
	 */
	public static BufferedImage roate90(BufferedImage src) {
		int width = src.getWidth();
		int height = src.getHeight();

		BufferedImage bRotate = new BufferedImage(height, width, src.getType());
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				bRotate.setRGB(height - 1 - y, x, src.getRGB(x, y));
		return bRotate;
	}
}
