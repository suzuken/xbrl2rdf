xbrlをrdfに変換するためのライブラリです。

構成
---

eclipseのプロジェクトになっています。

|+bin/
実行バイナリです。

|+data/
EDINETのタクノソミデータです。

|+DB/
TDBに格納されたxbrlデータのサンプルです。

|+doc/
APIドキュメントです。javadocが入っています。

|+lib/
各種外部ライブラリです。

|+output/
RDF出力用フォルダです。サンプルのrdfが入っています。

|+src/
プログラムのソースファイルです。

|+tdb/
TDBのソースコードが入っています。

|+tests/
テストフォルダです。

|+uml/
クラス図が載っています。

使い方
----

 java bin/xbrlparse/XbrlProcessor XBRLPATH

動作フロー
-----

* まずxbrlファイルをrdfに変換します。xbrlのパースを行い、DTSのパースも行います。
* 生成したrdfファイルを元に、TDBに格納します。


ロードマップ
------

* テストケースの整備
* コマンドラインオプションの整備
* TDBをSPARQLエンドポイントとして利用
