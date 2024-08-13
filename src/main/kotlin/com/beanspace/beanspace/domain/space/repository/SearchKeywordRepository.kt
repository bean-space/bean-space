package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.SearchKeyword
import org.springframework.data.jpa.repository.JpaRepository

interface SearchKeywordRepository : JpaRepository<SearchKeyword, Long>, SearchKeywordQueryDslRepository
