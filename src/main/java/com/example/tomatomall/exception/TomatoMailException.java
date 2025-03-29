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

<<<<<<< HEAD
  public static TomatoMailException productNotExist() {
    return new TomatoMailException("未找到商品");
=======
  public static TomatoMailException loginError() {return new TomatoMailException("密码错误");
>>>>>>> 4788d523c21cc75d7c3a554aaff5fc73680f6d09
  }
}
