package com.aggrepoint.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 与文件相关的一些工具方法
 * 
 * @author YJM
 */
public class FileUtils {
	/**
	 * 将文件内容作为一个字符串返回
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	static public String fileToString(String file, String encoding)
			throws Exception {
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[10240];
		int len = fis.read(buffer, 0, buffer.length);
		while (len > 0) {
			baos.write(buffer, 0, len);
			len = fis.read(buffer, 0, buffer.length);
		}
		fis.close();

		return baos.toString(encoding);
	}

	/**
	 * 生成指定的文件
	 * 
	 * @throws IOException
	 */
	static public void generateFiles(Collection<String> paths, String fileName,
			InputStream is) throws IOException {
		Vector<FileOutputStream> vecFiles = new Vector<FileOutputStream>();
		byte[] ba = new byte[102400];
		int len;

		for (String path : paths)
			vecFiles.add(new FileOutputStream(path + fileName));

		len = is.read(ba);
		while (len > 0) {
			for (FileOutputStream os : vecFiles)
				os.write(ba, 0, len);
			len = is.read(ba);
		}

		for (FileOutputStream os : vecFiles)
			os.close();
	}

	/**
	 * 生成指定的文件
	 */
	static public void generateFiles(Collection<String> fileNames,
			InputStream is) throws IOException {
		generateFiles(fileNames, "", is);
	}

	static public void generateFile(String fileName, InputStream is)
			throws IOException {
		generateFiles(Arrays.asList(fileName), is);
	}

	static public void createDirectory(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists() || !dir.isDirectory())
			dir.mkdirs();
	}

	/**
	 * 生成目录
	 */
	static public void createDirectories(Vector<String> dirs) {
		for (Enumeration<String> e = dirs.elements(); e.hasMoreElements();)
			createDirectory(e.nextElement());
	}

	/**
	 * 生成目录
	 */
	static public void createDirectories(Vector<String> dirs, String userName,
			String groupName) {
		for (Enumeration<String> e = dirs.elements(); e.hasMoreElements();)
			createDirectory(e.nextElement());
	}

	/**
	 * 删除目录
	 */
	static public void removeDirectory(File dir) {
		File[] files;
		files = dir.listFiles();
		for (int j = 0; j < files.length; j++) {
			if (files[j].isDirectory())
				removeDirectory(files[j]);
			else
				files[j].delete();
		}
		dir.delete();
	}

	/**
	 * 删除目录
	 */
	static public void removeDirectory(String path) {
		File dir;

		dir = new File(path);
		if (dir.exists() && dir.isDirectory())
			removeDirectory(dir);
	}

	/**
	 * 删除目录
	 */
	static public void removeDirectories(Vector<String> dirs) {
		File dir;
		int count = dirs.size();

		for (int i = 0; i < count; i++) {
			dir = new File(dirs.elementAt(i));
			if (dir.exists() && dir.isDirectory())
				removeDirectory(dir);
		}
	}

	/**
	 * 删除指定目录下的所有空的子目录
	 * 
	 * @param path
	 *            File
	 * @return true 表示指定目录中不包含任何文件，已经被删除
	 */
	static public boolean removeEmptyDirectories(File path) {
		boolean isEmpty = true;
		File[] files = path.listFiles();
		File file;
		for (int i = 0; i < files.length; i++) {
			file = files[i];

			if (file.isDirectory()) {
				if (!removeEmptyDirectories(file))
					isEmpty = false;
			} else
				isEmpty = false;
		}
		if (isEmpty) {
			path.delete();
			return true;
		}

		return false;
	}

	static public boolean removeEmptyDirectories(String path) {
		return removeEmptyDirectories(new File(path));
	}

	/**
	 * 拷贝文件
	 */
	static public void copyFile(String from, String to)
			throws FileNotFoundException, IOException {
		int len;
		byte[] ba = new byte[10 * 1024];

		FileOutputStream fos = new FileOutputStream(to);
		FileInputStream fis = new FileInputStream(from);
		len = fis.read(ba);
		while (len > 0) {
			fos.write(ba, 0, len);
			len = fis.read(ba);
		}
		fis.close();
		fos.close();
	}

	static public void copyDir(File from, File to, FilenameFilter filter,
			int[] counts) throws IOException {
		if (!from.isDirectory())
			return;
		to.mkdirs();
		if (counts != null && counts.length > 0)
			counts[0]++;

		File[] files;
		if (filter == null)
			files = from.listFiles();
		else
			files = from.listFiles(filter);
		for (File f : files) {
			if (!f.isDirectory()) {
				int len;
				byte[] ba = new byte[10 * 1024];

				FileOutputStream fos = new FileOutputStream(
						to.getCanonicalPath() + File.separator + f.getName());
				FileInputStream fis = new FileInputStream(f);
				len = fis.read(ba);
				while (len > 0) {
					fos.write(ba, 0, len);
					len = fis.read(ba);
				}
				fis.close();
				fos.close();

				if (counts != null && counts.length > 1)
					counts[1]++;
			} else
				copyDir(f,
						new File(to.getCanonicalPath() + File.separator
								+ f.getName()), filter, counts);
		}
	}

	/**
	 * 合并文件
	 */
	static public void combineFiles(String from1, String from2, String from3,
			String to) throws FileNotFoundException, IOException {
		int len;
		byte[] ba = new byte[10 * 1024];

		FileOutputStream fos = new FileOutputStream(to);
		FileInputStream fis = new FileInputStream(from1);

		len = fis.read(ba);
		while (len > 0) {
			fos.write(ba, 0, len);
			len = fis.read(ba);
		}
		fis.close();

		fis = new FileInputStream(from2);
		len = fis.read(ba);
		while (len > 0) {
			fos.write(ba, 0, len);
			len = fis.read(ba);
		}
		fis.close();

		fis = new FileInputStream(from3);
		len = fis.read(ba);
		while (len > 0) {
			fos.write(ba, 0, len);
			len = fis.read(ba);
		}
		fis.close();

		fos.close();
	}

	/**
	 * 压缩目录到Zip文件
	 * 
	 * @param path
	 * @param zip
	 */
	public static void zip(String path, String zip) throws Exception {
		// 要压缩到的Zip文件可能放在被压缩目录中，因此必须先获取文件列表
		File fileSource = new File(path);
		File[] files = fileSource.listFiles();
		byte[] buffer = new byte[10240];

		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zip));

		URI root = fileSource.toURI();

		for (File f : files)
			addFile(root, f, zout, buffer);
		zout.flush();
		zout.close();
	}

	public static void zip(String path, OutputStream os) throws Exception {
		File fileSource = new File(path);
		File[] files = fileSource.listFiles();
		byte[] buffer = new byte[10240];

		ZipOutputStream zout = new ZipOutputStream(os);

		URI root = fileSource.toURI();

		for (File f : files)
			addFile(root, f, zout, buffer);
		zout.flush();
		zout.close();
	}

	private static void addFile(URI root, File file, ZipOutputStream zip,
			byte[] buffer) throws Exception {
		if (file.isDirectory()) {
			for (File f : file.listFiles())
				addFile(root, f, zip, buffer);
		} else {
			FileInputStream fin = new FileInputStream(file);
			zip.putNextEntry(new ZipEntry(root.relativize(file.toURI())
					.getPath()));
			int length;
			while ((length = fin.read(buffer)) > 0) {
				zip.write(buffer, 0, length);
			}
			zip.closeEntry();
			fin.close();
		}
	}

	/**
	 * 解压Zip文件
	 * 
	 * @param rootPath
	 *            String 要存放解出文件的目录
	 * @param fis
	 *            InputStream Zip文件输入流
	 */
	public static void unzip(String rootPath, InputStream fis) throws Exception {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

		ZipEntry entry;
		int count;
		byte buffer[] = new byte[2048];
		String fileName;
		String pathName;
		FileOutputStream fos;
		BufferedOutputStream dest = null;

		while ((entry = zis.getNextEntry()) != null) {
			fileName = rootPath + entry.getName();

			// {创建目录
			// 注：在NT环境下测试时发现目录分隔符也是'/'
			pathName = fileName.substring(0, fileName.lastIndexOf('/') + 1);
			createDirectory(pathName);
			if (pathName.equals(fileName)) // 目录项，不是文件
				continue;
			// }

			// {解开文件
			fos = new FileOutputStream(fileName);
			dest = new BufferedOutputStream(fos, buffer.length);
			while ((count = zis.read(buffer, 0, buffer.length)) != -1) {
				dest.write(buffer, 0, count);
			}
			dest.flush();
			dest.close();
			// }
		}
		zis.close();
	}

	/**
	 * 解压Zip文件
	 * 
	 * @param rootPath
	 *            String 要存放解出文件的目录
	 * @param inputFile
	 *            String Zip文件
	 */
	public static void unzip(String rootPath, String inputFile)
			throws Exception {
		FileInputStream fis = new FileInputStream(inputFile);
		unzip(rootPath, fis);
		fis.close();
	}

	/**
	 * 解压Zip文件
	 * 
	 * @param vecDirs
	 *            String 要存放解出文件的目录
	 * @param fis
	 *            InputStream Zip文件输入流
	 */
	public static void unzip(Vector<String> vecDirs, InputStream fis)
			throws Exception {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

		ZipEntry entry;
		int count;
		byte buffer[] = new byte[102400];
		String fileName = "";
		String pathName = "";
		BufferedOutputStream dest = null;

		while ((entry = zis.getNextEntry()) != null) {
			Vector<String> vecFileNames = new Vector<String>();

			// 注：在NT环境下测试时发现Zip文件中的目录分隔符也是'/'
			for (Enumeration<String> e = vecDirs.elements(); e
					.hasMoreElements();)
				vecFileNames.add(e.nextElement() + "/" + entry.getName());

			// {创建目录
			for (Enumeration<String> e = vecFileNames.elements(); e
					.hasMoreElements();) {
				fileName = e.nextElement();
				pathName = fileName.substring(0, fileName.lastIndexOf('/') + 1);
				createDirectory(pathName);
			}
			if (pathName.equals(fileName)) // 目录项，不是文件
				continue;
			// }

			// {解开文件
			Vector<BufferedOutputStream> vecFiles = new Vector<BufferedOutputStream>();
			for (Enumeration<String> e = vecFileNames.elements(); e
					.hasMoreElements();)
				vecFiles.add(new BufferedOutputStream(new FileOutputStream(e
						.nextElement()), buffer.length));

			while ((count = zis.read(buffer, 0, buffer.length)) != -1)
				for (Enumeration<BufferedOutputStream> e = vecFiles.elements(); e
						.hasMoreElements();) {
					dest = e.nextElement();
					dest.write(buffer, 0, count);
				}
			for (Enumeration<BufferedOutputStream> e = vecFiles.elements(); e
					.hasMoreElements();) {
				dest = e.nextElement();
				dest.flush();
				dest.close();
			}
			// }
		}
		zis.close();
	}
}
