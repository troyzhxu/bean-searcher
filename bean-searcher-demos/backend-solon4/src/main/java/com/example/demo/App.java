package com.example.demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.web.cors.CrossFilter;

@SolonMain
public class App {

    public static void main(String[] args) {
        Solon.start(App.class, args, app -> app.router().filter(-1, new CrossFilter().allowedOrigins("*")));
    }

}
