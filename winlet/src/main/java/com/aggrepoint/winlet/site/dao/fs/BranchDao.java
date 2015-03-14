package com.aggrepoint.winlet.site.dao.fs;

import java.io.File;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.site.domain.Area;
import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class BranchDao {
	static final Log logger = LogFactory.getLog(BranchDao.class);

	public static Branch load(File dir) {
		if (!dir.isDirectory())
			return null;

		try {
			Branch branch = new Branch();

			branch.setPath(dir.getCanonicalPath());

			Utils.getNameAndOrder(branch, dir);
			Hashtable<String, String> cfgs = Utils.loadCfg(dir);
			branch.setStatic("true".equalsIgnoreCase(cfgs.get("static")));
			branch.setRule(cfgs.get("rule"));

			if (!branch.isStatic()) {
				branch.setTemplate(cfgs.get("template"));

				if (branch.getTemplate() == null)
					throw new Exception("Template not specified for branch.");

				for (File f : dir.listFiles()) {
					if (f.isDirectory()) { // 页面
						Page p = PageDao.load(f);
						if (p != null)
							branch.getRootPage().addPage(p);
					} else if (f.getName().endsWith(".html")) { // area内容
						Area a = AreaDao.load(f);
						if (a != null)
							branch.addArea(a);
					}
				}
			} else {
				branch.setIndex(cfgs.get("index"));
				if (branch.getIndex() != null
						&& !branch.getIndex().startsWith("/"))
					branch.setIndex("/" + branch.getIndex());
			}

			return branch;
		} catch (Throwable t) {
			logger.error("Error loading branch " + dir.getAbsolutePath(), t);
			return null;
		}
	}
}
