# CRUD処理を備えたRestAPIの作成

### 概要

職員番号を主キーとし、氏名と年齢をカラムにもつSQLを作成しています。

### CRUD処理の実装  

#### Read処理
  - employeeNumber検索(パスパラメータ検索)の実装
  - 全件検索及び部分一致検索(クエリパラメータ)の実装
  - エラーハンドリングの実装  

社員情報全件取得のcURLコマンド
  ```
  curl http://localhost:8080/employees
  ```
指定した社員情報取得のcURLコマンド
  ```
  curl http://localhost:8080/employees/{社員番号}
  ```
---
#### Create処理
  - 新規登録機能の実装
  - レスポンスボディの設定
    - ステータスコード201でレスポンス
    - 新規登録時に「employee created」のメッセージを表示させる
  - エラーハンドリング
    - ageカラムが整数意外だった場合「整数を入力してください」というエラーメッセージを表示させる
    - Nameにバリデーションを追加する

社員情報作成のcURLコマンド
  ```
curl -X POST -H "Content-Type: application/json" -d '{"name": "{社員名}}", "age": {社員年齢}}' http://localhost:8080/employees
  ```
----
#### Update処理
  - nameを更新することができる
  - ageを更新することができる  

社員情報更新のcURLコマンド
  ```
curl -X PATCH -H "Content-Type: application/json" -d '{"name": "{更新する社員名前}", "age": {更新する社員年齢}}' http://localhost:8080/employees/{更新したい社員の番号}
  ```
----
#### Delete処理
  - 社員番号を指定し、存在する場合は削除することができる

社員情報削除のcURLコマンド
  ```
curl -X DELETE http://localhost:8080/employees/{削除したい社員の番号} 
  ```
----

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
12.28　2時間  
12.31　1時間

R6  
1.3　2時間  
1.4　2時間  
1.5　2時間  
1.8　1時間30分  
1.9　2時間  
1.11　1時間30分  
1.12　2時間  
1.14　2時間  
1.15　1時間  
1.16　2時間  
1.17　2時間  
1.18　2時間  
1.19　2時間  
1.22　1時間  
1.23　2時間  
1.24　1時間  
1.30　1時間  
2.1　1時間  
2.4　2時間  
2.7　2時間  
2.9　1時間  
2.13　1時間  
2.14　2時間  
2.15　1時間  
2.16　1時間  
2.18　2時間  
2.19　1時間  
2.20　1時間  
2.26　2時間  
2.27　2時間  
3.4　2時間  
3.5　2時間  
3.6　2時間  
3.7　1時間  
3.8　1時間  
3.10　2時間  
3.11　2時間  
3.12　2時間  
3.13　3時間  
3.14　2時間30分  
3.15　2時間30分  
3.16　30分  
3.17　30分  
3.18　5時間  
3.19　2時間  

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
### The error may involve findById-Inline
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

#### 新規登録ができない
エラー状態
![ターミナルのキャプション](img/スクリーンショット 2024-01-05 4.58.05.png)
![エラーメッセージ](img/スクリーンショット 2024-01-05 4.58.31.png)

エラーメッセージ
```
java.lang.NullPointerException: Cannot invoke "java.lang.Integer.intValue()" because "employeeNumber" is null
```

解決方法

`NullPointerException: Cannot invoke "java.lang.Integer.intValue()" because "employeeNumber" is null`
というエラーメッセージから、
`employeeNumberがnullのため実行できない`
ということが読み取れます。
結論から言うと原因はint型でemployeeNumberを定義していることでした。
新規登録を行う場合、Serviceクラスで一旦空のemployeeNumberを作成しておき、Mapperに渡った時insertメソッドで自動採番されます。
ですのでemployeeNumberはnullを許容できる必要がありますがint型の場合はnullを許容できません。
よってInteger型に変更することで、エラーを解消しました。
![entityクラスの修正](img/スクリーンショット 2024-01-05 5.36.55.png)



#### 社員登録処理のエラーメッセージが表示されない
エラー状態
![ターミナルのキャプション](img/スクリーンショット 2024-01-12 5.22.09.png)

エラー内容  
社員情報を登録する方法としてageにint型を設定していますが、空や整数以外の内容が入力された場合
「無効な年齢です」というメッセージを表示させようとしていますが、エラーメッセージが表示されません。

解決方法
ageカラムにバリデーションを設ける。
