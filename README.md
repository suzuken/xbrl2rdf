xbrlをrdfに変換するためのライブラリです。

構成
---

eclipseのプロジェクトになっています。

* bin/ 実行バイナリです。
* data/ EDINETのタクノソミデータです。
* DB/ TDBに格納されたxbrlデータのサンプルです。
* doc/ APIドキュメントです。javadocが入っています。
* lib/ 各種外部ライブラリです。
* output/ RDF出力用フォルダです。サンプルのrdfが入っています。
* src/ プログラムのソースファイルです。
* tdb/ TDBのソースコードが入っています。
* tests/ テストフォルダです。
* uml/ クラス図が載っています。

使い方
----

Xbrlデータの標準出力

    java bin/xbrlreader/XbrlReader XBRLPATH

RDFの生成

    java bin/xbrlonto/RDFMaker XBRLPATH

app.confで各種設定をできます。

    # 名前空間の設定
    nsClass=http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/class#
    nsInstance=http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/instance#
    nsProperty=http://www.yamaguti.comp.ae.keio.ac.jp/xbrl_ontology/property#
    nsFoaf=http://xmlns.com/foaf/0.1/

    # Context Settings
    enableContextRef = Prior2YearNonConsolidatedDuration, Prior2YearNonConsolidatedInstant, \
    Prior2YearConsolidatedDuration, Prior2YearConsolidatedInstant, \
    Prior1YearNonConsolidatedDuration, Prior1YearNonConsolidatedInstant, \
    Prior1YearConsolidatedDuration, Prior1YearConsolidatedInstant, \
    CurrentYearNonConsolidatedDuration, CurrentYearNonConsolidatedInstant, \
    CurrentYearConsolidatedDuration, CurrentYearConsolidatedInstant

    # TDB setup
    tdbFactoryLoc = ./output/TDB/default

    # default rdf path for output
    # outputRDFPath = ./output/test2.rdf
    outputRDFDir = ./output

    # Database setting
    # for default, xbrl2rdf using TDB. When you use RDS to store RDF,
    # please confirm this setting.
    jdbcUrl = jdbc:mysql://localhost/mydb
    DBUser = user
    DBPassword = password
    DBType = MySQL

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
* RDFMakerで再帰的にrdfを生成できるようにする
