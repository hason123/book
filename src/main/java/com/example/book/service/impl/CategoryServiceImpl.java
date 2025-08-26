package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.entity.Book;
import com.example.book.entity.Category;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.BookRepository;
import com.example.book.repository.CategoryRepository;
import com.example.book.service.CategoryService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final MessageConfig messageConfig;
    private final String CATEGORY_NOT_FOUND= "error.category.notfound";
    //private final String BOOK_NOT_FOUND= "error.book.notfound";

    public CategoryServiceImpl(CategoryRepository categoryRepository, BookRepository bookRepository, MessageConfig messageConfig) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public CategoryResponseDTO getCategory(Long id) {
        log.info("Getting category with id {}", id);
        Category category = categoryRepository.findById(id).orElse(null);
        if(category == null){
            log.error("Category with id {} not found", id);
            throw new ResourceNotFoundException(messageConfig.getMessage(CATEGORY_NOT_FOUND,id));
        }
        log.info("Returning category {}", category);
        return convertEntityToDTO(category);
    }

    @Override
    public CategoryResponseDTO addCategory(CategoryResponseDTO request) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        categoryRepository.save(category);
        return convertEntityToDTO(category);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryResponseDTO request) {
        log.info("Updating category with id {}", id);
        Category updatedCategory = categoryRepository.findById(id).orElse(null);
        if(updatedCategory == null){
            log.error("Category with id {} not found", id);
            throw new ResourceNotFoundException(messageConfig.getMessage(CATEGORY_NOT_FOUND,id));
        }
        if(request.getCategoryName() != null){
            updatedCategory.setCategoryName(request.getCategoryName());
        }
        else updatedCategory.setCategoryName(updatedCategory.getCategoryName());
        categoryRepository.save(updatedCategory);
        log.info("Updated successfully");
        return convertEntityToDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(CATEGORY_NOT_FOUND, id)));
        category.getBooks().forEach(book ->
        {
            book.getCategories().remove(category);
            bookRepository.save(book);
        } );
        categoryRepository.delete(category);
    }

    @Override
    public PageResponseDTO<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        log.info("Getting category's page");
        Page<Category> categories = categoryRepository.findAll(pageable);
        Page<CategoryResponseDTO> categoryDTO = categories.map(category -> convertEntityToDTO(category));
        PageResponseDTO<CategoryResponseDTO> categoryPage = new PageResponseDTO<>(
                categoryDTO.getNumber() + 1,
                categoryDTO.getNumberOfElements(),
                categoryDTO.getTotalPages(),
                categoryDTO.getContent()
        );
        log.info("Returning category page!");
        return categoryPage;
    }

    @Override
    public void createCategoryWorkbook(HttpServletResponse response) throws IOException {
        log.info("Creating category workbook");
        List<Object[]> result = categoryRepository.findCategoryAndBookCount();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Category Report");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Tên thể loại");
        header.createCell(2).setCellValue("Số lượng sách");
        int rowNum = 1; int index = 1;
        //co ve Object[] luu nhieu lieu du lieu khac nhau
        for (Object[] record : result) {
            Category category = (Category) record[0];
            Long bookCount = (Long) record[1];
            Row excelRow = sheet.createRow(rowNum++);
            excelRow.createCell(0).setCellValue(index++);
            excelRow.createCell(1).setCellValue(category.getCategoryName());
            excelRow.createCell(2).setCellValue(bookCount);
        }
        response.setHeader("Content-Type", "attachment; filename=borrowing.xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        log.info("Successfully created category workbook");
    }

    public CategoryResponseDTO convertEntityToDTO(Category category) {
        CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setCategoryId(category.getCategoryId());
        List<CategoryResponseDTO.BookBasic> bookBasics =
            Optional.ofNullable(category.getBooks())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(book -> new CategoryResponseDTO.BookBasic(book.getBookId(), book.getBookName()))
                    .collect(Collectors.toList());
        categoryDTO.setBookBasic(bookBasics);
        return categoryDTO;
    }






}
