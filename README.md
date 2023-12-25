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