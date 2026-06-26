package com.aiarchitect.controller;

import com.aiarchitect.service.ExportService;
import com.aiarchitect.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * REST controller handling file downloads for architecture reports in different formats (PDF, Markdown, JSON).
 *
 * Ensures ownership validation by extracting principal token emails.
 */
@RestController
@RequestMapping(AppConstants.API_BASE_PATH + "/projects")
@RequiredArgsConstructor
@Tag(name = "Export", description = "Endpoints for downloading project architecture reports in different formats")
@SecurityRequirement(name = "bearerAuth")
public class ExportController {

    private final ExportService exportService;

    /**
     * Downloads the report formatted as Markdown (.md).
     */
    @GetMapping("/{id}/export/markdown")
    @Operation(summary = "Export report as Markdown file", description = "Returns a formatted .md file as an attachment download.")
    public ResponseEntity<byte[]> exportToMarkdown(@PathVariable Long id, Principal principal) {
        String markdown = exportService.exportToMarkdown(id, principal.getName());
        byte[] data = markdown.getBytes();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + id + ".md\"")
                .contentType(MediaType.parseMediaType("text/markdown"))
                .contentLength(data.length)
                .body(data);
    }

    /**
     * Downloads the report formatted as pretty-printed JSON (.json).
     */
    @GetMapping("/{id}/export/json")
    @Operation(summary = "Export report as JSON file", description = "Returns report fields as pretty-printed .json attachment download.")
    public ResponseEntity<byte[]> exportToJson(@PathVariable Long id, Principal principal) {
        String json = exportService.exportToJson(id, principal.getName());
        byte[] data = json.getBytes();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + id + ".json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(data.length)
                .body(data);
    }

    /**
     * Downloads the report formatted as print-ready PDF document (.pdf).
     */
    @GetMapping("/{id}/export/pdf")
    @Operation(summary = "Export report as print-ready PDF document", description = "Assembles and streams a styled PDF blueprint.")
    public ResponseEntity<byte[]> exportToPdf(@PathVariable Long id, Principal principal) {
        byte[] data = exportService.exportToPdf(id, principal.getName());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(data);
    }
}
