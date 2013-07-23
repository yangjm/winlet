package com.aggrepoint.winlet.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.ReqInfoImpl;
import com.aggrepoint.winlet.ViewInstance;
import com.aggrepoint.winlet.WinletManager;
import com.aggrepoint.winlet.spring.annotation.Window;
import com.aggrepoint.winlet.spring.annotation.Winlet;
import com.aggrepoint.winlet.spring.def.ActionDef;
import com.aggrepoint.winlet.spring.def.WidgetDef;

/**
 * 根据Winlet的要求处理Spring按缺省实现找到的HandlerMethod，返回要需要框架执行的HandlerMethod
 * 
 * @author Jim
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
			ViewInstance vi = WinletManager.getViewInstance(
					getApplicationContext(),
					req,
					WidgetDef.getDef(clz).getView(
							AnnotationUtils.findAnnotation(hm.getMethod(),
									Window.class).value()));

			req.setViewInstance(vi);

			if (req.getActionId() == null) {
				vi.clearSub();
				return new HandlerMethod(vi.getWinlet(), vi.getViewDef()
						.getMethod());
			} else {
				ActionDef action = vi.getViewDef().getWinletDef()
						.getAction(req.getActionId());
				return new HandlerMethod(vi.getWinlet(), action.getMethod());
			}
		}

		return hm;
	}
}
