/*******************************************************************************
 ** Transforms dr5hn state data to our StateProvince entity format.
 **
 ** Source format: {"id": 1, "name": "...", "country_id": 233, "country_code": "US",
 **                 "state_code": "CA", "type": "state", ...}
 ** Target format: {"countryAlpha2": "US", "code": "CA", "name": "...", "subdivisionType": "State"}
 *******************************************************************************/
package com.kingsrook.qbits.geodata.tools.transformers;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;


public class StateTransformer implements DataTransformer
{
   private Set<String> countryFilter;



   /*******************************************************************************
    ** Set a filter to only include states for specific countries.
    *******************************************************************************/
   public StateTransformer withCountryFilter(Set<String> countryCodes)
   {
      this.countryFilter = countryCodes;
      return this;
   }



   /*******************************************************************************
    ** Transform dr5hn states JSON to our entity format.
    *******************************************************************************/
   @Override
   public List<JSONObject> transform(String sourceJson)
   {
      JSONArray sourceArray = new JSONArray(sourceJson);
      List<JSONObject> results = new ArrayList<>();

      for(int i = 0; i < sourceArray.length(); i++)
      {
         JSONObject source = sourceArray.getJSONObject(i);
         JSONObject target = transformOne(source);
         if(target != null)
         {
            results.add(target);
         }
      }

      System.out.println("  Transformed " + results.size() + " states/provinces");
      return results;
   }



   /*******************************************************************************
    ** Transform a single state record.
    *******************************************************************************/
   private JSONObject transformOne(JSONObject source)
   {
      String countryCode = source.optString("country_code", null);
      String stateCode = source.optString("iso2", null);

      if(countryCode == null || countryCode.isEmpty() || stateCode == null || stateCode.isEmpty())
      {
         return null;
      }

      //////////////////////////////////////////////////////////////////////////
      // Apply country filter if set                                          //
      //////////////////////////////////////////////////////////////////////////
      if(countryFilter != null && !countryFilter.contains(countryCode))
      {
         return null;
      }

      JSONObject target = new JSONObject();
      target.put("countryAlpha2", countryCode);
      target.put("code", stateCode);
      target.put("name", source.optString("name", null));

      //////////////////////////////////////////////////////////////////////////
      // Capitalize subdivision type                                          //
      //////////////////////////////////////////////////////////////////////////
      String type = source.optString("type", null);
      if(type != null && !type.isEmpty())
      {
         target.put("subdivisionType", capitalizeFirst(type));
      }

      return target;
   }



   /*******************************************************************************
    ** Capitalize first letter of a string.
    *******************************************************************************/
   private String capitalizeFirst(String str)
   {
      if(str == null || str.isEmpty())
      {
         return str;
      }
      return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
   }
}
