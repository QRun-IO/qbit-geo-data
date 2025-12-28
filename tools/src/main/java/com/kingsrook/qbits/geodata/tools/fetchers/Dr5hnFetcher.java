/*******************************************************************************
 ** Fetcher for dr5hn/countries-states-cities-database on GitHub.
 **
 ** Data is licensed under ODbL (Open Database License).
 ** Source: https://github.com/dr5hn/countries-states-cities-database
 *******************************************************************************/
package com.kingsrook.qbits.geodata.tools.fetchers;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.zip.GZIPInputStream;


public class Dr5hnFetcher implements DataFetcher
{
   private static final String BASE_URL = "https://raw.githubusercontent.com/dr5hn/countries-states-cities-database/master/json/";

   private final HttpClient httpClient;



   /*******************************************************************************
    ** Constructor with default HTTP client.
    *******************************************************************************/
   public Dr5hnFetcher()
   {
      this.httpClient = HttpClient.newBuilder()
         .connectTimeout(Duration.ofSeconds(30))
         .build();
   }



   /*******************************************************************************
    ** Fetch countries data from dr5hn repository.
    *******************************************************************************/
   @Override
   public String fetchCountries() throws IOException
   {
      return fetch("countries.json");
   }



   /*******************************************************************************
    ** Fetch states/provinces data from dr5hn repository.
    *******************************************************************************/
   @Override
   public String fetchStates() throws IOException
   {
      return fetch("states.json");
   }



   /*******************************************************************************
    ** Fetch cities data from dr5hn repository.
    ** Cities file is gzip compressed, so we fetch and decompress it.
    *******************************************************************************/
   @Override
   public String fetchCities() throws IOException
   {
      return fetchGzip("cities.json.gz");
   }



   /*******************************************************************************
    ** Fetch and decompress a gzip file from the repository.
    *******************************************************************************/
   private String fetchGzip(String filename) throws IOException
   {
      String url = BASE_URL + filename;
      System.out.println("Fetching: " + url);

      try
      {
         HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMinutes(5))
            .GET()
            .build();

         HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

         if(response.statusCode() != 200)
         {
            throw new IOException("HTTP " + response.statusCode() + " fetching " + url);
         }

         byte[] compressed = response.body();
         System.out.println("  Fetched " + compressed.length + " bytes (compressed)");

         //////////////////////////////////////////////////////////////////////////
         // Decompress gzip content                                              //
         //////////////////////////////////////////////////////////////////////////
         try(GZIPInputStream gzipStream = new GZIPInputStream(new ByteArrayInputStream(compressed)))
         {
            String decompressed = new String(gzipStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("  Decompressed to " + decompressed.length() + " bytes");
            return decompressed;
         }
      }
      catch(InterruptedException e)
      {
         Thread.currentThread().interrupt();
         throw new IOException("Interrupted while fetching " + url, e);
      }
   }



   /*******************************************************************************
    ** Fetch a file from the repository.
    *******************************************************************************/
   private String fetch(String filename) throws IOException
   {
      String url = BASE_URL + filename;
      System.out.println("Fetching: " + url);

      try
      {
         HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMinutes(5))
            .GET()
            .build();

         HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

         if(response.statusCode() != 200)
         {
            throw new IOException("HTTP " + response.statusCode() + " fetching " + url);
         }

         String body = response.body();
         System.out.println("  Fetched " + body.length() + " bytes");
         return body;
      }
      catch(InterruptedException e)
      {
         Thread.currentThread().interrupt();
         throw new IOException("Interrupted while fetching " + url, e);
      }
   }
}
