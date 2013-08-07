package com.aggrepoint.winlet.site;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;

@Controller
public class SiteController {
	static final Log logger = LogFactory.getLog(SiteController.class);

	static final int CHECK_UPDATE_INTERVAL = 2000;

	@Autowired
	ServletContext context;

	FileSystemCfgLoader loader;
	/** 分支配置 */
	ArrayList<Branch> branches;

	private void updateBranches() {
		if (loader == null)
			loader = new FileSystemCfgLoader(
					context.getRealPath("/WEB-INF/site/branch"),
					CHECK_UPDATE_INTERVAL);

		branches = loader.load(branches);
	}

	@RequestMapping("/site/**")
	public String site(HttpServletRequest req, AccessRuleEngine engine) {
		try {
			String pagePath = req.getRequestURI().toString();
			int idx = pagePath.indexOf(req.getServletPath());
			pagePath = pagePath.substring(0, idx + 5);

			updateBranches();

			String path = req.getServletPath().substring(5);

			Branch branch = null;
			for (Branch b : branches) {
				if (b.getRule() == null || engine.eval(b.getRule())) {
					branch = b;
					break;
				}
			}

			if (branch == null)
				return "/WEB-INF/site/error/branchnotfound.jsp";

			Page page = branch.findPage(path, engine);
			if (page == null)
				return "/WEB-INF/site/error/pagenotfound.jsp";

			SiteContext sc = new SiteContext(pagePath, req.getContextPath(),
					branch, page);
			req.setAttribute(SiteContext.SITE_CONTEXT_KEY, sc);

			return "/WEB-INF/site/template/" + page.getTemplate() + ".jsp";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
