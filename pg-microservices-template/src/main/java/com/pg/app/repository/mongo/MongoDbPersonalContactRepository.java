/**
 * 
 */
package com.pg.app.repository.mongo;

/************************************************
 * Demonstrates a MongoDB repository used for CRUD operation on MongoDB via JPA.
 * 
 * NOTICE how the queries are build via the attributes for the Entity, you can also provide you own SQL if you so desire.
 */

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.pg.app.model.mongo.MongoDbPersonalContact;

/**
 * @author pgobin
 *
 */
public interface MongoDbPersonalContactRepository extends MongoRepository<MongoDbPersonalContact, String> {

	Page<MongoDbPersonalContact> findByPersonalContactFirstNameIgnoreCaseOrderByPersonalContactLastNameAsc(String personalContactFirstName, Pageable pageable);

	MongoDbPersonalContact findByPersonalContactUid(long personalContactUid);

	// get all recent message, this one should be used for voicemessages
	Page<MongoDbPersonalContact> findByPersonalContactLastNameIgnoreCaseAndPersonalContactUsage(String personalContactLastName, int personalContactUsage, Pageable pageable);

}