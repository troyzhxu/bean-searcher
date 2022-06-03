package com.demo

import java.text.SimpleDateFormat

class BootStrap {

    def init = { servletContext ->

        def d1 = new Department(name: 'Finance').save()
        def d2 = new Department(name: 'Technical').save()
        def d3 = new Department(name: 'Market').save()
        def d4 = new Department(name: 'Manager').save()
        def d5 = new Department(name: 'Engineering').save()
        def d6 = new Department(name: 'Maintenance').save()

        def sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

        new Employee(department: d1, name: 'Jack', age: 22, entryDate: sdf.parse('2019-06-23 12:01:01')).save()
        new Employee(department: d2, name: 'Tom', age: 21, entryDate: sdf.parse('2019-06-24 12:01:01')).save()
        new Employee(department: d3, name: 'Alice', age: 23, entryDate: sdf.parse('2019-06-21 12:01:01')).save()
        new Employee(department: d4, name: 'Json', age: 24, entryDate: sdf.parse('2019-06-26 12:01:01')).save()
        new Employee(department: d5, name: 'Troy', age: 22, entryDate: sdf.parse('2019-06-27 10:01:01')).save()
        new Employee(department: d6, name: 'Aide', age: 23, entryDate: sdf.parse('2019-06-27 12:01:01')).save()
        new Employee(department: d1, name: 'Tang', age: 24, entryDate: sdf.parse('2019-06-28 12:01:01')).save()
        new Employee(department: d2, name: 'Shu', age: 25, entryDate: sdf.parse('2019-06-28 12:01:01')).save()
        new Employee(department: d3, name: 'Zhang', age: 26, entryDate: sdf.parse('2019-06-29 12:01:01')).save()
        new Employee(department: d4, name: 'XuKong', age: 28, entryDate: sdf.parse('2019-06-30 12:01:01')).save()
        new Employee(department: d5, name: 'ShuLai', age: 29, entryDate: sdf.parse('2019-06-31 12:01:01')).save()
        new Employee(department: d6, name: 'TangLi', age: 27, entryDate: sdf.parse('2019-06-25 12:01:01')).save()
        new Employee(department: d1, name: 'LiMei', age: 32, entryDate: sdf.parse('2019-06-05 12:01:01')).save()
        new Employee(department: d2, name: 'ZhangSan', age: 25, entryDate: sdf.parse('2019-08-04 12:01:01')).save()
        new Employee(department: d3, name: 'LiSi', age: 35, entryDate: sdf.parse('2019-08-03 12:01:01')).save()
        new Employee(department: d4, name: 'WanWu', age: 26, entryDate: sdf.parse('2019-09-02 12:01:01')).save()
        new Employee(department: d5, name: 'Erma', age: 27, entryDate: sdf.parse('2019-09-01 12:01:01')).save()
        new Employee(department: d6, name: 'LaoYu', age: 21, entryDate: sdf.parse('2019-08-30 12:01:01')).save()
        new Employee(department: d1, name: 'WeiLiu', age: 31, entryDate: sdf.parse('2019-07-20 12:01:01')).save()
        new Employee(department: d2, name: 'ZhuBaJie', age: 30, entryDate: sdf.parse('2019-07-10 12:01:01')).save()

    }
    def destroy = {
    }
}
