package com.aggrepoint.winlet.site.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aggrepoint.winlet.AccessRuleEngine;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Branch extends Base {
	private String template;
	private List<Area> areas = new ArrayList<Area>();
	private Page rootPage;

	public Branch() {
		rootPage = new Page();
		rootPage.setName("");
		rootPage.setPath("/");
		rootPage.setSkip(true);
		rootPage.setBranch(this);
	}

	public void init() {
		Collections.sort(areas);
		for (Area area : areas)
			area.setCascade(true);

		Collections.sort(rootPage.getPages());

		for (Page p : rootPage.getPages())
			p.init(rootPage, areas, template);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public List<Area> getAreas() {
		return areas;
	}

	public void addArea(Area area) {
		areas.add(area);
	}

	public Page getRootPage() {
		return rootPage;
	}

	public Page getHome(AccessRuleEngine re) {
		List<Page> pages = rootPage.getPages(re, true, true);

		if (pages.size() == 0)
			return null;

		return pages.get(0).findNotSkip(re);
	}

	public Page findPage(String path, AccessRuleEngine re) {
		if (!path.endsWith("/"))
			path = path + "/";

		Page f = rootPage.findPage(path, re);

		return f == null || f == rootPage ? getHome(re) : f.findNotSkip(re);
	}
}
