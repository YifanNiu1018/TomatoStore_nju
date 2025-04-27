package com.example.tomatomall.exception;


public class TomatoMailException extends RuntimeException {
  public TomatoMailException(String message) {
    super(message);
  }
  public static TomatoMailException userNameAlreadyExist() {return new TomatoMailException("用户名已存在");
  }

  public static TomatoMailException notLogin() {return new TomatoMailException("未登录");
  }

  public static TomatoMailException userNotExist() {return new TomatoMailException("用户不存在");
  }

  public static TomatoMailException fileUploadFail() {return new TomatoMailException("图片上传失败");
  }

  public static TomatoMailException productNotExist() {return new TomatoMailException("未找到商品");}

  public static TomatoMailException loginError() {return new TomatoMailException("密码错误");
  }

  public static TomatoMailException stockpileNotEnough() {return new TomatoMailException("库存不足");
  }

  public static TomatoMailException cartItemNotExist() {return new TomatoMailException("购物车商品不存在");
  }

  public static TomatoMailException cartItemNotBelongToUser() {return new TomatoMailException("购物车商品不属于该用户");
  }

  public static TomatoMailException  advertisementNotFound() {return new TomatoMailException("广告不存在");
  }
}
