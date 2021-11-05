package com.demo

import com.demo.sbean.EmployeeBean
import com.ejlchina.searcher.BeanSearcher
import grails.converters.JSON

class EmployeeController {

    BeanSearcher beanSearcher

    def index() {
        render beanSearcher.search(EmployeeBean, params) as JSON
    }

}
