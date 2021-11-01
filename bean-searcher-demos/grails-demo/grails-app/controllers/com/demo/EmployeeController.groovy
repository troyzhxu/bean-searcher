package com.demo

import com.demo.sbean.EmployeeBean
import com.ejlchina.searcher.Searcher
import grails.converters.JSON

class EmployeeController {

    Searcher searcher

    def index() {
        render searcher.search(EmployeeBean, params) as JSON
    }

    def test() {
        def employees = Employee.findAllByNameIlike("S")
        println(employees)
        render employees as JSON
    }

}
