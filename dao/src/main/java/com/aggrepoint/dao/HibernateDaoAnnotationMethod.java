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
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import com.aggrepoint.dao.annotation.Delete;
import com.aggrepoint.dao.annotation.Find;
import com.aggrepoint.dao.annotation.Like;
import com.aggrepoint.dao.annotation.PageNum;
import com.aggrepoint.dao.annotation.PageSize;
import com.aggrepoint.dao.annotation.Param;
import com.aggrepoint.dao.annotation.Replace;
import com.aggrepoint.dao.annotation.Update;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class HibernateDaoAnnotationMethod<T> implements HibernateDaoMethod {
	static Pattern P_PART = Pattern.compile("#\\{(.+?)\\}");
	static Pattern P_COUNT = Pattern.compile(
			"(select[\\s\\w\\.,]+?)?(from.+?)(order\\s+by[\\s\\w\\.,]+)?$",
			Pattern.CASE_INSENSITIVE);
	static Pattern P_NUM_PARAM = Pattern.compile(":(\\d+)");

	static final int TYPE_FIND = 0;
	static final int TYPE_UPDATE = 1;
	static final int TYPE_DELETE = 2;
	static final int TYPE_FIND_SQL = 3;
	static final int TYPE_UPDATE_SQL = 4;
	static final int TYPE_DELETE_SQL = 5;
	static final int RETURN_VOID = 0;
	static final int RETURN_LIST = 1;
	static final int RETURN_PAGE = 2;
	static final int RETURN_OBJECT = 3;

	SessionFactory factory;
	Method method;
	Annotation[][] pans;
	int type;
	int retType;
	String hql;
	Class<?> entityClass;
	Vector<QueryPart> parts = new Vector<QueryPart>();
	HashSet<String> params = new HashSet<String>();
	Hashtable<String, Like> likes = new Hashtable<String, Like>();
	Hashtable<String, Replace> replaces = new Hashtable<String, Replace>();
	Hashtable<String, Function> funcs = new Hashtable<String, Function>();
	Hashtable<String, Integer> positions = new Hashtable<String, Integer>();
	int pageNumIdx = -1;
	int pageSizeIdx = -1;

	public HibernateDaoAnnotationMethod(Method method, Annotation ann,
			List<IFunc> funcs, SessionFactory factory) {
		this.factory = factory;
		this.method = method;

		if (ann.annotationType() == Find.class) {
			if (!"".equals(((Find) ann).sql())) {
				type = TYPE_FIND_SQL;
				hql = ((Find) ann).sql();

				entityClass = ((Find) ann).entity();
				if (entityClass == Object.class)
					entityClass = null;
			} else {
				type = TYPE_FIND;
				hql = ((Find) ann).value();
			}
		} else if (ann.annotationType() == Update.class) {
			if (!"".equals(((Update) ann).sql())) {
				type = TYPE_UPDATE_SQL;
				hql = ((Update) ann).sql();
			} else {
				type = TYPE_UPDATE;
				hql = ((Update) ann).value();
			}
		} else if (ann.annotationType() == Delete.class) {
			if (!"".equals(((Delete) ann).sql())) {
				type = TYPE_DELETE_SQL;
				hql = ((Delete) ann).sql();
			} else {
				type = TYPE_DELETE;
				hql = ((Delete) ann).value();
			}
		} else {
			throw new IllegalArgumentException("Unsupported annotation '" + ann
					+ "' on " + method.getDeclaringClass().getName() + "."
					+ method.getName());
		}

		Function[] functions = null;
		try {
			functions = Function.getFunctions(funcs, hql);
		} catch (FunctionNotFoundException e) {
			throw new IllegalArgumentException("Undefined function '"
					+ e.getName() + "' used by "
					+ method.getDeclaringClass().getName() + "."
					+ method.getName());
		}
		if (functions != null) {
			for (Function f : functions)
				this.funcs.put(f.getMatch(), f);

			// 为避免替换数字参数时影响到function的参数，先将function转换为特殊的字符串
			for (int i = 0; i < functions.length; i++)
				hql = hql.replaceAll(Pattern.quote(functions[i].match), "!!!!!"
						+ i + "!!!!!");
		}

		// 替换数字参数
		hql = P_NUM_PARAM.matcher(hql).replaceAll(":p_$1");

		if (functions != null) {
			// 恢复function
			for (int i = 0; i < functions.length; i++)
				hql = hql.replaceAll(Pattern.quote("!!!!!" + i + "!!!!!"),
						functions[i].match);
		}

		pans = method.getParameterAnnotations();
		for (int i = 0; i < pans.length; i++) {
			if (pans[i].length == 0) {
				String name = "p_" + Integer.toString(i + 1);

				if (positions.containsKey(name))
					throw new IllegalArgumentException(
							"Duplicated param/like/replace name: '" + name
									+ "' on "
									+ method.getDeclaringClass().getName()
									+ "." + method.getName());

				params.add(name);
				positions.put(name, i);
			} else {
				for (int j = 0; j < pans[i].length; j++)
					if (pans[i][j].annotationType() == Param.class) {
						Param p = (Param) pans[i][j];

						String name = p.value();
						if (name.equals(""))
							name = "p_" + Integer.toString(i + 1);

						if (positions.containsKey(name))
							throw new IllegalArgumentException(
									"Duplicated param/like/replace name: '"
											+ name
											+ "' on "
											+ method.getDeclaringClass()
													.getName() + "."
											+ method.getName());

						params.add(name);
						positions.put(name, i);
						break;
					} else if (pans[i][j].annotationType() == Like.class) {
						Like p = (Like) pans[i][j];

						String name = p.value();
						if (name.equals(""))
							name = "p_" + Integer.toString(i + 1);

						if (positions.containsKey(name))
							throw new IllegalArgumentException(
									"Duplicated param/like/replace name: '"
											+ name
											+ "' on "
											+ method.getDeclaringClass()
													.getName() + "."
											+ method.getName());

						likes.put(name, p);
						positions.put(name, i);
						break;
					} else if (pans[i][j].annotationType() == Replace.class) {
						Replace r = (Replace) pans[i][j];

						String name = r.value();
						if (name.equals(""))
							name = "p_" + Integer.toString(i + 1);

						if (positions.containsKey(name))
							throw new IllegalArgumentException(
									"Duplicated param/like/replace name: '"
											+ name
											+ "' on "
											+ method.getDeclaringClass()
													.getName() + "."
											+ method.getName());

						replaces.put(name, r);
						positions.put(name, i);
						break;
					} else if (pans[i][j].annotationType() == PageNum.class) {
						if (type != TYPE_FIND)
							throw new IllegalArgumentException(
									"@PageNum shouldn't be used on "
											+ method.getDeclaringClass()
													.getName() + "."
											+ method.getName());
						pageNumIdx = i;
						break;
					} else if (pans[i][j].annotationType() == PageSize.class) {
						if (type != TYPE_FIND)
							throw new IllegalArgumentException(
									"@PageSize shouldn't be used on "
											+ method.getDeclaringClass()
													.getName() + "."
											+ method.getName());
						pageSizeIdx = i;
						break;
					}
			}
		}

		int idx = 0;
		Matcher m = P_PART.matcher(hql);
		while (m.find()) {
			if (m.start() != idx)
				parts.add(new QueryPart(method, false, hql.substring(idx,
						m.start()), params, likes, replaces, this.funcs));
			parts.add(new QueryPart(method, true, m.group(1), params, likes,
					replaces, this.funcs));
			idx = m.end();
		}
		if (idx != hql.length())
			parts.add(new QueryPart(method, false, hql.substring(idx), params,
					likes, replaces, this.funcs));

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
		case TYPE_FIND_SQL:
			if (ret == List.class)
				retType = RETURN_LIST;
			else if (ret.getName().equals("void"))
				retType = RETURN_VOID;
			else
				retType = RETURN_OBJECT;
			break;
		case TYPE_UPDATE:
		case TYPE_DELETE:
		case TYPE_UPDATE_SQL:
		case TYPE_DELETE_SQL:
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

		for (String p : params.toArray(new String[params.size()]))
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
		for (String f : funcs.keySet())
			values.put(f, funcs.get(f).exec(method, args, pans));

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
		case TYPE_DELETE: {
			queryObject = factory.getCurrentSession().createQuery(query);
			for (String param : paramsInUse)
				HibernateDaoBaseMethod.applyNamedParameterToQuery(queryObject,
						param, values.get(param));

			int ret = queryObject.executeUpdate();

			if (retType == RETURN_VOID)
				return null;

			return ret;
		}
		case TYPE_FIND_SQL:
			SQLQuery q = factory.getCurrentSession().createSQLQuery(query);
			if (entityClass != null)
				q.addEntity(entityClass);

			queryObject = q;
			for (String param : paramsInUse)
				HibernateDaoBaseMethod.applyNamedParameterToQuery(queryObject,
						param, values.get(param));

			List<?> list = queryObject.list();

			switch (retType) {
			case RETURN_LIST:
				return list;
			case RETURN_VOID:
				return null;
			case RETURN_OBJECT:
				return list.size() > 0 ? list.get(0) : null;
			}
		case TYPE_UPDATE_SQL:
		case TYPE_DELETE_SQL: {
			queryObject = factory.getCurrentSession().createSQLQuery(query);
			for (String param : paramsInUse)
				HibernateDaoBaseMethod.applyNamedParameterToQuery(queryObject,
						param, values.get(param));

			int ret = queryObject.executeUpdate();

			if (retType == RETURN_VOID)
				return null;

			return ret;
		}
		}

		return null;
	}
}
