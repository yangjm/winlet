package com.aggrepoint.winlet;

import java.beans.FeatureDescriptor;
import java.util.Hashtable;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.persistence.Entity;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.annotation.AnnotationUtils;

import com.aggrepoint.winlet.spring.WinletDefaultFormattingConversionService;
import com.aggrepoint.winlet.spring.annotation.Winlet;
import com.aggrepoint.winlet.utils.BeanProperty;
import com.aggrepoint.winlet.utils.EncodeUtils;

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
			if (property.equals("r")) {
				val = ContextUtils.getReqInfo().getReturnDef();
			} else if (property.equals("sps")) {
				val = ContextUtils.getReqInfo().getSharedPageStorage();
			} else if (property.equals("ps")) {
				val = ContextUtils.getReqInfo().getPageStorage();
			} else if (property.equals("u")) {
				val = ContextUtils.getUser(ContextUtils.getRequest());
			} else if (property.equals("c")) { // config provider
				val = ContextUtils.getConfigProvider(ContextUtils.getRequest());
			} else if (property.equals("m")) { // object map
				val = ContextUtils.getListProvider(ContextUtils.getRequest());
			} else if (property.equals("cm")) { // code value map
				val = new ListProviderWrapper(1,
						ContextUtils.getListProvider(ContextUtils.getRequest()));
			} else if (property.equals("l")) { // object list
				val = new ListProviderWrapper(2,
						ContextUtils.getListProvider(ContextUtils.getRequest()));
			} else if (property.equals("cl")) { // code value list
				val = new ListProviderWrapper(3,
						ContextUtils.getListProvider(ContextUtils.getRequest()));
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
			} else if (base instanceof ConfigProvider) {
				val = ((ConfigProvider) base).getStr(property.toString());
				context.setPropertyResolved(true);
			} else if (base instanceof ListProviderWrapper) {
				switch (((ListProviderWrapper) base).getType()) {
				case 1:
					val = ((ListProvider) base).getCodeValueMap(property
							.toString());
					context.setPropertyResolved(true);
					break;
				case 2:
					val = ((ListProvider) base).getList(property.toString());
					context.setPropertyResolved(true);
					break;
				case 3:
					val = ((ListProvider) base).getCodeValueList(property
							.toString());
					context.setPropertyResolved(true);
					break;
				}
			} else if (base instanceof ListProvider) {
				val = ((ListProvider) base).getMap(property.toString());
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
			} else if (base instanceof HashMapWrapper) {
				val = ((HashMapWrapper<?, ?>) base).get(property.toString());
				context.setPropertyResolved(true);
			} else {
				if (AnnotationUtils.findAnnotation(base.getClass(),
						Winlet.class) != null) {
					try {
						val = getObjectValue(base, property.toString());
						context.setPropertyResolved(true);
					} catch (Exception e) {
					}
				} else if (property.toString().indexOf(".") == -1
						&& WinletDefaultFormattingConversionService.canFormat(
								base, property.toString())) {
					// Apply format annotation declared on fields
					val = WinletDefaultFormattingConversionService.format(base,
							property.toString());
					context.setPropertyResolved(true);
				} else if (AnnotationUtils.findAnnotation(base.getClass(),
						Entity.class) != null
						&& String.class.equals(getPropType(base,
								property.toString()))) {
					// 如果是Entity的属性并且类型为字符串则加上encoding
					val = EncodeUtils.html(new BeanWrapperImpl(base)
							.getPropertyValue(property.toString()));
					context.setPropertyResolved(true);
				}
			}
		}

		return val;
	}

	static Hashtable<String, Class<?>> PROP_TYPES = new Hashtable<String, Class<?>>();

	static Class<?> getPropType(Object obj, String prop) {
		String key = obj.getClass().getName() + "_" + prop;
		if (!PROP_TYPES.containsKey(key)) {
			Class<?> type = new BeanWrapperImpl(obj).getPropertyType(prop);
			if (type == null)
				return null;

			PROP_TYPES.put(key, type);
		}
		return PROP_TYPES.get(key);
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
		ServletContext ctx = evt.getServletContext();

		JspApplicationContext jspContext = JspFactory.getDefaultFactory()
				.getJspApplicationContext(ctx);
		jspContext.addELResolver(this);
	}
}
