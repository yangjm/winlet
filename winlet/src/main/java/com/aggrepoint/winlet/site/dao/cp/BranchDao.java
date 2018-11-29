package com.aggrepoint.winlet.site.dao.cp;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.site.domain.Area;
import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;

public class BranchDao {
	static final Log logger = LogFactory.getLog(BranchDao.class);

	public static Branch load(ResourceNode rn, String contextRoot) {
		if (!rn.isDir())
			return null;

		try {
			Branch branch = new Branch();

			// branch.setPath(dir.getCanonicalPath());

			branch.setOrder(rn.getOrder());
			branch.setName(rn.getName());

			Hashtable<String, String> cfgs = Utils.loadCfg(rn);
			branch.setStatic("true".equalsIgnoreCase(cfgs.get("static")));
			branch.setRule(cfgs.get("rule"));

			if (!branch.isStatic()) {
				branch.setTemplate(cfgs.get("template"));

				if (branch.getTemplate() == null)
					throw new Exception("Template not specified for branch.");

				branch.setTemplatePrefixes(
						com.aggrepoint.winlet.site.dao.fs.BranchDao.parseTemplatePrefix(cfgs.get("template-prefix")));

				for (ResourceNode r : rn.getChilds().values()) { // 页面
					Page p = PageDao.load(r, contextRoot);
					if (p != null)
						branch.getRootPage().addPage(p);
				}
				for (ResourceNode r : rn.getFiles().values()) {
					if (r.getName().endsWith(".html")) { // area内容
						Area a = AreaDao.load(r, contextRoot);
						if (a != null)
							branch.addArea(a);
					}
				}

			} else {
				branch.setIndex(cfgs.get("index"));
				if (branch.getIndex() != null && !branch.getIndex().startsWith("/"))
					branch.setIndex("/" + branch.getIndex());
			}

			return branch;
		} catch (Throwable t) {
			logger.error("Error loading branch " + rn.getPath(), t);
			return null;
		}
	}
}
