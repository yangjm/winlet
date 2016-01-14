package com.aggrepoint.winlet.site.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import com.aggrepoint.winlet.AccessRuleEngine;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Branch extends Base {
	private String path;
	private boolean isStatic;
	private String template;
	private Hashtable<String, String> templatePrefixes;
	/** 仅用于static branch，当用户给定的URL没有对应的文件存在则使用index对应的文件 */
	private String index;
	private List<Area> areas = new ArrayList<Area>();
	private Page rootPage;

	public Branch() {
		rootPage = new Page();
		rootPage.setName("");
		rootPage.setPath("/");
		rootPage.setDir("/");
		rootPage.setSkip(true);
		rootPage.setBranch(this);
	}

	public void init() {
		Collections.sort(areas);
		for (Area area : areas)
			area.setCascade(true);

		Collections.sort(rootPage.getPages());

		for (Page p : rootPage.getPages())
			p.init(rootPage, areas, template, templatePrefixes);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
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

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
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
		List<Page> pages = rootPage.getPages(re, true, true, false);

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
