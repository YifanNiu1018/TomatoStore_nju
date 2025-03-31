package com.example.tomatomall.vo;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.po.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserVO {
    private Integer id;

    private String username;

    private String name;

    private String avatar;

    private String telephone;

    private String password;

    private String location;

    private RoleEnum role;

    private String email;

    public User toPO() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setName(this.name);
        user.setAvatar(this.avatar);
        user.setTelephone(this.telephone);
        user.setPassword(this.password);
        user.setLocation(this.location);
        user.setRole(this.role);
        user.setEmail(this.email);
        return user;
    }
}
