package com.aggrepoint.winlet.spring;

import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.common.text.BytesText;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.aggrepoint.winlet.elasticsearch.BytesTextSerializer;
import com.aggrepoint.winlet.elasticsearch.InternalAggregationsSerializer;
import com.aggrepoint.winlet.elasticsearch.InternalTermsSerializer;

/**
 * 支持序列化Elasticsearch的对象类
 * 
 * @author Jim
 */
public class ElasticsearchJacksonConfig {
	@Autowired
	private RequestMappingHandlerAdapter adapter;

	@PostConstruct
	public void config() {
		List<HttpMessageConverter<?>> converters = adapter
				.getMessageConverters();

		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.serializerByType(BytesText.class, new BytesTextSerializer());
		builder.serializerByType(InternalTerms.class,
				new InternalTermsSerializer());
		builder.serializerByType(InternalAggregations.class,
				new InternalAggregationsSerializer());
		/*
		 * builder.serializerByType(Bucket.class, new
		 * StringTermsBucketSerializer());
		 * builder.serializerByType(InternalAggregations.class, new
		 * InternalAggregationsSerializer());
		 */

		converters.add(0,
				new MappingJackson2HttpMessageConverter(builder.build()));
	}
}