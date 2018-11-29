package com.aggrepoint.winlet.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import com.aggrepoint.winlet.site.dao.cp.BranchDao;
import com.aggrepoint.winlet.site.dao.cp.ResourceNode;
import com.aggrepoint.winlet.site.domain.Branch;

public class ClassPathCfgLoader implements CfgLoader {
	static final Log logger = LogFactory.getLog(ClassPathCfgLoader.class);

	public static final String BRANCH_DIR = "branches";

	List<ResourceNode> resourceNodes = new ArrayList<>();

	public ClassPathCfgLoader(Resource[] resources) {
		for (Resource res : resources) {
			ResourceNode rn = new ResourceNode(res);
			if (rn.getDir() == null)
				continue;
			if ("/".equals(rn.getDir()))
				resourceNodes.add(rn);
			else
				for (ResourceNode r : resourceNodes)
					if (!r.add(rn)) {
						logger.error("Unable to add resource " + res);
					}
		}

		for (ResourceNode rn : resourceNodes)
			System.out.println(rn.toString());
	}

	@Override
	public ArrayList<Branch> load(ArrayList<Branch> branches, String contextRoot) {
		if (branches != null)
			return branches;

		ArrayList<Branch> bs = new ArrayList<Branch>();

		for (ResourceNode rn : resourceNodes) {
			Branch b = BranchDao.load(rn, contextRoot);
			if (b != null) {
				b.init();
				bs.add(b);
			}
		}

		Collections.sort(bs);

		return bs;
	}
}
