package com.parko.zkcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.parko.system.SystemApplication;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableCaching
@ComponentScan({"com.parko.redis.utils","com.parko.system","com.parko.zkcenter"})
public class ZkCenterApplication  extends SpringBootServletInitializer
{
	  @Override
      protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
          return application.sources(ZkCenterApplication.class);
      }
    public static void main( String[] args )
    {
        System.out.println( "Hello World !" );
        SpringApplication.run(ZkCenterApplication.class, args);
    }
}
