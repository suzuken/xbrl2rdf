package util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

/**
 * @see Java 再帰的にファイルを検索 ／ Chat&Messenger
 * http://sattontanabe.blog86.fc2.com/blog-entry-55.html
 */
public class FileSearch {

    public static final int TYPE_FILE_OR_DIR = 1;
    public static final int TYPE_FILE = 2;
    public static final int TYPE_DIR = 3;

    
    /**
     * 指定したディレクトリ[directoryPath]から、
     * 検索対象のファイル[fileName]を再帰的に検索し、該当する
     * ファイルオブジェクトのリストを返します。
     * 
     * 例) 
     * File[] files =listFiles("C:/filelist/", "*.java");
     * 上記の例では、ディレクトリfilelistを再帰的に検索し、
     * 拡張子javaのファイルリストを取得します。
     * 
     * @param directoryPath 検索対象のディレクトリを表すパス
     * @param fileName 検索対象のファイル名 
     *                 ファイル名にはワイルドカード文字として*を指定可能
     * @return 検索にマッチしたファイルオブジェクト
     */
    public File[] listFiles(String directoryPath, String fileName) {
        // ワイルドカード文字として*を正規表現に変換
        if (fileName != null) {
            fileName = fileName.replace(".", "\\.");
            fileName = fileName.replace("*", ".*");
        }
        return listFiles(directoryPath, fileName, TYPE_FILE, true, 0);
    }

    /**
     * 指定したディレクトリ[directoryPath]から、正規表現として指定された
     * 検索対象のファイル[fileNamePattern]を再帰的に検索し、
     * 該当するファイルオブジェクトのリストを返します。
     * 
     * また、ファイルの更新日付が指定日数経過しているかどうかを検索条件に
     * 指定する事ができます。
     * 
     * 例) 
     * File[] files = 
     *         listFiles("C:/filelist/", "*.java",TYPE_FILE, true, 2);
     * 上記の例では、ディレクトリfilelistを再帰的に検索し、7日前以降に更新
     * された拡張子javaのファイルリストを取得します。
     * 
     * @param directoryPath 検索対象のディレクトリを表すパス
     * @param fileNamePattern 検索対象のファイル名[正規表現]
     * @param type 該当するファイルオブジェクトは[type]により、
     *                以下の指定が可能
     *                TYPE_FILE_OR_DIR・・・ファイル及びディレクトリ 
     *                TYPE_FILE・・・ファイル
     *                TYPE_DIR・・・ディレクトリ
     * @param isRecursive 再帰的に検索する場合はtrue
     * @param period 検索対象として、ファイルの更新日付が指定日数経過
     *                しているかどうかを設定可能
     *                0の場合は対象外
     *                1以上の場合、指定日数以降のファイルを検索対象とする
     *                -1以下の場合、指定日数以前のファイルを検索対象とする
     * @return 検索にマッチしたファイルオブジェクト
     */
    public File[] listFiles(String directoryPath, 
            String fileNamePattern, int type, 
            boolean isRecursive, int period) {
        
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException
            ("引数で指定されたパス[" + dir.getAbsolutePath() + 
                    "]はディレクトリではありません。");
        }
        File[] files = dir.listFiles();
        // その出力
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            addFile(type, fileNamePattern, set, file, period);
            // 再帰的に検索＆ディレクトリならば再帰的にリストに追加
            if (isRecursive && file.isDirectory()) {
                listFiles(file.getAbsolutePath(), fileNamePattern, 
                            type, isRecursive, period);
            }
        }
        return (File[]) set.toArray(new File[set.size()]);
    }

    private void addFile(int type, String match, TreeSet set,
            File file,int period) {
        switch (type) {
        case TYPE_FILE:
            if (!file.isFile()) {
                return;
            }
            break;
        case TYPE_DIR:
            if (!file.isDirectory()) {
                return;
            }
            break;
        }
        if (match != null && !file.getName().matches(match)) {
            return;
        }
        // 指定日数経過しているかどうかの指定がある場合
        if (period != 0) {
            // ファイル更新日付
            Date lastModifiedDate = new Date(file.lastModified());
            String lastModifiedDateStr = new SimpleDateFormat("yyyyMMdd")
                    .format(lastModifiedDate);

            // 指定の日付（１日をミリ秒で計算）
            long oneDayTime = 24L * 60L * 60L * 1000L; 
            long periodTime = oneDayTime * Math.abs(period);
            Date designatedDate = 
                new Date(System.currentTimeMillis() - periodTime);
            String designatedDateStr = new SimpleDateFormat("yyyyMMdd")
                    .format(designatedDate);
            if (period > 0) {
                if (lastModifiedDateStr.compareTo(designatedDateStr) < 0) {
                    return;
                }
            } else {
                if (lastModifiedDateStr.compareTo(designatedDateStr) > 0) {
                    return;
                }
            }
        }
        // 全ての条件に該当する場合リストに格納
        set.add(file);

    }

    /** アルファベット順に並べるためTreeSetを使用。 */
    private TreeSet set = new TreeSet();

    /**
     * インスタンスを生成後、続けて使用する場合は、このメソッドを
     * 呼び出しクリアする必要がある。
     * 例)
     *  FileSearch search = new FileSearch();
     *  File[] f1 = search.listFiles(C:/filelist/", "*.java");
     *  search.clear();
     *  File[] f2 = search.listFiles("C:/filelist/", "*.jsp"); 
     */
    public void clear(){
    	set.clear();
    }
}
