package com.aggrepoint.winlet.elasticsearch;

import java.io.IOException;

import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Jackson无法序列化Elasticsearch的InternalTerms类，因此需要自行实现serializer
 * 
 * @author Jim
 *
 */
public class InternalTermsSerializer extends JsonSerializer<InternalTerms> {
	@Override
	public void serialize(InternalTerms value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeStringField("name", value.getName());
		jgen.writeArrayFieldStart("buckets");
		for (Bucket bucket : value.getBuckets()) {
			jgen.writeStartObject();
			jgen.writeStringField("term", bucket.getKey());
			jgen.writeNumberField("count", bucket.getDocCount());
			Aggregations aggs = bucket.getAggregations();
			if (aggs != null)
				jgen.writeObjectField("aggs", aggs);
			jgen.writeEndObject();
		}
		jgen.writeEndArray();
		jgen.writeEndObject();
	}
}
