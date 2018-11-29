package com.aggrepoint.winlet.site.dao.fs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.site.domain.Area;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class AreaDao {
	static final Log logger = LogFactory.getLog(AreaDao.class);

	public static Area load(File file, String contextRoot) {
		if (file.isDirectory())
			return null;

		try {
			Area area = new Area();
			Utils.getNameAndOrder(area, file);
			return init(area, new FileInputStream(file), contextRoot);
		} catch (Exception e) {
			logger.error("Error load area: " + file.getAbsolutePath(), e);
			return null;
		}
	}

	public static Area init(Area area, InputStream is, String contextRoot) throws Exception {
		area.setName(area.getName().substring(0, area.getName().length() - 5));
		if (area.getName().startsWith("!")) {
			area.setCascade(true);
			area.setName(area.getName().substring(1));
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[10240];
		int len = is.read(buffer, 0, buffer.length);
		while (len > 0) {
			baos.write(buffer, 0, len);
			len = is.read(buffer, 0, buffer.length);
		}
		is.close();

		area.setContent(baos.toString("UTF-8"), contextRoot);

		return area;
	}
}
