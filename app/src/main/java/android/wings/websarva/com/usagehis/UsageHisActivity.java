package android.wings.websarva.com.usagehis;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.webserva.wings.android.usagehis.R;

import java.util.ArrayList;
import java.util.HashMap;

public class UsageHisActivity extends AppCompatActivity {

    // newNoodleの1-30が追加されたかどうかのフラグ true:追加された false:まだ追加されてない
    boolean newNoodleAddFlag = false;

    private static final String SELECTATTRIBUTE="";
    private static final String SELECTTABLE="";

//    String purposeText = "";
//    String monthText = "";
//    String companyText = "";
//    String nameText=  "";

    /**
     * SQL文用
     *
     */
    private static final String SQLPURPOSE = "目的";
    private static final String SQLDATE = "日付";
    private static final String SQLCOMPANY = "会社名";
    private static final String SQLNAME = "予約者名";
    private static final String SQLSTRBASE = "SELECT 予約ID 日付 開始時刻 終了時刻 会社名 目的 予約者名" +
            "FROM " +
            "WHERE " + SQLPURPOSE + " LIKE \'%";
    private static final String SQLSTRCON1 = "%\' AND " + SQLDATE + "LIKE \'%";
    private static final String SQLSTRCON2 = "%\' AND " + SQLCOMPANY + "LIKE \'%";
    private static final String SQLSTRCON3 = "%\' AND " + SQLNAME + " LIKE \'%";
    private static final String SQLSTRCONEND = "%\'";

    /**
     *
     * // 0 = date 日付
     * // 1 = purpose 目的
     * // 2 = time 時刻
     * // 3 = cname 会社名
     * // 4 = name 予約者名
     */
//    private static final String[] listAtrribute = {"date","purpose","time","cname","name"};
    /**
     * 検索ボックス,スピナーの内容保持用
     * // 0 = 目的スピナー
     * // 1 = 月スピナー
     * // 2 = 企業名検索ボックス
     * // 3 = 予約者名検索ボックス
     */
    String searchSQLVal[] = new String[4];

    /**
     * rsvtHistorySearch メソッドの識別用定数
     * // ST_PURPOSE = 目的スピナー
     * // ST_MONTH   = 月スピナー
     * // ST_COMPANY = 企業名検索ボックス
     * // ST_NAME    = 予約者名検索ボックス
     */
    private static final int ST_PURPOSE = 0;
    private static final int ST_MONTH   = 1;
    private static final int ST_COMPANY = 3;
    private static final int ST_NAME    = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_his);

        // 仮データ部
        // ListViewに表示する項目を生成
        ArrayList<HashMap<String, String>> listData = new ArrayList<>();

        String[] da = {"2018年8月30日 木曜日", "2018年9月1日 土曜日", "2018年9月3日 月曜日", "2018年9月10日 月曜日"};

        String[] purpose = {"打ち合わせ", "打ち合わせ", "打ち合わせ", "打ち合わせ"};

        String[] time = {"9:00~10:00","9:00~10:00","10:00~11:00","10:15~11:15"};

        String[] companyName = {"株式会社API","株式会社APEX","株式会社TWW","日本製造株式会社"};

        String[] name = {"予約者:吉松 正博","予約者:佐藤太郎","予約者:草加雅人","予約者:檀隼人"};


        for (int i = 0; i < 4; i++) {
            HashMap<String, String> data = new HashMap<>();
            data.put("date", da[i]);            //日付
            data.put("plans", purpose[i]);        //目的
            data.put("time", time[i]);          //時間
            data.put("cname", companyName[i]);          //会社名
            data.put("name",name[i]);
            listData.add(data);
        }

        /**
         * Adapterを生成
         *リストビュー自身のレイアウト。今回は自作。
         *受け渡し元項目名
         *受け渡し先ID
         */
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                listData, // 使用するデータ
                R.layout.custom_list_layout, // 自作したレイアウト
                new String[]{"date", "plans", "time","cname","name"}, // どの項目を
                new int[]{R.id.usageHis_cl_tv_date, R.id.usageHis_cl_tv_purpose, R.id.usageHis_cl_tv_time, R.id.usageHis_cl_tv_companyname,R.id.usageHis_cl_tv_outername} // どのidの項目に入れるか
        );
        // idがusageHis_list_vi_historyのListViewを取得
        ListView listView = (ListView) findViewById(R.id.usageHis_list_vi_history);
        listView.setAdapter(simpleAdapter);

        // 仮データ部ここまで


        /**
         * SQL文メモ
         * SELECT 予約ID 日付 開始時刻 終了時刻 会社名 目的 予約者名
         * FROM
         * WHERE 目的 = "%[PURPOSE]%" AND 日付 = '%[MONTH]%' AND 会社名 = '%[COMPANYNAME]%' AND 予約者名 = '%[NAME]%'
         */

        // リスナー群

        Spinner purposeSpinar = findViewById(R.id.usageHis_spinar_purpose);
        Spinner monthSpinar = findViewById(R.id.usageHis_spinar_month);
        EditText companySachvi = findViewById(R.id.usageHis_sach_vi_company);
        EditText nameSachvi = findViewById(R.id.usageHis_sach_vi_name);

        // 目的スピナー
        purposeSpinar.setOnItemClickListener((parent,view,position,id) ->{
            String w = "";
            // スピナーの選択された項目を取得してwに代入、一番上の項目が選択されてた場合は空文字列
            if(position!=0){
                w = (String)parent.getItemAtPosition(position);
            }
            // 検索メソッドを呼び出す
            rsvtHistorySearch(ST_PURPOSE,w);
        });

        // 月スピナー
        monthSpinar.setOnItemClickListener((parent,View,position,id )->{
            String w = "";
             // スピナーの選択された項目を取得してwに代入、一番上の項目が選択されてた場合は空文字列
            if(position!=0){
                w = (String)parent.getItemAtPosition(position);
            }
            // 検索メソッドを呼び出す
            rsvtHistorySearch(ST_MONTH,w);
        });
    }

    /**
     * 検索を行うメソッド
     * @param sachType
     * @param sachString
     */
    private void rsvtHistorySearch(int sachType, String sachString){
        searchSQLVal[sachType] = sachString;

        // データベース接続
        Dbhelper helper = new Dbhelper(UsageHisActivity.this);
        try(SQLiteDatabase db =helper.getWritableDatabase()){
            // SQL文の編集
            String strSQL = SQLSTRBASE +
                    searchSQLVal[0] +
                    SQLSTRCON1 + searchSQLVal[1] +
                    SQLSTRCON2 + searchSQLVal[2] +
                    SQLSTRCON3 + searchSQLVal[3] +
                    SQLSTRCONEND;
            // SQL文の実行
            Cursor cursor = db.rawQuery(strSQL,null);
            // SQL
            String note = "";
            HashMap<String,String> data = new HashMap<>();
            while(cursor.moveToNext()){
                int idxDay = cursor.getColumnIndex(SQLPURPOSE);
                data.put("date", cursor.getString(0));            //日付

            }
        }
        // データベース解放
        helper.close();
    }

    private void listInputer(){

    }

}