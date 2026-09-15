# SourceMonitor 改进前后对比说明

仓库：

```text
https://github.com/chopinhhm/library-borrowing-system
```

## 两个代码版本

改进前基线：

```text
git tag: sm-baseline
commit: b0e5ed8
```

改进后：

```text
git branch: feature/sourcemonitor-improvements
git tag: sm-after
commit: f6dc709
```

## SourceMonitor 分析范围

两个版本必须使用相同范围：

```text
src/main/java
src/test/java
文件过滤：*.java
编码：UTF-8
```

重点比较文件：

```text
src/main/java/com/chopinhhm/library/moduleb/CatalogService.java
src/main/java/com/chopinhhm/library/modulea/AccountService.java
```

## 代码改动摘要

`CatalogService.java`：

- `saveReaderType` 的参数校验拆成 `validateReaderType` 与 `validateReaderTypeNumbers`。
- `saveBook` 的库存计算拆成 `resolveAvailableCopies`、`resolveNewBookCopies` 和 `resolveUpdatedBookCopies`。
- `searchBooks` 的查询加载与过滤条件拆成 `loadCandidates`、`matchesCategory`、`matchesShelf` 和 `matchesAvailability`。

`AccountService.java`：

- `create` 的账号和密码校验拆成 `validateNewAccount` 与 `validatePassword`。
- `update` 的启用状态、密码和角色更新拆成独立方法。
- 默认角色处理提取为 `normalizeRole`。

## 静态初筛对照

以下数值用于交接和复核，最终报告以 SourceMonitor 导出为准：

| 指标 | 改进前 | 改进后 |
| --- | ---: | ---: |
| 物理行 | 1703 | 1762 |
| 有效代码行 | 1509 | 1553 |
| 分支语句比例 | 12.25% | 11.45% |
| 函数个数 | 212 | 225 |
| 最大圈复杂度 | 8 | 5 |
| 平均圈复杂度 | 1.35 | 1.32 |
| 最大块深度 | 2 | 2 |

主要目标是降低单个业务方法的复杂度，因此方法总数和物理行略有增加是预期结果。

## 同学需要返回

改进前、改进后各一套：

1. 建立 SourceMonitor 工程的过程截图。
2. 汇总分析报告。
3. 明细分析报告。
4. `CatalogService.java` 文件度量视图，包含雷达图和三维柱状图。
5. `AccountService.java` 文件度量视图，包含雷达图和三维柱状图。
6. 方法级复杂度明细，至少包含 `saveReaderType`、`saveBook`、`searchBooks`、`create` 和 `update`。
7. 如果支持，导出 XML 或 CSV。
