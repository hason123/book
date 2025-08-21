package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.entity.Category;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.BookRepository;
import com.example.book.repository.CategoryRepository;
import com.example.book.service.CategoryService;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Category category = categoryRepository.findById(id).orElse(null);
        if(category == null){
            throw new ResourceNotFoundException(messageConfig.getMessage(CATEGORY_NOT_FOUND,id));
        }
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
        Category updatedCategory = categoryRepository.findById(id).orElse(null);
        if(updatedCategory == null){
            throw new ResourceNotFoundException(messageConfig.getMessage(CATEGORY_NOT_FOUND,id));
        }
        updatedCategory.setCategoryName(request.getCategoryName());
        categoryRepository.save(updatedCategory);
        return convertEntityToDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(CATEGORY_NOT_FOUND,id)));
        categoryRepository.deleteById(id);
    }

    @Override
    public PageResponseDTO<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        Page<CategoryResponseDTO> categoryDTO = categories.map(category -> convertEntityToDTO(category));
        PageResponseDTO<CategoryResponseDTO> categoryPage = new PageResponseDTO<>(
                categoryDTO.getNumber() + 1,
                categoryDTO.getNumberOfElements(),
                categoryDTO.getTotalPages(),
                categoryDTO.getContent()
        );
        return categoryPage;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void createCategoryWorkbook(HttpServletResponse response) throws IOException {
        List<Object[]> result = categoryRepository.findCategoryAndBookCount();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Category Report");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Tên thể loại");
        header.createCell(2).setCellValue("Số lượng sách");
        int rowNum = 1; int index = 1;
        //co ve Object[] luu nhieu lieu di lieu khac nhau
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
    }

    public CategoryResponseDTO convertEntityToDTO(Category category) {
        CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setCategoryId(category.getCategoryId());
        List<CategoryResponseDTO.BookBasic> bookBasics =
            Optional.ofNullable(category.getBooks())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(book -> new CategoryResponseDTO.BookBasic(book.getBookId(), book.getBookName(), book.getAuthor()))
                    .collect(Collectors.toList());
        categoryDTO.setBookBasic(bookBasics);
        return categoryDTO;
    }






}
