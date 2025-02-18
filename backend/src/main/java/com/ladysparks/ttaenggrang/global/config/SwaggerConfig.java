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
                .group("1. 교사")
                .pathsToMatch("/teachers/**")
                .pathsToExclude("/teachers/jobs/**", "/teachers/nations/**", "/**/dashboard/**")
                .build();
    }

    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
                .group("2. 학생")
                .pathsToMatch("/students/**")
                .pathsToExclude("/**/dashboard/**")
                .build();
    }

    @Bean
    public GroupedOpenApi homeApi() {
        return GroupedOpenApi.builder()
                .group("3. 홈 화면")
                .pathsToMatch("/**/dashboard/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fcmApi() {
        return GroupedOpenApi.builder()
                .group("4. 국가 관리")
                .pathsToMatch("/teachers/jobs/**", "/teachers/nations/**", "/votes/**", "/salaries/**")
                .build();
    }

    @Bean
    public GroupedOpenApi bankApi() {
        return GroupedOpenApi.builder()
                .group("5. 은행")
                .pathsToMatch("/bank-accounts/**", "/bank-transactions/**", "/savings-products/**", "/savings-subscriptions/**", "/savings-deposits/**", "/savings-payouts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi taxApi() {
        return GroupedOpenApi.builder()
                .group("6. 국세청")
                .pathsToMatch("/taxes/**", "/tax-payments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi stockApi() {
        return GroupedOpenApi.builder()
                .group("7. 주식")
                .pathsToMatch("/stocks/**", "/etfs/**", "/news/**", "/stock-market/**", "/stock-history/**", "/stock-transactions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi itemApi() {
        return GroupedOpenApi.builder()
                .group("8. 상품")
                .pathsToMatch("/item-products/**", "/item-transactions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi weeklyReportApi() {
        return GroupedOpenApi.builder()
                .group("9. 주간 통계 보고서")
                .pathsToMatch("/weekly-report/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("0. 관리자")
                .pathsToMatch("/admin/**")
                .build();
    }

}