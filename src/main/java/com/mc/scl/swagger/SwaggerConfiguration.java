package com.mc.scl.swagger;

import com.mc.scl.util.CommonUtility;
import com.mc.scl.util.VariablesConstant;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Autowired
    private CommonUtility commonUtility;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.name:MC Shared Code Library API}")
    private String appName;

    @Bean
    public OpenAPI customOpenAPI() {
        Map<Object, Object> gitProperties = commonUtility.readGitProperties();
        
        String description = buildDescription(gitProperties);
        
        return new OpenAPI()
                .info(new Info()
                        .title(appName)
                        .version(appVersion)
                        .description(description)
                        .contact(new Contact()
                                .name("MC Development Team")
                                .email("dev@mc.com")
                                .url("https://mc.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("API-KEY")
                                .description("API Key for client authentication (clientCode and clientSecret)"))
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Bearer token for authenticated user operations")));

    }

    /**
     * Builds the API description with git information
     * @param gitProperties Map containing git properties
     * @return Formatted description string with git information
     */
    private String buildDescription(Map<Object, Object> gitProperties) {
        StringBuilder description = new StringBuilder();
        description.append("This page lists all the REST APIs for MC Shared Code Library Application.");
        
        if (!gitProperties.containsKey(VariablesConstant.RESPONSE)) {
            description.append("\n<div style=\"font-size: 12px;color: #3b4151;margin-top: -12px;font-weight: 300 !important;font-family: Source Code Pro,monospace\">");
            description.append("[ BranchName: ").append(gitProperties.get(VariablesConstant.GIT_BRANCH)).append(" ]</br>");
            description.append("[ BuildTime: ").append(gitProperties.get(VariablesConstant.GIT_BUILD_TIME)).append(" ]</br>");
            description.append("[ CommitTime: ").append(gitProperties.get(VariablesConstant.GIT_COMMIT_TIME)).append(" ]");
            description.append("</div>");
        }
        
        return description.toString();
    }

    /**
     * Group for Client Resource APIs - requires API Key authentication
     * This creates a separate OpenAPI spec accessible via dropdown in Swagger UI
     */
    @Bean
    public GroupedOpenApi clientResourceApi() {
        return GroupedOpenApi.builder()
                .group("Client Resource")
                .displayName("Client Resource APIs")
                .pathsToMatch("/client/**")
                .build();
    }

    /**
     * Group for User Resource APIs - requires Bearer token authentication  
     * This creates a separate OpenAPI spec accessible via dropdown in Swagger UI
     */
    @Bean
    public GroupedOpenApi userResourceApi() {
        return GroupedOpenApi.builder()
                .group("User Resources")
                .displayName("User Resource APIs")
                .pathsToMatch("/postLogin/**")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}