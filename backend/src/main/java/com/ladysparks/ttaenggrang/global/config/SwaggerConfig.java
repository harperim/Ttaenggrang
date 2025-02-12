package com.ladysparks.ttaenggrang.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private Info apiInfo() {
        return new Info()
                    .title("땡그랑 API")
                    .description("땡그랑 API 명세서")
                    .version("v1.0")
                    .contact(new Contact().name("LadySparks")
                            .email("www.ladysparks.com")
                            .url("suhmiji@gmail.com"))
                    .license(new License()
                            .name("License of API")
                            .url("API license URL"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/api"))
                .addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(apiInfo());
    }

    @Bean
    public GroupedOpenApi teacherApi() {
        return GroupedOpenApi.builder()
                .group("Teacher")
                .pathsToMatch("/teachers/**", "/salaries/**")
                .build();
    }

    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
                .group("Student")
                .pathsToMatch("/students/**")
                .build();
    }

//    @Bean
//    public GroupedOpenApi savingsGoalApi() {
//        return GroupedOpenApi.builder()
//                .group("savings-goal")
//                .pathsToMatch("/savings-goals/**")
//                .build();
//    }

    @Bean
    public GroupedOpenApi bankApi() {
        return GroupedOpenApi.builder()
                .group("Bank")
                .pathsToMatch("/bank-accounts/**", "/bank-transactions/**", "/savings-products/**", "/savings-subscriptions/**", "/savings-deposits/**")
                .build();
    }

    @Bean
    public GroupedOpenApi itemApi() {
        return GroupedOpenApi.builder()
                .group("Item")
                .pathsToMatch("/item-products/**", "/item-transactions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi stockApi() {
        return GroupedOpenApi.builder()
                .group("Stock")
                .pathsToMatch("/stocks/**")
                .build();
    }

    @Bean
    public GroupedOpenApi etfApi() {
        return GroupedOpenApi.builder()
                .group("Etf")
                .pathsToMatch("/etfs/**")
                .build();
    }

    @Bean
    public GroupedOpenApi newsApi() {
        return GroupedOpenApi.builder()
                .group("News")
                .pathsToMatch("/news/**")
                .build();
    }

    @Bean
    public GroupedOpenApi weeklyReportApi() {
        return GroupedOpenApi.builder()
                .group("Weekly-report")
                .pathsToMatch("/weekly-report/**")
                .build();
    }

    @Bean
    public GroupedOpenApi taxApi() {
        return GroupedOpenApi.builder()
                .group("Tax")
                .pathsToMatch("/taxes/**", "/tax-payments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi voteApi() {
        return GroupedOpenApi.builder()
                .group("Vote")
                .pathsToMatch("/votes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fcmApi() {
        return GroupedOpenApi.builder()
                .group("Notification")
                .pathsToMatch("/notifications/**")
                .build();
    }
}