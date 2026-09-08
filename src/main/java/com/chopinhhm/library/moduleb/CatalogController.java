package com.chopinhhm.library.moduleb;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public List<Book> books(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String shelfLocation,
                            @RequestParam(defaultValue = "false") boolean availableOnly) {
        return service.searchBooks(keyword, category, shelfLocation, availableOnly);
    }

    @PostMapping("/admin/books")
    public Book saveBook(@Valid @RequestBody Book book) { return service.saveBook(book); }

    @PutMapping("/admin/books/{id}")
    public Book updateBook(@PathVariable Long id, @Valid @RequestBody Book book) { book.setId(id); return service.saveBook(book); }

    @DeleteMapping("/admin/books/{id}")
    public void deleteBook(@PathVariable Long id) { service.deleteBook(id); }

    @GetMapping("/reader-types")
    public List<ReaderType> readerTypes() { return service.readerTypes(); }

    @PostMapping("/admin/reader-types")
    public ReaderType saveReaderType(@RequestBody ReaderType type) { return service.saveReaderType(type); }

    @PutMapping("/admin/reader-types/{id}")
    public ReaderType updateReaderType(@PathVariable Long id, @RequestBody ReaderType type) { type.setId(id); return service.saveReaderType(type); }

    @GetMapping("/admin/readers")
    public List<Reader> readers() { return service.readers(); }

    @PostMapping("/admin/readers")
    public Reader saveReader(@RequestBody ReaderRequest request) {
        Reader reader = request.toReader();
        return service.saveReader(reader, request.readerTypeId);
    }

    @PutMapping("/admin/readers/{id}")
    public Reader updateReader(@PathVariable Long id, @RequestBody ReaderRequest request) {
        return service.updateReader(id, request.toReader(), request.readerTypeId);
    }

    public static class ReaderRequest {
        public String cardNumber;
        public String name;
        public String email;
        public Long readerTypeId;
        public Reader.Status status;
        Reader toReader() {
            Reader reader = new Reader();
            reader.setCardNumber(cardNumber); reader.setName(name); reader.setEmail(email);
            if (status != null) reader.setStatus(status);
            return reader;
        }
    }
}
