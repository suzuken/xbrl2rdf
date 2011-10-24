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

    java bin/xbrlreader/XbrlReader input/*.xbrl

RDFの生成

    java bin/xbrlonto/RDFMaker input/*.xbrl

    java bin/xbrlonto/RDFMaker -o ./output input/*.xbrl

またはeclipseで

    ${project_loc}/input/EDINET/2011/2 .*.xbrl

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

* hasStartDateとhasEndDateをちゃんと同じ枠に入れるようにする。以下のようなミスが
  ある。本来は１つの期間の中にstartdateとenddateを入れるようにしなければならない
  。

  <rdf:Description rdf:about="http://info.edinet-fsa.go.jp/jp/fr/gaap/t/cte/2009-03-09#DisposalOfTreasuryStockTS">
    <j.0:hasAmount rdf:nodeID="A3"/>
    <j.0:hasAmount rdf:nodeID="A4"/>
    <j.0:hasStartDate rdf:datatype="http://www.w3.org/2001/XMLSchema#date">2007-04-01</j.0:hasStartDate>
    <j.0:hasStartDate rdf:datatype="http://www.w3.org/2001/XMLSchema#date">2008-04-01</j.0:hasStartDate>
    <j.0:hasEndDate rdf:datatype="http://www.w3.org/2001/XMLSchema#date">2008-03-31</j.0:hasEndDate>
    <j.0:hasEndDate rdf:datatype="http://www.w3.org/2001/XMLSchema#date">2009-03-31</j.0:hasEndDate>
  </rdf:Description>

* contextについては同じ指定を書き過ぎていてrdfが巨大になってしまっているので、内
  容を簡単に参照できるようにする。具体的には、コンテキストをrdfのグラフの中に埋
  め込んで、おなじものを参照させるようにする。
