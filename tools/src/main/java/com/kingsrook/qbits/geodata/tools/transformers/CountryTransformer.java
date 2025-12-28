/*******************************************************************************
 ** Transforms dr5hn country data to our Country entity format.
 **
 ** Source format: {"id": 1, "name": "...", "iso2": "US", "iso3": "USA", "numeric_code": "840", ...}
 ** Target format: {"alpha2Code": "US", "alpha3Code": "USA", "numericCode": 840, "name": "..."}
 *******************************************************************************/
package com.kingsrook.qbits.geodata.tools.transformers;


import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


public class CountryTransformer implements DataTransformer
{


   /*******************************************************************************
    ** Transform dr5hn countries JSON to our entity format.
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

      System.out.println("  Transformed " + results.size() + " countries");
      return results;
   }



   /*******************************************************************************
    ** Transform a single country record.
    *******************************************************************************/
   private JSONObject transformOne(JSONObject source)
   {
      String iso2 = source.optString("iso2", null);
      if(iso2 == null || iso2.isEmpty())
      {
         return null;
      }

      JSONObject target = new JSONObject();
      target.put("alpha2Code", iso2);
      target.put("alpha3Code", source.optString("iso3", null));

      //////////////////////////////////////////////////////////////////////////
      // Parse numeric code - may be string or int in source                  //
      //////////////////////////////////////////////////////////////////////////
      Object numericCode = source.opt("numeric_code");
      if(numericCode != null)
      {
         if(numericCode instanceof Number)
         {
            target.put("numericCode", ((Number) numericCode).intValue());
         }
         else if(numericCode instanceof String && !((String) numericCode).isEmpty())
         {
            try
            {
               target.put("numericCode", Integer.parseInt((String) numericCode));
            }
            catch(NumberFormatException e)
            {
               // Skip invalid numeric codes
            }
         }
      }

      target.put("name", source.optString("name", null));

      //////////////////////////////////////////////////////////////////////////
      // Use native name as official name if different                        //
      //////////////////////////////////////////////////////////////////////////
      String nativeName = source.optString("native", null);
      if(nativeName != null && !nativeName.equals(source.optString("name")))
      {
         target.put("officialName", nativeName);
      }

      return target;
   }
}
