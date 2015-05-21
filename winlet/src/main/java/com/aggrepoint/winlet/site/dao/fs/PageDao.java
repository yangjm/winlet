package com.aggrepoint.winlet.site.dao.fs;

import java.io.File;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.site.domain.Area;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PageDao {
	static final Log logger = LogFactory.getLog(PageDao.class);

	public static Page load(File dir) {
		if (!dir.isDirectory())
			return null;

		try {
			Page page = new Page();

			Utils.getNameAndOrder(page, dir);
			page.setPath(page.getName());
			Hashtable<String, String> cfgs = Utils.loadCfg(dir);
			page.setName(cfgs.get("name"));
			if (page.getName() == null)
				page.setName(page.getPath());
			page.setRule(cfgs.get("rule"));
			page.setTemplate(cfgs.get("template"));
			page.setLink(cfgs.get("link"));
			page.setSkip("".equals(cfgs.get("skip"))
					|| "true".equalsIgnoreCase(cfgs.get("skip")));
			page.setHide("".equals(cfgs.get("hide"))
					|| "true".equalsIgnoreCase(cfgs.get("hide")));
			page.setExpand("".equals(cfgs.get("expand"))
					|| "true".equalsIgnoreCase(cfgs.get("expand")));
			page.setStatic("".equals(cfgs.get("static"))
					|| "true".equalsIgnoreCase(cfgs.get("static")));

			if (!page.isStatic()) // 静态目录里面所有内容都视为静态内容，不再作为子目录处理
				for (File f : dir.listFiles()) {
					if (f.isDirectory()) { // 页面
						Page p = load(f);
						if (p != null)
							page.addPage(p);
					} else if (f.getName().endsWith(".html")) { // area内容
						Area a = AreaDao.load(f);
						if (a != null)
							page.addArea(a);
					}
				}

			return page;
		} catch (Exception e) {
			logger.error("Error load area: " + dir.getAbsolutePath(), e);
			return null;
		}
	}
}
