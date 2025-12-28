/*******************************************************************************
 ** Configuration for the Example Data QBit.
 *******************************************************************************/
package com.kingsrook.qbits.example;

import java.util.ArrayList;
import java.util.List;

import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitConfig;
import com.kingsrook.qqq.backend.core.utils.StringUtils;

import com.kingsrook.qbits.example.model.ExampleChildEntity;
import com.kingsrook.qbits.example.model.ExampleEntity;


/*******************************************************************************
 ** Configuration class for the Example Data QBit.
 **
 ** Data QBits configure:
 ** - Backend name for table storage
 ** - Table name prefix for multi-instance support
 ** - Which tables are enabled
 *******************************************************************************/
public class ExampleDataQBitConfig implements QBitConfig
{
   private String  backendName;
   private String  tableNamePrefix;
   private Boolean enableExampleEntity = true;
   private Boolean enableExampleChildEntity = true;



   /*******************************************************************************
    ** Validate configuration before QBit is produced.
    *******************************************************************************/
   @Override
   public void validate(QInstance qInstance, List<String> errors)
   {
      if(backendName == null)
      {
         errors.add("backendName is required");
      }
   }



   /*******************************************************************************
    ** Get list of enabled table names.
    *******************************************************************************/
   public List<String> getEnabledTableNames()
   {
      List<String> tables = new ArrayList<>();
      if(Boolean.TRUE.equals(enableExampleEntity))
      {
         tables.add(ExampleEntity.TABLE_NAME);
      }
      if(Boolean.TRUE.equals(enableExampleChildEntity))
      {
         tables.add(ExampleChildEntity.TABLE_NAME);
      }
      return tables;
   }



   /*******************************************************************************
    ** Apply prefix to a table name.
    *******************************************************************************/
   public String applyPrefix(String tableName)
   {
      if(StringUtils.hasContent(tableNamePrefix))
      {
         return tableNamePrefix + "_" + tableName;
      }
      return tableName;
   }



   /*******************************************************************************
    ** Getter for backendName
    *******************************************************************************/
   public String getBackendName()
   {
      return backendName;
   }



   /*******************************************************************************
    ** Fluent setter for backendName
    *******************************************************************************/
   public ExampleDataQBitConfig withBackendName(String backendName)
   {
      this.backendName = backendName;
      return this;
   }



   /*******************************************************************************
    ** Getter for tableNamePrefix
    *******************************************************************************/
   public String getTableNamePrefix()
   {
      return tableNamePrefix;
   }



   /*******************************************************************************
    ** Fluent setter for tableNamePrefix
    *******************************************************************************/
   public ExampleDataQBitConfig withTableNamePrefix(String tableNamePrefix)
   {
      this.tableNamePrefix = tableNamePrefix;
      return this;
   }



   /*******************************************************************************
    ** Getter for enableExampleEntity
    *******************************************************************************/
   public Boolean getEnableExampleEntity()
   {
      return enableExampleEntity;
   }



   /*******************************************************************************
    ** Fluent setter for enableExampleEntity
    *******************************************************************************/
   public ExampleDataQBitConfig withEnableExampleEntity(Boolean enableExampleEntity)
   {
      this.enableExampleEntity = enableExampleEntity;
      return this;
   }



   /*******************************************************************************
    ** Getter for enableExampleChildEntity
    *******************************************************************************/
   public Boolean getEnableExampleChildEntity()
   {
      return enableExampleChildEntity;
   }



   /*******************************************************************************
    ** Fluent setter for enableExampleChildEntity
    *******************************************************************************/
   public ExampleDataQBitConfig withEnableExampleChildEntity(Boolean enableExampleChildEntity)
   {
      this.enableExampleChildEntity = enableExampleChildEntity;
      return this;
   }
}
