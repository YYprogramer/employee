# CRUD処理を備えたRestAPIの作成

### 概要

職員番号を主キーとし、氏名と年齢をカラムにもつSQLを作成しています。

### CRUD処理の実装

- Read処理
  - id検索(パスパラメータ検索)の実装
  - 全件検索及び部分一致検索(クエリパラメータ)の実装
  - 部分一致検索(クエリパラメータ)の例外処理(NotFoundException)
  - id検索(パスパラメータ検索)の例外処理(NotFoundException)


- Create処理
  - データ登録処理の実装
  - データ登録における例外処理の確認 (MethodArgumentNotValidException)


- Update処理
  - データ更新処理の実装
  - データ更新における例外処理の確認 (NotFoundException)


- Delete処理
  - データ削除処理の実装
  - データ削除における例外処理の確認 (NotFoundException)


### データベース作成時の内容

| **employeeNumber** | **name** | **age** |      
|:------------------:|:--------:|:-------:|  
|         1          |  スティーブ   |   21    |  
|         2          |   マーク    |   20    |  
|         3          |   ジェフ    |   30    |  

### 実装時間
R5  
12.25　2時間  
12.26　1時間30分  
12.27　1時間


### エラーの共有と解決方法
#### Dockerの構築ができない
Dockerの構築を行う際
```
Creating employee_list ...
Creating employee_list ... error

ERROR: for employee_list  Cannot start service db: driver failed programming external connectivity on endpoint employee_list (85ba989733902f15c2b8ccfa5e4b443212d780d479ec48c292d38b503f930223): Bind for 0.0.0.0:3307 failed: port is already allocated

ERROR: for db  Cannot start service db: driver failed programming external connectivity on endpoint employee_list (85ba989733902f15c2b8ccfa5e4b443212d780d479ec48c292d38b503f930223): Bind for 0.0.0.0:3307 failed: port is already allocated
ERROR: Encountered errors while bringing up the project.
```
というエラーが発生
これは
`:3307 failed: port is already allocated`
という内容から
`ポート3307はすでに使用されています`
という意味でした。
ですのでDockerの今まで作成したContainers・Images・Volumesを削除することにより解決しました。

#### cURLコマンドを入力しても500エラーになる
エラー状態
![ターミナルのキャプション](img/スクリーンショット 2023-12-27 5.01.02.png)

エラーメッセージ
```
### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Table 'employee_list.employee_list' doesn't exist
### The error may exist in com/yy5/employee/mapper/EmployeeMapper.java (best guess)
### The error may involve com.yy5.employee.mapper.EmployeeMapper.findByEmployee-Inline
### The error occurred while setting parameters
### SQL: select * from employee_list WHERE employeeNumber LIKE CONCAT('%', ?, '%')
### Cause: java.sql.SQLSyntaxErrorException: Table 'employee_list.employee_list' doesn't exist
; bad SQL grammar []] with root cause

java.sql.SQLSyntaxErrorException: Table 'employee_list.employee_list' doesn't exist
```

解決方法

`Table 'employee_list.employee_list' doesn't exist`
というエラーメッセージから、
`employee_listデータベースのemployee_listテーブルを検索しようとしているが存在しない。`
ということが読み取れる。実装としてはemployee_listデータベースののemployeesテーブルを読み込んで欲しかったのでMapperを修正することで解決しました。
![Mapper修正のキャプション](img/スクリーンショット 2023-12-27 5.45.04.png)

#### 全件検索を行なっても、１つの結果を返されることが期待されてエラーになる
エラー状態
![ターミナルのキャプション](img/スクリーンショット 2023-12-28 4.46.06.png)

エラーメッセージ
```
org.apache.ibatis.exceptions.TooManyResultsException: Expected one result (or null) to be returned by selectOne(), but found: 3
```

解決方法

`Expected one result (or null) to be returned by selectOne(), but found: 3`
というエラーメッセージから、
`１つの結果を期待しているが３つの結果が見つかった`
ということが読み取れます。
結論から言うと原因はOptionalでレンスポンスをしていることでした。
Optionalは１つのレコードかNullの結果しかレスポンスしません。
よってOptionalからListに変更したところ解決しました。
![Controllerクラスの修正](img/スクリーンショット 2023-12-28 4.50.08.png)
![Mapperクラスの修正](img/スクリーンショット 2023-12-28 4.50.20.png)
![Serviceクラスの修正](img/スクリーンショット 2023-12-28 4.50.31.png)
