package com.pg.app.model.rdbm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*********************************************
 * Demonstrate an RDBM Entity via JPA
 * 
 * @author pgobin
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "RDBMPerson", uniqueConstraints = @UniqueConstraint(columnNames = { "mobilePhone" }))
public class RDBMPerson {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	private String firstName;
	private String lastName;
	private String officePhone;
	private String officeExtension;
	private String homePhone;
	@Column(nullable = false, unique = true)
	private String mobilePhone;

	@Temporal(TemporalType.DATE)
	private Date dob;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	/**
	 * @return the id
	 */

	public Long getId()
	{
		return id;
	}

	/**
	 * 
	 */
	public RDBMPerson()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the officePhone
	 */
	public String getOfficePhone()
	{
		return officePhone;
	}

	/**
	 * @param officePhone
	 *            the officePhone to set
	 */
	public void setOfficePhone(String officePhone)
	{
		this.officePhone = officePhone;
	}

	/**
	 * @return the officeExtension
	 */
	public String getOfficeExtension()
	{
		return officeExtension;
	}

	/**
	 * @param officeExtension
	 *            the officeExtension to set
	 */
	public void setOfficeExtension(String officeExtension)
	{
		this.officeExtension = officeExtension;
	}

	/**
	 * @return the homePhone
	 */
	public String getHomePhone()
	{
		return homePhone;
	}

	/**
	 * @param homePhone
	 *            the homePhone to set
	 */
	public void setHomePhone(String homePhone)
	{
		this.homePhone = homePhone;
	}

	/**
	 * @return the mobilePhone
	 */
	public String getMobilePhone()
	{
		return mobilePhone;
	}

	/**
	 * @param mobilePhone
	 *            the mobilePhone to set
	 */
	public void setMobilePhone(String mobilePhone)
	{
		this.mobilePhone = mobilePhone;
	}

	/**
	 * @return the dob
	 */
	public Date getDob()
	{
		return dob;
	}

	/**
	 * @param dob
	 *            the dob to set
	 */
	public void setDob(Date dob)
	{
		this.dob = dob;
	}

	/**
	 * @return the created
	 */
	public Date getCreated()
	{
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created)
	{
		this.created = created;
	}

	/**
	 * @return the updated
	 */
	public Date getUpdated()
	{
		return updated;
	}

	/**
	 * @param updated
	 *            the updated to set
	 */
	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

}
