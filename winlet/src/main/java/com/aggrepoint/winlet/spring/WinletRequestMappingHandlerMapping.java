package com.aggrepoint.winlet.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.aggrepoint.winlet.Context;
import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.ReqInfoImpl;
import com.aggrepoint.winlet.WindowInstance;
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
			WindowInstance wi = WinletManager.getWindowInstance(
					getApplicationContext(),
					req,
					WinletDef.getDef(clz).getWindow(
							AnnotationUtils.findAnnotation(hm.getMethod(),
									Window.class).value()));

			req.setWindowIntance(wi);

			if (req.getTranslateUpdate() != null) // 转换update窗口，不执行winlet方法
				return new HandlerMethod(TranslateUpdate.getInstance(),
						TranslateUpdate.getMethod());

			// 恢复include window时设置的parameter
			if (req.getRequest() instanceof WinletRequestWrapper)
				((WinletRequestWrapper) req.getRequest()).setParams(wi
						.getParams());
			else if (req.getRequest() instanceof ServletRequestWrapper)
				if (((ServletRequestWrapper) req.getRequest()).getRequest() instanceof WinletRequestWrapper) {
					((WinletRequestWrapper) ((ServletRequestWrapper) req
							.getRequest()).getRequest()).setParams(wi
							.getParams());
				}

			if (req.getActionId() == null) {
				wi.clearSub();
				return new HandlerMethod(wi.getWinlet(), wi.getWindowDef()
						.getMethod());
			} else {
				int idx = req.getActionId().indexOf(".");
				if (idx > 0) {
					String winlet = req.getActionId().substring(0, idx);
					String action = req.getActionId().substring(idx + 1);

					WinletDef def = WinletDef.getDef(WinletClassLoader
							.getWinletClassByPath(winlet));

					return new HandlerMethod(WinletManager.getWinlet(
							Context.get(), req, def), def.getAction(action)
							.getMethod());
				} else {
					ActionDef action = wi.getWindowDef().getWinletDef()
							.getAction(req.getActionId());
					return new HandlerMethod(wi.getWinlet(), action.getMethod());
				}
			}
		}

		return hm;
	}
}
