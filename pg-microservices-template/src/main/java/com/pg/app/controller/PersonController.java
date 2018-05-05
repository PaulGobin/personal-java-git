package com.pg.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pg.app.SwaggerConfig;
import com.pg.app.model.mongo.MongoPerson;
import com.pg.app.model.rdbm.RDBMPerson;
import com.pg.app.repository.mongo.MongoPersonRepository;
import com.pg.app.repository.rdbm.RDBMPersonRepository;

import io.swagger.annotations.ApiOperation;

/*******************************************************
 * Demonstrate how simple it is to expose a POJO as a rest service endpoint, along with any API documentation via swagger.
 * 
 * It uses a MongoDB and RDBM repository so that you can see how easy you can add MongoDB and RDBM capability to you service.
 * 
 * Also used JPA for MongoDB and RDBM
 * 
 * @see SwaggerConfig.java
 * @author pgobin
 *
 */
@RestController
@RequestMapping("/v1/PersonController")
public class PersonController {

	@Autowired
	RDBMPersonRepository _rdbmPersonRepository;

	@Autowired
	MongoPersonRepository _mongoPersonRepository;

	public PersonController()
	{
		// TODO Auto-generated constructor stub
	}

	/********************** RDBM ******************/
	@GetMapping("/rdbm/all")
	public List<RDBMPerson> findAllRDBMPerson()
	{
		return (List<RDBMPerson>) _rdbmPersonRepository.findAll();
	}

	@GetMapping("/rdbm/{personId}")
	public RDBMPerson findRDBMPerson(@PathVariable Long personId)
	{
		return _rdbmPersonRepository.findByid(personId);
	}

	@RequestMapping(method = RequestMethod.POST, produces = "application/json", value = "/saveRDBMPerson")
	@ApiOperation(value = "Create an RDBM Person or update it if it already exist.", notes = "Demonstrate a HTTP POST Verb")
	public RDBMPerson saveRDBMPerson(@RequestHeader HttpHeaders headers, @RequestBody(required = true) RDBMPerson req) throws Exception
	{
		return _rdbmPersonRepository.save(req);
	}

	/*********************** MONGO ***************/
	@ApiOperation(value = "Return all MongoPerson.", notes = "return ll records from mongo ")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "/mongo/findAllMongoPerson")
	public ResponseEntity<List<MongoPerson>> findAllMongoPerson(@RequestHeader HttpHeaders headers)
	{
		List<MongoPerson> resp = _mongoPersonRepository.findAll();
		return new ResponseEntity<List<MongoPerson>>(resp, HttpStatus.OK);
	}

	/**********************************************
	 * 
	 * @param personId
	 * @return
	 */
	@ApiOperation(value = "Return a MongoPerson object for the given personId.", notes = "Demonstrate an HTTP GET verb")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "/mongo/{personId}")
	public MongoPerson findMongoPerson(@PathVariable Long personId)
	{
		return _mongoPersonRepository.findByid(personId);
	}

	@RequestMapping(method = RequestMethod.POST, produces = "application/json", value = "/saveMongoPerson")
	@ApiOperation(value = "Create a MONGO Person or update it if it already exist.", notes = "My MONGO Person Note")
	public MongoPerson saveMongoPerson(@RequestHeader HttpHeaders headers, @RequestBody(required = true) MongoPerson req) throws Exception
	{
		return _mongoPersonRepository.save(req);
	}
}
