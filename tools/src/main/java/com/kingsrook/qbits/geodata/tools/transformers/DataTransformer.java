/*******************************************************************************
 ** Interface for transforming source data to entity format.
 *******************************************************************************/
package com.kingsrook.qbits.geodata.tools.transformers;


import java.util.List;
import org.json.JSONObject;


public interface DataTransformer
{
   /*******************************************************************************
    ** Transform raw JSON string to list of entity-formatted JSON objects.
    *******************************************************************************/
   List<JSONObject> transform(String sourceJson);
}
