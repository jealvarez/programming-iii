package org.umg.programming.iii.processor;

import org.umg.programming.iii.model.Field;

import java.util.Map;

public interface RecordProcessor {

    void createFile();

    Map<String, Field> getFieldsDefinition();

    void addFieldDefinition(final Field field);

    void addRecord(final Map<String, Object> record) throws Exception;

    Map<String, Object> search(final String valueToSearch) throws Exception;

    String[] getRecordsFromFile() throws Exception;

}
