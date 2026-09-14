package com.chopinhhm.library.common;

import com.chopinhhm.library.moduleb.Book;
import com.chopinhhm.library.moduleb.BookRepository;
import com.chopinhhm.library.moduleb.Reader;
import com.chopinhhm.library.moduleb.ReaderRepository;
import com.chopinhhm.library.moduleb.ReaderType;
import com.chopinhhm.library.moduleb.ReaderTypeRepository;
import com.chopinhhm.library.modulea.AccountService;
import com.chopinhhm.library.modulea.UserAccount;
import com.chopinhhm.library.modulec.Loan;
import com.chopinhhm.library.modulec.LoanRepository;
import com.chopinhhm.library.modulec.OverdueReminder;
import com.chopinhhm.library.modulec.OverdueReminderRepository;
import com.chopinhhm.library.modulec.Reservation;
import com.chopinhhm.library.modulec.ReservationRepository;
import com.chopinhhm.library.moduled.FineCalculator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public CommandLineRunner seedData(BookRepository books, ReaderRepository readers, ReaderTypeRepository types,
                                      AccountService accounts, LoanRepository loans, ReservationRepository reservations,
                                      OverdueReminderRepository reminders, FineCalculator fineCalculator) {
        return args -> {
            if (books.count() > 0) return;

            ReaderType student = readerType("学生读者", 5, 30, 1, "0.50");
            ReaderType teacher = readerType("教师读者", 10, 60, 2, "0.20");
            ReaderType external = readerType("校外读者", 2, 15, 0, "1.00");
            types.save(student); types.save(teacher); types.save(external);

            Reader demo = reader(readers, "R20260001", "演示读者", "reader@example.com", student);
            Reader huang = reader(readers, "R20260002", "黄浩茗", "huanghaoming@example.com", teacher);
            Reader he = reader(readers, "R20260003", "何小雅", "hexiaoya@example.com", student);
            Reader li = reader(readers, "R20260004", "李雨轩", "liyuxuan@example.com", student);
            Reader yang = reader(readers, "R20260005", "杨青", "yangqing@example.com", teacher);
            Reader guest = reader(readers, "R20260006", "校外读者", "guest@example.com", external);

            accounts.create("admin", adminPassword, UserAccount.Role.ADMIN, null);
            accounts.create("reader", readerPassword, UserAccount.Role.READER, demo.getId());
            accounts.create("huanghaoming", readerPassword, UserAccount.Role.READER, huang.getId());
            accounts.create("hexiaoya", readerPassword, UserAccount.Role.READER, he.getId());
            accounts.create("liyuxuan", readerPassword, UserAccount.Role.READER, li.getId());
            accounts.create("yangqing", readerPassword, UserAccount.Role.READER, yang.getId());

            Book java = books.save(book("9787111213826", "Java 编程思想", "Bruce Eckel", "计算机", "A-01-01", 4));
            Book csapp = books.save(book("9787115428028", "深入理解计算机系统", "Randal E. Bryant", "计算机", "A-01-02", 3));
            Book dream = books.save(book("9787020002207", "红楼梦", "曹雪芹", "文学", "B-02-01", 4));
            Book software = books.save(book("9787111268553", "软件工程", "Ian Sommerville", "计算机", "A-02-01", 3));
            Book algorithm = books.save(book("9787111421900", "数据结构与算法分析", "Mark Allen Weiss", "计算机", "A-02-02", 4));
            Book database = books.save(book("9787111386292", "数据库系统概念", "Abraham Silberschatz", "计算机", "A-02-03", 2));
            Book sapiens = books.save(book("9787508647357", "人类简史", "尤瓦尔·赫拉利", "社科", "C-01-01", 3));
            Book threeBody = books.save(book("9787536692930", "三体", "刘慈欣", "文学", "B-03-01", 5));
            Book alive = books.save(book("9787506365437", "活着", "余华", "文学", "B-03-02", 3));
            Book solitude = books.save(book("9787544253994", "百年孤独", "加西亚·马尔克斯", "文学", "B-03-03", 2));
            Book patterns = books.save(book("9787111129543", "设计模式", "Erich Gamma", "计算机", "A-03-01", 2));
            Book network = books.save(book("9787111414995", "计算机网络", "James Kurose", "计算机", "A-03-02", 3));

            LocalDate today = LocalDate.now();

            activeLoan(loans, demo, java, today.minusDays(8), today.plusDays(22), 0);
            Loan huangOverdue = activeLoan(loans, huang, csapp, today.minusDays(70), today.minusDays(10), 0);
            books.save(java); books.save(csapp);
            returnedLoan(loans, he, dream, today.minusDays(40), today.minusDays(10), today.minusDays(9),
                fineCalculator.calculate(today.minusDays(10), today.minusDays(9), student.getDailyFineRate()));
            activeLoan(loans, li, software, today.minusDays(5), today.plusDays(25), 0);
            books.save(software);
            returnedLoan(loans, yang, algorithm, today.minusDays(65), today.minusDays(5), today.minusDays(5),
                fineCalculator.calculate(today.minusDays(5), today.minusDays(5), teacher.getDailyFineRate()));

            reservations.save(reservation(he, database, LocalDateTime.now().minusHours(3), Reservation.Status.ACTIVE));
            reservations.save(reservation(li, sapiens, LocalDateTime.now().minusDays(1), Reservation.Status.ACTIVE));
            reservations.save(reservation(demo, threeBody, LocalDateTime.now().minusDays(4), Reservation.Status.CANCELLED));

            OverdueReminder reminder = new OverdueReminder();
            reminder.setLoan(huangOverdue);
            reminder.setRecipient(huang.getEmail());
            reminder.setMessage("请尽快归还《" + csapp.getTitle() + "》，应还日期为 " + huangOverdue.getDueAt());
            reminder.setSentAt(LocalDateTime.now().minusHours(2));
            reminders.save(reminder);
        };
    }

    private ReaderType readerType(String name, int maxBooks, int loanDays, int maxRenewals, String dailyFineRate) {
        ReaderType type = new ReaderType();
        type.setName(name);
        type.setMaxBooks(maxBooks);
        type.setLoanDays(loanDays);
        type.setMaxRenewals(maxRenewals);
        type.setDailyFineRate(new BigDecimal(dailyFineRate));
        return type;
    }

    private Reader reader(ReaderRepository readers, String cardNumber, String name, String email, ReaderType type) {
        Reader reader = new Reader();
        reader.setCardNumber(cardNumber);
        reader.setName(name);
        reader.setEmail(email);
        reader.setReaderType(type);
        return readers.save(reader);
    }

    private Book book(String isbn, String title, String author, String category, String shelf, int copies) {
        Book book = new Book();
        book.setIsbn(isbn); book.setTitle(title); book.setAuthor(author); book.setCategory(category);
        book.setShelfLocation(shelf); book.setTotalCopies(copies); book.setAvailableCopies(copies);
        return book;
    }

    private Loan activeLoan(LoanRepository loans, Reader reader, Book book, LocalDate borrowedAt, LocalDate dueAt,
                            int renewCount) {
        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setBook(book);
        loan.setBorrowedAt(borrowedAt);
        loan.setDueAt(dueAt);
        loan.setRenewCount(renewCount);
        loan.setStatus(Loan.Status.BORROWED);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return loans.save(loan);
    }

    private Loan returnedLoan(LoanRepository loans, Reader reader, Book book, LocalDate borrowedAt, LocalDate dueAt,
                              LocalDate returnedAt, BigDecimal fine) {
        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setBook(book);
        loan.setBorrowedAt(borrowedAt);
        loan.setDueAt(dueAt);
        loan.setReturnedAt(returnedAt);
        loan.setFine(fine);
        loan.setStatus(Loan.Status.RETURNED);
        return loans.save(loan);
    }

    private Reservation reservation(Reader reader, Book book, LocalDateTime createdAt, Reservation.Status status) {
        Reservation reservation = new Reservation();
        reservation.setReader(reader);
        reservation.setBook(book);
        reservation.setCreatedAt(createdAt);
        reservation.setStatus(status);
        return reservation;
    }
}
