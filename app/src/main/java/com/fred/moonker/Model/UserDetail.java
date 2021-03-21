package com.fred.moonker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @auther fred
 * @create 2021-03-21 12:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail {
    private Long userId;
    private Long articleNum;
    private Long likeNum;
    private Long markNum;
}
