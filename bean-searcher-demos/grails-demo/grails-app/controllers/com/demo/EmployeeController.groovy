package com.demo

import com.demo.sbean.EmployeeBean
import com.ejlchina.searcher.Searcher
import grails.converters.JSON

class EmployeeController {

    Searcher searcher

    def index() {
        render searcher.search(EmployeeBean, params) as JSON
    }

}
