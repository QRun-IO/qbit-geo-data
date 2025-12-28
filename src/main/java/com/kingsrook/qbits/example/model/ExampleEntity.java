/*******************************************************************************
 ** Example entity for the Data QBit.
 *******************************************************************************/
package com.kingsrook.qbits.example.model;

import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;


/*******************************************************************************
 ** Example entity demonstrating Data QBit patterns.
 **
 ** Key features:
 ** - Natural key field (code) for upsert matching
 ** - isActive field for soft deletes during sync
 ** - PossibleValueSource auto-generation
 *******************************************************************************/
@QMetaDataProducingEntity(producePossibleValueSource = true)
public class ExampleEntity extends QRecordEntity
{
   public static final String TABLE_NAME = "exampleEntity";

   @QField(isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true, maxLength = 10)
   private String code;  // Natural key for upsert

   @QField(isRequired = true, maxLength = 100)
   private String name;

   @QField(maxLength = 500)
   private String description;

   @QField
   private Boolean isActive = true;  // Soft delete flag



   /*******************************************************************************
    ** Getter for id
    *******************************************************************************/
   public Integer getId()
   {
      return id;
   }



   /*******************************************************************************
    ** Fluent setter for id
    *******************************************************************************/
   public ExampleEntity withId(Integer id)
   {
      this.id = id;
      return this;
   }



   /*******************************************************************************
    ** Getter for code
    *******************************************************************************/
   public String getCode()
   {
      return code;
   }



   /*******************************************************************************
    ** Fluent setter for code
    *******************************************************************************/
   public ExampleEntity withCode(String code)
   {
      this.code = code;
      return this;
   }



   /*******************************************************************************
    ** Getter for name
    *******************************************************************************/
   public String getName()
   {
      return name;
   }



   /*******************************************************************************
    ** Fluent setter for name
    *******************************************************************************/
   public ExampleEntity withName(String name)
   {
      this.name = name;
      return this;
   }



   /*******************************************************************************
    ** Getter for description
    *******************************************************************************/
   public String getDescription()
   {
      return description;
   }



   /*******************************************************************************
    ** Fluent setter for description
    *******************************************************************************/
   public ExampleEntity withDescription(String description)
   {
      this.description = description;
      return this;
   }



   /*******************************************************************************
    ** Getter for isActive
    *******************************************************************************/
   public Boolean getIsActive()
   {
      return isActive;
   }



   /*******************************************************************************
    ** Fluent setter for isActive
    *******************************************************************************/
   public ExampleEntity withIsActive(Boolean isActive)
   {
      this.isActive = isActive;
      return this;
   }
}
