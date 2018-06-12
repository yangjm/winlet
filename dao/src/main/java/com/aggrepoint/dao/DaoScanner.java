package com.aggrepoint.dao;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class DaoScanner extends ClassPathBeanDefinitionScanner {
	private static final Log logger = LogFactory.getLog(DaoScanner.class);

	private List<IFunc> functions;
	private HashMap<String, String> daoSessionFactoryNames = new HashMap<>();
	private HashMap<String, String> daoEntityManagerNames = new HashMap<>();
	private String entityManagerName;
	private String sessionFactoryName;

	public DaoScanner(BeanDefinitionRegistry registry,
			ResourceLoader resourceLoader, BeanNameGenerator beanNameGenerator,
			List<IFunc> funcs, String defaultEntityManager,
			String defaultSessionFactory, List<DaoDataSource> dataSources) {
		super(registry, false);

		functions = funcs;
		entityManagerName = defaultEntityManager;
		sessionFactoryName = defaultSessionFactory;
		setResourceLoader(resourceLoader);
		setBeanNameGenerator(beanNameGenerator);

		addIncludeFilter(new AssignableTypeFilter(DaoService.class) {
			@Override
			protected boolean matchClassName(String className) {
				return false;
			}
		});

		// exclude package-info.java
		addExcludeFilter(new TypeFilter() {
			public boolean match(MetadataReader metadataReader,
					MetadataReaderFactory metadataReaderFactory)
					throws IOException {
				String className = metadataReader.getClassMetadata()
						.getClassName();
				return className.endsWith("package-info");
			}
		});

		if (dataSources != null)
			for (DaoDataSource ds : dataSources) {
				if (StringUtils.isEmpty(ds.getClassNames())) {
					logger.warn("className not specified for DaoDataSource");
					continue;
				}
				String sf = ds.getSessionFactoryName();
				String em = ds.getEntityManagerName();
				if (StringUtils.isEmpty(sf) && StringUtils.isEmpty(em)) {
					logger.warn("Both sessionFactoryName and entityManagerName are not specified for DaoDataSource");
					continue;
				}

				for (String str : ds.getClassNames().split(",")) {
					str = str.trim();
					if (sf != null)
						daoSessionFactoryNames.put(str, sf);
					if (em != null)
						daoEntityManagerNames.put(str, em);
				}
			}
	}

	/**
	 * Calls the parent search that will search and register all the candidates.
	 * Then the registered objects are post processed to set them as
	 * MapperFactoryBeans
	 */
	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			logger.warn("No DaoService was found in '"
					+ Arrays.toString(basePackages)
					+ "' package. Please check your configuration.");
		} else {
			for (BeanDefinitionHolder holder : beanDefinitions) {
				GenericBeanDefinition definition = (GenericBeanDefinition) holder
						.getBeanDefinition();

				if (logger.isDebugEnabled()) {
					logger.debug("Creating DaoFactoryBean with name '"
							+ holder.getBeanName() + "' and '"
							+ definition.getBeanClassName() + "' daoInterface");
				}

				// the mapper interface is the original class of the bean
				// but, the actual class of the bean is HibernateDaoFactoryBean
				definition.getPropertyValues().add("daoInterface",
						definition.getBeanClassName());
				definition.getPropertyValues().add("funcs", functions);
				String className = definition.getBeanClassName();
				int idx = className.lastIndexOf(".");
				if (idx > 0)
					className = className.substring(idx + 1);
				String em = daoEntityManagerNames.get(className);
				if (em == null)
					em = entityManagerName;
				String sf = daoSessionFactoryNames.get(className);
				if (sf == null)
					sf = sessionFactoryName;
				definition.getPropertyValues().add("entityManagerFactoryName", em);
				definition.getPropertyValues().add("sessionFactoryName", sf);
				definition.setBeanClass(DaoFactoryBean.class);

				definition
						.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			}
		}

		return beanDefinitions;
	}

	static final String DAO_SERVICE_NAME = DaoService.class.getName();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isCandidateComponent(
			AnnotatedBeanDefinition beanDefinition) {
		if (!beanDefinition.getMetadata().isInterface()
				|| !beanDefinition.getMetadata().isIndependent())
			return false;

		String[] intfs = beanDefinition.getMetadata().getInterfaceNames();
		if (intfs == null || intfs.length != 1)
			return false;

		return DAO_SERVICE_NAME.equals(intfs[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkCandidate(String beanName,
			BeanDefinition beanDefinition) throws IllegalStateException {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		} else {
			logger.warn("Skipping DaoFactoryBean with name '" + beanName
					+ "' and '" + beanDefinition.getBeanClassName()
					+ "' daoInterface"
					+ ". Bean already defined with the same name!");
			return false;
		}
	}
}
