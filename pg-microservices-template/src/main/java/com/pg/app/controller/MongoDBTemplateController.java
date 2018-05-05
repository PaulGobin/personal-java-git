package com.pg.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.pg.app.AppConfig;
import com.pg.app.SwaggerConfig;
import com.pg.app.model.mongo.MongoDbPersonalContact;
import com.pg.app.repository.mongo.MongoDbPersonalContactRepository;

import io.swagger.annotations.ApiOperation;

/************************************************
 * Demonstrate how simple it is to expose a POJO as a rest service endpoint, along with any API documentation via swagger.
 * 
 * It uses a MongoDB repository so that you can see how easy you can add MongoDB capability to you service.
 * 
 * Also used JPA for MongoDB
 * 
 * @see SwaggerConfig.java
 * 
 * @author pgobin
 *
 */
@RestController
@RequestMapping("/v1/template-controller-mongo")
@RefreshScope
public class MongoDBTemplateController {

	private static final Logger log = LoggerFactory.getLogger(MongoDBTemplateController.class);

	@Autowired
	MongoDbPersonalContactRepository _mongoDbPersonalContactRepository;

	@Value("${server.port}")
	int serverPort;

	@Value("${spring.application.name}")
	String applicationName;

	@Autowired
	RestTemplate restTemplate;

	public MongoDBTemplateController()
	{
		// TODO Auto-generated constructor stub
	}

	/******************************************
	 * 
	 * @param headers
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, produces = "application/json", value = "/createPersonalContact")
	@ApiOperation(value = "Create a personal contact.", notes = "Demostrating how to create a document in MongoDB and return the newly created object via a ResponseEntity")
	public ResponseEntity<?> createPersonalContact(@RequestHeader HttpHeaders headers, @RequestBody(required = true) MongoDbPersonalContact req) throws Exception
	{
		log.debug("Request to create a personal contact");
		MongoDbPersonalContact resp = _mongoDbPersonalContactRepository.save(req);
		return new ResponseEntity<MongoDbPersonalContact>(resp, HttpStatus.OK);
	}

	/**************************************************
	 * 
	 * @param headers
	 * @param personalContactLastName
	 * @param personalContactUsage
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "Find all personal contacts by lastname and usage.", notes = " Find by lastname and usage pageable /page=0&size=3&sort=asc  default is 20 records ")
	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "/findByLastNameAndUsage/{personalContactLastName}/{personalContactUsage}")
	public ResponseEntity<?> findByLastNameAndUsage(@RequestHeader HttpHeaders headers, @PathVariable String personalContactLastName, int personalContactUsage, Pageable pageable)
	{
		log.info("Request findByPersonalContactLastNameAndPersonalContactUsage " + personalContactLastName + " and PersonalContactUsage " + personalContactUsage);
		Page<MongoDbPersonalContact> oResp = (Page<MongoDbPersonalContact>) _mongoDbPersonalContactRepository.findByPersonalContactLastNameIgnoreCaseAndPersonalContactUsage(personalContactLastName,
			personalContactUsage, pageable);
		if (oResp.hasContent())
		{
			return new ResponseEntity<Page<MongoDbPersonalContact>>(oResp, HttpStatus.OK);
		}
		return ResponseEntity.notFound().build();
	}

	/******************************************************
	 * 
	 * @param headers
	 * @param personalFirstName
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "Find all personal contacts by lastname.", notes = " Find by lastname")
	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "/findByFirstNamePaged/{personalFirstName}")
	public Page<MongoDbPersonalContact> findByFirstNamePaged(@RequestHeader HttpHeaders headers, @PathVariable String personalFirstName, Pageable pageable)
	{
		log.info("Request personalFirstNamePaged " + personalFirstName);

		Page<MongoDbPersonalContact> oResp = (Page<MongoDbPersonalContact>) _mongoDbPersonalContactRepository
			.findByPersonalContactFirstNameIgnoreCaseOrderByPersonalContactLastNameAsc(personalFirstName, pageable);
		return oResp;

	}

	/*****************************************************
	 * 8
	 * 
	 * @param headers
	 * @return
	 */
	@ApiOperation(value = "Find all personal contacts.", notes = "Get all personal contacts from MongoDB demo")
	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "/getAll")
	public Page<MongoDbPersonalContact> getAll(@RequestHeader HttpHeaders headers, Pageable pageable)
	{
		log.info("Request getAll..");
		Page<MongoDbPersonalContact> resp = _mongoDbPersonalContactRepository.findAll(pageable);
		return resp;
	}

	/*****************************************
	 * 8 Example demonstrating how to invoke an external rest API, this does not use the discovery/load balancing feature
	 * 
	 * @param headers
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "Calling other API.", notes = "Example showing how to use restTemplate to call other API endpoints. This DOES NOT use the feing load balancer/discovery feature.")
	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "/getAllViaRestAPIExample")
	public Page<MongoDbPersonalContact> getAllViaRestAPIExample(@RequestHeader HttpHeaders headers, Pageable pageable)
	{
		String sPort = AppConfig.getEnv().getProperty("server.port", "0");
		String getEndPoint = "http://localhost:" + sPort + "/v1/template-controller-mongo/getAll";
		RestTemplate restTemplate = new RestTemplate();
		log.info("Request getAll..");
		Page<MongoDbPersonalContact> resp = (Page<MongoDbPersonalContact>) restTemplate.getForObject(getEndPoint, MongoDbPersonalContact.class);
		return resp;
	}

	/*************************************************
	 * 
	 * @param headers
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "Calling other API.", notes = "Example showing how to use restTemplate to call other API endpoints. This DOES use the feing load balancer/discovery feature.")
	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "/getAllViaRestAPIExampleLBDisco")
	public ResponseEntity<?> getAllViaRestAPIExampleLBDisco(@RequestHeader HttpHeaders headers, Pageable pageable)
	{
		String getEndPoint = "http://" + applicationName + "/v1/template-controller-mongo/getAll";
		log.info("Request getAll using discovery and load balancing..");
		Object oResp = restTemplate.getForObject(getEndPoint, Object.class);
		return new ResponseEntity<Object>(oResp, HttpStatus.OK);
	}

}
