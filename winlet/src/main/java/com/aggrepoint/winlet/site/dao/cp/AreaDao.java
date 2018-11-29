package com.aggrepoint.winlet.site.dao.cp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.site.domain.Area;

public class AreaDao {
	static final Log logger = LogFactory.getLog(AreaDao.class);

	public static Area load(ResourceNode rn, String contextRoot) {
		if (rn.isDir())
			return null;

		try {
			Area area = new Area();
			area.setName(rn.getName());
			return com.aggrepoint.winlet.site.dao.fs.AreaDao.init(area, rn.getResource().getInputStream(), contextRoot);
		} catch (Exception e) {
			logger.error("Error load area: " + rn.getPath(), e);
			return null;
		}
	}
}
