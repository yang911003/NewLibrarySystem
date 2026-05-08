
---
專案結構總覽

NewLibrarySystem/                                                                                                                                                               
├── DB/                                                                                                                                                                       
│   ├── DDL.sql              ← 建表語法 (4張資料表)
│   ├── DML.sql              ← 範例資料 (8本書、16筆庫存)
│   └── StoredProcedures.sql ← 9支 Stored Procedures
├── src/main/java/.../
│   ├── config/              ← SecurityConfig, WebConfig (CORS)
│   ├── controller/          ← 展示層 (AuthController, BookController, BorrowController)
│   ├── service/             ← 業務層 (AuthService, BookService, BorrowService)
│   ├── repository/          ← 資料層 (透過 SimpleJdbcCall 呼叫 SP)
│   ├── model/               ← User, Book, Inventory, BorrowingRecord
│   ├── dto/                 ← Request/Response DTO
│   ├── common/              ← JwtUtil, XssRequestWrapper
│   ├── filter/              ← XssFilter, JwtAuthenticationFilter
│   └── exception/           ← GlobalExceptionHandler, BusinessException
└── frontend/                ← Vue 3 + Vite + Pinia
└── src/
├── views/           ← Login, Register, Books, MyBorrows
├── stores/          ← Pinia auth store
├── router/          ← Vue Router (含 auth guard)
└── api/             ← Axios 封裝

啟動步驟

1. 建立資料庫
   -- MySQL 依序執行：
   source DB/DDL.sql
   source DB/DML.sql
   source DB/StoredProcedures.sql

2. 設定資料庫連線（application.properties）
   spring.datasource.password=your_password

3. 啟動後端
   ./gradlew bootRun

4. 啟動前端
   cd frontend
   npm install
   npm run dev   # http://localhost:5173

核心設計說明

┌────────────────────┬────────────────────────────────────────────────────────────────────────────────┐
│        需求        │                                    實作方式                                    │
├────────────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 手機號碼註冊/登入  │ RegisterRequest / LoginRequest 加上正則驗證 ^09\d{8}$                          │
├────────────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 密碼加鹽雜湊       │ BCryptPasswordEncoder（BCrypt 已內建鹽值，嵌入於雜湊字串中）                   │
├────────────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ JWT 身份驗證       │ JwtAuthenticationFilter 攔截每個請求，解析 Bearer Token                        │
├────────────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 借還書 Transaction │ SP 內部 START TRANSACTION/COMMIT/ROLLBACK + Service 層 @Transactional 雙層保護 │
├────────────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 防止並發借書       │ SP 中用 SELECT ... FOR UPDATE 鎖定庫存列                                       │
├────────────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 防 SQL Injection   │ 所有 DB 操作透過 SimpleJdbcCall 參數化呼叫 SP（Prepared Statement）            │
├────────────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 防 XSS             │ XssFilter + XssRequestWrapper 對所有輸入做 HtmlUtils.htmlEscape()              │
└────────────────────┴────────────────────────────────────────────────────────────────────────────────┘

RESTful API

┌────────┬───────────────────────────────────┬────────────────────┬──────┐
│ Method │               Path                │        說明        │ 驗證 │
├────────┼───────────────────────────────────┼────────────────────┼──────┤
│ POST   │ /api/auth/register                │ 註冊               │ 公開 │
├────────┼───────────────────────────────────┼────────────────────┼──────┤
│ POST   │ /api/auth/login                   │ 登入，回傳 JWT     │ 公開 │
├────────┼───────────────────────────────────┼────────────────────┼──────┤
│ GET    │ /api/books                        │ 書籍列表含可借數量 │ 公開 │
├────────┼───────────────────────────────────┼────────────────────┼──────┤
│ GET    │ /api/books/{isbn}                 │ 書籍詳情           │ 公開 │
├────────┼───────────────────────────────────┼────────────────────┼──────┤
│ POST   │ /api/borrows/borrow               │ 借書               │ JWT  │
├────────┼───────────────────────────────────┼────────────────────┼──────┤
│ PUT    │ /api/borrows/return/{inventoryId} │ 還書               │ JWT  │
├────────┼───────────────────────────────────┼────────────────────┼──────┤
│ GET    │ /api/borrows/my                   │ 我的借閱紀錄       │ JWT  │
└────────┴───────────────────────────────────┴────────────────────┴──────┘

