package com.aggrepoint.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.FacetedPage;

/**
 * 目前版本的Spring Data Elasticsearch (1.0.0) 不支持提取aggregation结果。添加对Aggregation的支持。
 * 
 * @author Jim
 */
public class AggregateResultMapper extends DefaultResultMapper {
	@Override
	public <T> FacetedPage<T> mapResults(SearchResponse response,
			Class<T> clazz, Pageable pageable) {
		FacetedPage<T> result = super.mapResults(response, clazz, pageable);
		return new AggregatedPageImpl<T>(response, result, pageable);
	}
}
