package org.umg.programming.iii.processor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.umg.programming.iii.model.Field;
import org.umg.programming.iii.structure.tree.BTree;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class RecordProcessorImpl implements RecordProcessor {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static int lineNumber = 0;

    @Value("${file.path}")
    private String filePath;

    private File file;
    private boolean primaryKeyInserted = false;

    final private Map<String, Field> fieldsDefinition = new LinkedHashMap<>();
    final private BTree<String, Integer> bTree = new BTree<>();

    @Override
    public void createFile() {
        file = new File(filePath);
        FileUtils.deleteQuietly(file);
    }

    @Override
    public Map<String, Field> getFieldsDefinition() {
        return fieldsDefinition;
    }

    @Override
    public void addFieldDefinition(final Field field) {
        if (field.isPrimaryKey() && !primaryKeyInserted) {
            primaryKeyInserted = true;
            fieldsDefinition.put(field.getName(), field);
        }

        if (!field.isPrimaryKey()) {
            fieldsDefinition.put(field.getName(), field);
        }
    }

    @Override
    public void addRecord(final Map<String, Object> recordToBeWrite) throws Exception {
        boolean wasInserted = false;
        for (Map.Entry<String, Object> entry : recordToBeWrite.entrySet()) {
            final Field field = fieldsDefinition.get(entry.getKey());

            if (field.isPrimaryKey()) {
                if (isRecordFound(String.valueOf(entry.getValue())) == null) {
                    bTree.insert(String.valueOf(entry.getValue()), lineNumber);
                    wasInserted = true;
                }
            }

            if (wasInserted) {
                final String recordToWrite = prepareRecordToWriteInFile(entry.getValue(), field.getLength(), "");
                writeToFile(file, recordToWrite);
            } else {
                break;
            }
        }

        if (wasInserted) {
            lineNumber++;
            writeToFile(file, LINE_SEPARATOR);
        }
    }

    @Override
    public Map<String, Object> search(final String valueToSearch) throws Exception {
        final Integer recordFoundAtPosition = isRecordFound(valueToSearch);
        if (recordFoundAtPosition != null) {
            final String recordInFile = FileUtils.readLines(new File(filePath), UTF_8).get(recordFoundAtPosition);
            return transformRecordInFileToStructure(recordInFile);
        }

        return null;
    }

    @Override
    public String[] getRecordsFromFile() throws Exception {
        final String fileContent = FileUtils.readFileToString(file, UTF_8);
        final String[] records = StringUtils.split(fileContent, LINE_SEPARATOR);
        return records;
    }

    @Override
    public String getBTreeContent() {
        return bTree.traverse();
    }

    private Integer isRecordFound(final String valueToSearch) {
        final Integer lineNumber = bTree.search(valueToSearch);

        if (lineNumber != null) {
            return lineNumber;
        }

        return null;
    }

    private String prepareRecordToWriteInFile(final Object record, final int size, final String padText) {
        return StringUtils.rightPad(String.valueOf(record), size, padText);
    }

    private void writeToFile(final File file, final String text) throws Exception {
        FileUtils.writeStringToFile(file, text, UTF_8, true);
    }

    private Map<String, Object> transformRecordInFileToStructure(final String recordInFile) {
        final Map<String, Object> record = new LinkedHashMap<>();
        int startIndex = 0;
        for (Map.Entry<String, Field> entry : fieldsDefinition.entrySet()) {
            final int position = entry.getValue().getLength() + startIndex;
            record.put(entry.getKey(), StringUtils.substring(recordInFile, startIndex, position - 1).trim());
            startIndex = position;
        }
        return record;
    }

}
