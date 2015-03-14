package com.aggrepoint.winlet.spring;

import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.StringValueResolver;

/**
 * It would be better to be able to extend
 * FormattingConversionServiceFactoryBean and override afterPropertiesSet(). But
 * attributes used by afterPropertiesSet() is not visible to sub class, so has
 * to copy the whole implementation.
 * 
 * @author Jim Yang
 */
public class WinletFormattingConversionServiceFactoryBean implements
		FactoryBean<FormattingConversionService>, EmbeddedValueResolverAware,
		InitializingBean {

	private Set<?> converters;

	private Set<?> formatters;

	private Set<FormatterRegistrar> formatterRegistrars;

	private boolean registerDefaultFormatters = true;

	private StringValueResolver embeddedValueResolver;

	private FormattingConversionService conversionService;

	/**
	 * Configure the set of custom converter objects that should be added.
	 * 
	 * @param converters
	 *            instances of any of the following:
	 *            {@link org.springframework.core.convert.converter.Converter},
	 *            {@link org.springframework.core.convert.converter.ConverterFactory}
	 *            ,
	 *            {@link org.springframework.core.convert.converter.GenericConverter}
	 */
	public void setConverters(Set<?> converters) {
		this.converters = converters;
	}

	/**
	 * Configure the set of custom formatter objects that should be added.
	 * 
	 * @param formatters
	 *            instances of {@link Formatter} or
	 *            {@link AnnotationFormatterFactory}
	 */
	public void setFormatters(Set<?> formatters) {
		this.formatters = formatters;
	}

	/**
	 * <p>
	 * Configure the set of FormatterRegistrars to invoke to register Converters
	 * and Formatters in addition to those added declaratively via
	 * {@link #setConverters(Set)} and {@link #setFormatters(Set)}.
	 * <p>
	 * FormatterRegistrars are useful when registering multiple related
	 * converters and formatters for a formatting category, such as Date
	 * formatting. All types related needed to support the formatting category
	 * can be registered from one place.
	 * <p>
	 * FormatterRegistrars can also be used to register Formatters indexed under
	 * a specific field type different from its own &lt;T&gt;, or when
	 * registering a Formatter from a Printer/Parser pair.
	 * 
	 * @see FormatterRegistry#addFormatterForFieldType(Class, Formatter)
	 * @see FormatterRegistry#addFormatterForFieldType(Class, Printer, Parser)
	 */
	public void setFormatterRegistrars(
			Set<FormatterRegistrar> formatterRegistrars) {
		this.formatterRegistrars = formatterRegistrars;
	}

	/**
	 * Indicate whether default formatters should be registered or not.
	 * <p>
	 * By default, built-in formatters are registered. This flag can be used to
	 * turn that off and rely on explicitly registered formatters only.
	 * 
	 * @see #setFormatters(Set)
	 * @see #setFormatterRegistrars(Set)
	 */
	public void setRegisterDefaultFormatters(boolean registerDefaultFormatters) {
		this.registerDefaultFormatters = registerDefaultFormatters;
	}

	@Override
	public void setEmbeddedValueResolver(
			StringValueResolver embeddedValueResolver) {
		this.embeddedValueResolver = embeddedValueResolver;
	}

	@Override
	public void afterPropertiesSet() {
		this.conversionService = new WinletDefaultFormattingConversionService(
				this.embeddedValueResolver, this.registerDefaultFormatters);
		ConversionServiceFactory.registerConverters(this.converters,
				this.conversionService);
		registerFormatters();
	}

	private void registerFormatters() {
		if (this.formatters != null) {
			for (Object formatter : this.formatters) {
				if (formatter instanceof Formatter<?>) {
					this.conversionService
							.addFormatter((Formatter<?>) formatter);
				} else if (formatter instanceof AnnotationFormatterFactory<?>) {
					this.conversionService
							.addFormatterForFieldAnnotation((AnnotationFormatterFactory<?>) formatter);
				} else {
					throw new IllegalArgumentException(
							"Custom formatters must be implementations of Formatter or AnnotationFormatterFactory");
				}
			}
		}
		if (this.formatterRegistrars != null) {
			for (FormatterRegistrar registrar : this.formatterRegistrars) {
				registrar.registerFormatters(this.conversionService);
			}
		}
		installFormatters(this.conversionService);
	}

	/**
	 * Subclasses may override this method to register formatters and/or
	 * converters. Starting with Spring 3.1 however the recommended way of doing
	 * that is to through FormatterRegistrars.
	 * 
	 * @see #setFormatters(Set)
	 * @see #setFormatterRegistrars(Set)
	 * @deprecated since Spring 3.1 in favor of
	 *             {@link #setFormatterRegistrars(Set)}
	 */
	@Deprecated
	protected void installFormatters(FormatterRegistry registry) {
	}

	@Override
	public FormattingConversionService getObject() {
		return this.conversionService;
	}

	@Override
	public Class<? extends FormattingConversionService> getObjectType() {
		return FormattingConversionService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
