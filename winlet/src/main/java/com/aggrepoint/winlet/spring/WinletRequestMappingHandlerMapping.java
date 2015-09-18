package com.aggrepoint.winlet.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.aggrepoint.winlet.Context;
import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.ReqInfoImpl;
import com.aggrepoint.winlet.WinletManager;
import com.aggrepoint.winlet.spring.annotation.Window;
import com.aggrepoint.winlet.spring.annotation.Winlet;
import com.aggrepoint.winlet.spring.def.ActionDef;
import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * 根据Winlet的要求处理Spring按缺省实现找到的HandlerMethod，返回要需要框架执行的HandlerMethod
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletRequestMappingHandlerMapping extends
		RequestMappingHandlerMapping {
	Map<HandlerMethod, Class<?>> classMap = Collections
			.synchronizedMap(new HashMap<HandlerMethod, Class<?>>());

	public WinletRequestMappingHandlerMapping() {
		setOrder(-1);
		this.setInterceptors(new Object[] { new WinletHandlerInterceptor() });
	}

	@Override
	protected HandlerMethod lookupHandlerMethod(String lookupPath,
			HttpServletRequest request) throws Exception {
		ReqInfoImpl req = new ReqInfoImpl(request, lookupPath);
		LogInfoImpl.getLogInfo(request, null).setReqInfo(req);

		HandlerMethod hm = super.lookupHandlerMethod(lookupPath, request);
		if (hm == null)
			return null;

		Class<?> clz = null;

		if (!classMap.containsKey(hm)) {
			clz = hm.getBeanType();
			Winlet winlet = AnnotationUtils.findAnnotation(clz, Winlet.class);
			if (winlet == null)
				clz = null;

			classMap.put(hm, clz);
		} else
			clz = classMap.get(hm);

		if (clz != null) { // This is a Winlet
			if (req.getActionId() == null) { // 没有指定Action
				Window annotation = AnnotationUtils.findAnnotation(
						hm.getMethod(), Window.class);
				if (annotation == null) // 不是@Window方法
					return hm;

				WinletDef def = WinletDef.getDef(clz);
				Object winlet = WinletManager.getWinlet(Context.get(), def);
				req.setWinlet(def, winlet);

				return new HandlerMethod(winlet, def.getWindow(
						annotation.value()).getMethod());
			} else {
				int idx = req.getActionId().indexOf(".");
				if (idx > 0) {
					String winlet = req.getActionId().substring(0, idx);
					String action = req.getActionId().substring(idx + 1);

					WinletDef def = WinletDef.getDef(WinletClassLoader
							.getWinletClassByPath(winlet));
					Object w = WinletManager.getWinlet(Context.get(), def);
					req.setWinlet(def, w);

					return new HandlerMethod(w, def.getAction(action)
							.getMethod());
				} else {
					WinletDef def = WinletDef.getDef(clz);
					Object w = WinletManager.getWinlet(Context.get(), def);
					req.setWinlet(def, w);

					ActionDef action = def.getAction(req.getActionId());
					return new HandlerMethod(w, action.getMethod());
				}
			}
		}

		return hm;
	}
}
