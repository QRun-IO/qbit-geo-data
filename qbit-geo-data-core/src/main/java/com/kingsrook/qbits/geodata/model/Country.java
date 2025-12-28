/*******************************************************************************
 ** Country entity for geographic reference data.
 **
 ** Based on ISO 3166-1 standard.
 ** Natural key: alpha2Code (e.g., "US", "CA", "MX")
 *******************************************************************************/
package com.kingsrook.qbits.geodata.model;


import java.time.Instant;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;


@QMetaDataProducingEntity(producePossibleValueSource = true)
public class Country extends QRecordEntity
{
   public static final String TABLE_NAME = "country";

   @QField(isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true, maxLength = 2, label = "Alpha-2 Code")
   private String alpha2Code;

   @QField(maxLength = 3, label = "Alpha-3 Code")
   private String alpha3Code;

   @QField(label = "Numeric Code")
   private Integer numericCode;

   @QField(isRequired = true, maxLength = 100)
   private String name;

   @QField(maxLength = 100, label = "Official Name")
   private String officialName;

   @QField
   private Boolean isActive = true;

   @QField
   private Instant createDate;

   @QField
   private Instant modifyDate;



   //////////////////////////////////////////////////////////////////////////////
   // Fluent setters                                                           //
   //////////////////////////////////////////////////////////////////////////////

   public Country withId(Integer id)
   {
      this.id = id;
      return this;
   }


   public Country withAlpha2Code(String alpha2Code)
   {
      this.alpha2Code = alpha2Code;
      return this;
   }


   public Country withAlpha3Code(String alpha3Code)
   {
      this.alpha3Code = alpha3Code;
      return this;
   }


   public Country withNumericCode(Integer numericCode)
   {
      this.numericCode = numericCode;
      return this;
   }


   public Country withName(String name)
   {
      this.name = name;
      return this;
   }


   public Country withOfficialName(String officialName)
   {
      this.officialName = officialName;
      return this;
   }


   public Country withIsActive(Boolean isActive)
   {
      this.isActive = isActive;
      return this;
   }


   public Country withCreateDate(Instant createDate)
   {
      this.createDate = createDate;
      return this;
   }


   public Country withModifyDate(Instant modifyDate)
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


   public String getAlpha2Code()
   {
      return alpha2Code;
   }


   public String getAlpha3Code()
   {
      return alpha3Code;
   }


   public Integer getNumericCode()
   {
      return numericCode;
   }


   public String getName()
   {
      return name;
   }


   public String getOfficialName()
   {
      return officialName;
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
}
