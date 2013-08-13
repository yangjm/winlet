package com.aggrepoint.winlet.form.del;

import com.icebean.core.beanutil.BeanProperty;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class InputCheckBoxImpl extends InputImpl {
	public boolean bHasValueParam = false;
	public String strValue;

	@Override
	public void setAttr(String name, Object value) {
		if (name.equals("value")) {
			if (value != null && !value.equals(""))
				strValue = value.toString();
		} else
			super.setAttr(name, value);
	}

	@Override
	protected BeanProperty getProp(Class<?> clz, String name) throws Exception {
		try {
			return new BeanProperty(clz, name, true, true, null);
		} catch (Exception e) {
			BeanProperty prop = new BeanProperty(clz, name, true, true, null,
					String.class);
			bHasValueParam = true;
			return prop;
		}
	}

	@Override
	public Object getPropValue() throws Exception {
		if (strProperty == null)
			return objValue;

		BeanProperty prop = getProp();
		if (bHasValueParam)
			return prop.get(propObject, strValue);
		else
			return prop.get(propObject);
	}

	@Override
	protected void setPropValue(Object val) throws Exception {
		if (strProperty == null) {
			objValue = val;
			return;
		}

		BeanProperty prop = getProp();
		if (bHasValueParam)
			prop.set(propObject, val, strValue);
		else
			prop.set(propObject, val);
	}

	@Override
	Object toDisplay(Object val) throws Exception {
		if (val instanceof Boolean)
			return val;
		if (val instanceof Long)
			return ((Long) val).longValue() != 0;
		if (val instanceof Integer)
			return ((Integer) val).intValue() != 0;
		if (val instanceof Short)
			return ((Short) val).shortValue() != 0;
		if (val instanceof String)
			return val.equals(strValue);

		return super.toDisplay(val);
	}

	@Override
	Object fromDisplay(String[] values) throws Exception {
		if (values == null || values.length == 0)
			return false;

		for (int i = 0; i < values.length; i++)
			if (values[i].equals(strValue))
				return true;

		return false;
	}
}
