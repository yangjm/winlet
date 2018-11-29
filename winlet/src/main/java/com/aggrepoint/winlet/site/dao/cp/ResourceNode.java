package com.aggrepoint.winlet.site.dao.cp;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import com.aggrepoint.utils.TwoValues;
import com.aggrepoint.winlet.site.ClassPathCfgLoader;
import com.aggrepoint.winlet.site.Utils;

public class ResourceNode {
	static final Log logger = LogFactory.getLog(ResourceNode.class);

	private boolean isDir;
	/** full path including directory and name。null for not initialized */
	private String path;
	/** directory part of full path ends with / */
	private String dir;
	/** */
	private String name;
	private int order;
	private Resource resource;
	private HashMap<String, ResourceNode> files = new HashMap<>();
	private HashMap<String, ResourceNode> childs = new HashMap<>();

	static String PATTERN_SEPERATOR = File.separator.equals("\\") ? "\\\\" : File.separator;
	static Pattern P_PATH = Pattern
			.compile("(.*" + PATTERN_SEPERATOR + ")([^" + PATTERN_SEPERATOR + "]+)" + PATTERN_SEPERATOR + "?$");

	public ResourceNode(Resource res) {
		try {
			String url = java.net.URLDecoder.decode(res.getURL().getPath(), "UTF-8");
			int idx = url.indexOf("!/" + ClassPathCfgLoader.BRANCH_DIR + "/");
			if (idx < 0) {
				logger.error("Resource " + res + " isn't in /" + ClassPathCfgLoader.BRANCH_DIR + "/");
				return;
			}
			path = url.substring(idx + ClassPathCfgLoader.BRANCH_DIR.length() + 2);

			isDir = path.endsWith(File.separator);
			Matcher m = P_PATH.matcher(path);
			if (m.find()) {
				dir = m.group(1);
				name = m.group(2);
				TwoValues<String, Integer> tv = Utils.getNameAndOrder(name);
				name = tv.getOne();
				if (tv.getTwo() != null)
					order = tv.getTwo();
			}
			resource = res;
		} catch (Exception e) {
			logger.error("Error resource " + res, e);
			return;
		}
	}

	public String toString(String tab) {
		StringBuffer sb = new StringBuffer();
		sb.append(tab + path + " ! " + dir + " ! " + name + " ! " + order + " ! " + isDir + "\r\n");
		tab = tab + "\t";
		for (String name : files.keySet())
			sb.append(name + ": " + files.get(name).toString(tab));
		for (String name : childs.keySet())
			sb.append(name + ": " + childs.get(name).toString(tab));
		return sb.toString();
	}

	@Override
	public String toString() {
		return toString("");
	}

	public boolean add(ResourceNode res) {
		if (isDir == false || res.path == null)
			return false;
		if (res.dir.equals(path)) {
			if (res.isDir)
				childs.put(res.name, res);
			else
				files.put(res.name, res);
			return true;
		} else if (res.dir.startsWith(path))
			for (ResourceNode r : childs.values())
				if (r.add(res))
					return true;
		return false;
	}

	public boolean isDir() {
		return isDir;
	}

	public String getPath() {
		return path;
	}

	public String getDir() {
		return dir;
	}

	public String getName() {
		return name;
	}

	public int getOrder() {
		return order;
	}

	public Resource getResource() {
		return resource;
	}

	public HashMap<String, ResourceNode> getFiles() {
		return files;
	}

	public HashMap<String, ResourceNode> getChilds() {
		return childs;
	}
}
