package com.aggrepoint.winlet.site.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.AccessRuleEngine;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Page extends Base {
	static final Log logger = LogFactory.getLog(Page.class);

	private Branch branch;
	private String path;
	private String template;
	private String link;
	private String title;
	private boolean skip;
	private boolean hide;
	private boolean expand;
	private boolean isStatic;
	private List<Area> areas = new ArrayList<Area>();
	private Hashtable<String, List<Area>> areasByName = new Hashtable<String, List<Area>>();
	private List<Page> pages = new ArrayList<Page>();
	private Page parent;
	private int level;
	private String fullPath;
	private String fullDir;

	public void init(Page p, List<Area> cascade, String tmpl) {
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
			pg.init(this, c2, template);
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

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
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

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		if (this.isStatic)
			expand = true;
	}

	public List<Area> getAreas(String name) {
		return areasByName.get(name);
	}

	public List<Area> getAreas() {
		return areas;
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
			boolean constainsNotSkip) {
		if (pages.size() == 0)
			return pages;

		List<Page> list = new ArrayList<Page>();
		for (Page p : pages) {
			if (!includeHide && p.isHide())
				continue;

			try {
				if (p.getRule() == null || re.eval(p.getRule()))
					if (!constainsNotSkip || p.containsNotSkip(re))
						list.add(p);
			} catch (Exception e) {
				logger.error("Error evaluating rule \"" + p.getRule()
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
		if (path.equals(fullPath))
			return this;

		if (!path.startsWith(fullPath))
			return null;

		List<Page> list = getPages(re, true, true);
		for (Page p : list) {
			Page f = p.findPage(path, re);
			if (f != null)
				return f;
		}

		if (isExpand())
			return this;

		return null;
	}

	public Page findNotSkip(AccessRuleEngine re) {
		if (!skip)
			return this;

		List<Page> list = getPages(re, true, true);
		if (list.size() == 0)
			return this;

		return list.get(0).findNotSkip(re);
	}
}
