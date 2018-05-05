/**
 * 
 */
package com.pg.app.repository.rdbm;

import java.util.List;

/**
 * CustomerRepository.java
 * <br/>
 * @author pgobin Mar 2, 2015 10:13:36 PM
 * 
 *
 */
import org.springframework.data.repository.CrudRepository;

import com.pg.app.model.rdbm.RDBMPerson;

/**
 * Demonstrates a RDBM repository used for CRUD operation on an RDBM Database via JPA..
 * 
 * NOTICE how the queries are build via the attributes for the Entity, you can also provide you own SQL if you so desire.
 * 
 * @author pgobin
 *
 */
public interface RDBMPersonRepository extends CrudRepository<RDBMPerson, Long> {

	RDBMPerson findByid(Long id);
	List<RDBMPerson> findByFirstName(String firstName);

}
