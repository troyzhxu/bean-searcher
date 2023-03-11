module mydemo {

    requires org.apache.tomcat.embed.core;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.context;
    requires spring.webmvc;
    requires spring.web;
    requires spring.core;
    requires bean.searcher;
    requires data.core;
    requires xjsonkit.api;
    requires com.fasterxml.jackson.annotation;

    opens com.example;
    opens com.example.config;
    opens com.example.controller;
    opens com.example.sbean;
    opens db;
    opens templates;

}