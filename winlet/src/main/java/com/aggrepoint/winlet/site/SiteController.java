package com.aggrepoint.winlet.site;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.Context;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
@Controller
public class SiteController {
	static final Log logger = LogFactory.getLog(SiteController.class);

	static final int CHECK_UPDATE_INTERVAL = 2000;

	static ServletContext context;

	static FileSystemCfgLoader loader;
	/** 分支配置 */
	static ArrayList<Branch> branches;

	public static final String PAGE_PATH = "PAGE_PATH";
	public static final String PAGE_DATA = "PAGE_DATA";

	private static void updateBranches() {
		if (context == null)
			context = Context.get().getBean(ServletContext.class);

		if (loader == null)
			loader = new FileSystemCfgLoader(
					context.getRealPath("/WEB-INF/site/branch"),
					CHECK_UPDATE_INTERVAL);

		branches = loader.load(branches);
	}

	public static Branch getBranch(AccessRuleEngine engine) {
		updateBranches();
		Branch branch = null;
		for (Branch b : branches) {
			try {
				if (b.getRule() == null || engine.eval(b.getRule())) {
					branch = b;
					break;
				}
			} catch (Exception e) {
				logger.error(
						"Error evaluating branch access rule \"" + b.getRule()
								+ "\".", e);
			}
		}

		return branch;
	}

	public static Page getPage(AccessRuleEngine engine, String path) {
		updateBranches();
		Branch branch = null;
		for (Branch b : branches) {
			try {
				if (b.getRule() == null || engine.eval(b.getRule())) {
					branch = b;
					break;
				}
			} catch (Exception e) {
				logger.error(
						"Error evaluating branch access rule \"" + b.getRule()
								+ "\".", e);
			}
		}

		if (branch == null)
			return null;
		return branch.findPage(path, engine);
	}

	static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024 * 100];
		int n = 0;
		while (-1 != (n = input.read(buffer)))
			baos.write(buffer, 0, n);

		return baos.toByteArray();
	}

	static ConfigurableMimeFileTypeMap FILE_TYPE_MAP = new ConfigurableMimeFileTypeMap();

	static Object returnFile(Branch branch, String path) throws IOException {
		File file = new File(branch.getPath() + path);
		if ((!file.exists() || file.isDirectory()) && branch.getIndex() != null)
			file = new File(branch.getPath() + branch.getIndex());

		if (!file.exists() || file.isDirectory())
			return "/WEB-INF/site/error/pagenotfound.jsp";

		InputStream in = new FileInputStream(file);
		final HttpHeaders headers = new HttpHeaders();
		try {
			headers.setContentType(MediaType.valueOf(FILE_TYPE_MAP
					.getContentType(file)));
		} catch (Exception e) {
			headers.setContentType(MediaType
					.valueOf("application/octet-stream"));
		}
		byte[] bytes = toByteArray(in);
		in.close();
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
	}

	/**
	 * urlPrefix - 前段HTTP服务器或者反向代理添加的URL前段，例如/portal/site/home
	 */
	@RequestMapping(value = "/site/**")
	public Object site(
			HttpServletRequest req,
			HttpServletResponse resp,
			AccessRuleEngine engine,
			PsnRuleEngine psnEngine,
			@RequestHeader(value = "X-Url-Prefix", required = false) String urlPrefix) {
		String path = req.getServletPath().substring(5);

		try {
			Branch branch = getBranch(engine);
			if (branch == null) {
				return "/WEB-INF/site/error/pagenotfound.jsp";
			}

			if (branch.isStatic()) { // 静态分支，直接返回要访问的资源
				if (path.equals("/cfg.cfg")) // 不允许访问cfg.cfg
					path = "";

				return returnFile(branch, path);
			} else {
				Page page = branch.findPage(path, engine);
				if (page == null)
					return "/WEB-INF/site/error/pagenotfound.jsp";

				if (page.getLink() != null)
					return "redirect:" + page.getLink();

				if (page.isStatic()) {
					if (path.equals(page.getFullPath() + "cfg.cfg")) // 不允许访问cfg.cfg
						return "/WEB-INF/site/error/pagenotfound.jsp";

					return returnFile(branch,
							path.replace(page.getFullPath(), page.getFullDir()));
				} else {
					SiteContext sc = new SiteContext(req, page, urlPrefix);
					req.setAttribute(SiteContext.SITE_CONTEXT_KEY, sc);
					req.setAttribute(PAGE_PATH,
							sc.getPageUrl(page.getFullPath()));
					req.setAttribute(PAGE_DATA, page.getDataMap());

					return "/WEB-INF/site/template/"
							+ page.getPsnTemplate(psnEngine) + ".jsp";
				}
			}
		} catch (Exception e) {
			return "/WEB-INF/site/error/pagenotfound.jsp";
		}
	}
}
