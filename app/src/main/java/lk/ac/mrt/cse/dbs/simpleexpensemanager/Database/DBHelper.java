package lk.ac.mrt.cse.dbs.simpleexpensemanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Thilina Ashen Gamage on 11/19/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "140048P.db";

    public static final int DATABASE_VERSION = 1;

    public static final String ACCOUNT_TABLE_NAME = "ACCOUNT";
    public static final String ACCOUNT_COLUMN_accountNo = "accountNumber";
    public static final String ACCOUNT_COLUMN_bank = "bank";
    public static final String ACCOUNT_COLUMN_holder = "holder";
    public static final String ACCOUNT_COLUMN_balance = "balance";

    public static final String LOGS_TABLE_NAME = "LOG";
    public static final String LOGS_COLUMN_accountNo = "logaccountNumber";
    public static final String LOGS_COLUMN_date = "date";
    public static final String LOGS_COLUMN_type = "type";
    public static final String LOGS_COLUMN_amount = "amount";


    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table ACCOUNT " + "(accountNumber text primary key, bank text not null,holder text not null,balance real not null)");
        db.execSQL("create table LOG " + "(logaccountNumber text not null, date text not null,type text not null,amount real not null,primary key(logaccountNumber,date))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ACCOUNT");
        db.execSQL("DROP TABLE IF EXISTS LOG");
        onCreate(db);
    }

    public boolean insertAccount  (Account account )
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNumber", account.getAccountNo());
        contentValues.put("bank", account.getBankName());
        contentValues.put("holder", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        database.insert("ACCOUNT", null, contentValues);
        return true;
    }
    public boolean insertDataLogs  (String accountNumber, String date, String type,double amount )
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("logaccountNumber", accountNumber);
        contentValues.put("date", date);
        contentValues.put("type", type);
        contentValues.put("amount", amount);
        database.insert("LOG", null, contentValues);
        return true;
    }

    public Account getDataAccount(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from  ACCOUNT where accountNumber=" + id + "", null);
        String accountNo=res.getString(res.getColumnIndex(ACCOUNT_COLUMN_accountNo));
        String bank=res.getString(res.getColumnIndex(ACCOUNT_COLUMN_bank));
        String holder=res.getString(res.getColumnIndex(ACCOUNT_COLUMN_holder));
        Double balance=Double.parseDouble(res.getString(res.getColumnIndex(ACCOUNT_COLUMN_balance)));
        Account account=new Account(accountNo,bank,holder, balance);
        return account;
    }

    public Integer deleteAccount (String account)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("ACCOUNT", "accountNumber = ? ", new String[] { account });
    }

    public List<String> getAllAccountnumbers()
    {
        List<String> list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ACCOUNT", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            list.add(res.getString(res.getColumnIndex(ACCOUNT_COLUMN_accountNo)));
            res.moveToNext();
        }
        return list;
    }
    public List<Account> getAllAccounts()
    {
        List<Account> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ACCOUNT", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String accountNo=res.getString(res.getColumnIndex(ACCOUNT_COLUMN_accountNo));
            String bank=res.getString(res.getColumnIndex(ACCOUNT_COLUMN_bank));
            String holder=res.getString(res.getColumnIndex(ACCOUNT_COLUMN_holder));
            Double balance=Double.parseDouble(res.getString(res.getColumnIndex(ACCOUNT_COLUMN_balance)));
            Account account=new Account(accountNo,bank,holder, balance);
            list.add(account);
            res.moveToNext();
        }
        return list;
    }
    public List<Transaction> getAllLogtransactions() throws ParseException {
        List<Transaction> list = new ArrayList<Transaction>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from LOG", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Transaction newtransaction=new Transaction((getDate(res.getString(res.getColumnIndex(LOGS_COLUMN_date)))),res.getString(res.getColumnIndex(LOGS_COLUMN_accountNo)), ExpenseType.valueOf(res.getString(res.getColumnIndex(LOGS_COLUMN_type))),Double.parseDouble(res.getString(res.getColumnIndex(LOGS_COLUMN_amount))));
            list.add(newtransaction);
            res.moveToNext();
        }
        return list;
    }
    public static Date getDate(String date1){
        Date date = null;
        DateFormat fomatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        try {
            date = fomatter.parse(date1);
        } catch (ParseException ex) {

        }
        return date;
    }
}
