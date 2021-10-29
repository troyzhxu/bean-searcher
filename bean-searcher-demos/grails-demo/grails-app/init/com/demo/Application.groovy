package com.demo

import com.ejlchina.searcher.Searcher
import com.ejlchina.searcher.SearcherBuilder
import com.ejlchina.searcher.implement.MainSearchParamResolver
import com.ejlchina.searcher.implement.MainSearchSqlExecutor
import com.ejlchina.searcher.implement.pagination.PageNumPagination
import com.ejlchina.searcher.implement.pagination.Pagination
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
        def searchParamResolver = new MainSearchParamResolver()
        searchParamResolver.setPagination(new PageNumPagination())
        return SearcherBuilder.builder()
                .configSearchSqlExecutor(executor)
                .configSearchParamResolver(searchParamResolver)
                .build()
    }


}