package com.chopinhhm.library.common;

import com.chopinhhm.library.moduleb.Book;
import com.chopinhhm.library.moduleb.BookRepository;
import com.chopinhhm.library.moduleb.Reader;
import com.chopinhhm.library.moduleb.ReaderRepository;
import com.chopinhhm.library.moduleb.ReaderType;
import com.chopinhhm.library.moduleb.ReaderTypeRepository;
import com.chopinhhm.library.modulea.AccountService;
import com.chopinhhm.library.modulea.UserAccount;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class DataInitializer {
    @Value("${app.seed.admin-password:admin123}")
    private String adminPassword;
    @Value("${app.seed.reader-password:reader123}")
    private String readerPassword;

    @Bean
    public CommandLineRunner seedData(BookRepository books, ReaderRepository readers, ReaderTypeRepository types, AccountService accounts) {
        return args -> {
            if (books.count() > 0) return;
            ReaderType student = new ReaderType();
            student.setName("学生读者"); student.setMaxBooks(5); student.setLoanDays(30);
            student.setMaxRenewals(1); student.setDailyFineRate(new BigDecimal("0.50"));
            types.save(student);

            Reader reader = new Reader();
            reader.setCardNumber("R20260001"); reader.setName("演示读者");
            reader.setEmail("reader@example.com"); reader.setReaderType(student);
            readers.save(reader);

            accounts.create("admin", adminPassword, UserAccount.Role.ADMIN, null);
            accounts.create("reader", readerPassword, UserAccount.Role.READER, reader.getId());

            books.save(book("9787111213826", "Java 编程思想", "Bruce Eckel", "计算机", "A-01-01", 3));
            books.save(book("9787115428028", "深入理解计算机系统", "Randal E. Bryant", "计算机", "A-01-02", 2));
            books.save(book("9787020002207", "红楼梦", "曹雪芹", "文学", "B-02-01", 4));
        };
    }

    private Book book(String isbn, String title, String author, String category, String shelf, int copies) {
        Book book = new Book();
        book.setIsbn(isbn); book.setTitle(title); book.setAuthor(author); book.setCategory(category);
        book.setShelfLocation(shelf); book.setTotalCopies(copies); book.setAvailableCopies(copies);
        return book;
    }
}
