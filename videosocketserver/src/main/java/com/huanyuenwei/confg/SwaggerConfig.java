package com.huanyuenwei.confg;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.github.xiaoymin.swaggerbootstrapui.model.OrderExtensions;
import com.google.common.collect.Lists;
import com.huanyuenwei.Entuty.DeveloperApiInfo;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import java.util.List;


@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig{


      private final TypeResolver typeResolver;


    @Autowired
    public SwaggerConfig(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }


    @Bean
    public Docket groupRestApi() {
        List<ResolvedType> list= Lists.newArrayList();

        //SpringAddtionalModel springAddtionalModel= springAddtionalModelService.scan("com.swagger.bootstrap.ui.demo.extend");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .groupName("1.0版本接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.huanyuenwei"))
                .paths(PathSelectors.any())
                .build().useDefaultResponseMessages(false)
                .additionalModels(typeResolver.resolve(DeveloperApiInfo.class)).extensions(Lists.newArrayList(new OrderExtensions(2)))
                .securityContexts(Lists.newArrayList(securityContext())).securitySchemes(Lists.<SecurityScheme>newArrayList(apiKey()));
    }


    private ApiInfo groupApiInfo(){
        DeveloperApiInfoExtension apiInfoExtension=new DeveloperApiInfoExtension();

        apiInfoExtension.addDeveloper(new DeveloperApiInfo("代志华","18010091127@163.com","Java"));


        return new ApiInfoBuilder()
                .title("视频管理")
                .description("<div style='font-size:14px;color:red;'>视频接口api调用</div>")
                .termsOfServiceUrl("http://huanyuenwei.com/")
                .contact("daizhihua@huanyuenwei.com")
                .version("1.0")
                .extensions(Lists.newArrayList(apiInfoExtension))
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }
    /*private SecurityContext securityContext1() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth1())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }*/

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
    }

    private ApiKey apiKey() {

        return new ApiKey("BearerToken", "Authorization", "header");
    }


}
