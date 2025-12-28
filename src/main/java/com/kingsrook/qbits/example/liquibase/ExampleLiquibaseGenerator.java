/*******************************************************************************
 ** Liquibase changelog generator for the Example Data QBit.
 **
 ** This generator reads a template changelog and:
 ** 1. Substitutes ${prefix} with the configured table prefix
 ** 2. Removes sections for disabled tables
 ** 3. Writes the final changelog to the specified output path
 *******************************************************************************/
package com.kingsrook.qbits.example.liquibase;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import com.kingsrook.qbits.example.ExampleDataQBitConfig;


public class ExampleLiquibaseGenerator
{
   private static final String TEMPLATE_PATH = "/db/changelog-template.xml";



   /*******************************************************************************
    ** Generate a Liquibase changelog from the template.
    **
    ** @param config the QBit configuration
    ** @param outputPath where to write the generated changelog
    *******************************************************************************/
   public static void generate(ExampleDataQBitConfig config, Path outputPath) throws IOException
   {
      String template = readTemplate();
      String prefix = config.getTableNamePrefix();

      if(prefix == null || prefix.isEmpty())
      {
         prefix = "example";
      }

      /////////////////////////////////////////////////////////////////////////
      // Substitute prefix tokens                                            //
      /////////////////////////////////////////////////////////////////////////
      String changelog = template.replace("${prefix}", prefix);

      /////////////////////////////////////////////////////////////////////////
      // Remove disabled table sections                                      //
      /////////////////////////////////////////////////////////////////////////
      if(!Boolean.TRUE.equals(config.getEnableExampleChildEntity()))
      {
         changelog = removeSection(changelog, "exampleChildEntity");
      }

      /////////////////////////////////////////////////////////////////////////
      // Write output                                                        //
      /////////////////////////////////////////////////////////////////////////
      Files.createDirectories(outputPath.getParent());
      Files.writeString(outputPath, changelog);
   }



   /*******************************************************************************
    ** Read the changelog template from classpath.
    *******************************************************************************/
   private static String readTemplate() throws IOException
   {
      try(InputStream is = ExampleLiquibaseGenerator.class.getResourceAsStream(TEMPLATE_PATH))
      {
         if(is == null)
         {
            throw new IOException("Template not found: " + TEMPLATE_PATH);
         }
         return new String(is.readAllBytes(), StandardCharsets.UTF_8);
      }
   }



   /*******************************************************************************
    ** Remove a section from the changelog by its marker comments.
    **
    ** Sections are marked with:
    ** <!-- SECTION: sectionName -->
    ** ... content ...
    ** <!-- END SECTION: sectionName -->
    *******************************************************************************/
   private static String removeSection(String content, String sectionName)
   {
      String startMarker = "<!-- SECTION: " + sectionName + " -->";
      String endMarker = "<!-- END SECTION: " + sectionName + " -->";

      String regex = "(?s)" + Pattern.quote(startMarker) + ".*?" + Pattern.quote(endMarker);
      return content.replaceAll(regex, "");
   }
}
