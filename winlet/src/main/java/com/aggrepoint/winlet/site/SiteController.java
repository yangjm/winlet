package com.aggrepoint.winlet.site;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aggrepoint.winlet.AuthorizationEngine;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;
import com.aggrepoint.winlet.spring.config.WinletAutoConfiguration;

/**
 * <pre>
 * SpringBoot与非SpringBoot的文件布局区别
 * 
 * 				非SpringBoot												SpringBoot							
 * Branch		/src/main/webapp/WEB-INF/site/branch					/src/main/resources/branches
 * 模版			/src/main/webapp/WEB-INF/site/template					/src/main/webapp/WEB-INF/templates
 * 页面找不到		/src/main/webapp/WEB-INF/site/error/pagenotfound.jsp	/src/main/webapp/WEB-INF/pagenotfound.jsp
 * </pre>
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
@Controller
public class SiteController implements ApplicationContextAware {
	static final Log logger = LogFactory.getLog(SiteController.class);

	static final int CHECK_UPDATE_INTERVAL = 2000;

	static ServletContext context;

	static CfgLoader loader;
	/** 分支配置 */
	static ArrayList<Branch> branches;

	public static final String PAGE_PATH = "PAGE_PATH";
	public static final String PATH_EXPAND = "PATH_EXPAND";
	public static final String PAGE_DATA = "PAGE_DATA";

	private ApplicationContext applicationContext;
	private boolean isSpringBoot;
	private String pageNotFound;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		context = applicationContext.getBean(ServletContext.class);

		try {
			isSpringBoot = applicationContext.getBean(WinletAutoConfiguration.class) != null;
		} catch (Exception e) {
		}
		pageNotFound = isSpringBoot ? "/WEB-INF/pagenotfound.jsp" : "/WEB-INF/site/error/pagenotfound.jsp";
	}

	private void updateBranches(String contextRoot) {
		if (loader == null)
			if (!isSpringBoot) {
				// 非SpringBoot，Branches放在"/WEB-INF/site/branch"中
				loader = new FileSystemCfgLoader(context.getRealPath("/WEB-INF/site/branch"), CHECK_UPDATE_INTERVAL);
			} else {
				try {
					// SpringBoot，Branches放在BRANCH_DIR中
					Resource rootDir = applicationContext
							.getResource("classpath:/" + ClassPathCfgLoader.BRANCH_DIR + "/");
					String protocol = rootDir == null ? null : rootDir.getURL().getProtocol();

					if ("file".equalsIgnoreCase(protocol)) { // load site directory from file system
						loader = new FileSystemCfgLoader(rootDir.getURI().getPath(), CHECK_UPDATE_INTERVAL);
					} else if ("jar".equalsIgnoreCase(protocol)) { // load site directory from classpath
						PathMatchingResourcePatternResolver pm = new PathMatchingResourcePatternResolver(
								applicationContext.getClassLoader());
						loader = new ClassPathCfgLoader(pm.getResources("/" + ClassPathCfgLoader.BRANCH_DIR + "/**/*"));
					} else {
						logger.error("Unable to locate /branch/ directory: " + rootDir);
					}
				} catch (Exception e) {
					logger.error("Error", e);
				}
			}

		branches = loader.load(branches, contextRoot);
	}

	private String contextRoot = null;

	private String getContextRoot(HttpServletRequest req) {
		if (contextRoot == null)
			contextRoot = req.getServletContext().getContextPath();
		return contextRoot;
	}

	public HashSet<String> getBranchFirstLevelDirs(HttpServletRequest req) {
		HashSet<String> list = new HashSet<>();
		updateBranches(getContextRoot(req));
		for (Branch b : branches) {
			for (Page page : b.getRootPage().getPages()) {
				list.add(page.getFullPath());
			}
		}
		return list;
	}

	public Branch getBranch(AuthorizationEngine ap, String contextRoot) {
		updateBranches(contextRoot);
		Branch branch = null;
		for (Branch b : branches) {
			try {
				if (ap.check(b) == null) {
					branch = b;
					break;
				}
			} catch (Exception e) {
				logger.error("Error evaluating branch access rule \"" + b.getRule() + "\".", e);
			}
		}

		return branch;
	}

	public Page getPage(AuthorizationEngine ap, String path, String contextRoot) {
		updateBranches(contextRoot);
		Branch branch = null;
		for (Branch b : branches) {
			try {
				if (ap.check(b) == null) {
					branch = b;
					break;
				}
			} catch (Exception e) {
				logger.error("Error evaluating branch access rule \"" + b.getRule() + "\".", e);
			}
		}

		if (branch == null)
			return null;
		return branch.findPage(path, ap);
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

	Object returnFile(Branch branch, String path) throws IOException {
		File file = new File(branch.getPath() + path);
		if ((!file.exists() || file.isDirectory()) && branch.getIndex() != null)
			file = new File(branch.getPath() + branch.getIndex());

		if (!file.exists() || file.isDirectory())
			return pageNotFound;

		InputStream in = new FileInputStream(file);
		final HttpHeaders headers = new HttpHeaders();
		try {
			headers.setContentType(MediaType.valueOf(FILE_TYPE_MAP.getContentType(file)));
		} catch (Exception e) {
			headers.setContentType(MediaType.valueOf("application/octet-stream"));
		}
		byte[] bytes = toByteArray(in);
		in.close();
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
	}

	/**
	 * urlPrefix - 前段HTTP服务器或者反向代理添加的URL前段，例如/portal/site/home
	 */
	@RequestMapping(value = "/site/**")
	public Object site(HttpServletRequest req, HttpServletResponse resp, AuthorizationEngine ap,
			PsnRuleEngine psnEngine, @RequestHeader(value = "X-Url-Prefix", required = false) String urlPrefix) {
		String path = req.getServletPath().substring(5);

		try {
			Branch branch = getBranch(ap, getContextRoot(req));
			if (branch == null) {
				return pageNotFound;
			}

			if (branch.isStatic()) { // 静态分支，直接返回要访问的资源
				if (path.equals("/cfg.cfg")) // 不允许访问cfg.cfg
					path = "";

				return returnFile(branch, path);
			} else {
				Page page = branch.findPage(path, ap);
				if (page == null)
					return pageNotFound;

				if (page.getLink() != null)
					return "redirect:" + page.getLink();

				if (page.isStatic()) {
					if (path.equals(page.getFullPath() + "cfg.cfg")) // 不允许访问cfg.cfg
						return pageNotFound;

					return returnFile(branch, path.replace(page.getFullPath(), page.getFullDir()));
				} else {
					SiteContext sc = new SiteContext(req, page, urlPrefix);
					req.setAttribute(SiteContext.SITE_CONTEXT_KEY, sc);
					req.setAttribute(PAGE_PATH, sc.getPageUrl(page.getFullPath()));
					req.setAttribute(PAGE_DATA, page.getDataMap());
					if (page.isExpand() && !page.getFullPath().equals(path) && path.startsWith(page.getFullPath()))
						req.setAttribute(PATH_EXPAND, path.substring(page.getFullPath().length()));

					return isSpringBoot ? "/WEB-INF/templates/" + page.getPsnTemplate(psnEngine) + ".jsp"
							: "/WEB-INF/site/template/" + page.getPsnTemplate(psnEngine) + ".jsp";
				}
			}
		} catch (Exception e) {
			return pageNotFound;
		}
	}
}
