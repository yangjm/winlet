package com.aggrepoint.winlet.site.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.utils.TwoValues;
import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.PsnRuleEngine;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Page extends Base {
	static final Log logger = LogFactory.getLog(Page.class);

	private Branch branch;
	private String path;
	private String template;
	private Hashtable<String, String> templatePrefixes;
	private String link;
	private String title;
	private boolean skip;
	private boolean hide;
	private boolean expand;
	/** 扩展访问规则 */
	private String expandRule;
	private boolean isStatic;
	private List<Area> areas = new ArrayList<Area>();
	private Hashtable<String, List<Area>> areasByName = new Hashtable<String, List<Area>>();
	private List<Page> pages = new ArrayList<Page>();
	private Page parent;
	private int level;
	private String fullPath;
	private String fullDir;
	/** key为area name，twovalues中第一个值为规则，第二个值为如果符合规则要映射到的area name */
	private Map<String, List<TwoValues<String, String>>> areaMap = new HashMap<String, List<TwoValues<String, String>>>();

	protected HashMap<String, String> data;

	public void init(Page p, List<Area> cascade, String tmpl,
			Hashtable<String, String> prefix) {
		Collections.sort(pages);

		branch = p.branch;

		parent = p;
		level = p.getLevel() + 1;
		fullPath = p.fullPath + path + "/";
		fullDir = p.fullDir + dir + "/";

		if (cascade != null)
			areas.addAll(cascade);
		Collections.sort(areas);

		ArrayList<Area> c2 = new ArrayList<Area>();
		for (Area area : areas)
			if (area.isCascade())
				c2.add(area);

		if (template == null)
			template = tmpl;
		if (templatePrefixes == null)
			templatePrefixes = prefix;

		areasByName.clear();
		for (Area area : areas) {
			List<Area> list = areasByName.get(area.getName());
			if (list == null) {
				list = new ArrayList<Area>();
				areasByName.put(area.getName(), list);
			}
			list.add(area);
		}

		for (Page pg : pages)
			pg.init(this, c2, template, templatePrefixes);
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		this.fullPath = path;
	}

	@Override
	public void setDir(String dir) {
		this.dir = dir;
		this.fullDir = dir;
	}

	public String getPsnTemplate(PsnRuleEngine psnEngine) throws Exception {
		if (templatePrefixes == null)
			return template;

		for (String rule : templatePrefixes.keySet())
			if (psnEngine.eval(rule))
				return templatePrefixes.get(rule) + template;

		logger.warn("No matching tempalte prefix found for page" + fullPath
				+ ", use default template.");
		return template;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Hashtable<String, String> getTemplatePrefixes() {
		return templatePrefixes;
	}

	public void setTemplatePrefixes(Hashtable<String, String> templatePrefixes) {
		this.templatePrefixes = templatePrefixes;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public String getExpandRule() {
		return expandRule;
	}

	public void setExpandRule(String expandRule) {
		this.expandRule = expandRule;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		if (this.isStatic)
			expand = true;
	}

	public List<Area> getAreas(String name) {
		List<TwoValues<String, String>> list = areaMap.get(name);
		if (list != null) {
			PsnRuleEngine engine = ContextUtils.getPsnRuleEngine(ContextUtils
					.getRequest());
			for (TwoValues<String, String> map : list)
				try {
					if (engine.eval(map.getOne())) {
						name = map.getTwo();
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		return areasByName.get(name);
	}

	public List<Area> getAreas() {
		return areas;
	}

	public void addAreaMap(String from, String to, String rule) {
		List<TwoValues<String, String>> list = areaMap.get(from);
		if (list == null) {
			list = new ArrayList<TwoValues<String, String>>();
			areaMap.put(from, list);
		}
		list.add(new TwoValues<String, String>(rule, to));
	}

	public void addArea(Area area) {
		areas.add(area);
	}

	protected List<Page> getPages() {
		return pages;
	}

	protected boolean containsNotSkip(AccessRuleEngine re) {
		if (!skip)
			return true;

		for (Page page : pages)
			try {
				if (page.getRule() == null || re.eval(page.getRule()))
					if (page.containsNotSkip(re))
						return true;
			} catch (Exception e) {
				logger.error("Error evaluating rule \"" + page.getRule()
						+ "\" defined on page \"" + page.getFullPath() + "\".",
						e);
			}

		return false;
	}

	public List<Page> getPages(AccessRuleEngine re, boolean includeHide,
			boolean constainsNotSkip, boolean includeExpand) {
		if (pages.size() == 0)
			return pages;

		List<Page> list = new ArrayList<Page>();
		for (Page p : pages) {
			if (!includeHide && p.isHide())
				continue;

			try {
				if (p.getRule() == null || re.eval(p.getRule())
						|| includeExpand && p.getExpandRule() != null
						&& re.eval(p.getExpandRule()))
					if (!constainsNotSkip || p.containsNotSkip(re))
						list.add(p);
			} catch (Exception e) {
				logger.error("Error evaluating rule \"" + p.getRule()
						+ "\" or \"" + p.getExpandRule()
						+ "\" defined on page \"" + p.getFullPath() + "\".", e);
			}
		}

		return list;
	}

	public void addPage(Page page) {
		pages.add(page);
	}

	public Page getParent() {
		return parent;
	}

	public int getLevel() {
		return level;
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getFullDir() {
		return fullDir;
	}

	public Page findPage(String path, AccessRuleEngine re) {
		if (path.equals(fullPath)) {
			// 非扩展匹配，确认符合非扩展匹配规则
			// getPage()返回的页面有可能是符合扩展匹配但不符合非扩展匹配，所以需要检查
			try {
				if (rule == null || re.eval(rule))
					return this;
			} catch (Exception e) {
				logger.error("Error evaluating rule \"" + rule
						+ "\" defined on page \"" + getFullPath() + "\".", e);
			}

			return null;
		}

		if (!path.startsWith(fullPath))
			return null;

		List<Page> list = getPages(re, true, true, true);
		for (Page p : list) {
			Page f = p.findPage(path, re);
			if (f != null)
				return f;
		}

		if (isExpand())
			try {
				if (expandRule == null || re.eval(expandRule)) {
					// 扩展匹配，确认符合扩展匹配规则
					// getPage()返回的页面有可能是符合非扩展匹配但不符合扩展匹配，所以需要检查
					return this;
				}
			} catch (Exception e) {
				logger.error("Error evaluating rule \"" + expandRule
						+ "\" defined on page \"" + getFullPath() + "\".", e);
			}

		return null;
	}

	public Page findNotSkip(AccessRuleEngine re) {
		if (!skip)
			return this;

		List<Page> list = getPages(re, true, true, false);
		if (list.size() == 0)
			return this;

		return list.get(0).findNotSkip(re);
	}

	public HashMap<String, String> getDataMap() {
		if (data == null)
			data = new HashMap<String, String>();
		return data;
	}

	public void addData(String name, String value) {
		getDataMap().put(name, value);
	}

	public String getData(String name) {
		return getDataMap().get(name);
	}
}
