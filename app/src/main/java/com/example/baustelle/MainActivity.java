package com.example.baustelle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baustelle.data.DBHandler;
import com.example.baustelle.data.model.TodoCursorAdapter;
import com.example.baustelle.databinding.ActivityMainBinding;
import com.example.baustelle.ui.login.LoginActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    DBHandler db;
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private ArrayAdapter<String> mitarbeiter;
    private TodoCursorAdapter todoAdapter;
    private Cursor cursor;
    private long downloadID;
    private Cursor zeit;
    private final Handler progessBarHandler = new Handler();
    private ActivityMainBinding bindingMain;
    ExecutorService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// standard call       setContentView(R.layout.activity_main);

        // call with binding do prevent call "findViewById"
        bindingMain = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindingMain.getRoot());

 /*
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
 */
        // is necessary to get file form url
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
            StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sp = getSharedPreferences("com.example.baustelle",MODE_PRIVATE);
        db = new DBHandler(this);

        service = Executors.newSingleThreadExecutor();
        // disable progressbar
        bindingMain.progressBar.setVisibility(View.GONE);

        cursor = db.fetchMit();
        listView = findViewById(R.id.listView);
        todoAdapter = new TodoCursorAdapter(this, cursor);
        listView.setAdapter(todoAdapter);

//        listView.setEmptyView(findViewById(R.id.listView));
//        final String[] from = new String[] { "id","name", "description" };
//        final int[] to = new int[] { R.id.id, R.id.title, R.id.desc };
//        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_record, cursor, from, to, 0);
//        adapter.notifyDataSetChanged();

        Intent login = new Intent(this, LoginActivity.class);
//        startActivityForResult(login,200);

        String[] items = new String[]{"Bernhard", "Max", "Susi", "Perter"};
        mitarbeiter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,items);
        bindingMain.spMitarbeiter.setAdapter(mitarbeiter);

        Button btn = findViewById(R.id.StartWebView);
//        btn.requestFocus();
//        closeKeyBoard();

        // register receiver to check if download is completed then move file to ......
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

//        progressTest();
        fillZeiterfassung();

    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                try {
                    moveFile("/Question1.mp3");
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "ERROR move File " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the custom overflow menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor = db.fetchMit();
        listView = findViewById(R.id.listView);
        todoAdapter = new TodoCursorAdapter(this, cursor);
        listView.setAdapter(todoAdapter);
        todoAdapter.changeCursor(cursor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
            String test = sp.getString("username", "");
        }

    }

    public  void onNewBaustelleClick(View v) {
        bindingMain.baustelleID.setText("NEW");
        fillZeiterfassung();
    }

    public  void onEditBaustelleClick(View v){
        fillZeiterfassung();
    }

    public void onBaustelleAnlegenClick(View v){
        Intent baustelle = new Intent(this, BaustelleErfassung.class);
        startActivity(baustelle);
    }

    public void onWerteSaveClick(View v){
//        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put("Baustelle", bindingMain.baustelle.getText().toString());
        values.put("Mitarbeiter", bindingMain.spMitarbeiter.getSelectedItem().toString());
        values.put("ZeitSoll", bindingMain.zeitSoll.getText().toString());
        values.put("ZeitIst", bindingMain.zeitIst.getText().toString());

        // after adding all values we are passing
        // content values to our table.
        String id = bindingMain.baustelleID.getText().toString();
        if ( zeit != null ) id = zeit.getString(zeit.getColumnIndexOrThrow("_ID"));
        db.updateZeit(id, values);

        fillZeiterfassung();
    }

    public void onWebViewClick(View v){
        Intent webview = new Intent(this, WebViewActivity.class);
        startActivity(webview);
    }

    public void dbDownloadClick1(View v) {
        try {
            int downloadedSize = 0;
            URL url = new URL("http://192.168.1.191:80/test.txt");
//            URL url = new URL("https://upload.wikimedia.org/wikipedia/commons/e/e7/Java_Programming.pdf");
//            URL url = new URL("http://www.tutorialspoint.com/");
/*
            InputStream inputStream = url.openStream();
            Scanner s = new Scanner(inputStream);
            while(s.hasNext()) {
                System.out.println(s.nextLine());
            }
*/
            InputStream inputStream = url.openStream();

            int totalSize = 100000;
            bindingMain.progressBar.setMax(totalSize / 1000);
            bindingMain.progressBar.setProgress(0);

            FileOutputStream fileOutputStream = new FileOutputStream(getFilesDir() + "/mts1.mp3");
            int length;
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) > -1) {
                fileOutputStream.write(buffer, 0, length);
            }

//            bindingMain.simpleProgressBar.setMax(100);
//            bindingMain.simpleProgressBar.setProgress(70);
            fileOutputStream.close();
            inputStream.close();

            System.out.println("Download Baustelle.db" + "---- >Completed to dir " + getFilesDir());
            // this line generate Exception while this task run in Thread
            // Toast.makeText(MainActivity.this, "File saved successfully in!" + getFilesDir(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.d("IO", e.getMessage());
            System.out.println("LESE  DATEN EIN ...." + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void dbDownloadClick(View v) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindingMain.progressBar.setVisibility(View.VISIBLE);
                    }
                });
                dbDownloadClick1(v);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindingMain.progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });


        String DownloadUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e7/Java_Programming.pdf";
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request1.setDescription("Sample Music File");   //appears the same in Notification bar while downloading
        request1.setTitle("File1.mp3");
        request1.setVisibleInDownloadsUi(true);
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        request1.setDestinationInExternalFilesDir(

                getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, "Question1.mp3");
        try {

            DownloadManager manager1 = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadID = Objects.requireNonNull(manager1).enqueue(request1);

            // Donload
            if (DownloadManager.STATUS_SUCCESSFUL == 8) {
                System.out.println("LESE  DATEN EIN ...." + "Download completed in: " + getApplicationContext().toString());
                Toast.makeText(getBaseContext(), "File saved successfully in!" + getApplicationContext().toString(), Toast.LENGTH_LONG).show();
            }

            /*    Show intend Downloads
                Intent i = new Intent();
                i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(i);
 */

        } catch (Exception e) {
            Log.d("IO", e.getMessage());
            System.out.println("LESE  DATEN EIN ...." + e.getMessage());
        }
    }

    public void moveFile(String filename) throws RuntimeException, IOException {
        File from = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + filename);
        String tofiles = getFilesDir().getAbsolutePath() + "/";

        // use with API level 28 and higher
        // File to = new File(tofiles + "new1");
        // Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // this is to transfer files between different filesystems
        File newFile = new File(tofiles, filename);
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(from).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            from.delete();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }

 /*  Rename Files in same filesystem
        try {
            if(from.renameTo(to)) {
                System.out.println("from :" + from + " nach: " + to.toString());
            }
            else {
                System.out.println("ERROR --> : from :" + from + " nach: " + to.toString());
            }
        }
        catch (RuntimeException e) {
            System.out.println("ERROR --> : from :" + from + " nach: " + to.toString());
        }
 */

    }

    public  void fillZeiterfassung(){
        String x = bindingMain.baustelleID.getText().toString();

        zeit = db.fetchZeit(bindingMain.baustelleID.getText().toString());
        if (zeit != null) {
            zeit.moveToLast();

            bindingMain.baustelleID.setText(zeit.getString(zeit.getColumnIndexOrThrow("_ID")));
            bindingMain.baustelle.setText(zeit.getString(zeit.getColumnIndexOrThrow("Baustelle")));
            bindingMain.spMitarbeiter.setSelection(mitarbeiter.getPosition(zeit.getString(zeit.getColumnIndexOrThrow("Mitarbeiter"))));
            bindingMain.zeitSoll.setText(zeit.getString(zeit.getColumnIndexOrThrow("ZeitSOLL")));
            bindingMain.zeitIst.setText(zeit.getString(zeit.getColumnIndexOrThrow("ZeitIST")));
        }
        else {
            bindingMain.baustelleID.setText("NEW");
            bindingMain.zeitSoll.setText("8");
        }
    }

    public void progressTest() {
        new Thread(new Runnable() {
            public void run() {
                int progressStatus = 0;
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setMax(100);

                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    int finalProgressStatus = progressStatus;
                    progessBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(finalProgressStatus);
//                            textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}