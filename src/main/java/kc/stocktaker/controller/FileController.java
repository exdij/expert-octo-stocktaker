package kc.stocktaker.controller;


import kc.stocktaker.dto.ProductDTO;
import kc.stocktaker.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public List<ProductDTO> importDataFromFile(@RequestParam MultipartFile file,
                                               @RequestParam int accountingYear,
                                               @RequestParam(required = false, defaultValue = "false")
                                                           boolean forceOverwrite) throws IOException {
        return fileService.importFile(file, accountingYear, forceOverwrite);
    }

    @GetMapping
    public void generateFileFromProducts(@RequestParam int accountingYear,
                                         HttpServletResponse response) throws IOException {
        fileService.generateAndDownloadImportFile(accountingYear, response);
    }
}
