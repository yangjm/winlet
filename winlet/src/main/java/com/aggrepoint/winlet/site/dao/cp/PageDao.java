package com.aggrepoint.winlet.site.dao.cp;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.site.domain.Area;
import com.aggrepoint.winlet.site.domain.Page;

public class PageDao {
	static final Log logger = LogFactory.getLog(PageDao.class);

	public static Page load(ResourceNode rn, String contextRoot) {
		if (!rn.isDir())
			return null;

		try {
			Page page = new Page();

			page.setName(rn.getName());
			page.setOrder(rn.getOrder());

			page.setPath(page.getName());
			Hashtable<String, String> cfgs = Utils.loadCfg(rn);
			com.aggrepoint.winlet.site.dao.fs.PageDao.init(page, cfgs);

			if (!page.isStatic()) { // 静态目录里面所有内容都视为静态内容，不再作为子目录处理
				for (ResourceNode r : rn.getChilds().values()) { // 页面
					Page p = load(r, contextRoot);
					if (p != null)
						page.addPage(p);
				}
				for (ResourceNode r : rn.getFiles().values()) {
					if (r.getName().endsWith(".html")) { // area内容
						Area a = AreaDao.load(r, contextRoot);
						if (a != null)
							page.addArea(a);
					}
				}
			}

			return page;
		} catch (Exception e) {
			logger.error("Error load area: " + rn.getPath(), e);
			return null;
		}
	}
}
