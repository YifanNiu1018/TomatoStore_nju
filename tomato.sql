CREATE TABLE users
(
    id        INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username  VARCHAR(50)  NOT NULL COMMENT '用户名，不允许为空',
    password  VARCHAR(100) NOT NULL COMMENT '用户密码，仅参与插入操作',
    name      VARCHAR(50)  NOT NULL COMMENT '用户姓名，不允许为空',
    avatar    VARCHAR(255) COMMENT '用户头像链接',
    role      VARCHAR(50) COMMENT '用户身份',
    telephone VARCHAR(11) COMMENT '用户手机号，格式需符合1开头的11位数字',
    email     VARCHAR(100) COMMENT '用户邮箱，格式需符合邮箱规范',
    location  VARCHAR(255) COMMENT '用户所在地'
) COMMENT ='用户表';

CREATE TABLE products
(
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品id',
    title       VARCHAR(50)    NOT NULL COMMENT '商品名称，不允许为空',
    price       DECIMAL(10, 2) NOT NULL COMMENT '商品价格，不允许为空，最低为0元',
    rate        DOUBLE         NOT NULL COMMENT '商品评分，最低0分，最高10分',
    description VARCHAR(255) COMMENT '商品描述',
    cover       VARCHAR(500) COMMENT '商品封面url',
    detail      VARCHAR(500) COMMENT '商品详细说明'
) COMMENT ='商品表';

CREATE TABLE specifications
(
    id         INT AUTO_INCREMENT PRIMARY KEY COMMENT '规格id',
    item       VARCHAR(50)  NOT NULL COMMENT '规格名称，不允许为空',
    value      VARCHAR(255) NOT NULL COMMENT '规格内容，不允许为空',
    product_id INT          NOT NULL COMMENT '所属商品id，不允许为空',
    FOREIGN KEY (product_id) REFERENCES products (id)
) COMMENT ='商品规格表';

CREATE TABLE stockpiles
(
    id         INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品库存id',
    product_id INT NOT NULL COMMENT '所属商品id，不允许为空',
    amount     INT NOT NULL COMMENT '商品库存数，指可卖的商品数量，不允许为空',
    frozen     INT NOT NULL COMMENT '商品库存冻结数，指不可卖的商品数量，不允许为空',
    FOREIGN KEY (product_id) REFERENCES products (id)
) COMMENT ='商品库存表';

CREATE TABLE carts (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车商品id', 
    user_id INT NOT NULL COMMENT '用户id，关联用户表',
    product_id INT NOT NULL COMMENT '商品id，关联商品表',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量，默认为1',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) COMMENT='购物车商品表';

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    user_id INT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    payment_method VARCHAR(50) NOT NULL COMMENT '支付方式',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单支付状态（PENDING, SUCCESS, FAILED, TIMEOUT）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT='订单表';

CREATE TABLE carts_orders_relation (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    cart_item_id INT NOT NULL COMMENT '关联购物车商品ID',
    order_id INT NOT NULL COMMENT '关联订单ID',
    FOREIGN KEY (cart_item_id) REFERENCES carts(cart_item_id) ,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
) COMMENT='购物车商品与订单关联表';

