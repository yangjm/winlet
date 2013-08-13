package com.aggrepoint.winlet.site;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.aggrepoint.winlet.site.dao.fs.BranchDao;
import com.aggrepoint.winlet.site.domain.Branch;

/**
 * 负责从文件系统加载站点配置
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class FileSystemCfgLoader {
	/** 检查是否有变更的时间间隔 */
	private int interval;
	/** 根目录 */
	private File rootDir;
	/** 上一次检查配置文件时间 */
	long lastCheckTime;
	/** 上次检查配置文件时最大的文件时间 */
	long lastMaxModified;
	/** 上次检查配置文件时文件数量 */
	int lastCount;

	public FileSystemCfgLoader(String path, int checkInterval) {
		rootDir = new File(path);
		interval = checkInterval;
	}

	public synchronized ArrayList<Branch> load(ArrayList<Branch> branches) {
		boolean reload = false;

		if (lastMaxModified == 0
				|| System.currentTimeMillis() - lastCheckTime > interval) {
			long maxModified = getMaxModified(rootDir);
			int count = getCount(rootDir);
			if (maxModified > lastMaxModified || count != lastCount) {
				lastMaxModified = maxModified;
				lastCount = count;
				reload = true;
			}
			lastCheckTime = System.currentTimeMillis();
		}

		if (!reload)
			return branches;

		ArrayList<Branch> bs = new ArrayList<Branch>();

		for (File f : rootDir.listFiles()) {
			if (f.isDirectory()) {
				Branch b = BranchDao.load(f);
				if (b != null) {
					b.init();
					bs.add(b);
				}
			}
		}

		Collections.sort(bs);

		return bs;
	}

	public long getMaxModified(File file) {
		long max = file.lastModified();

		if (file.isDirectory())
			for (File f : file.listFiles()) {
				long m = getMaxModified(f);
				if (m > max)
					max = m;
			}

		return max;
	}

	public int getCount(File file) {
		int i = 1;

		if (file.isDirectory())
			for (File f : file.listFiles()) {
				i += getCount(f);
			}

		return i;
	}
}
