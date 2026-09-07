package com.chopinhhm.library.moduleb;

import com.chopinhhm.library.common.BusinessException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {
    private final BookRepository books;
    private final ReaderRepository readers;
    private final ReaderTypeRepository readerTypes;

    public CatalogService(BookRepository books, ReaderRepository readers, ReaderTypeRepository readerTypes) {
        this.books = books;
        this.readers = readers;
        this.readerTypes = readerTypes;
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return books.findAll();
        return books.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }

    @Transactional
    public Book saveBook(Book book) {
        if (book.getId() == null) book.setAvailableCopies(book.getTotalCopies());
        if (book.getAvailableCopies() > book.getTotalCopies()) throw new BusinessException("可借数量不能超过馆藏数量");
        return books.save(book);
    }

    public List<Reader> readers() { return readers.findAll(); }

    @Transactional
    public Reader saveReader(Reader reader, Long readerTypeId) {
        ReaderType type = readerTypes.findById(readerTypeId).orElseThrow(() -> new BusinessException("读者类型不存在"));
        reader.setReaderType(type);
        return readers.save(reader);
    }
}
