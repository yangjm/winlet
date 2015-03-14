package com.aggrepoint.winlet.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Bean属性定义
 * 
 * 创建日期：(2002-11-27)
 * 
 * @author: Yang Jiang Ming
 */
public class BeanProperty implements PropertyTypeCode {
	protected static Object[] EMPTYOBJECTARRAY = new Object[0];

	/** 属性名称。如果不是通过属性指定的，则为null */
	protected String m_strPropertyName;

	/** 属性字段名称。如果不映射到字段，则为null */
	protected String m_strFieldName;

	/** 属性字段。如果不映射到字段，则为null */
	protected Field m_f;

	/** 属性Get方法的名称。如果不对应到Get和Set方法则为null */
	protected String m_strGetName;

	/** 属性Get方法。如果不对应到Get和Set方法则为null */
	protected Method m_mGet;

	/** 属性Set方法的名称。如果不对应到Get和Set方法则为null */
	protected String m_strSetName;

	/** 属性Set方法。如果不对应到Get和Set方法则为null */
	protected Method m_mSet;

	/** 属性类型 */
	protected Class<?> m_cType;

	/** 用数值来表示的属性类型 */
	protected int m_iType;

	/** 属性空类型 */
	protected int m_iNullType;

	/**
	 * 获取数据类型
	 */
	public static int getTypeCode(Class<?> c) {
		int iType = 0;

		String strFieldType = c.getName();
		if (strFieldType.equals("short")
				|| strFieldType.equals(Short.class.getName()))
			iType = SHORT;
		else if (strFieldType.equals("int")
				|| strFieldType.equals(Integer.class.getName()))
			iType = INTEGER;
		else if (strFieldType.equals("long")
				|| strFieldType.equals(Long.class.getName()))
			iType = LONG;
		else if (strFieldType.equals("float")
				|| strFieldType.equals(Float.class.getName()))
			iType = FLOAT;
		else if (strFieldType.equals("double")
				|| strFieldType.equals(Double.class.getName()))
			iType = DOUBLE;
		else if (strFieldType.equals("boolean")
				|| strFieldType.equals(Boolean.class.getName()))
			iType = BOOLEAN;
		else if (strFieldType.equals("java.lang.String"))
			iType = STRING;
		else if (java.sql.Timestamp.class.isAssignableFrom(c))
			iType = TIMESTAMP;
		else if (java.sql.Date.class.isAssignableFrom(c))
			iType = DATE;
		else if (java.io.InputStream.class.isAssignableFrom(c))
			iType = INPUTSTREAM;
		else if (java.util.Collection.class.isAssignableFrom(c))
			iType = COLLECTION;
		return iType;
	}

	/**
	 * 获取数据类型
	 */
	static int getNullTypeCode(Class<?> c) {
		int iType = 0;

		String strFieldType = c.getName();

		if (strFieldType.equals("short")
				|| strFieldType.equals(Short.class.getName()))
			iType = java.sql.Types.SMALLINT;
		else if (strFieldType.equals("int")
				|| strFieldType.equals(Integer.class.getName()))
			iType = java.sql.Types.INTEGER;
		else if (strFieldType.equals("long")
				|| strFieldType.equals(Long.class.getName()))
			iType = java.sql.Types.BIGINT;
		else if (strFieldType.equals("float")
				|| strFieldType.equals(Float.class.getName()))
			iType = java.sql.Types.FLOAT;
		else if (strFieldType.equals("double")
				|| strFieldType.equals(Double.class.getName()))
			iType = java.sql.Types.DOUBLE;
		else if (strFieldType.equals("boolean")
				|| strFieldType.equals(Boolean.class.getName()))
			iType = java.sql.Types.BOOLEAN;
		else if (strFieldType.equals("java.lang.String"))
			iType = java.sql.Types.VARCHAR;
		else if (java.sql.Timestamp.class.isAssignableFrom(c))
			iType = java.sql.Types.TIMESTAMP;
		else if (java.sql.Date.class.isAssignableFrom(c))
			iType = java.sql.Types.DATE;
		else if (java.io.InputStream.class.isAssignableFrom(c))
			iType = java.sql.Types.BINARY;

		return iType;
	}

	/**
	 * 从方法数组中根据方法名称找出方法
	 */
	static Method getMethodByName(Method[] methods, String methodName) {
		for (int i = 0; i < methods.length; i++)
			if (methods[i].getName().equalsIgnoreCase(methodName))
				return methods[i];
		return null;
	}

	protected BeanProperty() {
	}

	protected void init(Class<?> cBean, String propertyName, boolean readable,
			boolean writable, Class<?> type, Class<?>... args)
			throws BeanPropertyException {

		m_strPropertyName = propertyName;

		m_strFieldName = m_strGetName = m_strSetName = null;
		m_f = null;
		m_mGet = m_mSet = null;

		m_cType = null;
		m_iType = 0;

		// {首先试将属性看作为字段
		if (args == null || args.length == 0) {
			try {
				m_f = cBean.getField(m_strPropertyName);

				m_strFieldName = m_strPropertyName;
				if (type != null)
					m_cType = type;
				else
					m_cType = m_f.getType();
			} catch (NoSuchFieldException e) {
			}

			if (m_f != null) { // 是字段
				m_iType = getTypeCode(m_cType);
				m_iNullType = getNullTypeCode(m_cType);
				return;
			}
		}
		// }

		Method[] methods = cBean.getMethods();
		Class<?>[] params;
		Class<?> cGetType = null, cSetType = null;

		// {尝试get方法
		if (readable) {
			m_mGet = getMethodByName(methods, "get" + propertyName);
			if (m_mGet == null)
				m_mGet = getMethodByName(methods, propertyName);
			if (m_mGet == null)
				throw new BeanPropertyException("Property " + propertyName
						+ " of class " + cBean.getName()
						+ " should be readable.");

			m_strGetName = m_mGet.getName();

			// {检查Get方法所带参数，并获取Get方法的返回值类型
			params = m_mGet.getParameterTypes();
			if (params.length != (args == null ? 0 : args.length))
				throw new BeanPropertyException("Getter method \""
						+ m_strGetName + "\" of class \"" + cBean.getName()
						+ "\" has wrong number of parameters.");
			if (args != null)
				for (int i = 0; i < params.length; i++)
					if (!params[i].isAssignableFrom(args[i]))
						throw new BeanPropertyException("Getter method \"("
								+ m_strGetName + ")\" of class \"("
								+ cBean.getName()
								+ ")\" has wrong parameter types.");

			cGetType = m_mGet.getReturnType();
			// }
		}
		// }

		// {尝试set方法
		if (writable) {
			m_mSet = getMethodByName(methods, "set" + propertyName);
			if (m_mSet == null)
				m_mSet = getMethodByName(methods, propertyName);

			if (m_mSet == null)
				throw new BeanPropertyException("Property " + propertyName
						+ " of class " + propertyName + " should be writable.");
			m_strSetName = m_mSet.getName();

			// {Set方法的参数类型
			params = m_mSet.getParameterTypes();
			if (params.length != (1 + (args == null ? 0 : args.length)))
				throw new BeanPropertyException("Setter method \""
						+ m_strSetName + "\" of class \"" + cBean.getName()
						+ "\" has wrong number of parameters.");
			if (args != null)
				for (int i = 0; i < args.length; i++)
					if (!params[i].isAssignableFrom(args[i]))
						throw new BeanPropertyException("Setter method \""
								+ m_strSetName + "\" of class \""
								+ cBean.getName()
								+ "\" has wrong parameter types.");

			cSetType = params[params.length - 1];
			// }
		}
		// }

		if (cGetType == null && cSetType == null) // 没有找到对应的属性
			throw new BeanPropertyException("Property " + propertyName
					+ " not exist in class " + cBean.getName() + ".");

		// {检验get方法与set方法是否一致
		if (cGetType != null && cSetType != null)
			if (!java.io.InputStream.class.isAssignableFrom(cGetType)
					&& !java.sql.Blob.class.isAssignableFrom(cSetType))
				if (!cSetType.equals(cGetType)) {
					throw new BeanPropertyException("Type of setter method \""
							+ m_strSetName + "\" and getter method \""
							+ m_strGetName + "\" of class \"" + cBean.getName()
							+ "\" do not match.");
				}

		if (type != null)
			m_cType = type;
		else if (cGetType != null)
			m_cType = cGetType;
		else
			m_cType = cSetType;
		// }

		m_iType = getTypeCode(m_cType);
		m_iNullType = getNullTypeCode(m_cType);

	}

	public BeanProperty(Class<?> cBean, String propertyName, boolean readable,
			boolean writable, Class<?> type, Class<?>... args)
			throws BeanPropertyException {
		init(cBean, propertyName, readable, writable, type, args);
	}

	public BeanProperty(Class<?> cBean, String propertyName, boolean readable,
			boolean writable) throws BeanPropertyException {
		init(cBean, propertyName, readable, writable, null);
	}

	/**
	 * 初始化
	 * 
	 * @param cBean
	 *            对应Bean的类型
	 * @param fieldName
	 *            如果该属性是通过字段去访问的，则为字段名称，否则应设置为null 如果该参数不为空，则忽略下面两个参数
	 * @param getName
	 *            如果该属性可通过get方法去访问，则为方法名称，否则应设置为null
	 * @param setName
	 *            如果该属性可通过set方法去设置，则为方法名称，否则应设置为null
	 */
	public BeanProperty(Class<?> cBean, String fieldName, String getName,
			String setName, Class<?>... args) throws BeanPropertyException {
		if (fieldName != null && fieldName.equals(""))
			fieldName = null;
		if (getName != null && getName.equals(""))
			getName = null;
		if (setName != null && setName.equals(""))
			setName = null;

		m_strPropertyName = m_strFieldName = m_strGetName = m_strSetName = null;
		m_f = null;
		m_mGet = m_mSet = null;

		if (fieldName == null && getName == null && setName == null)
			throw new BeanPropertyException(
					"Field name or setter method name or getter method name not specified.");

		m_cType = null;
		m_iType = 0;

		if (fieldName != null) { // 属性字段
			m_strFieldName = fieldName;

			try {
				m_f = cBean.getField(m_strFieldName);
			} catch (NoSuchFieldException e) {
				throw new BeanPropertyException("Field \"" + m_strFieldName
						+ "\" not exist in class \"" + cBean.getName() + "\"");
			}

			m_cType = m_f.getType();
		} else { // 属性方法
			m_strFieldName = null;

			Method[] methods = cBean.getMethods();
			Class<?>[] params;
			Class<?> cGetType = null, cSetType = null;

			// Get方法
			if (getName != null) {
				m_strGetName = getName;
				m_mGet = getMethodByName(methods, m_strGetName);

				if (m_mGet == null)
					throw new BeanPropertyException("Getter method \""
							+ m_strGetName + "\" not exist in class \""
							+ cBean.getName() + "\".");

				// {检查Get方法所带参数，并获取Get方法的返回值类型
				params = m_mGet.getParameterTypes();
				if (params.length != (args == null ? 0 : args.length))
					throw new BeanPropertyException("Getter method \""
							+ m_strGetName + "\" of class \"" + cBean.getName()
							+ "\" has wrong number of parameters.");
				if (args != null)
					for (int i = 0; i < params.length; i++)
						if (!params[i].isAssignableFrom(args[i]))
							throw new BeanPropertyException("Getter method \""
									+ m_strGetName + "\" of class \""
									+ cBean.getName()
									+ "\" has wrong parameter types.");

				cGetType = m_mGet.getReturnType();
				// }

			}

			// Set方法
			if (setName != null) {
				m_strSetName = setName;
				m_mSet = getMethodByName(methods, m_strSetName);

				if (m_mSet == null)
					throw new BeanPropertyException("Setter method \""
							+ m_strSetName + "\" not exist in class \""
							+ cBean.getName() + "\".");

				// {Set方法的参数类型
				params = m_mSet.getParameterTypes();
				if (params.length != (1 + (args == null ? 0 : args.length)))
					throw new BeanPropertyException("Setter method \""
							+ m_strSetName + "\" of class \"" + cBean.getName()
							+ "\" has wrong number of parameters.");
				if (args != null)
					for (int i = 0; i < args.length; i++)
						if (!params[i].isAssignableFrom(args[i]))
							throw new BeanPropertyException("Setter method \""
									+ m_strSetName + "\" of class \""
									+ cBean.getName()
									+ "\" has wrong parameter types.");

				cSetType = params[params.length - 1];
				// }
			}

			if (cGetType != null && cSetType != null)
				if (!java.io.InputStream.class.isAssignableFrom(cGetType)
						&& !java.sql.Blob.class.isAssignableFrom(cSetType))
					if (!cSetType.equals(cGetType))
						throw new BeanPropertyException(
								"Type of setter method \"" + m_strSetName
										+ "\" and getter method \""
										+ m_strGetName + "\" of class \""
										+ cBean.getName() + "\" do not match.");

			if (cGetType != null)
				m_cType = cGetType;
			else
				m_cType = cSetType;
		}

		m_iType = getTypeCode(m_cType);
		m_iNullType = getNullTypeCode(m_cType);
	}

	/**
	 * 获取属性名称
	 */
	public String getPropertyName() {
		return m_strPropertyName;
	}

	/**
	 * 获取属性类型
	 * 
	 * @return Class 属性类型
	 */
	public Class<?> getType() {
		return m_cType;
	}

	/**
	 * 获取属性类型编号
	 * 
	 * @return int 属性类型编号
	 */
	public int getTypeCode() {
		return m_iType;
	}

	public int getNullTypeCode() {
		return m_iNullType;
	}

	/**
	 * 获取属性对应的字段名称
	 */
	public String getFieldName() {
		if (m_strFieldName == null)
			return "";
		return m_strFieldName;
	}

	/**
	 * 获取属性对应的Get方法名称
	 */
	public String getGetMethodName() {
		if (m_strGetName == null)
			return "";
		return m_strGetName;
	}

	/**
	 * 获取属性对应的Set方法名称
	 */
	public String getSetMethodName() {
		if (m_strSetName == null)
			return "";
		return m_strSetName;
	}

	/**
	 * 获取属性名称。 如果属性对应字段，则返回字段名称，否则返回[get 方法名称]|[set 方法名称]
	 */
	public String getName() {
		if (m_strFieldName != null)
			return m_strFieldName;

		String str;

		if (m_strGetName != null)
			str = m_strGetName + "()|";
		else
			str = "|";

		if (m_strSetName != null)
			return str + m_strSetName;

		return str;
	}

	/**
	 * 获取属性值
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws BeanPropertyException
	 */
	public Object get(Object obj, Object... args)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, BeanPropertyException,
			NoSuchMethodException {
		if (m_f != null)
			return m_f.get(obj);

		if (m_mGet != null)
			if (args == null || args.length == 0)
				return m_mGet.invoke(obj);
			else
				return m_mGet.invoke(obj, args);

		throw new BeanPropertyException("Try to read write-only property.");
	}

	public static Object convert(Object val, int type, boolean[] noValue)
			throws SQLException {
		boolean bNoValue = false;

		if (val == null) {
			switch (type) {
			case SHORT:
				val = new Short((short) 0);
				break;
			case INTEGER:
				val = new Integer(0);
				break;
			case LONG:
				val = new Long(0l);
				break;
			case FLOAT:
				val = new Float(0.0f);
				break;
			case DOUBLE:
				val = new Double(0.0d);
				break;
			case BOOLEAN:
				val = new Boolean(false);
				break;
			}
		} else if (val instanceof String) {
			switch (type) {
			case SHORT:
				if (val.equals(""))
					bNoValue = true;
				else
					val = new Short((String) val);
				break;
			case INTEGER:
				if (val.equals(""))
					bNoValue = true;
				else
					val = new Integer((String) val);
				break;
			case LONG:
				if (val.equals(""))
					bNoValue = true;
				else
					val = new Long((String) val);
				break;
			case FLOAT:
				if (val.equals(""))
					bNoValue = true;
				else
					val = new Float((String) val);
				break;
			case DOUBLE:
				if (val.equals(""))
					bNoValue = true;
				else
					val = new Double((String) val);
				break;
			case BOOLEAN:
				if (val.equals(""))
					val = false;
				else
					val = new Boolean(
							(new Integer((String) val)).intValue() != 0);
				break;
			}
		} else if (type == BOOLEAN) {
			if (val instanceof Short)
				val = new Boolean(((Short) val).shortValue() != 0);
			else if (val instanceof Integer)
				val = new Boolean(((Integer) val).shortValue() != 0);
			else if (val instanceof Long)
				val = new Boolean(((Long) val).shortValue() != 0);
			else if (val instanceof BigDecimal)
				val = new Boolean(((BigDecimal) val).shortValue() != 0);
		} else if (type == SHORT) {
			if (val instanceof Integer)
				val = new Short(((Integer) val).shortValue());
			else if (val instanceof Long)
				val = new Short(((Long) val).shortValue());
			else if (val instanceof String)
				val = new Short((short) Integer.parseInt((String) val));
			else if (val instanceof BigDecimal)
				val = new Short(((BigDecimal) val).shortValue());
		} else if (type == INTEGER) {
			if (val instanceof Long)
				val = new Integer(((Long) val).intValue());
			else if (val instanceof String)
				val = new Integer(Integer.parseInt((String) val));
			else if (val instanceof BigDecimal)
				val = new Integer(((BigDecimal) val).intValue());
		} else if (type == LONG) {
			if (val instanceof String)
				val = new Long(Long.parseLong((String) val));
			else if (val instanceof BigDecimal)
				val = new Long(((BigDecimal) val).longValue());
		} else if (type == FLOAT) {
			if (val instanceof Long)
				val = new Float(((Long) val).longValue());
			else if (val instanceof String)
				val = new Float(Float.parseFloat((String) val));
			else if (val instanceof Double)
				val = new Float(((Double) val).floatValue());
			else if (val instanceof BigDecimal)
				val = new Float(((BigDecimal) val).floatValue());
		} else if (type == DOUBLE) {
			if (val instanceof Long)
				val = new Double(((Long) val).longValue());
			else if (val instanceof String)
				val = new Double(Double.parseDouble((String) val));
			else if (val instanceof Float)
				val = new Double(((Float) val).floatValue());
			else if (val instanceof BigDecimal)
				val = new Double(((BigDecimal) val).doubleValue());
		} else if (type == DATE) {
			if (val instanceof java.util.Date)
				val = new java.sql.Date(((java.util.Date) val).getTime());
		} else if (type == TIMESTAMP) {
			if (!(val instanceof java.sql.Timestamp)
					&& val instanceof java.util.Date)
				val = new java.sql.Timestamp(((java.util.Date) val).getTime());
		} else if (type == STRING) {
			val = val.toString();
		}

		noValue[0] = bNoValue;
		return val;
	}

	/**
	 * 设置属性值
	 * 
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws BeanPropertyException
	 */
	public void set(Object obj, Object val, Object... args)
			throws SQLException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			BeanPropertyException, NoSuchMethodException {
		boolean noValue[] = new boolean[1];
		val = convert(val, getTypeCode(), noValue);

		if (noValue[0])
			return;

		if (m_f != null) {
			m_f.set(obj, val);
			return;
		}

		if (m_mSet != null) {
			if (args == null || args.length == 0)
				m_mSet.invoke(obj, val);
			else {
				Object[] all = new Object[args.length + 1];
				for (int i = 0; i < args.length; i++)
					all[i] = args[i];
				all[all.length - 1] = val;
				m_mSet.invoke(obj, all);
			}
			return;
		}

		throw new BeanPropertyException("Try to write read-only property.");
	}

	/**
	 * 判断属性是否为数值型
	 */
	public boolean isNum() {
		return (getTypeCode() == SHORT || getTypeCode() == INTEGER || getTypeCode() == LONG);
	}

	/**
	 * 判断属性是否为可读写
	 */
	public boolean isReadWrite() {
		if (m_f != null)
			return true;

		if (m_mSet != null && m_mGet != null)
			return true;

		return false;
	}

	/**
	 * 判断属性是否为只读
	 */
	public boolean isReadOnly() {
		if (m_f != null)
			return false;

		if (m_mSet != null)
			return false;

		return true;
	}

	/**
	 * 判断属性是否为只写
	 */
	public boolean isWriteOnly() {
		if (m_f != null)
			return false;

		if (m_mGet != null)
			return false;

		return true;
	}

	/**
	 * 获取数值型的属性值
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws BeanPropertyException
	 */
	public long getNum(Object obj) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			BeanPropertyException, NoSuchMethodException {
		switch (getTypeCode()) {
		case SHORT:
			if (m_f != null)
				return (long) m_f.getShort(obj);
			if (m_mGet != null)
				return ((Short) m_mGet.invoke(obj, EMPTYOBJECTARRAY))
						.shortValue();
			break;
		case INTEGER:
			if (m_f != null)
				return (long) m_f.getInt(obj);
			if (m_mGet != null)
				return ((Integer) m_mGet.invoke(obj, EMPTYOBJECTARRAY))
						.intValue();
			break;
		case LONG:
			if (m_f != null)
				return m_f.getLong(obj);
			if (m_mGet != null)
				return ((Long) m_mGet.invoke(obj, EMPTYOBJECTARRAY))
						.longValue();
			break;
		default:
			throw new BeanPropertyException("Property is not numeric.");
		}

		throw new BeanPropertyException("Try to read write-only property.");
	}

	/**
	 * 获取数值型的属性值
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws BeanPropertyException
	 */
	public void setNum(Object obj, long val) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			BeanPropertyException, NoSuchMethodException {
		switch (getTypeCode()) {
		case SHORT:
			if (m_f != null) {
				m_f.setShort(obj, (short) val);
				return;
			}
			if (m_mSet != null) {
				m_mSet.invoke(obj, new Object[] { new Short((short) val) });
				return;
			}
			break;
		case INTEGER:
			if (m_f != null) {
				m_f.setInt(obj, (int) val);
				return;
			}
			if (m_mSet != null) {
				m_mSet.invoke(obj, new Object[] { new Integer((int) val) });
				return;
			}
			break;
		case LONG:
			if (m_f != null) {
				m_f.setLong(obj, val);
				return;
			}
			if (m_mSet != null) {
				m_mSet.invoke(obj, new Object[] { new Long(val) });
				return;
			}
			break;
		default:
			throw new BeanPropertyException("Property is not numeric.");
		}

		throw new BeanPropertyException("Try to write read-only property.");
	}
}