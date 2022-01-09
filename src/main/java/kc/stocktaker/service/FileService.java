package kc.stocktaker.service;

import kc.stocktaker.dto.ProductDTO;
import kc.stocktaker.entity.Product;
import kc.stocktaker.mapper.ProductMapper;
import kc.stocktaker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public List<ProductDTO> importFile(MultipartFile file, int accountingYear,
                                       boolean forceOverwrite) throws IOException {
        List<ProductDTO> products = readProductsFromFile(file, accountingYear);
        saveProductsToDatabase(products, accountingYear, forceOverwrite);

        return products;
    }

    private void saveProductsToDatabase(List<ProductDTO> productDTOS, int accountingYear, boolean forceOverwrite) {
        List<Product> products = productDTOS.stream()
                .map(productMapper::mapProductDTOToEntity)
                .collect(Collectors.toList());
        if(productRepository.existsByAccountingYear(accountingYear)) {
            if(forceOverwrite) {
                productRepository.deleteAllByAccountingYear(accountingYear);
            } else {
                throw new IllegalArgumentException(
                        "Products from accounting year " + accountingYear + " already exist in database." +
                                " Set flag forceOverwrite to true if you wish to continue");
            }
        }
        productRepository.saveAll(products);
    }

    private List<ProductDTO> readProductsFromFile(MultipartFile file, int accountingYear) throws IOException {
        InputStream inputStream = file.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream, "windows-1250"))
                .lines()
                .filter(line -> line.startsWith("Linia"))
                .map(line -> readLine(line, accountingYear))
                .collect(Collectors.toList());
    }

    private ProductDTO readLine(String line, int accountingYear) {
        String newLine = line.replace("}", "{");
        String[] splitted = newLine.split("\\{");
        String name = splitted[1];
        long ean = Long.parseLong(splitted[3]);
        String category = splitted[9];
        BigDecimal quantity = new BigDecimal(splitted[15]);
        BigDecimal grossCost = new BigDecimal(splitted[17].substring(1));
        BigDecimal grossValue =  new BigDecimal(splitted[19].substring(1));
        int productId = Integer.parseInt(splitted[27]);

        return ProductDTO.builder()
                .name(name)
                .ean(ean)
                .category(category)
                .quantity(quantity)
                .startingQuantity(quantity)
                .grossCost(grossCost)
                .grossValue(grossValue)
                .productId(productId)
                .accountingYear(accountingYear)
                .build();
    }

    public void generateAndDownloadImportFile(int accountingYear, HttpServletResponse response)
            throws IOException {

        List<ProductDTO> products = productRepository.findAllByAccountingYear(accountingYear).stream()
                .map(productMapper::mapEntityToProductDTO)
                .collect(Collectors.toList());
        downloadImportFile(response, products);
    }

    private void downloadImportFile(HttpServletResponse response, List<ProductDTO> products) throws IOException {
        String filename = "import.txt";
        setupResponseProperties(response, filename);
        writeProductsToServletResponse(response, products);
    }

    private void writeProductsToServletResponse(HttpServletResponse response, List<ProductDTO> products) throws IOException {
        OutputStream out = response.getOutputStream();
        String init = "TypPolskichLiter:WIN\n" +
                "TypDok:RM\n" +
                "NrDok:REM/22/1\n" +
                "IloscLinii:21668\n";
        writeToStream(out, init);

        products.stream()
                .map(this::getProductLine)
                .forEach(line -> {
                    safelyWriteToStream(out, line);
                });
        out.close();
    }

    private void safelyWriteToStream(OutputStream out, String line) {
        try {
            writeToStream(out, line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToStream(OutputStream out, String line) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(line.getBytes("windows-1250"));
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        inputStream.close();
    }

    private String getProductLine(ProductDTO product) {
        return String.format("Linia:Kod{%s}Ilosc{%s}\n", product.getEan(), product.getQuantity());
    }

    private void setupResponseProperties(HttpServletResponse response, String fileName) {
        String header = String.format("attachment; filename=\"%s\"", fileName);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, header);
        response.setContentType("text/plain");
        response.setCharacterEncoding("windows-1250");
    }
}
