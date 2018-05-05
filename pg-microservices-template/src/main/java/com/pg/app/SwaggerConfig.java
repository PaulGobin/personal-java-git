package com.pg.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/******************************************
 * This class auto configure swagger and also expose your rest API documentation to http://xxxxxx:port/swagger-ui.html. The documentation headers such as the title, version etc are
 * pulled from the config service at start-up. If none is defined, then the global default is used.
 * 
 * @author pgobin
 *
 */

@Configuration
@EnableSwagger2
@RefreshScope
public class SwaggerConfig {

	private static final Logger log = Logger.getLogger(SwaggerConfig.class);

	@Value("${api.app.title}")
	private String __TITLE;
	@Value("${api.app.description}")
	private String __DESCRIPTION;
	@Value("${api.app.version}")
	private String __VERSION;

	@Value("${api.TermsAndCondition.URL}")
	private String __T_AND_C_URL;
	@Value("${api.contact.email}")
	private String __CONTACT_EMAIL;
	@Value("${api.license.info}")
	private String __LICENSE_INFO;

	@Value("${api.license.url}")
	private String __LICENSE_URL;

	@Value("${api.organization.name}")
	private String ORG_NAME;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${api.header.params.defs:-}")
	private String apiHeaderParamsDefs;

	public SwaggerConfig()
	{
		// TODO Auto-generated constructor stub
	}

	/*********************************************
	 * 
	 * @return
	 */
	@Bean
	public Docket api()
	{
		log.info("Building Swagger documentation...");
		List<Parameter> headerParameters = getHeaderParameters();
		if (headerParameters.isEmpty() == false)
		{
			return new Docket(DocumentationType.SWAGGER_2).globalOperationParameters(headerParameters).groupName(applicationName).select()
				.apis(RequestHandlerSelectors.basePackage("com.pg.app.controller")).paths(PathSelectors.any()).build().apiInfo(apiInfo());
		} else
		{
			return new Docket(DocumentationType.SWAGGER_2).groupName(applicationName).select().apis(RequestHandlerSelectors.basePackage("com.pg.app.controller")).paths(PathSelectors.any()).build()
				.apiInfo(apiInfo());
		}
	}

	/*******
	 * 
	 * @return
	 */
	private List<Parameter> getHeaderParameters()
	{

		List<HeaderParams> headerParamsObj = new ArrayList<>();
		List<Parameter> paramBuilders = new ArrayList<>();
		if (StringUtils.startsWith(apiHeaderParamsDefs, "-"))
		{
			return paramBuilders;
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		try
		{
			headerParamsObj = mapper.readValue(apiHeaderParamsDefs, new TypeReference<List<HeaderParams>>() {
			});
			for (HeaderParams parameter : headerParamsObj)
			{
				String name = parameter.headerName;
				String desc = parameter.desc;
				boolean require = parameter.require;
				paramBuilders.add(new ParameterBuilder().name(name).description(desc).modelRef(new ModelRef("string")).parameterType("header").required(require).build());

			}
		} catch (Exception e)
		{
			e.printStackTrace();
			log.error("Error in your swagger api.header.params.defs definition config", e);
		}

		return paramBuilders;
	}

	/******************************
	 * 
	 * @return
	 */
	private ApiInfo apiInfo()
	{
		springfox.documentation.service.Contact c = new springfox.documentation.service.Contact(ORG_NAME, __LICENSE_URL, __CONTACT_EMAIL);
		return new ApiInfoBuilder().title(__TITLE).description(__DESCRIPTION).termsOfServiceUrl(__T_AND_C_URL).contact(c).license(__LICENSE_INFO).licenseUrl(__LICENSE_URL).version(__VERSION).build();
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	static class HeaderParams {
		public String headerName;
		public String desc;
		public boolean require = false;
	}

}
