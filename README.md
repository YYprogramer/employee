# CRUD処理を備えたRestAPIの作成

### 概要

職員番号をIDとし、氏名と年齢をカラムにもつSQLを作成しています。

### CRUD処理の実装

- Read処理
  - 全件検索及び部分一致検索(クエリパラメータ)の実装
  - id検索(パスパラメータ検索)の実装
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


#### データベース作成時の内容

| **employee number** | **name** | **age** |      
|:-------------------:|:--------:|:-------:|  
|          1          |  スティーブ   |   21    |  
|          2          |   マーク    |   20    |  
|          3          |   ジェフ    |   30    |  
