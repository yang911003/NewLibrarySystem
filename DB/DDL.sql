-- =============================================
-- Library System DDL
-- Database: MySQL 8.0+
-- =============================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS library_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE library_system;

-- ---------------------------------------------
-- Table: users (使用者)
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id           BIGINT          NOT NULL AUTO_INCREMENT,
    phone_number      VARCHAR(20)     NOT NULL COMMENT '手機號碼（登入帳號）',
    password          VARCHAR(255)    NOT NULL COMMENT 'BCrypt雜湊密碼（含鹽值）',
    user_name         VARCHAR(100)    NOT NULL COMMENT '使用者名稱',
    registration_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '註冊時間',
    last_login_time   DATETIME                 COMMENT '最後登入時間',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='使用者資料表';

-- ---------------------------------------------
-- Table: books (書籍)
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS books (
    isbn              VARCHAR(20)     NOT NULL COMMENT '國際標準書號',
    name              VARCHAR(255)    NOT NULL COMMENT '書名',
    author            VARCHAR(100)             COMMENT '作者',
    introduction      TEXT                     COMMENT '書籍簡介',
    PRIMARY KEY (isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='書籍資料表';

-- ---------------------------------------------
-- Table: inventory (庫存)
-- Status: AVAILABLE=在庫, BORROWED=出借中,
--         PROCESSING=整理中, LOST=遺失,
--         DAMAGED=損毀, DISCARDED=廢棄
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id      BIGINT          NOT NULL AUTO_INCREMENT,
    isbn              VARCHAR(20)     NOT NULL COMMENT '對應書籍ISBN',
    store_time        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入庫時間',
    status            ENUM('AVAILABLE','BORROWED','PROCESSING','LOST','DAMAGED','DISCARDED')
                                      NOT NULL DEFAULT 'AVAILABLE' COMMENT '書籍狀態',
    PRIMARY KEY (inventory_id),
    KEY idx_isbn (isbn),
    KEY idx_status (status),
    CONSTRAINT fk_inventory_book FOREIGN KEY (isbn) REFERENCES books (isbn)
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='庫存資料表';

-- ---------------------------------------------
-- Table: borrowing_records (借閱紀錄)
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS borrowing_records (
    record_id         BIGINT          NOT NULL AUTO_INCREMENT,
    user_id           BIGINT          NOT NULL COMMENT '借閱使用者ID',
    inventory_id      BIGINT          NOT NULL COMMENT '借出庫存ID',
    borrowing_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借出時間',
    return_time       DATETIME                 COMMENT '歸還時間（NULL表示未歸還）',
    PRIMARY KEY (record_id),
    KEY idx_user_id (user_id),
    KEY idx_inventory_id (inventory_id),
    CONSTRAINT fk_borrow_user      FOREIGN KEY (user_id)      REFERENCES users     (user_id),
    CONSTRAINT fk_borrow_inventory FOREIGN KEY (inventory_id) REFERENCES inventory (inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借閱紀錄資料表';