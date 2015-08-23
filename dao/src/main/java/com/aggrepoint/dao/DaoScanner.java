package com.aggrepoint.dao;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.SessionFactory;
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

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class DaoScanner extends ClassPathBeanDefinitionScanner {
	private List<IFunc> functions;
	private EntityManager entityManager;
	private SessionFactory sessionFactory;

	public DaoScanner(BeanDefinitionRegistry registry,
			ResourceLoader resourceLoader, BeanNameGenerator beanNameGenerator,
			List<IFunc> funcs, EntityManager manager, SessionFactory factory) {
		super(registry, false);

		functions = funcs;
		entityManager = manager;
		sessionFactory = factory;
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
				definition.getPropertyValues().add("entityManager",
						entityManager);
				definition.getPropertyValues().add("sessionFactory",
						sessionFactory);
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
