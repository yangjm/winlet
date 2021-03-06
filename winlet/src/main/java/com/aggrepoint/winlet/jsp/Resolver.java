package com.aggrepoint.winlet.jsp;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.persistence.Entity;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.annotation.AnnotationUtils;

import com.aggrepoint.winlet.ConfigProvider;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.HashMapWrapper;
import com.aggrepoint.winlet.ListProvider;
import com.aggrepoint.winlet.ListProviderWrapper;
import com.aggrepoint.winlet.PageStorage;
import com.aggrepoint.winlet.SharedPageStorage;
import com.aggrepoint.winlet.WinletEl;
import com.aggrepoint.winlet.spring.WinletDefaultFormattingConversionService;
import com.aggrepoint.winlet.spring.annotation.Winlet;
import com.aggrepoint.winlet.utils.BeanProperty;
import com.aggrepoint.winlet.utils.EncodeUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * 支持在EL中通过w.访问Winlet对象，通过ps.访问Page Storage，通过ret.访问响应码对象，通过win访问taglib功能等
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Resolver extends javax.el.ELResolver {
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

	public static String lineBreakToHtml(String str) {
		try {
			if (str == null)
				return "";
			return str.replace("&#xd;&#xa;", "<br>").replace("&#xd;", "<br>")
					.replace("&#xa;", "<br>");
		} catch (Exception e) {
			return str;
		}
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		Object val = null;

		if (base == null) {
			if (property.equals("r")) {
				val = ContextUtils.getReqInfo().getReturnDef();
			} else if (property.equals("req")) {
				val = ContextUtils.getReqInfo();
			} else if (property.equals("reqid")) {
				val = ContextUtils.getReqInfo().getRequestId();
			} else if (property.equals("sps")) {
				val = ContextUtils.getReqInfo().getSharedPageStorage();
			} else if (property.equals("ps")) {
				val = ContextUtils.getReqInfo().getPageStorage();
			} else if (property.equals("prule")) {
				val = ContextUtils.getPsnRuleEngine(ContextUtils.getRequest());
			} else if (property.equals("arule")) {
				val = ContextUtils.getAccessRuleEngine(ContextUtils
						.getRequest());
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
			} else if (base instanceof JsonNode) {
				JsonNode node = ((JsonNode) base).get(property.toString());
				val = node;

				if (node != null) {
					if (node.isNull()) {
						val = null;
					} else if (node.isArray() && node instanceof ArrayNode) {
						List<JsonNode> dst = new ArrayList<JsonNode>();
						for (Iterator<JsonNode> it = ((ArrayNode) node)
								.elements(); it.hasNext();)
							dst.add(it.next());
						val = dst;
					} else if (node.isInt())
						val = node.asInt();
					else if (node.isBigDecimal() || node.isDouble()
							|| node.isFloatingPointNumber())
						val = node.asDouble();
					else if (node.isBoolean())
						val = node.asBoolean();
					else if (node.isBigInteger() || node.isLong())
						val = node.asLong();
					else if (node.isTextual())
						val = node.asText();
				}

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
				} else if ((AnnotationUtils.findAnnotation(base.getClass(),
						Entity.class) != null || AnnotationUtils
						.findAnnotation(base.getClass(), EncodeString.class) != null)
						&& String.class.equals(getPropType(base,
								property.toString()))) {
					// 如果是Entity的属性并且类型为字符串则加上encoding，并且将换行转换为<br>
					val = lineBreakToHtml(EncodeUtils.html(new BeanWrapperImpl(
							base).getPropertyValue(property.toString())));
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
}
