/*******************************************************************************
 ** Unit tests for GeoDataSyncStep.
 *******************************************************************************/
package com.kingsrook.qbits.geodata.sync;


import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepInput;
import com.kingsrook.qqq.backend.core.model.actions.processes.RunBackendStepOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class GeoDataSyncStepTest
{

   /*******************************************************************************
    ** Test that constant is correctly defined.
    *******************************************************************************/
   @Test
   void testFieldConstant()
   {
      assertThat(GeoDataSyncStep.FIELD_TABLE_NAME_PREFIX).isEqualTo("tableNamePrefix");
   }



   /*******************************************************************************
    ** Test that run throws exception when prefix is missing.
    *******************************************************************************/
   @Test
   void testRun_missingPrefix_throwsException()
   {
      GeoDataSyncStep step = new GeoDataSyncStep();
      RunBackendStepInput input = new RunBackendStepInput();
      RunBackendStepOutput output = new RunBackendStepOutput();

      assertThatThrownBy(() -> step.run(input, output))
         .isInstanceOf(QException.class)
         .hasMessageContaining("tableNamePrefix is required");
   }



   /*******************************************************************************
    ** Test that run throws exception when prefix is empty.
    *******************************************************************************/
   @Test
   void testRun_emptyPrefix_throwsException()
   {
      GeoDataSyncStep step = new GeoDataSyncStep();
      RunBackendStepInput input = new RunBackendStepInput();
      input.addValue(GeoDataSyncStep.FIELD_TABLE_NAME_PREFIX, "");
      RunBackendStepOutput output = new RunBackendStepOutput();

      assertThatThrownBy(() -> step.run(input, output))
         .isInstanceOf(QException.class)
         .hasMessageContaining("tableNamePrefix is required");
   }
}
