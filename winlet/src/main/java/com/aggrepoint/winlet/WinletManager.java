package com.aggrepoint.winlet;

import java.util.Hashtable;

import org.springframework.context.ApplicationContext;

import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * 负责管理Winlet实例和WindowInstance实例
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletManager {
	/******************************************************************************
	 *
	 * Winlet实例
	 *
	 *****************************************************************************/
	static Hashtable<String, Object> WINLETS = new Hashtable<String, Object>();

	static long SEQ_START = 10000;
	static long seqId = SEQ_START;

	public static long getSeqId() {
		if (seqId == Long.MAX_VALUE)
			seqId = SEQ_START;
		else
			seqId++;
		return seqId;
	}

	/**
	 * 获取Winlet实例，如果不存在则创建
	 * 
	 * @param context
	 * @param req
	 * @param winletDef
	 * @return
	 * @throws Exception
	 */
	public static synchronized Object getWinlet(ApplicationContext context,
			WinletDef winletDef) throws Exception {
		Object winlet = WINLETS.get(winletDef.getName());
		if (winlet == null) {
			winlet = context.getBean(winletDef.getName());
			WINLETS.put(winletDef.getName(), winlet);
		}
		return winlet;
	}
}
