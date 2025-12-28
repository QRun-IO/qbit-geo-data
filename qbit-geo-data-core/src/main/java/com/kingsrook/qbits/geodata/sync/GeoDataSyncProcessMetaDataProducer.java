/*******************************************************************************
 ** MetaData producer for the Geo Data Sync process.
 **
 ** Defines a process that syncs all geographic reference data (countries,
 ** states/provinces, cities) from bundled JSON files into database tables.
 *******************************************************************************/
package com.kingsrook.qbits.geodata.sync;


import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeType;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;
import com.kingsrook.qqq.backend.core.model.metadata.processes.QBackendStepMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.processes.QFunctionInputMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.processes.QProcessMetaData;


public class GeoDataSyncProcessMetaDataProducer implements MetaDataProducerInterface<QProcessMetaData>
{
   public static final String NAME = "geoDataSync";



   /*******************************************************************************
    ** Produce the process metadata.
    *******************************************************************************/
   @Override
   public QProcessMetaData produce(QInstance qInstance)
   {
      QBackendStepMetaData syncStep = new QBackendStepMetaData()
         .withName("sync")
         .withCode(new QCodeReference()
            .withName(GeoDataSyncStep.class.getName())
            .withCodeType(QCodeType.JAVA))
         .withInputData(new QFunctionInputMetaData()
            .withField(new QFieldMetaData(GeoDataSyncStep.FIELD_TABLE_NAME_PREFIX, QFieldType.STRING)
               .withIsRequired(true)
               .withLabel("Table Name Prefix")));

      return new QProcessMetaData()
         .withName(NAME)
         .withLabel("Sync Geographic Data")
         .withStep(syncStep);
   }
}
