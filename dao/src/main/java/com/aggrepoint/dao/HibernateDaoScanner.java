package com.aggrepoint.dao;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
public class HibernateDaoScanner extends ClassPathBeanDefinitionScanner {
	List<IFunc> functions;

	public HibernateDaoScanner(BeanDefinitionRegistry registry,
			ResourceLoader resourceLoader, BeanNameGenerator beanNameGenerator,
			List<IFunc> funcs) {
		super(registry, false);

		functions = funcs;
		setResourceLoader(resourceLoader);
		setBeanNameGenerator(beanNameGenerator);

		addIncludeFilter(new AssignableTypeFilter(HibernateDao.class) {
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
			logger.warn("No HibernateDao was found in '"
					+ Arrays.toString(basePackages)
					+ "' package. Please check your configuration.");
		} else {
			for (BeanDefinitionHolder holder : beanDefinitions) {
				GenericBeanDefinition definition = (GenericBeanDefinition) holder
						.getBeanDefinition();

				if (logger.isDebugEnabled()) {
					logger.debug("Creating HibernateDaoFactoryBean with name '"
							+ holder.getBeanName() + "' and '"
							+ definition.getBeanClassName() + "' daoInterface");
				}

				// the mapper interface is the original class of the bean
				// but, the actual class of the bean is HibernateDaoFactoryBean
				definition.getPropertyValues().add("daoInterface",
						definition.getBeanClassName());
				definition.getPropertyValues().add("funcs", functions);
				definition.setBeanClass(HibernateDaoFactoryBean.class);

				definition
						.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			}
		}

		return beanDefinitions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isCandidateComponent(
			AnnotatedBeanDefinition beanDefinition) {
		return (beanDefinition.getMetadata().isInterface() && beanDefinition
				.getMetadata().isIndependent());
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
			logger.warn("Skipping HibernateDaoFactoryBean with name '"
					+ beanName + "' and '" + beanDefinition.getBeanClassName()
					+ "' daoInterface"
					+ ". Bean already defined with the same name!");
			return false;
		}
	}
}
