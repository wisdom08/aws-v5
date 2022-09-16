package com.example.awsv5.service;

import com.example.awsv5.domain.Book;
import com.example.awsv5.domain.BookRepo;
import com.example.awsv5.web.dto.BookRespDto;
import com.example.awsv5.web.dto.BookSaveReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepo bookRepo;

    @Transactional(rollbackFor = RuntimeException.class)
    public BookRespDto createBook(BookSaveReqDto reqDto) {
        Book bookEntity = bookRepo.save(reqDto.toEntity());
        return new BookRespDto(bookEntity);
    }

    @Transactional(readOnly = true)
    public List<BookRespDto> getBooks() {
        List<Book> booksEntity = bookRepo.findAll();
        return booksEntity.stream()
                .map(BookRespDto::new)
                .collect(Collectors.toList());
    }
}
