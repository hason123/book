package com.example.book.service.impl;


import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.PageDTO;
import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import com.example.book.entity.Book;
import com.example.book.entity.Borrowing;
import com.example.book.entity.User;
import com.example.book.repository.BookRepository;
import com.example.book.repository.BorrowingRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.BorrowingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class BorrowingServiceImpl implements BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BorrowingResponseDTO addBorrowing(Borrowing borrowing) {


        Optional<User> userAdded = userRepository.findById(borrowing.getUser().getUserId());
        Optional<Book> bookAdded = bookRepository.findById(borrowing.getBook().getBookId());
        if (checkDuplicate(borrowing)) {
            throw new IllegalStateException("Bạn hiện tại đã mượn quyển sách này!");
        }
        if(userAdded.isPresent() && bookAdded.isPresent()) {
            borrowing.setUser(userAdded.get());
            borrowing.setBook(bookAdded.get());
            if(bookAdded.get().getQuantity() == 0){
                throw new IllegalStateException ("Book with ID " + borrowing.getBook().getBookId() + " is out of stock");
            }
        }

        borrowingRepository.save(borrowing);
        borrowBooks(borrowing);


        return convertBorrowingToDTO(borrowing);
    }

    @Override
    public Optional<Borrowing> getBorrowingById(Long id) {
        return borrowingRepository.findById(id);
    }

    @Override
    public void deleteBookById(Long id) {
        borrowingRepository.deleteById(id);
    }

    //TODO: this code is shortened and looks clean, but i haven't understood this yet,
    //don't know if this thing even works
    @Override
    public BorrowingResponseDTO updateBook(Long id, Borrowing borrowing) {

        Optional<Borrowing> existedBorrowing = borrowingRepository.findById(id);

        Borrowing updatedBorrowing = existedBorrowing
                .orElseThrow(() -> new EntityNotFoundException("Borrowing not found"));

        User newUser = userRepository.findById(borrowing.getUser().getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with ID " + borrowing.getUser().getUserId() + " not found"));
        updatedBorrowing.setUser(newUser);

        Book oldBook = updatedBorrowing.getBook();
        Book newBook = bookRepository.findById(borrowing.getBook().getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book with ID " + borrowing.getBook().getBookId() + " not found"));

        updatedBorrowing.setBorrowDate(borrowing.getBorrowDate());
        updatedBorrowing.setReturnDate(borrowing.getReturnDate());
        if(!oldBook.equals(newBook)){
            updatedBorrowing.setBook(newBook);
            //luc nay da UPDATE het du lieu roi
            borrowBooks(updatedBorrowing);
        }
        if (checkDuplicate(updatedBorrowing)) {
            throw new IllegalStateException("Bạn hiện tại đã mượn quyển sách này!");
        }
        borrowingRepository.save(updatedBorrowing);
        return convertBorrowingToDTO(updatedBorrowing);
    }

    //TODO: books out of stock meaning cannot borrow, cannot borrow the same book during borrow date - return date
    //book's quantity reduces by one during the time of borrowing
    //https://www.baeldung.com/java-date-to-localdate-and-localdatetime
    private void borrowBooks(Borrowing borrowing) {
        LocalDate localDate = LocalDate.now();

        LocalDate borrowLocalDate = borrowing.getBorrowDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate returnLocalDate = borrowing.getReturnDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if ((localDate.isEqual(borrowLocalDate)|| localDate.isAfter(borrowLocalDate)) &&
                (localDate.isEqual(returnLocalDate) || localDate.isBefore(returnLocalDate))){
            borrowing.getBook().setQuantity(borrowing.getBook().getQuantity() - 1);
        }

    }

    private boolean checkDuplicate(Borrowing borrowing) {
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        for(Borrowing b : borrowingList){
           boolean checkDate;
           checkDate = (b.getBorrowDate().compareTo(borrowing.getBorrowDate()) >= 0) &&
                   (b.getBorrowDate().compareTo(borrowing.getReturnDate()) <= 0);
            if(b.getUser().getUserId().equals(borrowing.getUser().getUserId()) &&
                    b.getBook().getBookId().equals(borrowing.getBook().getBookId()) &&
                    checkDate){
                return true;
            }
        }
        return false;
    }

    @Override
    public PageDTO<BorrowingResponseDTO> getBorrowingPage(Pageable pageable) {
        Page<Borrowing> borrowingPage = borrowingRepository.findAllCustomSort(pageable);
        Page<BorrowingResponseDTO> borrowingResponseDTO = borrowingPage.map(borrowing -> convertBorrowingToDTO(borrowing));
        PageDTO<BorrowingResponseDTO> pageDTO = new PageDTO<>(
                borrowingResponseDTO.getNumber() + 1,
                borrowingResponseDTO.getNumberOfElements(),
                borrowingResponseDTO.getTotalPages(),
                borrowingResponseDTO.getContent()
        );
        return pageDTO;
    }


    @Override
    public List<BorrowingResponseDTO> getAllBorrowings() {
        List<BorrowingResponseDTO> borrowList = new ArrayList<>();
        List<Borrowing> borrows = borrowingRepository.findAll();
        for(Borrowing borrow : borrows ) {
            BorrowingResponseDTO borrowDTO = convertBorrowingToDTO(borrow);
            borrowList.add(borrowDTO);
        }
        return borrowList;
    }

    public BorrowingResponseDTO convertBorrowingToDTO(Borrowing borrowing) {
        BorrowingResponseDTO borrowingDTO = new BorrowingResponseDTO();
        borrowingDTO.setBorrowingId(borrowing.getId());
        borrowingDTO.setBorrowingDate(borrowing.getBorrowDate());
        borrowingDTO.setReturnDate(borrowing.getReturnDate());
        UserCommentPostDTO userBorrowing = new UserCommentPostDTO();
        userBorrowing.setUserId(borrowing.getUser().getUserId());
        userBorrowing.setUserName(borrowing.getUser().getUserName());
        borrowingDTO.setUserCommentPostDTO(userBorrowing);
        BorrowingResponseDTO.BookDTO bookDTO = new BorrowingResponseDTO.BookDTO();
        bookDTO.setBookId(borrowing.getBook().getBookId());
        bookDTO.setBookName(borrowing.getBook().getBookName());
        borrowingDTO.setBookDTO(bookDTO);
        return borrowingDTO;
    }



}
