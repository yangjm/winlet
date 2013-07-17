package com.aggrepoint.dao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.aggrepoint.dao.annotation.Delete;
import com.aggrepoint.dao.annotation.Find;
import com.aggrepoint.dao.annotation.Like;
import com.aggrepoint.dao.annotation.PageNum;
import com.aggrepoint.dao.annotation.PageSize;
import com.aggrepoint.dao.annotation.Param;
import com.aggrepoint.dao.annotation.Replace;
import com.aggrepoint.dao.annotation.Update;

public class HibernateDaoAnnotationMethod<T> implements HibernateDaoMethod {
	static Pattern P_PART = Pattern.compile("#\\{(.+?)\\}");
	static Pattern P_COUNT = Pattern.compile(
			"(select[\\s\\w\\.,]+?)?(from.+?)(order\\s+by[\\s\\w\\.,]+)?$",
			Pattern.CASE_INSENSITIVE);

	static final int TYPE_FIND = 0;
	static final int TYPE_UPDATE = 1;
	static final int TYPE_DELETE = 2;
	static final int RETURN_VOID = 0;
	static final int RETURN_LIST = 1;
	static final int RETURN_PAGE = 2;
	static final int RETURN_OBJECT = 3;

	SessionFactory factory;
	int type;
	int retType;
	String hql;
	Vector<QueryPart> parts = new Vector<QueryPart>();
	Hashtable<String, Param> params = new Hashtable<String, Param>();
	Hashtable<String, Like> likes = new Hashtable<String, Like>();
	Hashtable<String, Replace> replaces = new Hashtable<String, Replace>();
	Hashtable<String, Integer> positions = new Hashtable<String, Integer>();
	int pageNumIdx = -1;
	int pageSizeIdx = -1;

	public HibernateDaoAnnotationMethod(Method method, Annotation ann,
			SessionFactory factory) {
		this.factory = factory;

		if (ann.annotationType() == Find.class) {
			type = TYPE_FIND;
			hql = ((Find) ann).value();
		} else if (ann.annotationType() == Update.class) {
			type = TYPE_UPDATE;
			hql = ((Update) ann).value();
		} else if (ann.annotationType() == Delete.class) {
			type = TYPE_DELETE;
			hql = ((Delete) ann).value();
		} else {
			throw new IllegalArgumentException("Unsupported annotation '" + ann
					+ "' on " + method.getDeclaringClass().getName() + "."
					+ method.getName());
		}

		Annotation[][] pans = method.getParameterAnnotations();
		for (int i = 0; i < pans.length; i++) {
			for (int j = 0; j < pans[i].length; j++)
				if (pans[i][j].annotationType() == Param.class) {
					Param p = (Param) pans[i][j];

					if (positions.containsKey(p.value()))
						throw new IllegalArgumentException(
								"Duplicated param/like/replace name: '"
										+ p.value() + "' on "
										+ method.getDeclaringClass().getName()
										+ "." + method.getName());

					params.put(p.value(), p);
					positions.put(p.value(), i);
					break;
				} else if (pans[i][j].annotationType() == Like.class) {
					Like p = (Like) pans[i][j];

					if (positions.containsKey(p.value()))
						throw new IllegalArgumentException(
								"Duplicated param/like/replace name: '"
										+ p.value() + "' on "
										+ method.getDeclaringClass().getName()
										+ "." + method.getName());

					likes.put(p.value(), p);
					positions.put(p.value(), i);
					break;
				} else if (pans[i][j].annotationType() == Replace.class) {
					Replace r = (Replace) pans[i][j];

					if (positions.containsKey(r.value()))
						throw new IllegalArgumentException(
								"Duplicated param/like/replace name: '"
										+ r.value() + "' on "
										+ method.getDeclaringClass().getName()
										+ "." + method.getName());

					replaces.put(r.value(), r);
					positions.put(r.value(), i);
					break;
				} else if (pans[i][j].annotationType() == PageNum.class) {
					if (type != TYPE_FIND)
						throw new IllegalArgumentException(
								"@PageNum shouldn't be used on "
										+ method.getDeclaringClass().getName()
										+ "." + method.getName());
					pageNumIdx = i;
					break;
				} else if (pans[i][j].annotationType() == PageSize.class) {
					if (type != TYPE_FIND)
						throw new IllegalArgumentException(
								"@PageSize shouldn't be used on "
										+ method.getDeclaringClass().getName()
										+ "." + method.getName());
					pageSizeIdx = i;
					break;
				}
		}

		int idx = 0;
		Matcher m = P_PART.matcher(hql);
		while (m.find()) {
			if (m.start() != idx)
				parts.add(new QueryPart(method, false, hql.substring(idx,
						m.start()), params, likes, replaces));
			parts.add(new QueryPart(method, true, m.group(1), params, likes,
					replaces));
			idx = m.end();
		}
		if (idx != hql.length())
			parts.add(new QueryPart(method, false, hql.substring(idx), params,
					likes, replaces));

		Class<?> ret = method.getReturnType();
		switch (type) {
		case TYPE_FIND:
			if (ret == List.class)
				retType = RETURN_LIST;
			else if (ret == PageList.class)
				retType = RETURN_PAGE;
			else if (ret.getName().equals("void"))
				retType = RETURN_VOID;
			else
				retType = RETURN_OBJECT;
			break;
		case TYPE_UPDATE:
		case TYPE_DELETE:
			if (ret.getName().equals("int") && !(ret == Integer.class))
				retType = RETURN_OBJECT;
			else if (ret.getName().equals("void"))
				retType = RETURN_VOID;
			else
				throw new IllegalArgumentException(
						"@Update or @Delete method doesn't return int: "
								+ method.getDeclaringClass().getName() + "."
								+ method.getName());
		}
	}

	public Object invoke(Object[] args) {
		HashMap<String, Object> values = new HashMap<String, Object>();

		for (String p : params.keySet())
			values.put(p, args[positions.get(p)]);
		for (String p : likes.keySet()) {
			Object val = args[positions.get(p)];
			if (val != null) {
				Like l = likes.get(p);
				if (l.prefix())
					val = "%" + val.toString();
				if (l.suffix())
					val = val.toString() + "%";
			}
			values.put(p, val);
		}
		for (String p : replaces.keySet())
			values.put(p, args[positions.get(p)]);

		HashSet<String> paramsInUse = new HashSet<String>();

		StringBuffer sb = new StringBuffer();
		for (QueryPart part : parts) {
			String p = part.get(values);
			if (p != null) {
				sb.append(p);
				for (String pm : part.getParamDepends())
					paramsInUse.add(pm);
			}
		}

		String query = sb.toString();
		Query queryObject;

		switch (type) {
		case TYPE_FIND:
			PageList<T> pl = null;
			boolean paging = false;
			int pageNum = -1;
			int pageSize = -1;

			if (pageNumIdx >= 0 && pageSizeIdx >= 0) {
				paging = true;
				pageNum = (Integer) args[pageNumIdx];
				pageSize = (Integer) args[pageSizeIdx];

				if (pageNum <= 0)
					pageNum = 1;
				if (pageSize <= 0)
					pageSize = 1;
			}

			if (retType == RETURN_PAGE) {
				pl = new PageList<T>();

				Matcher m = P_COUNT.matcher(query);
				if (!m.find() || m.groupCount() < 3)
					throw new IllegalArgumentException(
							"Unable to conver hql to count hql '" + query);

				queryObject = factory.getCurrentSession().createQuery(
						"select count(*) " + m.group(2));

				for (String param : paramsInUse)
					HibernateDaoBaseMethod.applyNamedParameterToQuery(
							queryObject, param, values.get(param));

				Object c = queryObject.uniqueResult();
				if (c instanceof Long)
					pl.setTotalCount(((Long) c).intValue());
				else
					pl.setTotalCount((Integer) c);

				if (paging) {
					pl.setPageSize(pageSize);
					pl.setTotalPage((int) Math.ceil((double) pl.getTotalCount()
							/ pl.getPageSize()));
					if (pageNum > pl.getTotalPage())
						pageNum = pl.getTotalPage();
					pl.setCurrentPage(pageNum);
				}
			}

			List<?> result = null;

			if (pl == null || pl.getTotalCount() != 0) {
				queryObject = factory.getCurrentSession().createQuery(query);
				for (String param : paramsInUse)
					HibernateDaoBaseMethod.applyNamedParameterToQuery(
							queryObject, param, values.get(param));
				if (paging) {
					queryObject.setFirstResult((pageNum - 1) * pageSize);
					queryObject.setMaxResults(pageSize);
				}
				result = queryObject.list();
			}

			switch (retType) {
			case RETURN_LIST:
				return result;
			case RETURN_PAGE:
				if (result == null)
					result = new ArrayList<T>();
				pl.setList(result);
				return pl;
			case RETURN_VOID:
				return null;
			case RETURN_OBJECT:
				return result.size() > 0 ? result.get(0) : null;
			}
		case TYPE_UPDATE:
		case TYPE_DELETE:
			queryObject = factory.getCurrentSession().createQuery(query);
			for (String param : paramsInUse)
				HibernateDaoBaseMethod.applyNamedParameterToQuery(queryObject,
						param, values.get(param));

			if (retType == RETURN_VOID)
				return null;

			return queryObject.executeUpdate();
		}

		return null;
	}
}
