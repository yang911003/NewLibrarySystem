# NewLibrarySystem

一套以 **Spring Boot + Vue 3** 實作的圖書館借還書系統，涵蓋 JWT 身份驗證、XSS/SQL Injection 防護、Stored Procedure 交易管理與並發控制。

---

## 技術棧

| 層級 | 技術 |
|------|------|
| 後端 | Java 17、Spring Boot 3、Spring Security、JJWT |
| 資料庫 | MySQL 8、Stored Procedures、SELECT FOR UPDATE |
| 前端 | Vue 3、Vite、Pinia、Vue Router、Axios |

---

## 專案結構

```
NewLibrarySystem/
├── DB/
│   ├── DDL.sql                ← 建表語法（4 張資料表）
│   ├── DML.sql                ← 範例資料（8 本書、16 筆庫存）
│   └── StoredProcedures.sql   ← 9 支 Stored Procedures
├── src/main/java/.../
│   ├── config/                ← SecurityConfig, WebConfig (CORS)
│   ├── controller/            ← AuthController, BookController, BorrowController
│   ├── service/               ← AuthService, BookService, BorrowService
│   ├── repository/            ← 透過 SimpleJdbcCall 呼叫 SP
│   ├── model/                 ← User, Book, Inventory, BorrowingRecord
│   ├── dto/                   ← Request / Response DTO
│   ├── common/                ← JwtUtil, XssRequestWrapper
│   ├── filter/                ← XssFilter, JwtAuthenticationFilter
│   └── exception/             ← GlobalExceptionHandler, BusinessException
└── frontend/
    └── src/
        ├── views/             ← Login, Register, Books, MyBorrows
        ├── stores/            ← Pinia auth store
        ├── router/            ← Vue Router（含 auth guard）
        └── api/               ← Axios 封裝
```

---

## 快速啟動

### 1. 建立資料庫

```sql
source DB/DDL.sql
source DB/DML.sql
source DB/StoredProcedures.sql
```

### 2. 設定資料庫連線

編輯 `src/main/resources/application.properties`：

```properties
spring.datasource.password=your_password
```

### 3. 啟動後端

```bash
./gradlew bootRun
```

### 4. 啟動前端

```bash
cd frontend
npm install
npm run dev   # http://localhost:5173
```

---

## RESTful API

| Method | Path | 說明 | 驗證 |
|--------|------|------|------|
| POST | `/api/auth/register` | 註冊 | 公開 |
| POST | `/api/auth/login` | 登入，回傳 JWT | 公開 |
| GET | `/api/books` | 書籍列表含可借數量 | 公開 |
| GET | `/api/books/{isbn}` | 書籍詳情 | 公開 |
| POST | `/api/borrows/borrow` | 借書 | JWT |
| PUT | `/api/borrows/return/{inventoryId}` | 還書 | JWT |
| GET | `/api/borrows/my` | 我的借閱紀錄 | JWT |

---

## 核心設計

| 需求 | 實作方式 |
|------|---------|
| 手機號碼註冊/登入 | `RegisterRequest` / `LoginRequest` 正則驗證 `^09\d{8}$` |
| 密碼加鹽雜湊 | `BCryptPasswordEncoder`（BCrypt 已內建鹽值） |
| JWT 身份驗證 | `JwtAuthenticationFilter` 攔截每個請求，解析 Bearer Token |
| 借還書 Transaction | SP 內部 `START TRANSACTION/COMMIT/ROLLBACK` + `@Transactional` 雙層保護 |
| 防止並發借書 | SP 中 `SELECT ... FOR UPDATE` 鎖定庫存列 |
| 防 SQL Injection | 所有 DB 操作透過 `SimpleJdbcCall` 參數化呼叫 SP |
| 防 XSS | `XssFilter` + `XssRequestWrapper` 對所有輸入做 `HtmlUtils.htmlEscape()` |