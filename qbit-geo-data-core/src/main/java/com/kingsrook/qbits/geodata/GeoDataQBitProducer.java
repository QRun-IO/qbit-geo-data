/*******************************************************************************
 ** Producer for the Geographic Data QBit.
 **
 ** Produces country, state/province, and city reference data tables with
 ** support for table prefixing and multi-instance deployment.
 *******************************************************************************/
package com.kingsrook.qbits.geodata;


import java.util.ArrayList;
import java.util.List;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerHelper;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerOutput;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.QPossibleValueSource;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitProducer;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.SourceQBitAware;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qbits.geodata.model.City;
import com.kingsrook.qbits.geodata.model.Country;
import com.kingsrook.qbits.geodata.model.StateProvince;


public class GeoDataQBitProducer implements QBitProducer
{
   public static final String GROUP_ID    = "com.kingsrook.qbits";
   public static final String ARTIFACT_ID = "qbit-geo-data";
   public static final String VERSION     = "0.1.0";

   private GeoDataQBitConfig config;



   /*******************************************************************************
    ** Produce the QBit metadata into the QInstance.
    *******************************************************************************/
   @Override
   public void produce(QInstance qInstance, String namespace) throws QException
   {
      /////////////////////////////////////////////////////////////////////////
      // Validate configuration                                              //
      /////////////////////////////////////////////////////////////////////////
      List<String> errors = new ArrayList<>();
      config.validate(qInstance, errors);
      if(!errors.isEmpty())
      {
         throw new QException("Configuration errors: " + String.join(", ", errors));
      }

      /////////////////////////////////////////////////////////////////////////
      // Create and register QBit identity                                   //
      /////////////////////////////////////////////////////////////////////////
      QBitMetaData qBitMetaData = new QBitMetaData()
         .withGroupId(GROUP_ID)
         .withArtifactId(ARTIFACT_ID)
         .withVersion(VERSION)
         .withNamespace(namespace)
         .withConfig(config);
      qInstance.addQBit(qBitMetaData);

      /////////////////////////////////////////////////////////////////////////
      // Discover and produce all component metadata                         //
      /////////////////////////////////////////////////////////////////////////
      List<MetaDataProducerInterface<?>> producers =
         MetaDataProducerHelper.findProducers(getClass().getPackageName());

      for(MetaDataProducerInterface<?> producer : producers)
      {
         ///////////////////////////////////////////////////////////////////////
         // Skip disabled components                                         //
         ///////////////////////////////////////////////////////////////////////
         if(!isProducerEnabled(producer))
         {
            continue;
         }

         MetaDataProducerOutput output = producer.produce(qInstance);

         ///////////////////////////////////////////////////////////////////////
         // Apply table prefix and backend if configured                     //
         ///////////////////////////////////////////////////////////////////////
         if(output instanceof QTableMetaData table)
         {
            String prefixedName = config.applyPrefix(table.getName());
            table.setName(prefixedName);
            table.setBackendName(config.getBackendName());

            /////////////////////////////////////////////////////////////////////
            // Update FK references to use prefixed table names               //
            /////////////////////////////////////////////////////////////////////
            updateForeignKeyReferences(table);
         }

         ///////////////////////////////////////////////////////////////////////
         // Apply prefix to PossibleValueSource names                        //
         ///////////////////////////////////////////////////////////////////////
         if(output instanceof QPossibleValueSource pvs)
         {
            String prefixedName = config.applyPrefix(pvs.getName());
            pvs.setName(prefixedName);
            if(pvs.getTableName() != null)
            {
               pvs.setTableName(config.applyPrefix(pvs.getTableName()));
            }
         }

         ///////////////////////////////////////////////////////////////////////
         // Mark scope for all SourceQBitAware outputs                       //
         ///////////////////////////////////////////////////////////////////////
         if(output instanceof SourceQBitAware sqa)
         {
            sqa.setSourceQBitName(qBitMetaData.getName());
         }

         output.addSelfToInstance(qInstance);
      }
   }



   /*******************************************************************************
    ** Update foreign key references to use prefixed table names.
    *******************************************************************************/
   private void updateForeignKeyReferences(QTableMetaData table)
   {
      table.getFields().values().forEach(field ->
      {
         if(field.getPossibleValueSourceName() != null)
         {
            String pvsName = field.getPossibleValueSourceName();
            //////////////////////////////////////////////////////////////////
            // Only prefix our own tables, not external PVS references     //
            //////////////////////////////////////////////////////////////////
            if(pvsName.equals(Country.TABLE_NAME) ||
               pvsName.equals(StateProvince.TABLE_NAME) ||
               pvsName.equals(City.TABLE_NAME))
            {
               field.setPossibleValueSourceName(config.applyPrefix(pvsName));
            }
         }
      });
   }



   /*******************************************************************************
    ** Check if a producer should be enabled based on configuration.
    *******************************************************************************/
   private boolean isProducerEnabled(MetaDataProducerInterface<?> producer)
   {
      String className = producer.getClass().getSimpleName();

      if(className.contains("Country") && !Boolean.TRUE.equals(config.getEnableCountries()))
      {
         return false;
      }
      if(className.contains("StateProvince") && !Boolean.TRUE.equals(config.getEnableStateProvinces()))
      {
         return false;
      }
      if(className.contains("City") && !Boolean.TRUE.equals(config.getEnableCities()))
      {
         return false;
      }

      return true;
   }



   //////////////////////////////////////////////////////////////////////////////
   // Fluent setters                                                           //
   //////////////////////////////////////////////////////////////////////////////

   public GeoDataQBitProducer withConfig(GeoDataQBitConfig config)
   {
      this.config = config;
      return this;
   }


   public GeoDataQBitConfig getConfig()
   {
      return config;
   }
}
