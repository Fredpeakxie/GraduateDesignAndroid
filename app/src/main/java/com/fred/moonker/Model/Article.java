package com.fred.moonker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @auther fred
 * @create 2021-02-19 8:59
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    private Long articleId;
    private Long authorId;
    private Long type;
    private String title;//50
    private Long readNum;
    private String articleContent;//longtext
    private List<String> pics;

}
