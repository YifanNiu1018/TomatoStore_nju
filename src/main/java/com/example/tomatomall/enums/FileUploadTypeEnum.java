package com.example.tomatomall.enums;

public enum FileUploadTypeEnum {

    BOARD_COVER("板块封面", Constants.IMAGE_SUFFIX),
    ARTICLE_COVER("文章封面", Constants.IMAGE_SUFFIX),
    COMMENT_IMAGE("评论图片", Constants.IMAGE_SUFFIX),
    AVATAR("个人头像", Constants.IMAGE_SUFFIX);


    private String desc;
    private String[] suffixArray;

    FileUploadTypeEnum(String desc, String[] suffixArray) {
        this.desc = desc;
        this.suffixArray = suffixArray;
    }

    public String getDesc() {
        return desc;
    }

    public String[] getSuffixArray() {
        return suffixArray;
    }
}
