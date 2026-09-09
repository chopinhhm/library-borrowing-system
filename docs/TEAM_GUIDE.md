# 团队协作说明

所有功能分支以公共基线为起点。成员开发前先同步 `dev`，完成后通过 Pull Request 合并。

| 成员 | 分支 | 主要目录 |
| --- | --- | --- |
| 黄浩茗 | `feature/A-auth` | `modulea`、公共配置 |
| 何小雅 | `feature/B-data` | `moduleb` |
| 李雨轩 | `feature/C-circulation` | `modulec` |
| 杨青 | `feature/D-analysis` | `moduled`、统计与度量 |

开发流程：

```bash
git fetch origin
git switch feature/A-auth
git merge origin/dev
# 修改并测试
mvn test
git add .
git commit -m "feat(A): 描述本次功能"
git push origin feature/A-auth
```

禁止多人共用同一 GitHub Token。每位成员应使用自己的 GitHub 账号和邮箱提交。
