/*******************************************************************************
 ** Producer for the Example Data QBit.
 *******************************************************************************/
package com.kingsrook.qbits.example;

import java.util.List;

import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerHelper;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerOutput;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitProducer;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.SourceQBitAware;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;


/*******************************************************************************
 ** Producer class for the Example Data QBit.
 **
 ** Handles table prefixing and scope tracking for multi-instance support.
 *******************************************************************************/
public class ExampleDataQBitProducer implements QBitProducer
{
   public static final String GROUP_ID    = "com.kingsrook.qbits";
   public static final String ARTIFACT_ID = "qbit-example-data";
   public static final String VERSION     = "0.1.0";

   private ExampleDataQBitConfig config;



   /*******************************************************************************
    ** Produce this QBit into the given QInstance.
    *******************************************************************************/
   @Override
   public void produce(QInstance qInstance, String namespace) throws QException
   {
      ///////////////////////////////////////////
      // Create and register QBit identity    //
      ///////////////////////////////////////////
      QBitMetaData qBitMetaData = new QBitMetaData()
         .withGroupId(GROUP_ID)
         .withArtifactId(ARTIFACT_ID)
         .withVersion(VERSION)
         .withNamespace(namespace)
         .withConfig(config);

      qInstance.addQBit(qBitMetaData);

      ///////////////////////////////////////////
      // Discover producers in this package   //
      ///////////////////////////////////////////
      List<MetaDataProducerInterface<?>> producers =
         MetaDataProducerHelper.findProducers(getClass().getPackageName());

      ///////////////////////////////////////////
      // Produce each with prefix and scope   //
      ///////////////////////////////////////////
      for(MetaDataProducerInterface<?> producer : producers)
      {
         MetaDataProducerOutput output = producer.produce(qInstance);

         // Apply prefix to table names
         if(output instanceof QTableMetaData table)
         {
            String prefixedName = config.applyPrefix(table.getName());
            table.setName(prefixedName);
            table.setBackendName(config.getBackendName());
         }

         // Mark scope
         if(output instanceof SourceQBitAware sqa)
         {
            sqa.setSourceQBitName(qBitMetaData.getName());
         }

         output.addSelfToInstance(qInstance);
      }
   }



   /*******************************************************************************
    ** Fluent setter for config
    *******************************************************************************/
   public ExampleDataQBitProducer withConfig(ExampleDataQBitConfig config)
   {
      this.config = config;
      return this;
   }
}
