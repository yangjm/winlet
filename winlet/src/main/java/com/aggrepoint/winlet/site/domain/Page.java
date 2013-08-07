package com.aggrepoint.winlet.site.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.AccessRuleEngine;

public class Page extends Base {
	static final Log logger = LogFactory.getLog(Page.class);

	private String path;
	private String template;
	private String link;
	private boolean skip;
	private List<Area> areas = new ArrayList<Area>();
	private Hashtable<String, List<Area>> areasByName = new Hashtable<String, List<Area>>();
	private List<Page> pages = new ArrayList<Page>();
	private Page parent;
	private int level;
	private String fullPath;

	public void init(Page p, List<Area> cascade, String tmpl) {
		Collections.sort(pages);

		parent = p;
		level = p.getLevel() + 1;
		fullPath = p.fullPath + path + "/";

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		this.fullPath = path;
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

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
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

	public List<Page> getPages(AccessRuleEngine re) {
		if (pages.size() == 0)
			return pages;

		List<Page> list = new ArrayList<Page>();
		for (Page p : pages) {
			try {
				if (p.getRule() == null || re.eval(p.getRule()))
					list.add(p);
			} catch (Exception e) {
				logger.error("Error evaluating rule \"" + rule
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

	public Page findPage(String path, AccessRuleEngine re) {
		if (path.equals(fullPath))
			return this;

		if (!path.startsWith(path))
			return null;

		List<Page> list = getPages(re);
		for (Page p : list) {
			Page f = p.findPage(path, re);
			if (f != null)
				return f;
		}

		return null;
	}

	public Page findNotSkip(AccessRuleEngine re) {
		if (!skip)
			return this;

		List<Page> list = getPages(re);
		if (list.size() == 0)
			return this;

		return list.get(0).findNotSkip(re);
	}
}
