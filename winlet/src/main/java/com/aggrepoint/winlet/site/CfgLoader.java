package com.aggrepoint.winlet.site;

import java.util.ArrayList;

import com.aggrepoint.winlet.site.domain.Branch;

public interface CfgLoader {
	ArrayList<Branch> load(ArrayList<Branch> branches, String contextRoot);
}
