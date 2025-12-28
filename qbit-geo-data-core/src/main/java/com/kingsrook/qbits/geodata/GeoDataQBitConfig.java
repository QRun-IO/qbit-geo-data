/*******************************************************************************
 ** Configuration class for the Geographic Data QBit.
 **
 ** Supports:
 ** - Table prefixing for multi-instance deployment
 ** - Selective table enablement (countries, states, cities)
 ** - Country filtering for regional deployments
 *******************************************************************************/
package com.kingsrook.qbits.geodata;


import java.util.ArrayList;
import java.util.List;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitConfig;
import com.kingsrook.qqq.backend.core.utils.StringUtils;
import com.kingsrook.qbits.geodata.model.City;
import com.kingsrook.qbits.geodata.model.Country;
import com.kingsrook.qbits.geodata.model.StateProvince;


public class GeoDataQBitConfig implements QBitConfig
{
   private String       backendName;
   private String       tableNamePrefix;
   private Boolean      enableCountries      = true;
   private Boolean      enableStateProvinces = true;
   private Boolean      enableCities         = true;
   private List<String> countryFilter;  // Limit to specific alpha2 codes



   /*******************************************************************************
    ** Validate the configuration before the QBit is produced.
    *******************************************************************************/
   @Override
   public void validate(QInstance qInstance, List<String> errors)
   {
      if(!StringUtils.hasContent(backendName))
      {
         errors.add("backendName is required for GeoDataQBit");
      }

      if(qInstance.getBackend(backendName) == null)
      {
         errors.add("Backend not found: " + backendName);
      }

      if(Boolean.TRUE.equals(enableCities) && !Boolean.TRUE.equals(enableStateProvinces))
      {
         errors.add("enableStateProvinces must be true when enableCities is true (cities reference states)");
      }

      if(Boolean.TRUE.equals(enableStateProvinces) && !Boolean.TRUE.equals(enableCountries))
      {
         errors.add("enableCountries must be true when enableStateProvinces is true (states reference countries)");
      }
   }



   /*******************************************************************************
    ** Get list of enabled table names (without prefix).
    *******************************************************************************/
   public List<String> getEnabledTableNames()
   {
      List<String> tables = new ArrayList<>();
      if(Boolean.TRUE.equals(enableCountries))
      {
         tables.add(Country.TABLE_NAME);
      }
      if(Boolean.TRUE.equals(enableStateProvinces))
      {
         tables.add(StateProvince.TABLE_NAME);
      }
      if(Boolean.TRUE.equals(enableCities))
      {
         tables.add(City.TABLE_NAME);
      }
      return tables;
   }



   /*******************************************************************************
    ** Apply prefix to a table name if configured.
    *******************************************************************************/
   public String applyPrefix(String tableName)
   {
      if(StringUtils.hasContent(tableNamePrefix))
      {
         return tableNamePrefix + "_" + tableName;
      }
      return tableName;
   }



   //////////////////////////////////////////////////////////////////////////////
   // Getters and fluent setters                                               //
   //////////////////////////////////////////////////////////////////////////////

   public String getBackendName()
   {
      return backendName;
   }


   public GeoDataQBitConfig withBackendName(String backendName)
   {
      this.backendName = backendName;
      return this;
   }


   public String getTableNamePrefix()
   {
      return tableNamePrefix;
   }


   public GeoDataQBitConfig withTableNamePrefix(String tableNamePrefix)
   {
      this.tableNamePrefix = tableNamePrefix;
      return this;
   }


   public Boolean getEnableCountries()
   {
      return enableCountries;
   }


   public GeoDataQBitConfig withEnableCountries(Boolean enableCountries)
   {
      this.enableCountries = enableCountries;
      return this;
   }


   public Boolean getEnableStateProvinces()
   {
      return enableStateProvinces;
   }


   public GeoDataQBitConfig withEnableStateProvinces(Boolean enableStateProvinces)
   {
      this.enableStateProvinces = enableStateProvinces;
      return this;
   }


   public Boolean getEnableCities()
   {
      return enableCities;
   }


   public GeoDataQBitConfig withEnableCities(Boolean enableCities)
   {
      this.enableCities = enableCities;
      return this;
   }


   public List<String> getCountryFilter()
   {
      return countryFilter;
   }


   public GeoDataQBitConfig withCountryFilter(List<String> countryFilter)
   {
      this.countryFilter = countryFilter;
      return this;
   }
}
