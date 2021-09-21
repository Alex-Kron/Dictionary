package com.alexkron.dictionary.controller;

import com.alexkron.dictionary.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
public class DictionaryController {
    @Autowired
    DictionaryService dictionaryService;

    @GetMapping("/get/{id}")
    public String get(@PathVariable("id") String id) {
        return dictionaryService.get(id);
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") String id) {
        return dictionaryService.remove(id);
    }

    @GetMapping("/set/id/{id}/value/{value}")
    public String set(@PathVariable("id") String id, @PathVariable("value") String value) {
        if (dictionaryService.set(id, value)) {
            return "Successfully set";
        } else {
            return "Failed to set";
        }
    }

    @GetMapping("/set/id/{id}/value/{value}/ttl/{ttl}")
    public String set(@PathVariable("id") String id, @PathVariable("value") String value, @PathVariable("ttl") long ttl) {
        if (dictionaryService.set(id, value, ttl)) {
            return "Successfully set";
        } else {
            return "Failed to set";
        }
    }

    @GetMapping("/dump")
    public ResponseEntity<InputStreamResource> dump() throws FileNotFoundException {
        File file = dictionaryService.dump();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getName() +  "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @PostMapping("/load")
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name, @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            File sysFile = new File("loadedDump.txt");
            try (OutputStream os = new FileOutputStream(sysFile)) {
                os.write(file.getBytes());
            }
            dictionaryService.load(sysFile);
            return "Successfully loaded";
        } else {
            return "Loading failed";
        }
    }
}
