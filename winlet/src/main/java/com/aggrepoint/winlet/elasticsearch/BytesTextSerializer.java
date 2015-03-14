package com.aggrepoint.winlet.elasticsearch;

import java.io.IOException;

import org.elasticsearch.common.text.BytesText;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Jackson无法序列化Elasticsearch的BytesText类，因此需要自行实现serializer
 * 
 * @author Jim
 *
 */
public class BytesTextSerializer extends JsonSerializer<BytesText> {
	@Override
	public void serialize(BytesText value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeString(value.string());
	}
}
