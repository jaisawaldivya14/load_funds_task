package com.vault.load.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FileWriterService {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${classpath:src/main/resources/actual_output.txt}")
    private String filePath;

    public void writeListToFile(List<String> list) throws IOException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            for (String item : list) {
                writer.write(item + "\n");
            }
        }
    }

}


