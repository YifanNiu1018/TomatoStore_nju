package com.example.tomatomall.vo;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.po.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AccountVO {
    private Integer id;

    private String username;

    private String name;

    private String avatar;

    private String telephone;

    private String password;

    private String location;

    private RoleEnum role;

    private String email;

    public Account toPO() {
        Account account = new Account();
        account.setId(this.id);
        account.setUsername(this.username);
        account.setName(this.name);
        account.setAvatar(this.avatar);
        account.setTelephone(this.telephone);
        account.setPassword(this.password);
        account.setLocation(this.location);
        account.setRole(this.role);
        account.setEmail(this.email);
        return account;
    }
}
