package com.aggrepoint.winlet.site.dao.cp;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Utils {
	static final Log logger = LogFactory.getLog(Utils.class);

	/**
	 * 负责读取cfg.cfg中的配置信息
	 * 
	 * @param dir
	 * @return
	 */
	public static Hashtable<String, String> loadCfg(ResourceNode rn) {
		Hashtable<String, String> cfgs = new Hashtable<String, String>();

		if (!rn.isDir())
			return cfgs;

		ResourceNode cfg = rn.getFiles().get("cfg.cfg");
		if (cfg == null)
			return cfgs;

		try {
			LineNumberReader lnr = new LineNumberReader(
					new InputStreamReader(cfg.getResource().getInputStream(), "UTF-8"));
			String line = lnr.readLine();

			while (line != null) {
				line = line.trim();

				if (!line.equals("") && !line.startsWith("#")) {
					int idx = line.indexOf(":");
					if (idx > 0)
						cfgs.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
					else
						cfgs.put(line, "");
				}
				line = lnr.readLine();
			}
			lnr.close();
		} catch (Exception e) {
			logger.error("Error reading site configuration file: " + cfg.getPath(), e);
		}

		return cfgs;
	}

}
