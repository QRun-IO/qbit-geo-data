/*******************************************************************************
 ** State/Province entity for geographic reference data.
 **
 ** Based on ISO 3166-2 standard.
 ** Natural key: countryId + code (e.g., US + "CA" for California)
 *******************************************************************************/
package com.kingsrook.qbits.geodata.model;


import java.time.Instant;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;


@QMetaDataProducingEntity(produceTableMetaData = true, producePossibleValueSource = true)
public class StateProvince extends QRecordEntity
{
   public static final String TABLE_NAME = "stateProvince";

   @QField(isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true, possibleValueSourceName = "country")
   private Integer countryId;

   @QField(isRequired = true, maxLength = 10, label = "Code")
   private String code;

   @QField(isRequired = true, maxLength = 100)
   private String name;

   @QField(maxLength = 50, label = "Type")
   private String subdivisionType;  // "State", "Province", "Territory", etc.

   @QField
   private Boolean isActive = true;

   @QField
   private Instant createDate;

   @QField
   private Instant modifyDate;



   //////////////////////////////////////////////////////////////////////////////
   // Fluent setters                                                           //
   //////////////////////////////////////////////////////////////////////////////

   public StateProvince withId(Integer id)
   {
      this.id = id;
      return this;
   }


   public StateProvince withCountryId(Integer countryId)
   {
      this.countryId = countryId;
      return this;
   }


   public StateProvince withCode(String code)
   {
      this.code = code;
      return this;
   }


   public StateProvince withName(String name)
   {
      this.name = name;
      return this;
   }


   public StateProvince withSubdivisionType(String subdivisionType)
   {
      this.subdivisionType = subdivisionType;
      return this;
   }


   public StateProvince withIsActive(Boolean isActive)
   {
      this.isActive = isActive;
      return this;
   }


   public StateProvince withCreateDate(Instant createDate)
   {
      this.createDate = createDate;
      return this;
   }


   public StateProvince withModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
      return this;
   }



   //////////////////////////////////////////////////////////////////////////////
   // Getters                                                                  //
   //////////////////////////////////////////////////////////////////////////////

   public Integer getId()
   {
      return id;
   }


   public Integer getCountryId()
   {
      return countryId;
   }


   public String getCode()
   {
      return code;
   }


   public String getName()
   {
      return name;
   }


   public String getSubdivisionType()
   {
      return subdivisionType;
   }


   public Boolean getIsActive()
   {
      return isActive;
   }


   public Instant getCreateDate()
   {
      return createDate;
   }


   public Instant getModifyDate()
   {
      return modifyDate;
   }


   /*******************************************************************************
    ** Setter for id (QRecordEntity discovers a field only from a getter and setter pair).
    *******************************************************************************/
   public void setId(Integer id)
   {
      this.id = id;
   }


   /*******************************************************************************
    ** Setter for countryId (QRecordEntity discovers a field only from a getter and setter pair).
    *******************************************************************************/
   public void setCountryId(Integer countryId)
   {
      this.countryId = countryId;
   }


   /*******************************************************************************
    ** Setter for code (QRecordEntity discovers a field only from a getter and setter pair).
    *******************************************************************************/
   public void setCode(String code)
   {
      this.code = code;
   }


   /*******************************************************************************
    ** Setter for name (QRecordEntity discovers a field only from a getter and setter pair).
    *******************************************************************************/
   public void setName(String name)
   {
      this.name = name;
   }


   /*******************************************************************************
    ** Setter for subdivisionType (QRecordEntity discovers a field only from a getter and setter pair).
    *******************************************************************************/
   public void setSubdivisionType(String subdivisionType)
   {
      this.subdivisionType = subdivisionType;
   }


   /*******************************************************************************
    ** Setter for isActive (QRecordEntity discovers a field only from a getter and setter pair).
    *******************************************************************************/
   public void setIsActive(Boolean isActive)
   {
      this.isActive = isActive;
   }


   /*******************************************************************************
    ** Setter for createDate (QRecordEntity discovers a field only from a getter and setter pair).
    *******************************************************************************/
   public void setCreateDate(Instant createDate)
   {
      this.createDate = createDate;
   }


   /*******************************************************************************
    ** Setter for modifyDate (QRecordEntity discovers a field only from a getter and setter pair).
    *******************************************************************************/
   public void setModifyDate(Instant modifyDate)
   {
      this.modifyDate = modifyDate;
   }
}
