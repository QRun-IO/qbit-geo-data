/*******************************************************************************
 ** Writes transformed data to JSON files with consistent formatting.
 **
 ** Sorts entries for stable git diffs and uses pretty-printing for readability.
 *******************************************************************************/
package com.kingsrook.qbits.geodata.tools.writers;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


public class JsonDataWriter
{
   private final Path outputDir;



   /*******************************************************************************
    ** Constructor.
    *******************************************************************************/
   public JsonDataWriter(Path outputDir)
   {
      this.outputDir = outputDir;
   }



   /*******************************************************************************
    ** Write countries data, sorted by alpha2Code.
    *******************************************************************************/
   public void writeCountries(List<JSONObject> data) throws IOException
   {
      data.sort(Comparator.comparing(o -> o.optString("alpha2Code", "")));
      write("countries.json", data);
   }



   /*******************************************************************************
    ** Write states data, sorted by countryAlpha2 + code.
    *******************************************************************************/
   public void writeStates(List<JSONObject> data) throws IOException
   {
      data.sort(Comparator
         .comparing((JSONObject o) -> o.optString("countryAlpha2", ""))
         .thenComparing(o -> o.optString("code", "")));
      write("states.json", data);
   }



   /*******************************************************************************
    ** Write cities data, sorted by countryAlpha2 + stateCode + name.
    *******************************************************************************/
   public void writeCities(List<JSONObject> data) throws IOException
   {
      data.sort(Comparator
         .comparing((JSONObject o) -> o.optString("countryAlpha2", ""))
         .thenComparing(o -> o.optString("stateCode", ""))
         .thenComparing(o -> o.optString("name", "")));
      write("cities.json", data);
   }



   /*******************************************************************************
    ** Write a JSON array to file with pretty formatting.
    *******************************************************************************/
   private void write(String filename, List<JSONObject> data) throws IOException
   {
      Path filePath = outputDir.resolve(filename);

      //////////////////////////////////////////////////////////////////////////
      // Ensure output directory exists                                       //
      //////////////////////////////////////////////////////////////////////////
      Files.createDirectories(outputDir);

      //////////////////////////////////////////////////////////////////////////
      // Build JSON array and format with 2-space indentation                 //
      //////////////////////////////////////////////////////////////////////////
      JSONArray array = new JSONArray(data);
      String json = array.toString(2);

      //////////////////////////////////////////////////////////////////////////
      // Write to file                                                        //
      //////////////////////////////////////////////////////////////////////////
      Files.writeString(filePath, json + "\n", StandardCharsets.UTF_8);
      System.out.println("  Wrote " + data.size() + " entries to " + filePath);
   }
}
