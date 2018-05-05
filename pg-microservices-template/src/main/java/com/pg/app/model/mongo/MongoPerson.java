package com.pg.app.model.mongo;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/******************************************
 * Demonstrate a MongoDB document
 * 
 * @author pgobin
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "MockCollection")
public class MongoPerson {

	@Id
	private Long id;
	private String firstName;
	private String lastName;
	private String officePhone;
	private String officeExtension;
	private String homePhone;
	private String mobilePhone;
	private Date dob;

	private long created;
	private long updated;

	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
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
	public long getCreated()
	{
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(long created)
	{
		this.created = created;
	}

	/**
	 * @return the updated
	 */
	public long getUpdated()
	{
		return updated;
	}

	/**
	 * @param updated
	 *            the updated to set
	 */
	public void setUpdated(long updated)
	{
		this.updated = updated;
	}

}
