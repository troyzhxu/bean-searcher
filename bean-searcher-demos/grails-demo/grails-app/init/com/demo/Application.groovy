package com.demo

import com.ejlchina.searcher.Searcher
import com.ejlchina.searcher.SearcherBuilder
import com.ejlchina.searcher.implement.MainSearchSqlExecutor
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean

import javax.sql.DataSource

@CompileStatic
class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }


    @Bean
    Searcher searcher(DataSource dataSource) {
        def executor = new MainSearchSqlExecutor(dataSource)
        return SearcherBuilder.builder()
                .configSearchSqlExecutor(executor)
                .build()
    }


}