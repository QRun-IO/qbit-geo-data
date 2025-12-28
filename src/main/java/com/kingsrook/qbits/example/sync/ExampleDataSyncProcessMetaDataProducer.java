/*******************************************************************************
 ** MetaData producer for the example data sync process.
 *******************************************************************************/
package com.kingsrook.qbits.example.sync;


import java.util.List;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;
import com.kingsrook.qqq.backend.core.model.metadata.processes.QBackendStepMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.processes.QProcessMetaData;
import com.kingsrook.qqq.backend.core.model.MetaDataProducerInterface;
import com.kingsrook.qbits.example.model.ExampleEntity;


public class ExampleDataSyncProcessMetaDataProducer implements MetaDataProducerInterface<QProcessMetaData>
{
   public static final String NAME = "exampleDataSyncProcess";



   /*******************************************************************************
    ** Produce the process metadata.
    *******************************************************************************/
   @Override
   public QProcessMetaData produce(QInstance qInstance) throws QException
   {
      return new QProcessMetaData()
         .withName(NAME)
         .withLabel("Example Data Sync")
         .withInputFields(List.of(
            new QFieldMetaData("tableName", QFieldType.STRING),
            new QFieldMetaData("naturalKeyField", QFieldType.STRING),
            new QFieldMetaData("dataResourcePath", QFieldType.STRING)
         ))
         .withStepList(List.of(
            new QBackendStepMetaData()
               .withName("sync")
               .withCode(new QCodeReference(ExampleDataSyncStep.class))
         ));
   }
}
