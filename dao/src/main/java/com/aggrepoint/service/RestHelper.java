package com.aggrepoint.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.MethodParametersAttribute;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class RestHelper {
	/**
	 * <pre>
	 * 将Dao对象方法上的Rest相关注解复制到ServiceImpl方法上。如果没有指定路径，则用方法名称作为路径
	 * 只支持GetMapping, PostMapping和RequestMapping
	 * </pre>
	 * 
	 * @param daoMethod
	 * @param newMethod
	 */
	static void copyRestAnnotations(CtMethod daoMethod, CtMethod newMethod) {
		ConstPool cp = newMethod.getMethodInfo().getConstPool();

		for (Object attr : daoMethod.getMethodInfo().getAttributes()) {
			if (attr instanceof AnnotationsAttribute) {
				AnnotationsAttribute aa = (AnnotationsAttribute) attr;
				List<Annotation> toCopy = new ArrayList<>();

				for (Annotation anno : aa.getAnnotations()) {
					String typeName = anno.getTypeName();
					if (("org.springframework.web.bind.annotation.GetMapping".equals(typeName)
							|| "org.springframework.web.bind.annotation.PostMapping".equals(typeName)
							|| "org.springframework.web.bind.annotation.RequestMapping".equals(typeName))) {
						Annotation annot = new Annotation(typeName, cp);
						toCopy.add(annot);

						if (anno.getMemberValue("value") == null) {
							ArrayMemberValue arrayValue = new ArrayMemberValue(cp);
							arrayValue.setValue(new MemberValue[] { new StringMemberValue(newMethod.getName(), cp) });
							annot.addMemberValue("value", arrayValue);
						}

						Set<?> names = anno.getMemberNames();
						if (names != null)
							for (Object memberName : names) {
								String n = memberName.toString();
								annot.addMemberValue(n, anno.getMemberValue(n));
							}
					}
				}

				if (toCopy.size() > 0) {
					AnnotationsAttribute ar = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
					toCopy.forEach(ar::addAnnotation);
					newMethod.getMethodInfo().addAttribute(ar);
				}
			}
		}
	}

	/**
	 * 对ServiceImpl依照ServiceDao加入的方法启用REST访问
	 */
	static boolean enableRest(CtMethod method, String domainTypeName) {
		MethodInfo mi = method.getMethodInfo();
		String restMethod = null;
		String url = null;
		boolean requestBody = false;
		boolean pathVar = false;
		String param = null;

		switch (method.getName() + mi.getDescriptor()) {
		case "create(Ljava/lang/Object;)Ljava/io/Serializable;":
			restMethod = "org.springframework.web.bind.annotation.PostMapping";
			url = "create";
			requestBody = true;
			param = "entity";
			break;
		case "createOrUpdate(Ljava/lang/Object;)V":
			restMethod = "org.springframework.web.bind.annotation.PostMapping";
			url = "createOrUpdate";
			param = "entity";
			requestBody = true;
			break;
		case "find(Ljava/io/Serializable;)Ljava/lang/Object;":
			restMethod = "org.springframework.web.bind.annotation.GetMapping";
			url = "find/{id}";
			param = "id";
			pathVar = true;
			break;
		case "find()Ljava/util/List;":
			restMethod = "org.springframework.web.bind.annotation.GetMapping";
			url = "findAll";
			break;
		case "update(Ljava/lang/Object;)V":
			restMethod = "org.springframework.web.bind.annotation.PostMapping";
			url = "update";
			param = "entity";
			requestBody = true;
			break;
		case "delete(Ljava/lang/Object;)V":
			restMethod = "org.springframework.web.bind.annotation.PostMapping";
			url = "delete";
			param = "entity";
			requestBody = true;
			break;
		}

		if (restMethod == null)
			return false;

		mi.setDescriptor(mi.getDescriptor().replace("java/lang/Object", domainTypeName));
		// 暂时只支持Long
		mi.setDescriptor(mi.getDescriptor().replace("java/io/Serializable", "java/lang/Long"));
		ConstPool cp = mi.getConstPool();

		AnnotationsAttribute ar = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation(restMethod, cp);
		ArrayMemberValue arrayValue = new ArrayMemberValue(cp);
		arrayValue.setValue(new MemberValue[] { new StringMemberValue(url, cp) });
		annot.addMemberValue("value", arrayValue);
		ar.addAnnotation(annot);
		mi.addAttribute(ar);

		if (requestBody) {
			Annotation[][] anns = new Annotation[1][];
			anns[0] = new Annotation[1];
			anns[0][0] = new Annotation("org.springframework.web.bind.annotation.RequestBody", cp);
			ParameterAnnotationsAttribute paa = new ParameterAnnotationsAttribute(cp,
					"RuntimeVisibleParameterAnnotations");
			paa.setAnnotations(anns);
			mi.addAttribute(paa);
		}

		if (pathVar) {
			Annotation[][] anns = new Annotation[1][];
			anns[0] = new Annotation[1];
			annot = anns[0][0] = new Annotation("org.springframework.web.bind.annotation.PathVariable", cp);
			annot.addMemberValue("value", new StringMemberValue(param, cp));
			ParameterAnnotationsAttribute paa = new ParameterAnnotationsAttribute(cp,
					"RuntimeVisibleParameterAnnotations");
			paa.setAnnotations(anns);
			mi.addAttribute(paa);
		}

		if (param != null) {
			MethodParametersAttribute mpa = new MethodParametersAttribute(cp, new String[] { param }, new int[] { 0 });
			mi.addAttribute(mpa);
		}

		return true;
	}

	/**
	 * 如果ServiceImpl中的方法没有参数属性而Dao对应的方法有，则拷贝过来
	 */
	static void copyParamAttrs(CtMethod daoMethod, CtMethod newMethod) {
		MethodParametersAttribute ma = (MethodParametersAttribute) daoMethod.getMethodInfo()
				.getAttribute("MethodParameters");
		if (ma != null) {
			MethodInfo methodInfo = newMethod.getMethodInfo();
			if (methodInfo.getAttribute("MethodParameters") == null)
				methodInfo.addAttribute(ma.copy(methodInfo.getConstPool(), null));
		}
	}
}
