package com.example.book.service.impl;

import com.example.book.dto.RequestDTO.SearchBookRequest;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.PageDTO;
import com.example.book.entity.Book;
import com.example.book.entity.Category;
import com.example.book.repository.BookRepository;
import com.example.book.repository.CategoryRepository;
import com.example.book.service.BookService;
import com.example.book.service.CategoryService;
import com.example.book.specification.BookSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository, CategoryService categoryService) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    @Override
    public BookResponseDTO addBook(Book book) {
        if(book.getCategories() != null) {
           List<Category> categories = new ArrayList<>();
           for(Category category : book.getCategories()) {
               Optional<Category> categoryAdded = categoryRepository.findById(category.getCategoryId());
               if(categoryAdded.isPresent()) {
                   categories.add(categoryAdded.get());
               }
               else{
                   throw new EntityNotFoundException(
                           "Category with ID " + category.getCategoryId() + " not found"
                   );
               }
           }
           book.setCategories(categories);
        }
        bookRepository.save(book);
        return convertBookToDTO(book);
    }

    @Override
    public Optional<Book> getBookById(Long id) {
       return bookRepository.findById(id);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookResponseDTO updateBook(Long id, Book book) {
        try{
            Book updatedBook = bookRepository.getReferenceById(id);
            updatedBook.setAuthor(book.getAuthor());
            updatedBook.setBookDesc(book.getBookDesc());
            updatedBook.setBookName(book.getBookName());
            updatedBook.setPageCount(book.getPageCount());
            updatedBook.setPublisher(book.getPublisher());
            updatedBook.setQuantity(book.getQuantity());
            if(updatedBook.getCategories() != null) {
                List<Category> categories = new ArrayList<>();
                for(Category category : updatedBook.getCategories()) {
                    Optional<Category> categoryAdded = categoryRepository.findById(category.getCategoryId());
                    if(categoryAdded.isPresent()) {
                        categories.add(categoryAdded.get());
                    }
                    else{
                        throw new EntityNotFoundException(
                                "Category with ID " + category.getCategoryId() + " not found"
                        );
                    }
                }
                updatedBook.setCategories(categories);
            }
            bookRepository.save(updatedBook);
            return convertBookToDTO(updatedBook);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @Override
    public List<BookResponseDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        List <BookResponseDTO> bookResponseDTOS = new ArrayList<>();
        for (Book book : books) {
            BookResponseDTO bookResponseDTO = convertBookToDTO(book);
            bookResponseDTOS.add(bookResponseDTO);
        }
        return bookResponseDTOS;
    }

    @Override
    public PageDTO<BookResponseDTO> searchBooks(SearchBookRequest request, Pageable pageable) {
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

        return new PageDTO<>(
                bookResponseDTO.getNumber() + 1,
                bookResponseDTO.getNumberOfElements(),
                bookResponseDTO.getTotalPages(),
                bookResponseDTO.getContent()
        );
    }

    @Override
    public PageDTO<BookResponseDTO> getBookPage(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        Page<BookResponseDTO> bookResponseDTO = bookPage.map(book -> convertBookToDTO(book));
        PageDTO<BookResponseDTO> pageDTO = new PageDTO<>(
                bookResponseDTO.getNumber() + 1,
                bookResponseDTO.getNumberOfElements(),
                bookResponseDTO.getTotalPages(),
                bookResponseDTO.getContent()
                );
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

        List<BookResponseDTO.CategoryDTO> categoryDTOs = new ArrayList<>();
        if (book.getCategories() != null) {
            for (Category category : book.getCategories()) {
                BookResponseDTO.CategoryDTO dto = new BookResponseDTO.CategoryDTO();
                dto.setCategoryId(category.getCategoryId());
                dto.setCategoryName(category.getCategoryName());
                categoryDTOs.add(dto);
            }
        }
        bookDTO.setCategories(categoryDTOs);
        return bookDTO;
    }
}
