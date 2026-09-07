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
        if (book.getId() == null) {
            if (books.existsByIsbn(book.getIsbn())) throw new BusinessException("ISBN 已存在");
            book.setAvailableCopies(book.getTotalCopies());
        } else {
            Book existing = getBook(book.getId());
            int borrowed = existing.getTotalCopies() - existing.getAvailableCopies();
            if (book.getTotalCopies() < borrowed) throw new BusinessException("馆藏数量不能小于当前借出数量");
            book.setAvailableCopies(book.getTotalCopies() - borrowed);
        }
        if (book.getAvailableCopies() > book.getTotalCopies()) throw new BusinessException("可借数量不能超过馆藏数量");
        return books.save(book);
    }

    public Book getBook(Long id) { return books.findById(id).orElseThrow(() -> new BusinessException("图书不存在")); }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBook(id);
        if (book.getAvailableCopies() != book.getTotalCopies()) throw new BusinessException("存在未归还副本，不能删除");
        books.delete(book);
    }

    public List<Reader> readers() { return readers.findAll(); }

    public List<ReaderType> readerTypes() { return readerTypes.findAll(); }

    @Transactional
    public ReaderType saveReaderType(ReaderType type) {
        if (type.getName() == null || type.getName().trim().isEmpty()) throw new BusinessException("读者类型名称不能为空");
        if (type.getMaxBooks() < 1 || type.getLoanDays() < 1 || type.getMaxRenewals() < 0) throw new BusinessException("借阅规则数值不合法");
        if (type.getDailyFineRate() == null || type.getDailyFineRate().signum() < 0) throw new BusinessException("罚金标准不合法");
        return readerTypes.save(type);
    }

    @Transactional
    public Reader saveReader(Reader reader, Long readerTypeId) {
        if (reader.getId() == null && readers.existsByCardNumber(reader.getCardNumber())) throw new BusinessException("借阅证号已存在");
        ReaderType type = readerTypes.findById(readerTypeId).orElseThrow(() -> new BusinessException("读者类型不存在"));
        reader.setReaderType(type);
        return readers.save(reader);
    }

    @Transactional
    public Reader updateReader(Long id, Reader reader, Long readerTypeId) {
        Reader existing = readers.findById(id).orElseThrow(() -> new BusinessException("读者不存在"));
        existing.setCardNumber(reader.getCardNumber());
        existing.setName(reader.getName());
        existing.setEmail(reader.getEmail());
        existing.setStatus(reader.getStatus() == null ? existing.getStatus() : reader.getStatus());
        return saveReader(existing, readerTypeId);
    }
}
