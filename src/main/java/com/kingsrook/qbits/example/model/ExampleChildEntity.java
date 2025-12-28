/*******************************************************************************
 ** Example child entity for the Data QBit.
 *******************************************************************************/
package com.kingsrook.qbits.example.model;

import com.kingsrook.qqq.backend.core.model.data.QField;
import com.kingsrook.qqq.backend.core.model.data.QRecordEntity;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingEntity;


/*******************************************************************************
 ** Example child entity with foreign key to parent.
 **
 ** Demonstrates composite natural key pattern.
 *******************************************************************************/
@QMetaDataProducingEntity(producePossibleValueSource = true)
public class ExampleChildEntity extends QRecordEntity
{
   public static final String TABLE_NAME = "exampleChildEntity";

   @QField(isPrimaryKey = true)
   private Integer id;

   @QField(isRequired = true)
   private Integer exampleEntityId;  // FK to parent

   @QField(isRequired = true, maxLength = 10)
   private String code;  // Part of composite natural key

   @QField(isRequired = true, maxLength = 100)
   private String name;

   @QField
   private Boolean isActive = true;



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
   public ExampleChildEntity withId(Integer id)
   {
      this.id = id;
      return this;
   }



   /*******************************************************************************
    ** Getter for exampleEntityId
    *******************************************************************************/
   public Integer getExampleEntityId()
   {
      return exampleEntityId;
   }



   /*******************************************************************************
    ** Fluent setter for exampleEntityId
    *******************************************************************************/
   public ExampleChildEntity withExampleEntityId(Integer exampleEntityId)
   {
      this.exampleEntityId = exampleEntityId;
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
   public ExampleChildEntity withCode(String code)
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
   public ExampleChildEntity withName(String name)
   {
      this.name = name;
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
   public ExampleChildEntity withIsActive(Boolean isActive)
   {
      this.isActive = isActive;
      return this;
   }
}
