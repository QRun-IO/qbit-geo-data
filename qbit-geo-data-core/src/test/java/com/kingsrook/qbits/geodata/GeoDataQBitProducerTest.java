/*******************************************************************************
 ** Unit tests for GeoDataQBitProducer.
 *******************************************************************************/
package com.kingsrook.qbits.geodata;


import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.QBackendMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class GeoDataQBitProducerTest
{

   /*******************************************************************************
    ** Test that producer throws exception when config is invalid.
    *******************************************************************************/
   @Test
   void testProduce_invalidConfig_throwsException()
   {
      GeoDataQBitProducer producer = new GeoDataQBitProducer()
         .withConfig(new GeoDataQBitConfig());  // Missing backendName

      QInstance qInstance = new QInstance();

      assertThatThrownBy(() -> producer.produce(qInstance, "test"))
         .isInstanceOf(QException.class)
         .hasMessageContaining("backendName is required");
   }



   /*******************************************************************************
    ** Test that producer throws exception when backend not found.
    *******************************************************************************/
   @Test
   void testProduce_backendNotFound_throwsException()
   {
      GeoDataQBitProducer producer = new GeoDataQBitProducer()
         .withConfig(new GeoDataQBitConfig()
            .withBackendName("nonexistent"));

      QInstance qInstance = new QInstance();

      assertThatThrownBy(() -> producer.produce(qInstance, "test"))
         .isInstanceOf(QException.class)
         .hasMessageContaining("Backend not found");
   }



   /*******************************************************************************
    ** Test that producer creates QBit metadata.
    *******************************************************************************/
   @Test
   void testProduce_createsQBitMetaData() throws QException
   {
      QInstance qInstance = new QInstance();
      qInstance.addBackend(new QBackendMetaData().withName("rdbms"));

      GeoDataQBitProducer producer = new GeoDataQBitProducer()
         .withConfig(new GeoDataQBitConfig()
            .withBackendName("rdbms")
            .withTableNamePrefix("geo")
            .withEnableCountries(true)
            .withEnableStateProvinces(false)
            .withEnableCities(false));

      producer.produce(qInstance, "my-geo-qbit");

      assertThat(qInstance.getQBits()).isNotEmpty();
      assertThat(qInstance.getQBits().values().stream()
         .anyMatch(qb -> "my-geo-qbit".equals(qb.getNamespace()))).isTrue();
   }



   /*******************************************************************************
    ** Test that producer stores config in QBit metadata.
    *******************************************************************************/
   @Test
   void testProduce_storesConfigInQBitMetaData() throws QException
   {
      QInstance qInstance = new QInstance();
      qInstance.addBackend(new QBackendMetaData().withName("rdbms"));

      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withBackendName("rdbms")
         .withTableNamePrefix("shipping")
         .withEnableCountries(true)
         .withEnableStateProvinces(false)
         .withEnableCities(false);

      GeoDataQBitProducer producer = new GeoDataQBitProducer()
         .withConfig(config);

      producer.produce(qInstance, "shipping-geo");

      assertThat(qInstance.getQBits().values().stream()
         .filter(qb -> "shipping-geo".equals(qb.getNamespace()))
         .findFirst()
         .map(qb -> qb.getConfig())
         .orElse(null)).isEqualTo(config);
   }



   /*******************************************************************************
    ** Test fluent withConfig method.
    *******************************************************************************/
   @Test
   void testWithConfig_returnsSelf()
   {
      GeoDataQBitProducer producer = new GeoDataQBitProducer();
      GeoDataQBitConfig config = new GeoDataQBitConfig();

      GeoDataQBitProducer result = producer.withConfig(config);

      assertThat(result).isSameAs(producer);
      assertThat(producer.getConfig()).isSameAs(config);
   }



   /*******************************************************************************
    ** Test constants are correctly defined.
    *******************************************************************************/
   @Test
   void testConstants()
   {
      assertThat(GeoDataQBitProducer.GROUP_ID).isEqualTo("com.kingsrook.qbits");
      assertThat(GeoDataQBitProducer.ARTIFACT_ID).isEqualTo("qbit-geo-data");
      assertThat(GeoDataQBitProducer.VERSION).isEqualTo("0.1.0");
   }
}
