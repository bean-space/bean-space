package com.beanspace.beanspace

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.ZoneId
import java.util.TimeZone

@EnableJpaAuditing
@SpringBootApplication
class BeanSpaceApplication

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")))
    runApplication<BeanSpaceApplication>(*args)
}
