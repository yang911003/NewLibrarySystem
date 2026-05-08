-- =============================================
-- Library System Stored Procedures
-- =============================================

SET NAMES utf8mb4;
USE library_system;

DROP PROCEDURE IF EXISTS sp_register_user;
DROP PROCEDURE IF EXISTS sp_get_user_by_phone;
DROP PROCEDURE IF EXISTS sp_update_last_login;
DROP PROCEDURE IF EXISTS sp_get_all_books;
DROP PROCEDURE IF EXISTS sp_get_book_by_isbn;
DROP PROCEDURE IF EXISTS sp_borrow_book;
DROP PROCEDURE IF EXISTS sp_return_book;
DROP PROCEDURE IF EXISTS sp_get_user_borrows;
DROP PROCEDURE IF EXISTS sp_get_inventory_by_isbn;

DELIMITER //

-- ---------------------------------------------
-- sp_register_user: 使用者註冊
-- p_result: 1=成功, 0=手機號碼已存在, -1=系統錯誤
-- ---------------------------------------------
CREATE PROCEDURE sp_register_user(
    IN  p_phone_number VARCHAR(20),
    IN  p_password     VARCHAR(255),
    IN  p_user_name    VARCHAR(100),
    OUT p_user_id      BIGINT,
    OUT p_result       INT,
    OUT p_message      VARCHAR(255) CHARACTER SET utf8mb4
)
BEGIN
    DECLARE v_count INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result  = -1;
        SET p_message = '系統錯誤，請稍後再試';
        SET p_user_id = NULL;
    END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_count
    FROM users
    WHERE phone_number = p_phone_number;

    IF v_count > 0 THEN
        ROLLBACK;
        SET p_result  = 0;
        SET p_message = '手機號碼已被註冊';
        SET p_user_id = NULL;
    ELSE
        INSERT INTO users (phone_number, password, user_name, registration_time)
        VALUES (p_phone_number, p_password, p_user_name, NOW());

        SET p_user_id = LAST_INSERT_ID();
        COMMIT;
        SET p_result  = 1;
        SET p_message = '註冊成功';
    END IF;
END //

-- ---------------------------------------------
-- sp_get_user_by_phone: 依手機號碼查詢使用者
-- ---------------------------------------------
CREATE PROCEDURE sp_get_user_by_phone(
    IN p_phone_number VARCHAR(20)
)
BEGIN
    SELECT user_id,
           phone_number,
           password,
           user_name,
           registration_time,
           last_login_time
    FROM users
    WHERE phone_number = p_phone_number
    LIMIT 1;
END //

-- ---------------------------------------------
-- sp_update_last_login: 更新最後登入時間
-- ---------------------------------------------
CREATE PROCEDURE sp_update_last_login(
    IN p_user_id BIGINT
)
BEGIN
    UPDATE users
    SET    last_login_time = NOW()
    WHERE  user_id = p_user_id;
END //

-- ---------------------------------------------
-- sp_get_all_books: 取得所有書籍與可借數量
-- ---------------------------------------------
CREATE PROCEDURE sp_get_all_books()
BEGIN
    SELECT
        b.isbn,
        b.name,
        b.author,
        b.introduction,
        COUNT(i.inventory_id)                                      AS total_copies,
        SUM(CASE WHEN i.status = 'AVAILABLE' THEN 1 ELSE 0 END)   AS available_copies
    FROM books b
    LEFT JOIN inventory i ON b.isbn = i.isbn
    GROUP BY b.isbn, b.name, b.author, b.introduction
    ORDER BY b.name;
END //

-- ---------------------------------------------
-- sp_get_book_by_isbn: 依ISBN查詢書籍詳情
-- ---------------------------------------------
CREATE PROCEDURE sp_get_book_by_isbn(
    IN p_isbn VARCHAR(20)
)
BEGIN
    SELECT
        b.isbn,
        b.name,
        b.author,
        b.introduction,
        COUNT(i.inventory_id)                                      AS total_copies,
        SUM(CASE WHEN i.status = 'AVAILABLE' THEN 1 ELSE 0 END)   AS available_copies
    FROM books b
    LEFT JOIN inventory i ON b.isbn = i.isbn
    WHERE b.isbn = p_isbn
    GROUP BY b.isbn, b.name, b.author, b.introduction;
END //

-- ---------------------------------------------
-- sp_get_inventory_by_isbn: 取得書籍可借庫存
-- ---------------------------------------------
CREATE PROCEDURE sp_get_inventory_by_isbn(
    IN p_isbn VARCHAR(20)
)
BEGIN
    SELECT inventory_id, isbn, store_time, status
    FROM   inventory
    WHERE  isbn = p_isbn
      AND  status = 'AVAILABLE'
    ORDER BY store_time ASC
    LIMIT 1;
END //

-- ---------------------------------------------
-- sp_borrow_book: 借書（含Transaction）
-- p_result: 1=成功, 0=業務失敗, -1=系統錯誤
-- ---------------------------------------------
CREATE PROCEDURE sp_borrow_book(
    IN  p_user_id      BIGINT,
    IN  p_isbn         VARCHAR(20),
    OUT p_result       INT,
    OUT p_message      VARCHAR(255) CHARACTER SET utf8mb4,
    OUT p_inventory_id BIGINT
)
BEGIN
    DECLARE v_inventory_id BIGINT  DEFAULT NULL;
    DECLARE v_status       VARCHAR(20);
    DECLARE v_active_count INT     DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result       = -1;
        SET p_message      = '系統錯誤，交易已回滾';
        SET p_inventory_id = NULL;
    END;

    START TRANSACTION;

    -- 查詢並鎖定第一本可借的庫存（FOR UPDATE防止並發問題）
    SELECT inventory_id INTO v_inventory_id
    FROM   inventory
    WHERE  isbn   = p_isbn
      AND  status = 'AVAILABLE'
    ORDER BY store_time ASC
    LIMIT 1
    FOR UPDATE;

    IF v_inventory_id IS NULL THEN
        ROLLBACK;
        SET p_result       = 0;
        SET p_message      = '此書目前無可借閱的庫存';
        SET p_inventory_id = NULL;
    ELSE
        -- 確認使用者未重複借閱同一庫存
        SELECT COUNT(*) INTO v_active_count
        FROM   borrowing_records
        WHERE  user_id      = p_user_id
          AND  inventory_id = v_inventory_id
          AND  return_time  IS NULL;

        IF v_active_count > 0 THEN
            ROLLBACK;
            SET p_result       = 0;
            SET p_message      = '您已借閱此本書籍';
            SET p_inventory_id = NULL;
        ELSE
            -- 更新庫存狀態為已借出
            UPDATE inventory
            SET    status = 'BORROWED'
            WHERE  inventory_id = v_inventory_id;

            -- 新增借閱紀錄
            INSERT INTO borrowing_records (user_id, inventory_id, borrowing_time)
            VALUES (p_user_id, v_inventory_id, NOW());

            COMMIT;
            SET p_result       = 1;
            SET p_message      = '借閱成功';
            SET p_inventory_id = v_inventory_id;
        END IF;
    END IF;
END //

-- ---------------------------------------------
-- sp_return_book: 還書（含Transaction）
-- p_result: 1=成功, 0=業務失敗, -1=系統錯誤
-- ---------------------------------------------
CREATE PROCEDURE sp_return_book(
    IN  p_user_id      BIGINT,
    IN  p_inventory_id BIGINT,
    OUT p_result       INT,
    OUT p_message      VARCHAR(255) CHARACTER SET utf8mb4
)
BEGIN
    DECLARE v_record_id BIGINT DEFAULT NULL;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result  = -1;
        SET p_message = '系統錯誤，交易已回滾';
    END;

    START TRANSACTION;

    -- 查詢有效借閱紀錄並鎖定
    SELECT record_id INTO v_record_id
    FROM   borrowing_records
    WHERE  user_id      = p_user_id
      AND  inventory_id = p_inventory_id
      AND  return_time  IS NULL
    LIMIT 1
    FOR UPDATE;

    IF v_record_id IS NULL THEN
        ROLLBACK;
        SET p_result  = 0;
        SET p_message = '找不到有效的借閱紀錄';
    ELSE
        -- 更新庫存狀態為可借閱
        UPDATE inventory
        SET    status = 'AVAILABLE'
        WHERE  inventory_id = p_inventory_id;

        -- 更新借閱紀錄的還書時間
        UPDATE borrowing_records
        SET    return_time = NOW()
        WHERE  record_id = v_record_id;

        COMMIT;
        SET p_result  = 1;
        SET p_message = '還書成功';
    END IF;
END //

-- ---------------------------------------------
-- sp_get_user_borrows: 查詢使用者借閱紀錄
-- ---------------------------------------------
CREATE PROCEDURE sp_get_user_borrows(
    IN p_user_id BIGINT
)
BEGIN
    SELECT
        br.record_id,
        br.user_id,
        br.inventory_id,
        br.borrowing_time,
        br.return_time,
        i.isbn,
        i.status       AS inventory_status,
        b.name         AS book_name,
        b.author       AS book_author
    FROM borrowing_records br
    JOIN inventory i ON br.inventory_id = i.inventory_id
    JOIN books     b ON i.isbn          = b.isbn
    WHERE br.user_id = p_user_id
    ORDER BY br.borrowing_time DESC;
END //

DELIMITER ;