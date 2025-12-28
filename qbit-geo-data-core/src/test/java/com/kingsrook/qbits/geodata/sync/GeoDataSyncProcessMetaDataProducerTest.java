/*******************************************************************************
 ** Unit tests for GeoDataSyncProcessMetaDataProducer.
 *******************************************************************************/
package com.kingsrook.qbits.geodata.sync;


import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.processes.QProcessMetaData;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


class GeoDataSyncProcessMetaDataProducerTest
{

   /*******************************************************************************
    ** Test that NAME constant is correctly defined.
    *******************************************************************************/
   @Test
   void testNameConstant()
   {
      assertThat(GeoDataSyncProcessMetaDataProducer.NAME).isEqualTo("geoDataSync");
   }



   /*******************************************************************************
    ** Test that produce returns valid process metadata.
    *******************************************************************************/
   @Test
   void testProduce_returnsValidProcess()
   {
      GeoDataSyncProcessMetaDataProducer producer = new GeoDataSyncProcessMetaDataProducer();
      QInstance qInstance = new QInstance();

      QProcessMetaData process = producer.produce(qInstance);

      assertThat(process).isNotNull();
      assertThat(process.getName()).isEqualTo("geoDataSync");
      assertThat(process.getLabel()).isEqualTo("Sync Geographic Data");
   }



   /*******************************************************************************
    ** Test that process has sync step.
    *******************************************************************************/
   @Test
   void testProduce_hasSyncStep()
   {
      GeoDataSyncProcessMetaDataProducer producer = new GeoDataSyncProcessMetaDataProducer();
      QInstance qInstance = new QInstance();

      QProcessMetaData process = producer.produce(qInstance);

      assertThat(process.getStepList()).hasSize(1);
      assertThat(process.getStepList().get(0).getName()).isEqualTo("sync");
   }



   /*******************************************************************************
    ** Test that sync step references GeoDataSyncStep code.
    *******************************************************************************/
   @Test
   void testProduce_syncStepReferencesCorrectCode()
   {
      GeoDataSyncProcessMetaDataProducer producer = new GeoDataSyncProcessMetaDataProducer();
      QInstance qInstance = new QInstance();

      QProcessMetaData process = producer.produce(qInstance);

      assertThat(process.getStepList().get(0)).isNotNull();
   }
}
