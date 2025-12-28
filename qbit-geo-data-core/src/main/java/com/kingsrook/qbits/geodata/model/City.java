/*******************************************************************************
 ** City entity for geographic reference data.
 **
 ** Based on GeoNames data (cities with population > 15,000).
 ** Natural key: stateProvinceId + name
 *******************************************************************************/
package com.kingsrook.qbits.geodata.model;


import java.math.BigDecimal;
import java.time.Instant;
import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;


@QMetaDataProducingEntity(producePossibleValueSource = true)
public class City extends QRecordEntity
{
   public static final String TABLE_NAME = "city";

   @QField(isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true, possibleValueSourceName = "stateProvince")
   private Integer stateProvinceId;

   @QField(isRequired = true, maxLength = 200)
   private String name;

   @QField(label = "ASCII Name", maxLength = 200)
   private String asciiName;

   @QField
   private Integer population;

   @QField(label = "Latitude")
   private BigDecimal latitude;

   @QField(label = "Longitude")
   private BigDecimal longitude;

   @QField(maxLength = 50, label = "Time Zone")
   private String timezone;

   @QField
   private Boolean isActive = true;

   @QField
   private Instant createDate;

   @QField
   private Instant modifyDate;



   //////////////////////////////////////////////////////////////////////////////
   // Fluent setters                                                           //
   //////////////////////////////////////////////////////////////////////////////

   public City withId(Integer id)
   {
      this.id = id;
      return this;
   }


   public City withStateProvinceId(Integer stateProvinceId)
   {
      this.stateProvinceId = stateProvinceId;
      return this;
   }


   public City withName(String name)
   {
      this.name = name;
      return this;
   }


   public City withAsciiName(String asciiName)
   {
      this.asciiName = asciiName;
      return this;
   }


   public City withPopulation(Integer population)
   {
      this.population = population;
      return this;
   }


   public City withLatitude(BigDecimal latitude)
   {
      this.latitude = latitude;
      return this;
   }


   public City withLongitude(BigDecimal longitude)
   {
      this.longitude = longitude;
      return this;
   }


   public City withTimezone(String timezone)
   {
      this.timezone = timezone;
      return this;
   }


   public City withIsActive(Boolean isActive)
   {
      this.isActive = isActive;
      return this;
   }


   public City withCreateDate(Instant createDate)
   {
      this.createDate = createDate;
      return this;
   }


   public City withModifyDate(Instant modifyDate)
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


   public Integer getStateProvinceId()
   {
      return stateProvinceId;
   }


   public String getName()
   {
      return name;
   }


   public String getAsciiName()
   {
      return asciiName;
   }


   public Integer getPopulation()
   {
      return population;
   }


   public BigDecimal getLatitude()
   {
      return latitude;
   }


   public BigDecimal getLongitude()
   {
      return longitude;
   }


   public String getTimezone()
   {
      return timezone;
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
