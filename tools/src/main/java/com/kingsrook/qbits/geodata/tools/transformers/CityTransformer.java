/*******************************************************************************
 ** Transforms dr5hn city data to our City entity format.
 **
 ** Source format: {"id": 1, "name": "...", "state_id": 1416, "state_code": "CA",
 **                 "country_id": 233, "country_code": "US", "latitude": "34.05",
 **                 "longitude": "-118.24", ...}
 ** Target format: {"countryAlpha2": "US", "stateCode": "CA", "name": "...",
 **                 "latitude": 34.05, "longitude": -118.24, ...}
 *******************************************************************************/
package com.kingsrook.qbits.geodata.tools.transformers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;


public class CityTransformer implements DataTransformer
{
   private Integer minPopulation;
   private Set<String> countryFilter;



   /*******************************************************************************
    ** Set minimum population filter.
    *******************************************************************************/
   public CityTransformer withMinPopulation(Integer minPopulation)
   {
      this.minPopulation = minPopulation;
      return this;
   }



   /*******************************************************************************
    ** Set a filter to only include cities for specific countries.
    *******************************************************************************/
   public CityTransformer withCountryFilter(Set<String> countryCodes)
   {
      this.countryFilter = countryCodes;
      return this;
   }



   /*******************************************************************************
    ** Transform dr5hn cities JSON to our entity format.
    *******************************************************************************/
   @Override
   public List<JSONObject> transform(String sourceJson)
   {
      JSONArray sourceArray = new JSONArray(sourceJson);
      List<JSONObject> results = new ArrayList<>();

      int skippedPopulation = 0;
      int skippedCountry = 0;

      for(int i = 0; i < sourceArray.length(); i++)
      {
         JSONObject source = sourceArray.getJSONObject(i);

         //////////////////////////////////////////////////////////////////////////
         // Apply country filter first (fast path)                               //
         //////////////////////////////////////////////////////////////////////////
         String countryCode = source.optString("country_code", null);
         if(countryFilter != null && !countryFilter.contains(countryCode))
         {
            skippedCountry++;
            continue;
         }

         //////////////////////////////////////////////////////////////////////////
         // Apply population filter                                              //
         //////////////////////////////////////////////////////////////////////////
         if(minPopulation != null)
         {
            int population = source.optInt("population", 0);
            if(population < minPopulation)
            {
               skippedPopulation++;
               continue;
            }
         }

         JSONObject target = transformOne(source);
         if(target != null)
         {
            results.add(target);
         }
      }

      System.out.println("  Transformed " + results.size() + " cities" +
         (skippedPopulation > 0 ? " (skipped " + skippedPopulation + " below population threshold)" : "") +
         (skippedCountry > 0 ? " (skipped " + skippedCountry + " outside country filter)" : ""));
      return results;
   }



   /*******************************************************************************
    ** Transform a single city record.
    *******************************************************************************/
   private JSONObject transformOne(JSONObject source)
   {
      String countryCode = source.optString("country_code", null);
      String stateCode = source.optString("state_code", null);
      String name = source.optString("name", null);

      if(countryCode == null || countryCode.isEmpty() || name == null || name.isEmpty())
      {
         return null;
      }

      JSONObject target = new JSONObject();
      target.put("countryAlpha2", countryCode);
      target.put("stateCode", stateCode != null && !stateCode.isEmpty() ? stateCode : JSONObject.NULL);
      target.put("name", name);

      //////////////////////////////////////////////////////////////////////////
      // ASCII name for search/sorting                                        //
      //////////////////////////////////////////////////////////////////////////
      String wikiDataId = source.optString("wikiDataId", null);
      if(wikiDataId != null && !wikiDataId.isEmpty())
      {
         // We don't need wikiDataId, but could use it for lookups if needed
      }

      //////////////////////////////////////////////////////////////////////////
      // Population                                                           //
      //////////////////////////////////////////////////////////////////////////
      int population = source.optInt("population", 0);
      if(population > 0)
      {
         target.put("population", population);
      }

      //////////////////////////////////////////////////////////////////////////
      // Coordinates - parse as BigDecimal for precision                      //
      //////////////////////////////////////////////////////////////////////////
      String latStr = source.optString("latitude", null);
      String lonStr = source.optString("longitude", null);

      if(latStr != null && !latStr.isEmpty())
      {
         try
         {
            target.put("latitude", new BigDecimal(latStr));
         }
         catch(NumberFormatException e)
         {
            // Skip invalid coordinates
         }
      }

      if(lonStr != null && !lonStr.isEmpty())
      {
         try
         {
            target.put("longitude", new BigDecimal(lonStr));
         }
         catch(NumberFormatException e)
         {
            // Skip invalid coordinates
         }
      }

      return target;
   }
}
