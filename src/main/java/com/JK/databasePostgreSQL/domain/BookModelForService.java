package com.JK.databasePostgreSQL.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookModelForService {
    private String isbn;
    private String title;
    private Long authorId;
}
