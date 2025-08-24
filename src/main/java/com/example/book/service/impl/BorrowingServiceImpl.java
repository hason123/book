package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.BorrowingType;
import com.example.book.dto.RequestDTO.BorrowingRequestDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.entity.Book;
import com.example.book.entity.Borrowing;
import com.example.book.entity.User;
import com.example.book.exception.BusinessException;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.BookRepository;
import com.example.book.repository.BorrowingRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.BorrowingService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingServiceImpl implements BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final MessageConfig messageConfig;
    private final String BORROWING_NOT_FOUND = "error.borrowing.notfound";
    private final String USER_NOT_FOUND = "error.user.notfound";
    private final String BOOK_NOT_FOUND = "error.book.notfound";
    private final String BOOK_OUT_OF_STOCK = "error.borrowing.outOfStock";
    private final String BORROWING_WRONG_DATE = "error.borrowing.wrongDate";
    private final String BORROWING_TITLE_EXCEL = "borrowing.title.excel";

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository, BookRepository bookRepository, UserRepository userRepository, MessageConfig messageConfig) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public BorrowingResponseDTO addBorrowing(BorrowingRequestDTO request) {
        User userAdded = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND , request.getUserId())));
        Book bookAdded = bookRepository.findById(request.getBookId()).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(BOOK_NOT_FOUND, request.getBookId())));
        Borrowing borrowing = new Borrowing();
        borrowing.setUser(userAdded);
        borrowing.setBook(bookAdded);
        borrowing.setBorrowDate(request.getBorrowingDate());
        if(request.getReturnDate()!=null){
            borrowing.setReturnDate(request.getReturnDate());
        }
        if (checkDuplicate(borrowing)) {
            throw new BusinessException(messageConfig.getMessage(BORROWING_WRONG_DATE));
        }
        if(bookAdded.getQuantity() == 0){
            throw new BusinessException (messageConfig.getMessage(BOOK_OUT_OF_STOCK, request.getBookId()));
        }
        borrowingRepository.save(borrowing);
        //borrowBooks(borrowing);
        return convertBorrowingToDTO(borrowing);
    }

    @Override
    public BorrowingResponseDTO getBorrowingById(Long id) {
        Borrowing borrowing = borrowingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(BORROWING_NOT_FOUND, id)));
        return convertBorrowingToDTO(borrowing);
    }

    @Override
    public void deleteBookById(Long id) {
        if(borrowingRepository.existsById(id)) {
            borrowingRepository.deleteById(id);
        }
        else throw new ResourceNotFoundException(messageConfig.getMessage(BORROWING_NOT_FOUND, id));
    }

    //don't know if this thing even works
    @Override
    public BorrowingResponseDTO updateBorrowing(Long id, BorrowingRequestDTO request) {
        Borrowing updatedBorrowing = borrowingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(BORROWING_NOT_FOUND, id)));
        User newUser = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND , request.getUserId())));
        updatedBorrowing.setUser(newUser);
        updatedBorrowing.setBorrowDate(request.getBorrowingDate());
        updatedBorrowing.setReturnDate(request.getReturnDate());
        Book oldBook = updatedBorrowing.getBook();
        Book newBook = bookRepository.findById(request.getBookId()).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(BOOK_NOT_FOUND, request.getBookId())));
        if(!oldBook.equals(newBook)){
            updatedBorrowing.setBook(newBook);
            oldBook.setQuantity(oldBook.getQuantity() + 1);
            bookRepository.save(oldBook);
            //luc nay da UPDATE het du lieu roi
           // borrowBooks(updatedBorrowing);
        }
        if (checkDuplicate(updatedBorrowing)) {
            throw new BusinessException(messageConfig.getMessage(BORROWING_WRONG_DATE));
        }
        borrowingRepository.save(updatedBorrowing);
        return convertBorrowingToDTO(updatedBorrowing);
    }

    @Override
    public PageResponseDTO<BorrowingResponseDTO> getBorrowingPage(Pageable pageable) {
        Page<Borrowing> borrowingPage = borrowingRepository.findAllCustomSort(pageable);
        Page<BorrowingResponseDTO> borrowingResponseDTO = borrowingPage.map(this::convertBorrowingToDTO);
        PageResponseDTO<BorrowingResponseDTO> pageDTO = new PageResponseDTO<>(
                borrowingResponseDTO.getNumber() + 1,
                borrowingResponseDTO.getNumberOfElements(),
                borrowingResponseDTO.getTotalPages(),
                borrowingResponseDTO.getContent()
        );
        return pageDTO;
    }

    private void borrowBooks() {
        LocalDate localDate = LocalDate.now();
        for(Borrowing borrowing : borrowingRepository.findAll()){
            LocalDate borrowLocalDate = borrowing.getBorrowDate();
            LocalDate returnLocalDate= borrowing.getReturnDate();
            if ((localDate.isEqual(borrowLocalDate)|| localDate.isAfter(borrowLocalDate)) &&
                    (localDate.isEqual(returnLocalDate) || localDate.isBefore(returnLocalDate)))
            {
                borrowing.getBook().setQuantity(borrowing.getBook().getQuantity() - 1);
                bookRepository.save(borrowing.getBook());
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void borrowingStatus(){
        LocalDate currentDate = LocalDate.now();
        for(Borrowing borrowing : borrowingRepository.findAll()){
            if(borrowing.getReturnDate() != null) {
                borrowing.setStatus(BorrowingType.RETURNED);
            }
            else{
                if (currentDate.isAfter(borrowing.getBorrowDate().plusMonths(1))) {
                    borrowing.setStatus(BorrowingType.DUE);
                }
                else borrowing.setStatus(BorrowingType.BORROWING);
            }
        }
    }

    private boolean checkDuplicate(Borrowing borrowing) {
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        for(Borrowing b : borrowingList){
            boolean checkDate = ((b.getBorrowDate().isAfter(borrowing.getBorrowDate())) && (b.getBorrowDate().isAfter(borrowing.getReturnDate())))
                    || ((b.getReturnDate().isBefore(borrowing.getBorrowDate())) && (b.getReturnDate().isBefore(borrowing.getReturnDate())));
            if(b.getUser().getUserId().equals(borrowing.getUser().getUserId()) &&
                    b.getBook().getBookId().equals(borrowing.getBook().getBookId()) &&
                    !checkDate){
                return true;
            }
        }
        return false;
    }

    public BorrowingResponseDTO convertBorrowingToDTO(Borrowing borrowing) {
        BorrowingResponseDTO borrowingDTO = new BorrowingResponseDTO();
        borrowingDTO.setBorrowingId(borrowing.getId());
        borrowingDTO.setBorrowingDate(borrowing.getBorrowDate());
        borrowingDTO.setReturnDate(borrowing.getReturnDate());
        borrowingDTO.setUsername(borrowing.getUser().getUserName());
        borrowingDTO.setBookName(borrowing.getBook().getBookName());
        borrowingDTO.setStatus(borrowing.getStatus().toString());
        return borrowingDTO;
    }

    @Override
    public void createBorrowingWorkbook(HttpServletResponse response) throws IOException {
        List<Book> books = borrowingRepository.findCurrentBorrowingBooks();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(messageConfig.getMessage(BORROWING_TITLE_EXCEL));
        Row header = sheet.createRow(0); //excel is zero-based
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Tên sách");
        header.createCell(2).setCellValue("Tác giả");
        int rowNum = 1; int x = 1;
        for(Book book : books){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(x++);
            row.createCell(1).setCellValue(book.getBookName());
            row.createCell(2).setCellValue(book.getAuthor());
        }
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
