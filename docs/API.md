# API 使用说明

系统使用 HTTP Basic Authentication。管理员默认账号为 `admin`，读者默认账号为 `reader`。正式部署时应通过环境变量修改初始密码。

## 公共认证接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/auth/me` | 当前登录账号、角色和关联读者 |
| GET | `/api/books?keyword=` | 查询馆藏 |
| GET | `/api/reader-types` | 查询借阅规则 |

## 管理员接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST/PUT/DELETE | `/api/admin/books` | 新增、编辑、删除图书 |
| GET/POST/PUT | `/api/admin/readers` | 查询、新增、编辑读者 |
| POST/PUT | `/api/admin/reader-types` | 新增、编辑读者类型和规则 |
| GET/POST/PATCH | `/api/admin/accounts` | 查询、新增、启停账号 |
| GET | `/api/admin/statistics` | 流通概览统计 |
| GET | `/api/admin/logs` | 查询操作日志 |

## 借阅流通接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/circulation/borrow?readerId=&bookId=` | 借书 |
| POST | `/api/circulation/loans/{id}/return` | 还书并结算罚金 |
| POST | `/api/circulation/loans/{id}/renew` | 续借 |
| POST | `/api/circulation/reserve?readerId=&bookId=` | 预约 |
| POST | `/api/circulation/reservations/{id}/cancel` | 取消预约 |
| GET | `/api/circulation/readers/{id}/loans` | 读者借阅记录 |
| GET | `/api/circulation/readers/{id}/reservations` | 读者预约记录 |
| GET | `/api/circulation/admin/overdue` | 逾期清单 |

交互式接口文档位于 `/swagger-ui/index.html`。
