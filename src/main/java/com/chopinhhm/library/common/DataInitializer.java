package com.chopinhhm.library.common;

import com.chopinhhm.library.modulea.AccountService;
import com.chopinhhm.library.modulea.UserAccount;
import com.chopinhhm.library.modulea.UserAccountRepository;
import com.chopinhhm.library.moduleb.Book;
import com.chopinhhm.library.moduleb.BookRepository;
import com.chopinhhm.library.moduleb.Reader;
import com.chopinhhm.library.moduleb.ReaderRepository;
import com.chopinhhm.library.moduleb.ReaderType;
import com.chopinhhm.library.moduleb.ReaderTypeRepository;
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
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Value("${app.seed.admin-password:admin123}")
    private String adminPassword;
    @Value("${app.seed.reader-password:reader123}")
    private String readerPassword;

    @Bean
    public CommandLineRunner seedData(BookRepository books, ReaderRepository readers, ReaderTypeRepository types,
                                      UserAccountRepository accountRepository, AccountService accountService,
                                      LoanRepository loans, ReservationRepository reservations,
                                      OverdueReminderRepository reminders, FineCalculator fineCalculator) {
        return args -> {
            LocalDate today = LocalDate.now();

            ReaderType student = readerType(types, "学生读者", 8, 30, 1, "0.50");
            ReaderType teacher = readerType(types, "教师读者", 10, 60, 2, "0.20");
            ReaderType graduate = readerType(types, "研究生", 8, 45, 2, "0.30");
            ReaderType external = readerType(types, "校外读者", 2, 15, 0, "1.00");
            ReaderType visitor = readerType(types, "访客", 1, 7, 0, "0.00");

            List<Reader> members = new ArrayList<Reader>();
            members.add(reader(readers, "R20260001", "演示读者", "reader@example.com", student));
            members.add(reader(readers, "R20260002", "黄浩茗", "huanghaoming@example.com", teacher));
            members.add(reader(readers, "R20260003", "何小雅", "hexiaoya@example.com", student));
            members.add(reader(readers, "R20260004", "李雨轩", "liyuxuan@example.com", student));
            members.add(reader(readers, "R20260005", "杨青", "yangqing@example.com", teacher));
            members.add(reader(readers, "R20260006", "陈晨", "chenchen@example.com", graduate));
            members.add(reader(readers, "R20260007", "王芳", "wangfang@example.com", student));
            members.add(reader(readers, "R20260008", "赵强", "zhaoqiang@example.com", graduate));
            members.add(reader(readers, "R20260009", "孙悦", "sunyue@example.com", student));
            members.add(reader(readers, "R20260010", "周杰", "zhoujie@example.com", teacher));
            members.add(reader(readers, "R20260011", "吴敏", "wumin@example.com", external));
            members.add(reader(readers, "R20260012", "郑浩", "zhenghao@example.com", student));
            members.add(reader(readers, "R20260013", "冯雪", "fengxue@example.com", graduate));
            members.add(reader(readers, "R20260014", "蒋楠", "jiangnan@example.com", student));
            members.add(reader(readers, "R20260015", "沈林", "shenlin@example.com", teacher));
            members.add(reader(readers, "R20260016", "韩梅", "hanmei@example.com", student));
            members.add(reader(readers, "R20260017", "曹阳", "caoyang@example.com", external));
            members.add(reader(readers, "R20260018", "邓丽", "dengli@example.com", student));
            members.add(reader(readers, "R20260019", "许涛", "xutao@example.com", graduate));
            members.add(reader(readers, "R20260020", "高远", "gaoyuan@example.com", visitor));

            account(accountRepository, accountService, "admin", adminPassword, UserAccount.Role.ADMIN, null);
            String[] usernames = {"reader", "huanghaoming", "hexiaoya", "liyuxuan", "yangqing",
                "chenchen", "wangfang", "zhaoqiang", "sunyue", "zhoujie", "wumin", "zhenghao",
                "fengxue", "jiangnan", "shenlin", "hanmei", "caoyang", "dengli", "xutao", "gaoyuan"};
            for (int i = 0; i < members.size(); i++) {
                account(accountRepository, accountService, usernames[i], readerPassword, UserAccount.Role.READER, members.get(i).getId());
            }

            List<Book> catalog = new ArrayList<Book>();
            catalog.add(book(books, "9787111213826", "Java 编程思想", "Bruce Eckel", "计算机", "A-01-01", 4));
            catalog.add(book(books, "9787115428028", "深入理解计算机系统", "Randal E. Bryant", "计算机", "A-01-02", 3));
            catalog.add(book(books, "9787020002207", "红楼梦", "曹雪芹", "文学", "B-02-01", 4));
            catalog.add(book(books, "9787111268553", "软件工程", "Ian Sommerville", "计算机", "A-02-01", 3));
            catalog.add(book(books, "9787111421900", "数据结构与算法分析", "Mark Allen Weiss", "计算机", "A-02-02", 4));
            catalog.add(book(books, "9787111386292", "数据库系统概念", "Abraham Silberschatz", "计算机", "A-02-03", 2));
            catalog.add(book(books, "9787508647357", "人类简史", "尤瓦尔·赫拉利", "社科", "C-01-01", 3));
            catalog.add(book(books, "9787536692930", "三体", "刘慈欣", "文学", "B-03-01", 5));
            catalog.add(book(books, "9787506365437", "活着", "余华", "文学", "B-03-02", 3));
            catalog.add(book(books, "9787544253994", "百年孤独", "加西亚·马尔克斯", "文学", "B-03-03", 2));
            catalog.add(book(books, "9787111129543", "设计模式", "Erich Gamma", "计算机", "A-03-01", 2));
            catalog.add(book(books, "9787111414995", "计算机网络", "James Kurose", "计算机", "A-03-02", 3));
            catalog.add(book(books, "9787111544935", "操作系统导论", "Remzi Arpaci-Dusseau", "计算机", "A-04-01", 4));
            catalog.add(book(books, "9787111187776", "算法导论", "Thomas Cormen", "计算机", "A-04-02", 3));
            catalog.add(book(books, "9787111251217", "编译原理", "Alfred Aho", "计算机", "A-04-03", 2));
            catalog.add(book(books, "9787111307549", "人工智能导论", "Stuart Russell", "计算机", "A-05-01", 3));
            catalog.add(book(books, "9787302526377", "机器学习", "周志华", "计算机", "A-05-02", 3));
            catalog.add(book(books, "9787115460851", "Python 编程", "Eric Matthes", "计算机", "A-05-03", 5));
            catalog.add(book(books, "9787020047451", "平凡的世界", "路遥", "文学", "B-03-04", 4));
            catalog.add(book(books, "9787020032693", "围城", "钱钟书", "文学", "B-03-05", 3));
            catalog.add(book(books, "9787532118368", "白鹿原", "陈忠实", "文学", "B-04-01", 2));
            catalog.add(book(books, "9787544258593", "明朝那些事儿", "当年明月", "历史", "D-01-01", 4));
            catalog.add(book(books, "9787108009821", "万历十五年", "黄仁宇", "历史", "D-01-02", 3));
            catalog.add(book(books, "9787301244267", "经济学原理", "N. Gregory Mankiw", "经济", "E-01-01", 3));
            catalog.add(book(books, "9787100101709", "国富论", "Adam Smith", "经济", "E-01-02", 2));
            catalog.add(book(books, "9787115349422", "社会心理学", "Elliot Aronson", "社科", "C-02-01", 3));
            catalog.add(book(books, "9787108007438", "乡土中国", "费孝通", "社科", "C-02-02", 4));
            catalog.add(book(books, "9787535732309", "时间简史", "Stephen Hawking", "科普", "F-01-01", 3));
            catalog.add(book(books, "9787508645612", "自私的基因", "Richard Dawkins", "科普", "F-01-02", 2));
            catalog.add(book(books, "9787544270878", "宇宙的琴弦", "Brian Greene", "科普", "F-01-03", 2));

            seedLoans(books, loans, fineCalculator, members, catalog, today);
            seedReservations(reservations, members, catalog);
            seedReminders(loans, reminders, today);
        };
    }

    private ReaderType readerType(ReaderTypeRepository repository, String name, int maxBooks, int loanDays,
                                  int maxRenewals, String dailyFineRate) {
        ReaderType existing = repository.findByName(name).orElse(null);
        if (existing != null) return existing;
        ReaderType type = new ReaderType();
        type.setName(name);
        type.setMaxBooks(maxBooks);
        type.setLoanDays(loanDays);
        type.setMaxRenewals(maxRenewals);
        type.setDailyFineRate(new BigDecimal(dailyFineRate));
        return repository.save(type);
    }

    private Reader reader(ReaderRepository repository, String cardNumber, String name, String email, ReaderType type) {
        Reader existing = repository.findByCardNumber(cardNumber).orElse(null);
        if (existing != null) return existing;
        Reader reader = new Reader();
        reader.setCardNumber(cardNumber);
        reader.setName(name);
        reader.setEmail(email);
        reader.setReaderType(type);
        return repository.save(reader);
    }

    private void account(UserAccountRepository repository, AccountService service, String username, String password,
                         UserAccount.Role role, Long readerId) {
        if (repository.existsByUsername(username)) return;
        service.create(username, password, role, readerId);
    }

    private Book book(BookRepository repository, String isbn, String title, String author, String category,
                      String shelf, int copies) {
        Book existing = repository.findByIsbn(isbn).orElse(null);
        if (existing != null) return existing;
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setShelfLocation(shelf);
        book.setTotalCopies(copies);
        book.setAvailableCopies(copies);
        return repository.save(book);
    }

    private void seedLoans(BookRepository books, LoanRepository loans, FineCalculator fineCalculator,
                           List<Reader> members, List<Book> catalog, LocalDate today) {
        List<LoanSeed> seeds = new ArrayList<LoanSeed>();
        seeds.add(new LoanSeed(members.get(0), catalog.get(0), -8, 22, null, 0));
        seeds.add(new LoanSeed(members.get(1), catalog.get(1), -70, -10, null, 0));
        seeds.add(new LoanSeed(members.get(2), catalog.get(2), -40, -10, -9, 0));
        seeds.add(new LoanSeed(members.get(3), catalog.get(3), -5, 25, null, 0));
        seeds.add(new LoanSeed(members.get(4), catalog.get(4), -65, -5, -5, 0));
        seeds.add(new LoanSeed(members.get(5), catalog.get(5), -20, 25, null, 0));
        seeds.add(new LoanSeed(members.get(6), catalog.get(6), -18, -3, null, 0));
        seeds.add(new LoanSeed(members.get(7), catalog.get(7), -50, -8, -6, 0));
        seeds.add(new LoanSeed(members.get(8), catalog.get(8), -12, 18, null, 0));
        seeds.add(new LoanSeed(members.get(9), catalog.get(9), -30, 0, null, 1));
        seeds.add(new LoanSeed(members.get(10), catalog.get(10), -10, 5, null, 0));
        seeds.add(new LoanSeed(members.get(11), catalog.get(11), -15, 0, -1, 0));
        seeds.add(new LoanSeed(members.get(12), catalog.get(12), -22, -7, null, 0));
        seeds.add(new LoanSeed(members.get(13), catalog.get(13), -7, 23, null, 0));
        seeds.add(new LoanSeed(members.get(14), catalog.get(14), -80, -20, -18, 0));
        seeds.add(new LoanSeed(members.get(15), catalog.get(15), -6, 39, null, 0));
        seeds.add(new LoanSeed(members.get(16), catalog.get(16), -11, 4, null, 0));
        seeds.add(new LoanSeed(members.get(17), catalog.get(17), -4, 26, null, 0));
        seeds.add(new LoanSeed(members.get(18), catalog.get(18), -35, -5, null, 1));
        seeds.add(new LoanSeed(members.get(19), catalog.get(19), -3, 4, null, 0));
        seeds.add(new LoanSeed(members.get(0), catalog.get(20), -28, -13, null, 0));
        seeds.add(new LoanSeed(members.get(1), catalog.get(21), -14, 1, null, 0));
        seeds.add(new LoanSeed(members.get(2), catalog.get(22), -60, -15, -12, 0));
        seeds.add(new LoanSeed(members.get(3), catalog.get(23), -9, 6, null, 0));
        seeds.add(new LoanSeed(members.get(4), catalog.get(24), -19, -4, null, 0));
        seeds.add(new LoanSeed(members.get(5), catalog.get(25), -2, 13, null, 0));
        seeds.add(new LoanSeed(members.get(6), catalog.get(26), -25, -10, null, 0));
        seeds.add(new LoanSeed(members.get(7), catalog.get(27), -8, 7, null, 0));
        seeds.add(new LoanSeed(members.get(8), catalog.get(28), -16, -1, null, 0));
        seeds.add(new LoanSeed(members.get(9), catalog.get(29), -45, -15, -14, 0));

        for (int i = 0; i < seeds.size() && loans.count() < 30; i++) {
            LoanSeed seed = seeds.get(i);
            LocalDate borrowedAt = today.plusDays(seed.borrowedOffset);
            LocalDate dueAt = today.plusDays(seed.dueOffset);
            if (seed.returnedOffset == null) {
                activeLoan(books, loans, seed.reader, seed.book, borrowedAt, dueAt, seed.renewCount);
            } else {
                LocalDate returnedAt = today.plusDays(seed.returnedOffset);
                BigDecimal fine = fineCalculator.calculate(dueAt, returnedAt,
                    seed.reader.getReaderType().getDailyFineRate());
                returnedLoan(loans, seed.reader, seed.book, borrowedAt, dueAt, returnedAt, fine);
            }
        }
    }

    private void seedReservations(ReservationRepository reservations, List<Reader> members, List<Book> catalog) {
        List<ReservationSeed> seeds = new ArrayList<ReservationSeed>();
        seeds.add(new ReservationSeed(members.get(2), catalog.get(5), -3, Reservation.Status.ACTIVE));
        seeds.add(new ReservationSeed(members.get(3), catalog.get(6), -24, Reservation.Status.ACTIVE));
        seeds.add(new ReservationSeed(members.get(0), catalog.get(7), -96, Reservation.Status.CANCELLED));
        seeds.add(new ReservationSeed(members.get(5), catalog.get(8), -8, Reservation.Status.ACTIVE));
        seeds.add(new ReservationSeed(members.get(6), catalog.get(9), -30, Reservation.Status.FULFILLED));
        seeds.add(new ReservationSeed(members.get(7), catalog.get(10), -5, Reservation.Status.ACTIVE));
        seeds.add(new ReservationSeed(members.get(8), catalog.get(11), -50, Reservation.Status.CANCELLED));
        seeds.add(new ReservationSeed(members.get(9), catalog.get(12), -12, Reservation.Status.ACTIVE));
        seeds.add(new ReservationSeed(members.get(10), catalog.get(13), -6, Reservation.Status.ACTIVE));
        seeds.add(new ReservationSeed(members.get(11), catalog.get(14), -72, Reservation.Status.CANCELLED));
        seeds.add(new ReservationSeed(members.get(12), catalog.get(15), -18, Reservation.Status.ACTIVE));
        seeds.add(new ReservationSeed(members.get(13), catalog.get(16), -4, Reservation.Status.ACTIVE));

        for (int i = 0; i < seeds.size() && reservations.count() < 15; i++) {
            ReservationSeed seed = seeds.get(i);
            Reservation reservation = new Reservation();
            reservation.setReader(seed.reader);
            reservation.setBook(seed.book);
            reservation.setCreatedAt(LocalDateTime.now().plusHours(seed.createdOffsetHours));
            reservation.setStatus(seed.status);
            reservations.save(reservation);
        }
    }

    private void seedReminders(LoanRepository loans, OverdueReminderRepository reminders, LocalDate today) {
        if (reminders.count() >= 8) return;
        List<Loan> overdue = loans.findByStatusAndDueAtBeforeOrderByDueAtAsc(Loan.Status.BORROWED, today);
        for (int i = 0; i < overdue.size() && reminders.count() < 8; i++) {
            Loan loan = overdue.get(i);
            OverdueReminder reminder = new OverdueReminder();
            reminder.setLoan(loan);
            reminder.setRecipient(loan.getReader().getEmail());
            reminder.setMessage("请尽快归还《" + loan.getBook().getTitle() + "》，应还日期为 " + loan.getDueAt());
            reminder.setSentAt(LocalDateTime.now().minusHours(i + 1L));
            reminders.save(reminder);
        }
    }

    private Loan activeLoan(BookRepository books, LoanRepository loans, Reader reader, Book book, LocalDate borrowedAt,
                            LocalDate dueAt, int renewCount) {
        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setBook(book);
        loan.setBorrowedAt(borrowedAt);
        loan.setDueAt(dueAt);
        loan.setRenewCount(renewCount);
        loan.setStatus(Loan.Status.BORROWED);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        books.save(book);
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

    private static class LoanSeed {
        private final Reader reader;
        private final Book book;
        private final int borrowedOffset;
        private final int dueOffset;
        private final Integer returnedOffset;
        private final int renewCount;

        private LoanSeed(Reader reader, Book book, int borrowedOffset, int dueOffset, Integer returnedOffset,
                         int renewCount) {
            this.reader = reader;
            this.book = book;
            this.borrowedOffset = borrowedOffset;
            this.dueOffset = dueOffset;
            this.returnedOffset = returnedOffset;
            this.renewCount = renewCount;
        }
    }

    private static class ReservationSeed {
        private final Reader reader;
        private final Book book;
        private final int createdOffsetHours;
        private final Reservation.Status status;

        private ReservationSeed(Reader reader, Book book, int createdOffsetHours, Reservation.Status status) {
            this.reader = reader;
            this.book = book;
            this.createdOffsetHours = createdOffsetHours;
            this.status = status;
        }
    }
}
