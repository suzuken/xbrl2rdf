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

Xbrlデータの標準出力

    java bin/xbrlreader/XbrlReader XBRLPATH

RDFの生成

    java bin/xbrlonto/RDFMaker XBRLPATH

動作フロー
-----

* まずxbrlファイルをrdfに変換します。xbrlのパースを行い、DTSのパースも行います。
** デフォルトではxbrlファイルと同名のrdfファイルがoutputディレクトリに生成されます。
* 生成したrdfファイルを元に、TDBに格納します。


TODO
------

* XbrlReaderのテストケースの整備
* RDFMakerをXbrlReaderに対応させる
* コマンドラインオプションの整備
* TDBをSPARQLエンドポイントとして利用
