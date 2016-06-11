package org.umg.programming.iii.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.umg.programming.iii.model.Field;
import org.umg.programming.iii.model.Search;
import org.umg.programming.iii.processor.RecordProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class MainController {

    @Autowired
    private RecordProcessor recordProcessor;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/", method = GET)
    public String getMain() {
        return "main";
    }

    @RequestMapping(value = "/create-file", method = GET)
    public String createFile() {
        recordProcessor.createFile();
        return "main";
    }

    @RequestMapping(value = "/define-field-structure", method = GET)
    public String defineFieldStructure(final Model model) {
        model.addAttribute("field", new Field());
        return "field-structure";
    }

    @RequestMapping(value = "/define-field-structure", method = POST)
    public String defineFieldStructureSubmit(@ModelAttribute final Field field, final Model model) {
        model.addAttribute("field", new Field());
        recordProcessor.addFieldDefinition(field);
        return "field-structure";
    }

    @RequestMapping(value = "/insert-record", method = GET)
    public String insertRecord(final Model model) throws Exception {
        final String fieldsDefinition = objectMapper.writeValueAsString(recordProcessor.getFieldsDefinition());
        model.addAttribute("fieldsDefinition", fieldsDefinition);
        return "insert-record";
    }

    @RequestMapping(value = "/insert-record", method = POST)
    public String insertRecordSubmit(@RequestBody final String record, final Model model) throws Exception {
        final TypeReference<LinkedHashMap<String, Object>> typeReference = new TypeReference<LinkedHashMap<String, Object>>() { };
        final LinkedHashMap<String, Object> recordMap = objectMapper.readValue(record, typeReference);
        recordProcessor.addRecord(recordMap);

        return "insert-record";
    }

    @RequestMapping(value = "/search", method = GET)
    public String search(final Model model) {
        model.addAttribute("search", new Search());
        return "search";
    }

    @RequestMapping(value = "/search", method = POST)
    public String search(@ModelAttribute final Search search, final Model model) throws Exception {
        model.addAttribute("search", new Search());
        final Map<String, Object> recordMap = recordProcessor.search(search.getValueToSearch());
        model.addAttribute("recordMap", recordMap);
        return "search";
    }

    @RequestMapping(value = "/display", method = GET)
    public String displayRecordsInFile(final Model model) throws Exception {
        final String[] records = recordProcessor.getRecordsFromFile();
        model.addAttribute("records", records);
        return "display-file";
    }

    @RequestMapping(value = "/display-tree", method = GET)
    public String displayBTreeContent(final Model model) throws Exception {
        String content = recordProcessor.getBTreeContent();
        model.addAttribute("content", content);
        return "display-tree";
    }

}
