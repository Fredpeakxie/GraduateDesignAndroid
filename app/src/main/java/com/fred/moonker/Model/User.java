package com.fred.moonker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long UserID;
    private String username;//20
    private String password;//20
    private String email;//20
    private String nickname;//20
    private String introduction;//100

    @Override
    public String toString() {
        return "User{" +
                "UserID=" + UserID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", introduction='" + introduction + '\'' +
                '}';
    }
}
