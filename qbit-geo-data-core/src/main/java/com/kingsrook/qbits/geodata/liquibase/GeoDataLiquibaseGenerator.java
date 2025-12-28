/*******************************************************************************
 ** Liquibase changelog generator for the Geographic Data QBit.
 **
 ** Reads the bundled changelog template and generates a final changelog with:
 ** - Prefix substitution for table/constraint/index names
 ** - Section removal for disabled entities
 *******************************************************************************/
package com.kingsrook.qbits.geodata.liquibase;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.utils.StringUtils;
import com.kingsrook.qbits.geodata.GeoDataQBitConfig;

import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


public class GeoDataLiquibaseGenerator
{
   private static final QLogger LOG = QLogger.getLogger(GeoDataLiquibaseGenerator.class);

   private static final String  TEMPLATE_RESOURCE = "/db/changelog-template.xml";
   private static final Pattern SECTION_PATTERN   = Pattern.compile(
      "\\s*<!-- SECTION: (\\w+) -->.*?<!-- END SECTION: \\1 -->\\s*",
      Pattern.DOTALL
   );



   /*******************************************************************************
    ** Generate a Liquibase changelog from the template using the provided config.
    **
    ** @param config the QBit configuration with prefix and enabled tables
    ** @param outputPath the path to write the generated changelog
    ** @throws IOException if template cannot be read or output cannot be written
    *******************************************************************************/
   public static void generate(GeoDataQBitConfig config, Path outputPath) throws IOException
   {
      String template = loadTemplate();
      String result = processTemplate(template, config);

      Files.createDirectories(outputPath.getParent());
      Files.writeString(outputPath, result, StandardCharsets.UTF_8);

      LOG.info("Generated Liquibase changelog", logPair("outputPath", outputPath));
   }



   /*******************************************************************************
    ** Generate changelog content as a string without writing to a file.
    **
    ** @param config the QBit configuration with prefix and enabled tables
    ** @return the generated changelog XML content
    ** @throws IOException if template cannot be read
    *******************************************************************************/
   public static String generate(GeoDataQBitConfig config) throws IOException
   {
      String template = loadTemplate();
      return processTemplate(template, config);
   }



   /*******************************************************************************
    ** Load the changelog template from classpath resources.
    *******************************************************************************/
   private static String loadTemplate() throws IOException
   {
      try(InputStream is = GeoDataLiquibaseGenerator.class.getResourceAsStream(TEMPLATE_RESOURCE))
      {
         if(is == null)
         {
            throw new IOException("Changelog template not found: " + TEMPLATE_RESOURCE);
         }
         return new String(is.readAllBytes(), StandardCharsets.UTF_8);
      }
   }



   /*******************************************************************************
    ** Process the template by removing disabled sections and substituting prefix.
    *******************************************************************************/
   private static String processTemplate(String template, GeoDataQBitConfig config)
   {
      String result = template;

      /////////////////////////////////////////////////////////////////////////
      // Build set of sections to remove based on disabled entities         //
      /////////////////////////////////////////////////////////////////////////
      Set<String> sectionsToRemove = new HashSet<>();
      if(!Boolean.TRUE.equals(config.getEnableCountries()))
      {
         sectionsToRemove.add("country");
      }
      if(!Boolean.TRUE.equals(config.getEnableStateProvinces()))
      {
         sectionsToRemove.add("stateProvince");
      }
      if(!Boolean.TRUE.equals(config.getEnableCities()))
      {
         sectionsToRemove.add("city");
      }

      /////////////////////////////////////////////////////////////////////////
      // Remove disabled sections                                            //
      /////////////////////////////////////////////////////////////////////////
      if(!sectionsToRemove.isEmpty())
      {
         Matcher matcher = SECTION_PATTERN.matcher(result);
         StringBuffer sb = new StringBuffer();
         while(matcher.find())
         {
            String sectionName = matcher.group(1);
            if(sectionsToRemove.contains(sectionName))
            {
               matcher.appendReplacement(sb, "\n");
            }
         }
         matcher.appendTail(sb);
         result = sb.toString();
      }

      /////////////////////////////////////////////////////////////////////////
      // Substitute prefix placeholder                                       //
      /////////////////////////////////////////////////////////////////////////
      String prefix = config.getTableNamePrefix();
      if(StringUtils.hasContent(prefix))
      {
         result = result.replace("${prefix}", prefix);
      }
      else
      {
         //////////////////////////////////////////////////////////////////////////
         // No prefix configured - remove the prefix and underscore              //
         //////////////////////////////////////////////////////////////////////////
         result = result.replace("${prefix}_", "");
         result = result.replace("${prefix}-", "");
         result = result.replace("${prefix}", "");
      }

      return result;
   }
}
