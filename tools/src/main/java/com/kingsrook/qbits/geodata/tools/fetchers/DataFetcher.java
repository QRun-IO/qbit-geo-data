/*******************************************************************************
 ** Interface for fetching geographic data from external sources.
 *******************************************************************************/
package com.kingsrook.qbits.geodata.tools.fetchers;


import java.io.IOException;


public interface DataFetcher
{
   /*******************************************************************************
    ** Fetch countries data as raw JSON string.
    *******************************************************************************/
   String fetchCountries() throws IOException;



   /*******************************************************************************
    ** Fetch states/provinces data as raw JSON string.
    *******************************************************************************/
   String fetchStates() throws IOException;



   /*******************************************************************************
    ** Fetch cities data as raw JSON string.
    *******************************************************************************/
   String fetchCities() throws IOException;
}
