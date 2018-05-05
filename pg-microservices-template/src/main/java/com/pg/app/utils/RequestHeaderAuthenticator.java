package com.pg.app.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.pg.app.AppConfig;

/**************************************************************
 * 
 * @author pgobin
 * 
 * LATER CHANGE THIS TO AUTOMATICALL HOOK INTO AN oAUTH2 Service
 *
 */
public class RequestHeaderAuthenticator {

	private static Log log = LogFactory.getLog(RequestHeaderAuthenticator.class);

	/******************************************************************
	 * 
	 * @param headers
	 * @return
	 */
	public static HttpStatus isAuthorized(HttpHeaders headers)
	{
		String bypassAuthCheck = AppConfig.getEnv().getProperty("security.header.auth.bypass", "false");
		if (StringUtils.equalsIgnoreCase(bypassAuthCheck, "true"))
		{
			log.warn("*** NOTE THAT security.header.auth.bypass is set to true");
			return HttpStatus.OK;
		}

		if (headers.containsKey("x-account") == false)
		{
			log.error("*** Missing x-account in http header");
			return HttpStatus.UNAUTHORIZED;
		}
		if (headers.containsKey("x-authkey") == false)
		{
			log.error("*** Missing x-authkey in http header");
			return HttpStatus.UNAUTHORIZED;
		}
		// check if there is an account configured.4
		String account = headers.getFirst("x-account");
		String authKey = headers.getFirst("x-authkey");
		String definedAuthKey = AppConfig.getEnv().getProperty(account, "");
		if (StringUtils.isEmpty(definedAuthKey))
		{
			log.error("*** Invalid account in http header:" + account);
			return HttpStatus.UNAUTHORIZED;
		}

		if (StringUtils.equalsIgnoreCase(authKey, definedAuthKey))
		{
			return HttpStatus.OK;
		}

		log.error("Invalid x-authkey for account:" + account);
		return HttpStatus.UNAUTHORIZED;

	}

	/**************************************************************
	 * 
	 * @return
	 */
	public static HttpHeaders getHttpHeaderForInterAPICalls(String appName) throws Exception
	{
		HttpHeaders internalHeader = new HttpHeaders();
		String authToken = AppConfig.getEnv().getProperty(appName, "");
		if (StringUtils.isEmpty(authToken))
		{
			throw new Exception("Unable to create HttpHeaders for " + appName + " because it is not defined or the aithToken is enpty in your configuration");
		}
		internalHeader.set("x-account", appName);
		internalHeader.set("x-authkey", authToken);
		return internalHeader;
	}
}
