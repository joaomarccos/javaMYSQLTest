package com.ef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Parser {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Parser.class, args);
		ctx.getBean(Cli.class).init(args);
	}
}
