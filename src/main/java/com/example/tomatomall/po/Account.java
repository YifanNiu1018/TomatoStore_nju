package com.example.tomatomall.po;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.vo.AccountVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Account {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "user_name", unique = true)
    private String username;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "telephone")
    private String telephone;

    @Basic
    @Column(name = "password")
    private String password;

    @Basic
    @Column(name = "avatar")
    private String avatar;

    @Basic
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "location")
    private String location;

    public AccountVO toVO() {
        AccountVO accountVO = new AccountVO();
        accountVO.setId(this.id);
        accountVO.setUsername(this.username);
        accountVO.setName(this.name);
        accountVO.setAvatar(this.avatar);
        accountVO.setTelephone(this.telephone);
        accountVO.setPassword(this.password);
        accountVO.setLocation(this.location);
        accountVO.setRole(this.role);
        accountVO.setEmail(this.email);
        return accountVO;
    }
}
