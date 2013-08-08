package com.aggrepoint.winlet.site.dao.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.site.domain.Base;

/**
 * 工具类
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Utils {
	static final Log logger = LogFactory.getLog(Utils.class);

	/**
	 * 负责读取cfg.cfg中的配置信息
	 * 
	 * @param dir
	 * @return
	 */
	public static Hashtable<String, String> loadCfg(File dir) {
		Hashtable<String, String> cfgs = new Hashtable<String, String>();

		if (!dir.isDirectory())
			return cfgs;

		for (File f : dir.listFiles()) {
			if (f.isDirectory())
				continue;

			if (f.getName().equals("cfg.cfg")) {
				try {
					LineNumberReader lnr = new LineNumberReader(
							new InputStreamReader(new FileInputStream(f),
									"UTF-8"));
					String line = lnr.readLine();
					while (line != null) {
						int idx = line.indexOf(":");
						if (idx > 0)
							cfgs.put(line.substring(0, idx).trim(), line
									.substring(idx + 1).trim());
						line = lnr.readLine();
					}
					lnr.close();
				} catch (Exception e) {
					logger.error(
							"Error reading site configuration file: "
									+ f.getAbsolutePath(), e);
				}
				break;
			}
		}

		return cfgs;
	}

	public static void getNameAndOrder(Base base, File file) {
		try {
			String name = file.getName();
			int idx = name.indexOf("#");
			if (idx > 0) {
				base.setOrder(Integer.parseInt(name.substring(0, idx)));
				base.setName(name.substring(idx + 1));
			} else
				base.setName(name);
		} catch (Throwable t) {
			logger.error("Error extracting name and order from file name: "
					+ file.getAbsolutePath(), t);
		}
	}
}
