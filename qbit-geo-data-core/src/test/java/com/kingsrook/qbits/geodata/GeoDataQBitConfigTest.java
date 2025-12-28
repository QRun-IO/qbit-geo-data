/*******************************************************************************
 ** Unit tests for GeoDataQBitConfig.
 *******************************************************************************/
package com.kingsrook.qbits.geodata;


import java.util.ArrayList;
import java.util.List;
import com.kingsrook.qqq.backend.core.model.metadata.QBackendMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


class GeoDataQBitConfigTest
{

   /*******************************************************************************
    ** Test that validation fails when backendName is missing.
    *******************************************************************************/
   @Test
   void testValidate_missingBackendName_addsError()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig();
      QInstance qInstance = new QInstance();
      List<String> errors = new ArrayList<>();

      config.validate(qInstance, errors);

      assertThat(errors).contains("backendName is required for GeoDataQBit");
   }



   /*******************************************************************************
    ** Test that validation fails when backend is not found in QInstance.
    *******************************************************************************/
   @Test
   void testValidate_backendNotFound_addsError()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withBackendName("nonexistent");
      QInstance qInstance = new QInstance();
      List<String> errors = new ArrayList<>();

      config.validate(qInstance, errors);

      assertThat(errors).contains("Backend not found: nonexistent");
   }



   /*******************************************************************************
    ** Test that validation fails when cities enabled without states.
    *******************************************************************************/
   @Test
   void testValidate_citiesWithoutStates_addsError()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withBackendName("rdbms")
         .withEnableCities(true)
         .withEnableStateProvinces(false);

      QInstance qInstance = new QInstance();
      qInstance.addBackend(new QBackendMetaData().withName("rdbms"));
      List<String> errors = new ArrayList<>();

      config.validate(qInstance, errors);

      assertThat(errors).contains("enableStateProvinces must be true when enableCities is true (cities reference states)");
   }



   /*******************************************************************************
    ** Test that validation fails when states enabled without countries.
    *******************************************************************************/
   @Test
   void testValidate_statesWithoutCountries_addsError()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withBackendName("rdbms")
         .withEnableStateProvinces(true)
         .withEnableCountries(false);

      QInstance qInstance = new QInstance();
      qInstance.addBackend(new QBackendMetaData().withName("rdbms"));
      List<String> errors = new ArrayList<>();

      config.validate(qInstance, errors);

      assertThat(errors).contains("enableCountries must be true when enableStateProvinces is true (states reference countries)");
   }



   /*******************************************************************************
    ** Test that validation passes with valid configuration.
    *******************************************************************************/
   @Test
   void testValidate_validConfig_noErrors()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withBackendName("rdbms")
         .withTableNamePrefix("shipping")
         .withEnableCountries(true)
         .withEnableStateProvinces(true)
         .withEnableCities(true);

      QInstance qInstance = new QInstance();
      qInstance.addBackend(new QBackendMetaData().withName("rdbms"));
      List<String> errors = new ArrayList<>();

      config.validate(qInstance, errors);

      assertThat(errors).isEmpty();
   }



   /*******************************************************************************
    ** Test getEnabledTableNames returns all tables when all enabled.
    *******************************************************************************/
   @Test
   void testGetEnabledTableNames_allEnabled()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withEnableCountries(true)
         .withEnableStateProvinces(true)
         .withEnableCities(true);

      List<String> tables = config.getEnabledTableNames();

      assertThat(tables).containsExactly("country", "stateProvince", "city");
   }



   /*******************************************************************************
    ** Test getEnabledTableNames returns only countries when others disabled.
    *******************************************************************************/
   @Test
   void testGetEnabledTableNames_onlyCountries()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withEnableCountries(true)
         .withEnableStateProvinces(false)
         .withEnableCities(false);

      List<String> tables = config.getEnabledTableNames();

      assertThat(tables).containsExactly("country");
   }



   /*******************************************************************************
    ** Test applyPrefix with prefix configured.
    *******************************************************************************/
   @Test
   void testApplyPrefix_withPrefix()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withTableNamePrefix("shipping");

      assertThat(config.applyPrefix("country")).isEqualTo("shipping_country");
      assertThat(config.applyPrefix("stateProvince")).isEqualTo("shipping_stateProvince");
   }



   /*******************************************************************************
    ** Test applyPrefix without prefix returns original name.
    *******************************************************************************/
   @Test
   void testApplyPrefix_withoutPrefix()
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig();

      assertThat(config.applyPrefix("country")).isEqualTo("country");
   }
}
