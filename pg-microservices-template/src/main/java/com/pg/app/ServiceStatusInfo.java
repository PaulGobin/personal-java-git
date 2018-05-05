package com.pg.app;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@EnableAspectJAutoProxy
@Aspect

/**********************************************************
 * This class allows you to automatically track your API service calls. By simply adding this class to you project, you will get this tracking feature automatically. You will then
 * be able to view status at the endpoint xxx/svc-status.
 * 
 * It tracks the 10 most recent API calls via AspectJ so you do not have to write any code to be able to make user of this feature. If you don't want this featre, simply remove
 * this class from your project.
 * 
 * @author pgobin
 *
 */
public final class ServiceStatusInfo {

	static Logger log = LoggerFactory.getLogger(ServiceStatusInfo.class);

	private static final Date _SERVERSTARTED = new Date();

	private static final ConcurrentHashMap<String, List<String>> requestTracker = new ConcurrentHashMap<>();

	public ServiceStatusInfo()
	{
		// TODO Auto-generated constructor stub
	}

	/*******************************************************
	 * 
	 * @return
	 */
	@GetMapping("/svc-info")
	public String status()
	{
		return getBasicInfo();

	}

	/********************************************************
	 * 
	 * @return
	 */
	private String getBasicInfo()
	{
		Runtime _runtime = Runtime.getRuntime();
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("Service Name", AppConfig.getEnv().getProperty("spring.application.name"));
		resp.put("Service Started", _SERVERSTARTED);
		resp.put("Profiles", getActiveProfiles());
		resp.put("Free Memory", _runtime.freeMemory());
		resp.put("Total Free Memory", _runtime.totalMemory());
		resp.put("Available Processor(s)", _runtime.availableProcessors());

		resp.put("OS Name", osBean.getName());
		resp.put("OS Arch", osBean.getArch());
		resp.put("OS Version", osBean.getVersion());

		InetAddress ip;
		String hostname;
		try
		{
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			resp.put("Host Name", hostname);
			resp.put("Host IP", ip);

		} catch (UnknownHostException e)
		{
			// no op
		}

		StringBuffer sb = new StringBuffer("<b>");
		for (Map.Entry<String, Object> entry : resp.entrySet())
		{
			sb.append("<br/>" + entry.getKey() + " : " + entry.getValue());
		}
		sb.append("</b>");
		sb.append(getAPIRequestInfo());
		return sb.toString();
	}

	/************************************************
	 * 
	 * @return
	 */
	private String getActiveProfiles()
	{
		StringBuilder sb = new StringBuilder();
		String[] activeProfiles = AppConfig.getEnv().getActiveProfiles();
		for (String p : activeProfiles)
		{
			sb.append(p + ",");
		}
		return sb.toString();
	}

	/*************************************************
	 * 
	 * @return
	 */
	private String getAPIRequestInfo()
	{
		StringBuffer sb = new StringBuffer("<br/><br/>**** API CALL STATS ****<br/>");

		for (Map.Entry<String, List<String>> entry : requestTracker.entrySet())
		{
			sb.append("<br/><b>" + entry.getKey() + "</b></br>");
			List<String> l = entry.getValue();
			for (String s : l)
			{
				sb.append(s + "<br/>");
			}
		}
		return sb.toString();
	}

	/**************************************************
	 * 8
	 * 
	 * @param key
	 * @param value
	 */
	private void addAPIStats(String key, String value)
	{
		if (requestTracker.containsKey(key) == false)
		{
			requestTracker.put(key, Collections.synchronizedList(new ArrayList<String>()));
		}
		List<String> val = requestTracker.get(key);
		if (val.size() > 10)
		{
			val.subList(0, 9).clear();
		}
		val.add(0, value);
	}

	/***********************************************************
	 * 8 Add AspectJ interseptor to trace API calls
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.bvn.app.controller..*(..))")
	public Object profileExecutionTime(ProceedingJoinPoint joinPoint)
	{

		log.debug("AspectJ=> profileExecutionTime() Async");
		Object result = null;
		// Runnable runnable = () -> {
		try
		{
			long start = System.currentTimeMillis();
			String className = joinPoint.getSignature().getDeclaringTypeName();
			String methodName = joinPoint.getSignature().getName();
			String apiName = className + "." + methodName;
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			String requestId = UUID.randomUUID().toString();
			log.info("----->>>>>\nREQUESTED_ID: {}\nHOST: {} HttpMethod: {}\nURI: {}\nAPI: {}\nArguments: {}\n", requestId, request.getHeader("host"), request.getMethod(), request.getRequestURI(),
				apiName, Arrays.toString(joinPoint.getArgs()));
			try
			{
				result = joinPoint.proceed();
				log.info("API CALL RESULT", result);
			} catch (Throwable e)
			{
				log.error("Error in AspectJ API Status Tracker", e);
			}

			long elapsedTime = System.currentTimeMillis() - start;
			String statVal = "Execution Time: " + elapsedTime + " ms [REQUESTED_ID: " + requestId + "]";
			log.info(statVal);
			addAPIStats("[" + apiName + "]", statVal);

		} catch (Throwable e)
		{
			log.error("Error processRecentChatRequest ", e);
		}
		return result;
	}
	// ThreadPoolManager.executorService.execute(runnable);
	// }
}
