package com.beanspace.beanspace.domain.image.repository

import com.beanspace.beanspace.domain.image.model.PhotoPost
import org.springframework.data.jpa.repository.JpaRepository

interface PhotoPostRepository : JpaRepository<PhotoPost, Long>