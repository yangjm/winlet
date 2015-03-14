package com.aggrepoint.winlet.elasticsearch;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.InternalAggregations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Jackson无法序列化Elasticsearch的InternalAggregations类，因此需要自行实现serializer
 * 
 * @author Jim
 *
 */
public class InternalAggregationsSerializer extends
		JsonSerializer<InternalAggregations> {
	@Override
	public void serialize(InternalAggregations value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		Map<String, Aggregation> map = value.asMap();

		jgen.writeStartObject();
		for (String key : map.keySet())
			jgen.writeObjectField(key, map.get(key));
		jgen.writeEndObject();
	}
}
