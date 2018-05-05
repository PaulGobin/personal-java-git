/**
 * 
 */
package com.pg.app.repository.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.pg.app.model.mongo.MongoPerson;

/**
 * Demonstrates a RDBM repository used for CRUD operation on an RDBM Database via JPA..
 * 
 * NOTICE how the queries are build via the attributes for the Entity, you can also provide you own SQL if you so desire.
 * 
 * @author pgobin
 *
 */
public interface MongoPersonRepository extends MongoRepository<MongoPerson, Long> {

	MongoPerson findByid(Long id);

	Page<MongoPerson> findByFirstName(String firstName, Pageable pageable);

}