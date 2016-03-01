package com.koushik.bits.api;

import com.koushik.bits.api.filter.StatsFilter;
import com.koushik.bits.api.filter.ValidationFilter;
import com.koushik.bits.common.constants.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"com.koushik.bits.api"})
@EnableConfigurationProperties
public class Application extends SpringBootServletInitializer
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder)
    {
        return builder.sources(Application.class);
    }

    @Bean
    public FilterRegistrationBean auditLogFilterRegistration(ApplicationContext context, MessageSource messageSource)
    {
        System.out.println("****In filter reg bean");
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        StatsFilter statsFilter = new StatsFilter(context, messageSource);
        registrationBean.setFilter(statsFilter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean validationFilterRegistration(MessageSource messageSource)
    {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        ValidationFilter validationFilter = new ValidationFilter(messageSource);
        registrationBean.setFilter(validationFilter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add(Constants.COMPUTE_ANALYTICS_PATH);
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }
}
