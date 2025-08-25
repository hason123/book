package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.dto.RequestDTO.BookRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchBookRequest;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.entity.Book;
import com.example.book.entity.Category;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.BookRepository;
import com.example.book.repository.CategoryRepository;
import com.example.book.service.BookService;
import com.example.book.specification.BookSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final MessageConfig messageConfig;
    private final String CATEGORY_NOT_FOUND= "error.category.notfound";
    private final String BOOK_NOT_FOUND= "error.book.notfound";

    public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository, MessageConfig messageConfig) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public BookResponseDTO addBook(BookRequestDTO request) {
        log.info("Adding new book");
        Book book = new Book();
        book.setAuthor(request.getAuthor());
        book.setBookDesc(request.getBookDesc());
        book.setBookName(request.getBookName());
        book.setPageCount(request.getPageCount());
        book.setPublisher(request.getPublisher());
        book.setQuantity(request.getQuantity());
        if (request.getCategoryIDs() != null) {
            List<Category> categories = request.getCategoryIDs().stream()
                    .map(categoryID -> categoryRepository.findById(categoryID)
                            .orElseThrow(() ->
                            {
                                log.error("Category with id: {} not found", categoryID);
                                return new ResourceNotFoundException(messageConfig.getMessage(CATEGORY_NOT_FOUND,categoryID));
                            }))
                    .toList();
            book.setCategories(categories);
        }
        bookRepository.save(book);
        log.info("Book with id: {} has been added", book.getBookId());
        return convertBookToDTO(book);
    }

    @Override
    public BookResponseDTO getBookById(Long id) {
        log.info("Getting book with id: {}", id);
        Book book = bookRepository.findById(id).orElse(null);
        if(book == null){
            log.error("Book with id: {} not found", id);
            throw new ResourceNotFoundException(messageConfig.getMessage(BOOK_NOT_FOUND,id));
        }
        log.info("Return book with id {}", book.getBookId());
        return convertBookToDTO(book);
    }

    @Override
    public void deleteBookById(Long id) {
        log.info("Deleting book with id: {}", id);
        if(!bookRepository.existsById(id)){
            log.error(messageConfig.getMessage(BOOK_NOT_FOUND,id));
            throw new ResourceNotFoundException(messageConfig.getMessage(BOOK_NOT_FOUND,id));
        }
        log.info("Book with id: {} has been deleted", id);
        bookRepository.deleteById(id);
    }

    @Override
    public BookResponseDTO updateBook(Long id, BookRequestDTO request) {
        log.info("Updating book with id: {}", id);
        Book updatedBook = bookRepository.findById(id).orElse(null);
        if(updatedBook == null){
            log.error(messageConfig.getMessage(BOOK_NOT_FOUND,id));
            throw new ResourceNotFoundException(messageConfig.getMessage(BOOK_NOT_FOUND,id));
        }
        updatedBook.setAuthor(request.getAuthor());
        updatedBook.setBookDesc(request.getBookDesc());
        updatedBook.setBookName(request.getBookName());
        updatedBook.setPageCount(request.getPageCount());
        updatedBook.setPublisher(request.getPublisher());
        updatedBook.setQuantity(request.getQuantity());
        if (request.getCategoryIDs() != null) {
            List<Category> categories = request.getCategoryIDs().stream()
                    .map(categoryID -> categoryRepository.findById(categoryID)
                            .orElseThrow(() ->
                            {
                                log.error(messageConfig.getMessage(CATEGORY_NOT_FOUND,categoryID));
                                return new ResourceNotFoundException(messageConfig.getMessage(CATEGORY_NOT_FOUND,categoryID));
                            }))
                    .toList();
            updatedBook.setCategories(categories);
        }
        bookRepository.save(updatedBook);
        return convertBookToDTO(updatedBook);
    }

    @Override
    public PageResponseDTO<BookResponseDTO> searchBooks(SearchBookRequest request, Pageable pageable) {
        log.info("Searching books with request: {}", request);
        String bookName = request.getBookName();
        String author = request.getAuthor();
        Long bookId = request.getBookId();
        String language = request.getLanguage();
        String printType = request.getPrintType();
        String publisher = request.getPublisher();
        Integer minQuantity = request.getMinQuantity();
        Integer maxQuantity = request.getMaxQuantity();
        Integer minPageCount = request.getMinPageCount();
        Integer maxPageCount = request.getMaxPageCount();
        List<String> categories = request.getCategories();

        Specification<Book> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(bookName)) {
            spec = spec.and(BookSpecification.likeName(bookName));
        }
        if (StringUtils.hasText(author)) {
            spec = spec.and(BookSpecification.likeAuthor(author));
        }
        if (bookId != null) {
            spec = spec.and(BookSpecification.hasId(bookId));
        }
        if (StringUtils.hasText(language)) {
            spec = spec.and(BookSpecification.likeLanguage(language));
        }
        if (StringUtils.hasText(printType)) {
            spec = spec.and(BookSpecification.likePrintType(printType));
        }
        if (StringUtils.hasText(publisher)) {
            spec = spec.and(BookSpecification.likePublisher(publisher));
        }
        if (minPageCount != null) {
            spec = spec.and(BookSpecification.moreThanPageCount(minPageCount));
        }
        if (maxPageCount != null) {
            spec = spec.and(BookSpecification.lessThanPageCount(maxPageCount));
        }
        if (minQuantity != null) {
            spec = spec.and(BookSpecification.moreThanEqualQuantity(minQuantity));
        }
        if (maxQuantity != null) {
            spec = spec.and(BookSpecification.lessThanEqualQuantity(maxQuantity));
        }
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(BookSpecification.hasBookWithCategories(categories));
        }

        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        Page<BookResponseDTO> bookResponseDTO = bookPage.map(this::convertBookToDTO);
        log.info("Returning books with {} books found", bookResponseDTO.getTotalElements());
        return new PageResponseDTO<>(
                bookResponseDTO.getNumber() + 1,
                bookResponseDTO.getNumberOfElements(),
                bookResponseDTO.getTotalPages(),
                bookResponseDTO.getContent()
        );
    }

    @Override
    public PageResponseDTO<BookResponseDTO> getBookPage(Pageable pageable) {
        log.info("Getting book page with pageable: {}", pageable);
        Page<Book> bookPage = bookRepository.findAll(pageable);
        Page<BookResponseDTO> bookResponseDTO = bookPage.map(this::convertBookToDTO);
        PageResponseDTO<BookResponseDTO> pageDTO = new PageResponseDTO<>(
                bookResponseDTO.getNumber() + 1,
                bookResponseDTO.getNumberOfElements(),
                bookResponseDTO.getTotalPages(),
                bookResponseDTO.getContent()
                );
        log.info("Returning books with {} books found", pageDTO.getTotalElements());
        return pageDTO;
    }

    public BookResponseDTO convertBookToDTO(Book book) {
        BookResponseDTO bookDTO = new BookResponseDTO();
        bookDTO.setBookId(book.getBookId());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setBookDesc(book.getBookDesc());
        bookDTO.setBookName(book.getBookName());
        bookDTO.setPageCount(book.getPageCount());
        bookDTO.setPublisher(book.getPublisher());
        bookDTO.setQuantity(book.getQuantity());
        bookDTO.setPrintType(book.getPrintType());
        bookDTO.setLanguage(book.getLanguage());
        bookDTO.setBookDesc(book.getBookDesc());
        List<BookResponseDTO.CategoryDTO> categoryDTOs =
                Optional.ofNullable(book.getCategories())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .map(c -> new BookResponseDTO.CategoryDTO(c.getCategoryId(), c.getCategoryName()))
                        .collect(Collectors.toList());
        bookDTO.setCategories(categoryDTOs);
        return bookDTO;
    }
}
