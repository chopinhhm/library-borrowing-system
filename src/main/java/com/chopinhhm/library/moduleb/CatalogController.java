package com.chopinhhm.library.moduleb;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final CatalogService service;
    public CatalogController(CatalogService service) { this.service = service; }

    @GetMapping("/books")
    public List<Book> books(@RequestParam(required = false) String keyword) { return service.searchBooks(keyword); }

    @PostMapping("/admin/books")
    public Book saveBook(@Valid @RequestBody Book book) { return service.saveBook(book); }

    @GetMapping("/admin/readers")
    public List<Reader> readers() { return service.readers(); }

    @PostMapping("/admin/readers")
    public Reader saveReader(@RequestBody ReaderRequest request) {
        Reader reader = new Reader();
        reader.setCardNumber(request.cardNumber);
        reader.setName(request.name);
        reader.setEmail(request.email);
        return service.saveReader(reader, request.readerTypeId);
    }

    public static class ReaderRequest {
        public String cardNumber;
        public String name;
        public String email;
        public Long readerTypeId;
    }
}
