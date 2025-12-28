/*******************************************************************************
 ** Main entry point for geo-data acquisition.
 **
 ** Fetches geographic data from external sources, transforms it to our entity
 ** format, and writes it to JSON files for inclusion in the QBit.
 **
 ** Usage:
 **   mvn exec:java
 **   mvn exec:java -Dexec.args="--min-city-population=50000"
 **   mvn exec:java -Dexec.args="--countries-only"
 *******************************************************************************/
package com.kingsrook.qbits.geodata.tools;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.kingsrook.qbits.geodata.tools.fetchers.DataFetcher;
import com.kingsrook.qbits.geodata.tools.fetchers.Dr5hnFetcher;
import com.kingsrook.qbits.geodata.tools.transformers.CityTransformer;
import com.kingsrook.qbits.geodata.tools.transformers.CountryTransformer;
import com.kingsrook.qbits.geodata.tools.transformers.StateTransformer;
import com.kingsrook.qbits.geodata.tools.writers.JsonDataWriter;
import org.json.JSONObject;


public class DataAcquisitionRunner
{
   private static final Path DEFAULT_OUTPUT_DIR = Paths.get("../qbit-geo-data-core/src/main/resources/data");

   private Path outputDir = DEFAULT_OUTPUT_DIR;
   private Integer minCityPopulation = null;
   private Set<String> countryFilter = null;
   private boolean countriesOnly = false;
   private boolean statesOnly = false;
   private boolean citiesOnly = false;



   /*******************************************************************************
    ** Main entry point.
    *******************************************************************************/
   public static void main(String[] args)
   {
      try
      {
         DataAcquisitionRunner runner = new DataAcquisitionRunner();
         runner.parseArgs(args);
         runner.run();
      }
      catch(Exception e)
      {
         System.err.println("Error: " + e.getMessage());
         e.printStackTrace();
         System.exit(1);
      }
   }



   /*******************************************************************************
    ** Parse command line arguments.
    *******************************************************************************/
   private void parseArgs(String[] args)
   {
      for(String arg : args)
      {
         if(arg.startsWith("--output-dir="))
         {
            outputDir = Paths.get(arg.substring("--output-dir=".length()));
         }
         else if(arg.startsWith("--min-city-population="))
         {
            minCityPopulation = Integer.parseInt(arg.substring("--min-city-population=".length()));
         }
         else if(arg.startsWith("--countries="))
         {
            String[] codes = arg.substring("--countries=".length()).split(",");
            countryFilter = new HashSet<>(Arrays.asList(codes));
         }
         else if(arg.equals("--countries-only"))
         {
            countriesOnly = true;
         }
         else if(arg.equals("--states-only"))
         {
            statesOnly = true;
         }
         else if(arg.equals("--cities-only"))
         {
            citiesOnly = true;
         }
         else if(arg.equals("--help") || arg.equals("-h"))
         {
            printHelp();
            System.exit(0);
         }
         else
         {
            System.err.println("Unknown argument: " + arg);
            printHelp();
            System.exit(1);
         }
      }
   }



   /*******************************************************************************
    ** Print usage help.
    *******************************************************************************/
   private void printHelp()
   {
      System.out.println("Usage: DataAcquisitionRunner [options]");
      System.out.println();
      System.out.println("Options:");
      System.out.println("  --output-dir=<path>          Output directory (default: ../qbit-geo-data-core/src/main/resources/data)");
      System.out.println("  --min-city-population=<n>    Only include cities with population >= n");
      System.out.println("  --countries=<codes>          Comma-separated country codes to include (e.g., US,CA,MX)");
      System.out.println("  --countries-only             Only fetch and write countries");
      System.out.println("  --states-only                Only fetch and write states");
      System.out.println("  --cities-only                Only fetch and write cities");
      System.out.println("  --help, -h                   Show this help");
   }



   /*******************************************************************************
    ** Run the data acquisition process.
    *******************************************************************************/
   public void run() throws Exception
   {
      System.out.println("=== Geo-Data Acquisition ===");
      System.out.println("Output directory: " + outputDir.toAbsolutePath());
      if(minCityPopulation != null)
      {
         System.out.println("Min city population: " + minCityPopulation);
      }
      if(countryFilter != null)
      {
         System.out.println("Country filter: " + countryFilter);
      }
      System.out.println();

      DataFetcher fetcher = new Dr5hnFetcher();
      JsonDataWriter writer = new JsonDataWriter(outputDir);

      //////////////////////////////////////////////////////////////////////////
      // Determine what to fetch                                              //
      //////////////////////////////////////////////////////////////////////////
      boolean fetchCountries = !statesOnly && !citiesOnly;
      boolean fetchStates = !countriesOnly && !citiesOnly;
      boolean fetchCities = !countriesOnly && !statesOnly;

      //////////////////////////////////////////////////////////////////////////
      // Fetch and transform countries                                        //
      //////////////////////////////////////////////////////////////////////////
      if(fetchCountries)
      {
         System.out.println("Countries:");
         String countriesJson = fetcher.fetchCountries();
         List<JSONObject> countries = new CountryTransformer().transform(countriesJson);
         writer.writeCountries(countries);
         System.out.println();
      }

      //////////////////////////////////////////////////////////////////////////
      // Fetch and transform states                                           //
      //////////////////////////////////////////////////////////////////////////
      if(fetchStates)
      {
         System.out.println("States/Provinces:");
         String statesJson = fetcher.fetchStates();
         StateTransformer transformer = new StateTransformer();
         if(countryFilter != null)
         {
            transformer.withCountryFilter(countryFilter);
         }
         List<JSONObject> states = transformer.transform(statesJson);
         writer.writeStates(states);
         System.out.println();
      }

      //////////////////////////////////////////////////////////////////////////
      // Fetch and transform cities                                           //
      //////////////////////////////////////////////////////////////////////////
      if(fetchCities)
      {
         System.out.println("Cities:");
         String citiesJson = fetcher.fetchCities();
         CityTransformer transformer = new CityTransformer();
         if(minCityPopulation != null)
         {
            transformer.withMinPopulation(minCityPopulation);
         }
         if(countryFilter != null)
         {
            transformer.withCountryFilter(countryFilter);
         }
         List<JSONObject> cities = transformer.transform(citiesJson);
         writer.writeCities(cities);
         System.out.println();
      }

      System.out.println("=== Complete ===");
   }
}
