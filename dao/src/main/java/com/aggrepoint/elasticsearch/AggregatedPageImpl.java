package com.aggrepoint.elasticsearch;

import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.FacetedPageImpl;

/**
 * @author Jim
 *
 * @param <T>
 */
public class AggregatedPageImpl<T> extends FacetedPageImpl<T> implements
		AggregatedPage<T> {
	private static final long serialVersionUID = 1L;

	private Aggregations aggs;

	private SearchResponse resp;

	public AggregatedPageImpl(List<T> content) {
		super(content);
	}

	public AggregatedPageImpl(SearchResponse response,
			FacetedPage<T> facetedPage, Pageable pageable) {
		super(facetedPage.getContent(), pageable, facetedPage
				.getTotalElements(), facetedPage.getFacets());
		resp = response;
		aggs = response.getAggregations();
	}

	@Override
	public Aggregations getAggregations() {
		return aggs;
	}

	@Override
	public SearchResponse getResponse() {
		return resp;
	}
}
