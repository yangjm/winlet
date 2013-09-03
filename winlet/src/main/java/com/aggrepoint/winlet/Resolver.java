package com.aggrepoint.winlet;

import java.beans.FeatureDescriptor;
import java.util.Hashtable;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.springframework.core.annotation.AnnotationUtils;

import com.aggrepoint.winlet.spring.annotation.Winlet;
import com.icebean.core.beanutil.BeanProperty;

/**
 * 支持在EL中通过w.访问Winlet对象，通过ps.访问Page Storage，通过ret.访问响应码对象，通过win访问taglib功能等
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Resolver extends javax.el.ELResolver implements
		ServletContextListener {
	static Hashtable<String, BeanProperty> m_htProperties = new Hashtable<String, BeanProperty>();

	static public Object getObjectValue(Object obj, String property)
			throws Exception {
		String key = obj.getClass() + "_" + property.toString();

		// Try to get property from cache
		BeanProperty bp = m_htProperties.get(key);
		if (bp != null) {
			return bp.get(obj);
		} else {
			bp = new BeanProperty(obj.getClass(), property, true, false);
			m_htProperties.put(key, bp);
			return bp.get(obj);
		}
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext arg0, Object arg1) {
		return null;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext arg0,
			Object arg1) {
		return null;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		return null;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		Object val = null;

		if (base == null) {
			if (property.equals("w")) {
				val = ContextUtils.getReqInfo().getViewInstance().getWinlet();
			} else if (property.equals("sps")) {
				val = ContextUtils.getReqInfo().getSharedPageStorage();
			} else if (property.equals("ps")) {
				val = ContextUtils.getReqInfo().getPageStorage();
			} else if (property.equals("return")) {
				val = ContextUtils.getReqInfo().getReturnDef();
			} else if (property.equals("u")) {
				val = ContextUtils.getUser(ContextUtils.getRequest());
			} else if (property.equals("c")) {
				val = ContextUtils
						.getCodeMapProvider(ContextUtils.getRequest());
			} else if (property.equals("f")) {
				// val = ThreadContext.getAttribute(THREAD_ATTR_REQUEST);
			} else if (property.equals("e")) {
				// val = ThreadContext.getAttribute(THREAD_ATTR_EXCEPTION);
			} else if (property.equals("win"))
				val = new WinletEl();
			// else if (property.equals("rinfo"))
			// val = WinletReqInfo.getInfo((IModuleRequest) ThreadContext
			// .getAttribute(THREAD_ATTR_REQUEST));

			if (val != null)
				context.setPropertyResolved(true);
		} else {
			if (base instanceof PageStorage) {
				val = ((PageStorage) base).getAttribute(property);
				context.setPropertyResolved(true);
			} else if (base instanceof SharedPageStorage) {
				val = ((SharedPageStorage) base).getAttribute(property);
				context.setPropertyResolved(true);
			} else if (base instanceof CodeMapProvider) {
				val = ((CodeMapProvider) base).getMap(property.toString());
				context.setPropertyResolved(true);
			} else if (base instanceof WinletEl) {
				WinletEl winEl = (WinletEl) base;
				if (winEl.getMethod() == null) {
					winEl.setMethod(property.toString());
					val = winEl;
				} else {
					val = winEl.execute(property.toString());
					context.setPropertyResolved(true);
				}
			} else if (base instanceof CodeMapWrapper) {
				val = ((CodeMapWrapper) base).get(property.toString());
				context.setPropertyResolved(true);
			} else {
				Winlet winlet = AnnotationUtils.findAnnotation(base.getClass(),
						Winlet.class);
				if (winlet != null) {
					try {
						val = getObjectValue(base, property.toString());
						context.setPropertyResolved(true);
					} catch (Exception e) {
					}
				}
			}
		}

		return val;
	}

	@Override
	public boolean isReadOnly(ELContext arg0, Object arg1, Object arg2)
			throws NullPointerException, PropertyNotFoundException, ELException {
		return false;
	}

	@Override
	public void setValue(ELContext arg0, Object arg1, Object arg2, Object arg3)
			throws NullPointerException, PropertyNotFoundException,
			PropertyNotWritableException, ELException {
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext context = evt.getServletContext();
		JspApplicationContext jspContext = JspFactory.getDefaultFactory()
				.getJspApplicationContext(context);
		jspContext.addELResolver(new Resolver());
	}
}
