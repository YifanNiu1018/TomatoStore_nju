package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "boards")
public class ForumBoard {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "board_id")
    private Integer boardId;

    @Column(name = "board_name")
    private String boardName;

    @Column(name = "cover")
    private String cover;

    @Column(name = "board_desc")
    private String boardDesc;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "joined_count")
    private Integer joinedCount;

    @Column(name = "create_user_id")
    private String createUserId;


    private List<ForumBoard> children;
}
