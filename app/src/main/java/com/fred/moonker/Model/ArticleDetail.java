package com.fred.moonker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetail {
    private Long articleID;
    private Long authorID;
    private String title;//50
    private Long type;
    private String nickname;//t_user
    private String articleContent;//html
    private Long readNum;
    private Long likeNum;//t_like
    private Long markNum;//t_mark
}

