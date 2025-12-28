/*******************************************************************************
 ** Unit tests for GeoDataLiquibaseGenerator.
 *******************************************************************************/
package com.kingsrook.qbits.geodata.liquibase;


import java.io.IOException;
import com.kingsrook.qbits.geodata.GeoDataQBitConfig;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


class GeoDataLiquibaseGeneratorTest
{

   /*******************************************************************************
    ** Test that prefix is substituted in generated changelog.
    *******************************************************************************/
   @Test
   void testGenerate_substitutesPrefix() throws IOException
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withTableNamePrefix("shipping")
         .withEnableCountries(true)
         .withEnableStateProvinces(true)
         .withEnableCities(true);

      String result = GeoDataLiquibaseGenerator.generate(config);

      assertThat(result).contains("tableName=\"shipping_country\"");
      assertThat(result).contains("tableName=\"shipping_state_province\"");
      assertThat(result).contains("tableName=\"shipping_city\"");
      assertThat(result).contains("id=\"shipping-create-country-v1\"");
      assertThat(result).doesNotContain("${prefix}");
   }



   /*******************************************************************************
    ** Test that city section is removed when cities disabled.
    *******************************************************************************/
   @Test
   void testGenerate_removesCitySection() throws IOException
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withTableNamePrefix("test")
         .withEnableCountries(true)
         .withEnableStateProvinces(true)
         .withEnableCities(false);

      String result = GeoDataLiquibaseGenerator.generate(config);

      assertThat(result).contains("test_country");
      assertThat(result).contains("test_state_province");
      assertThat(result).doesNotContain("test_city");
      assertThat(result).doesNotContain("SECTION: city");
   }



   /*******************************************************************************
    ** Test that stateProvince section is removed when states disabled.
    *******************************************************************************/
   @Test
   void testGenerate_removesStateProvinceSection() throws IOException
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withTableNamePrefix("test")
         .withEnableCountries(true)
         .withEnableStateProvinces(false)
         .withEnableCities(false);

      String result = GeoDataLiquibaseGenerator.generate(config);

      assertThat(result).contains("test_country");
      assertThat(result).doesNotContain("test_state_province");
      assertThat(result).doesNotContain("test_city");
   }



   /*******************************************************************************
    ** Test that all sections removed when all disabled.
    *******************************************************************************/
   @Test
   void testGenerate_removesAllSections() throws IOException
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withTableNamePrefix("test")
         .withEnableCountries(false)
         .withEnableStateProvinces(false)
         .withEnableCities(false);

      String result = GeoDataLiquibaseGenerator.generate(config);

      assertThat(result).doesNotContain("createTable");
      assertThat(result).contains("databaseChangeLog");
   }



   /*******************************************************************************
    ** Test generation without prefix removes placeholder.
    *******************************************************************************/
   @Test
   void testGenerate_noPrefix() throws IOException
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withEnableCountries(true)
         .withEnableStateProvinces(false)
         .withEnableCities(false);

      String result = GeoDataLiquibaseGenerator.generate(config);

      assertThat(result).contains("tableName=\"country\"");
      assertThat(result).doesNotContain("${prefix}");
      assertThat(result).doesNotContain("_country\"");
   }



   /*******************************************************************************
    ** Test that foreign key constraints use correct prefixed table names.
    *******************************************************************************/
   @Test
   void testGenerate_foreignKeysUsePrefixedNames() throws IOException
   {
      GeoDataQBitConfig config = new GeoDataQBitConfig()
         .withTableNamePrefix("billing")
         .withEnableCountries(true)
         .withEnableStateProvinces(true)
         .withEnableCities(true);

      String result = GeoDataLiquibaseGenerator.generate(config);

      assertThat(result).contains("referencedTableName=\"billing_country\"");
      assertThat(result).contains("referencedTableName=\"billing_state_province\"");
      assertThat(result).contains("constraintName=\"billing_fk_state_province_country\"");
   }
}
