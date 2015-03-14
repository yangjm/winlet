package com.aggrepoint.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.domain.Page;

/**
 * 
 * @author Jim
 *
 * @param <T>
 */
public interface AggregatedPage<T> extends Page<T> {
	SearchResponse getResponse();
	Aggregations getAggregations();
}
