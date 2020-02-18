package com.aws.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.context.WebApplicationContext;

@SpringBootApplication
@EnableAsync
@ComponentScan({"com.aws.s3"})
public class AwsS3JavaApplication extends SpringBootServletInitializer implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(AwsS3JavaApplication.class);

	public static void main(String[] args) {

		System.setProperty("server.servlet.context-path", "/AwsS3Java-0.0.1");
		SpringApplication.run(AwsS3JavaApplication.class, args);
		logger.debug("---***************************<<---- APPLICATION STARTED---->>***************************---");
	}

	@Override
	public void run(String... args) throws Exception {
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder app){		
		logger.debug("zzzzzzzzzzzzzzzzzzz");		
		//app.run();
		return app.sources(AwsS3JavaApplication.class);
	}
	
	

}
