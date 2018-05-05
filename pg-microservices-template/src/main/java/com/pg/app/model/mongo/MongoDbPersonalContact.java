package com.pg.app.model.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**************************************************
 * Demonstrates a MongoDB document
 * 
 * @author pgobin
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "PersonalContactCollection")
public class MongoDbPersonalContact {

	// Personal contact info
	private long personalContactUid = -1;
	private String personalContactFirstName = "";
	private String personalContactLastName = "";
	private boolean personalContactIsFavorite = false;
	private String personalContactValue = "";
	private String personalContactDescription = "";
	private int personalContactUsage = -1;

	public MongoDbPersonalContact()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param userId
	 * @param uidCustomerId
	 * @param firstName
	 * @param lastName
	 * @param personalContactUid
	 * @param personalContactFirstName
	 * @param personalContactLastName
	 * @param personalContactIsFavorite
	 * @param personalContactValue
	 * @param personalContactDescription
	 * @param personalContactUsage
	 */
	public MongoDbPersonalContact(long personalContactUid, String personalContactFirstName, String personalContactLastName, boolean personalContactIsFavorite, String personalContactValue,
		String personalContactDescription, int personalContactUsage)
	{
		this.personalContactUid = personalContactUid;
		this.personalContactFirstName = personalContactFirstName;
		this.personalContactLastName = personalContactLastName;
		this.personalContactIsFavorite = personalContactIsFavorite;
		this.personalContactValue = personalContactValue;
		this.personalContactDescription = personalContactDescription;
		this.personalContactUsage = personalContactUsage;
	}

	/**
	 * @return the personalContactUid
	 */
	public long getPersonalContactUid()
	{
		return personalContactUid;
	}

	/**
	 * @param personalContactUid
	 *            the personalContactUid to set
	 */
	public void setPersonalContactUid(long personalContactUid)
	{
		this.personalContactUid = personalContactUid;
	}

	/**
	 * @return the personalContactFirstName
	 */
	public String getPersonalContactFirstName()
	{
		return personalContactFirstName;
	}

	/**
	 * @param personalContactFirstName
	 *            the personalContactFirstName to set
	 */
	public void setPersonalContactFirstName(String personalContactFirstName)
	{
		this.personalContactFirstName = personalContactFirstName;
	}

	/**
	 * @return the personalContactLastName
	 */
	public String getPersonalContactLastName()
	{
		return personalContactLastName;
	}

	/**
	 * @param personalContactLastName
	 *            the personalContactLastName to set
	 */
	public void setPersonalContactLastName(String personalContactLastName)
	{
		this.personalContactLastName = personalContactLastName;
	}

	/**
	 * @return the personalContactIsFavorite
	 */
	public boolean isPersonalContactIsFavorite()
	{
		return personalContactIsFavorite;
	}

	/**
	 * @param personalContactIsFavorite
	 *            the personalContactIsFavorite to set
	 */
	public void setPersonalContactIsFavorite(boolean personalContactIsFavorite)
	{
		this.personalContactIsFavorite = personalContactIsFavorite;
	}

	/**
	 * @return the personalContactValue
	 */
	public String getPersonalContactValue()
	{
		return personalContactValue;
	}

	/**
	 * @param personalContactValue
	 *            the personalContactValue to set
	 */
	public void setPersonalContactValue(String personalContactValue)
	{
		this.personalContactValue = personalContactValue;
	}

	/**
	 * @return the personalContactDescription
	 */
	public String getPersonalContactDescription()
	{
		return personalContactDescription;
	}

	/**
	 * @param personalContactDescription
	 *            the personalContactDescription to set
	 */
	public void setPersonalContactDescription(String personalContactDescription)
	{
		this.personalContactDescription = personalContactDescription;
	}

	/**
	 * @return the personalContactUsage
	 */
	public int getPersonalContactUsage()
	{
		return personalContactUsage;
	}

	/**
	 * @param personalContactUsage
	 *            the personalContactUsage to set
	 */
	public void setPersonalContactUsage(int personalContactUsage)
	{
		this.personalContactUsage = personalContactUsage;
	}

}
