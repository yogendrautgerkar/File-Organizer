package com.example.vergil.fileorganiser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private File root,myDirectory,sourceFile,destinationFile;
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<File> imageList = new ArrayList<File>();
    private ArrayList<File> musicList = new ArrayList<File>();
    private ArrayList<File> videoList = new ArrayList<File>();
    private ArrayList<File> docList = new ArrayList<File>();
    private ArrayList<String> foldername=new ArrayList<String>();
    private int imagecount = 0,musiccount=0,docscount=0,videoscount=0;;
    private CheckBox image,music,video,doc;
    private Button scbtn;
    private Toast t;
    private String[] title = {
            "Images",
            "Music",
            "Videos",
            "Documents"
    };
    private static final int PERMS_REQUEST_CODE = 123;
    ProgressDialog progressDoalog;
    Handler handle;
    Boolean b=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle(" File Oragniser");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        /*******************checking shared preferences*****************************/
        image = (CheckBox)findViewById(R.id.checkimage);
        image.setChecked(getFromSP("image"));
        image.setOnCheckedChangeListener(this);
        music = (CheckBox)findViewById(R.id.checkmp3);
        music.setChecked(getFromSP("music"));
        music.setOnCheckedChangeListener(this);
        video = (CheckBox)findViewById(R.id.checkvideo);
        video.setChecked(getFromSP("video"));
        video.setOnCheckedChangeListener(this);
        doc = (CheckBox)findViewById(R.id.checkdoc);
        doc.setChecked(getFromSP("doc"));
        doc.setOnCheckedChangeListener(this);
        /* End shared preferences*/

        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setTitle("Moving");

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        /*Check if folder image,music,video & Document folder are created*/
        if(hasPermissions())
        {
            checkfolder();
        }
        else
        {
            requestPerms();
        }
        /*end Check if folder image,music,video & Document folder are created*/

        scbtn=(Button)findViewById(R.id.scanbtn);

        /*******************On button click **********************************/
        scbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(scbtn.getText().equals("Move")) {
                    /*check if checkbox are not selected*/
                    checkboxcheck();
                    if (b==true)
                    {
                        b=false;
                    }
                    else {
                    /*end check if checkbox are not selected*/

                        imagecount = 0;
                        musiccount = 0;
                        docscount = 0;
                        videoscount = 0;
                        fileList.clear();
                        imageList.clear();
                        musicList.clear();
                        videoList.clear();
                        docList.clear();
                        getfile(root);
                        movecheck();
                        move();
                    }
                }
            }
        });
        /*******************end On button click **********************************/
    }

    /*Scan the whole internal memory for files*/
    public ArrayList<File> getfile(File dir) {

        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory())
                {
                    if(listFile[i].getParent().equals(root+"/Android")
                            ||listFile[i].getParent().equals(root+"/Documents")
                            ||listFile[i].getParent().equals(root+"/Images")
                            ||listFile[i].getParent().equals(root+"/Music")
                            ||listFile[i].getParent().equals(root+"/Videos"))
                    {}
                    else {
                        //System.out.println(listFile[i].getParent());

                        getfile(listFile[i]);
                    }
                }
                else
                {
                    if(image.isChecked())
                    {
                        //System.out.println("Image checked");

                        if (listFile[i].getName().endsWith(".png")
                                || listFile[i].getName().endsWith(".jpg")
                                || listFile[i].getName().endsWith(".jpeg")
                                || listFile[i].getName().endsWith(".gif")
                                || listFile[i].getName().endsWith(".bmp"))

                        {
                            fileList.add(listFile[i]);
                            imageList.add(listFile[i]);
                            imagecount++;
                        }
                    }
                    else
                    {
                        //System.out.println("Image notchecked");
                    }
                    if(music.isChecked())
                    {
                        //System.out.println("Music checked");
                        if (listFile[i].getName().endsWith(".wav")
                                || listFile[i].getName().endsWith(".mp3"))
                        {
                            fileList.add(listFile[i]);
                            musicList.add(listFile[i]);
                            musiccount++;
                        }
                    }
                    else
                    {
                        //System.out.println("Music not checked");
                    }
                    if(video.isChecked())
                    {
                        //System.out.println("Video checked");
                        if (listFile[i].getName().endsWith(".mp4")
                                || listFile[i].getName().endsWith(".avi")
                                || listFile[i].getName().endsWith(".mkv")
                                || listFile[i].getName().endsWith(".wmv")
                                || listFile[i].getName().endsWith(".flv")
                                || listFile[i].getName().endsWith(".mpeg")
                                || listFile[i].getName().endsWith(".MP4"))

                        {
                            fileList.add(listFile[i]);
                            videoList.add(listFile[i]);
                            videoscount++;
                        }

                    }
                    else
                    {
                        //System.out.println("Video not checked");
                    }
                    if(doc.isChecked())
                    {
                        //System.out.println("Doc checked");
                        if (listFile[i].getName().endsWith(".pdf")
                                || listFile[i].getName().endsWith(".doc")
                                || listFile[i].getName().endsWith(".docx")
                                || listFile[i].getName().endsWith(".ppt")
                                || listFile[i].getName().endsWith(".pptx")
                                || listFile[i].getName().endsWith(".xlsx")
                                || listFile[i].getName().endsWith(".xls")
                                || listFile[i].getName().endsWith(".txt"))

                        {
                            fileList.add(listFile[i]);
                            docList.add(listFile[i]);
                            docscount++;
                        }
                    }
                    else
                    {
                        //System.out.println("Doc not checked");
                    }
                }
            }
        }
        return fileList;
    }
    /*end Scan the whole internal memory for files*/

    /*Get the checkbox save value from shared preferences*/
    private boolean getFromSP(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PROJECT_NAME", android.content.Context.MODE_PRIVATE);
        System.out.println(preferences);
        return preferences.getBoolean(key, false);
    }
    /*end Get the checkbox save value from shared preferences*/

    /*save checkbox value in shared preferences*/
    private void saveInSp(String key,boolean value){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PROJECT_NAME", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    /*end save checkbox value in shared preferences*/

    /*checks if checkbox value changed if changed save to shared preferences*/
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        switch(buttonView.getId()){
            case R.id.checkimage:
                saveInSp("image",isChecked);
                break;
            case R.id.checkmp3:
                saveInSp("music",isChecked);
                break;

            case R.id.checkvideo:
                saveInSp("video",isChecked);
                break;

            case R.id.checkdoc:
                saveInSp("doc",isChecked);
                break;
        }
    }
    /* end checks if checkbox value changed if changed save to shared preferences*/

    /*Check if folder image,music,video & Document folder are created*/
    public void checkfolder()
    {
        System.out.println("Checking");
        for(int j=0;j<4;j++) {
            myDirectory = new File(root, title[j]);
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            else
            {
                System.out.println("Exist");
            }
        }
    }
    /*end Check if folder image,music,video & Document folder are created*/

    /*check if checkbox are not selected*/
    public void checkboxcheck()
    {
        if (!image.isChecked()&& !music.isChecked()&& !video.isChecked()&& !doc.isChecked())
        {
            b=true;
            Toast.makeText(this, "error",
                    Toast.LENGTH_LONG).show();
        }
    }
    /*end check if checkbox are not selected*/

    /*check and create folder before moving*/
    public void movecheck()
    {
        if(image.isChecked())
        {
            foldername.clear();
            for(int j=0;j<imageList.size();j++) {
                String s = imageList.get(j).toString();
                String[] parts = s.split("0/");
                String[] parts1 = parts[1].split("/");
                foldername.add(j, parts1[0]);
            }
            HashSet hs = new HashSet();
            hs.addAll(foldername);
            foldername.clear();
            foldername.addAll(hs);
            for (int i=0;i<foldername.size();i++)
            {
                System.out.println("Folder:"+foldername.get(i));
            }
            for(int j=0;j<foldername.size();j++) {
                myDirectory = new File(root+"/Images", foldername.get(j));
                if (!myDirectory.exists()) {
                    myDirectory.mkdirs();
                }
                else
                {
                    System.out.println("Exist");
                }
            }
        }
        else
        {
            System.out.println("not check");
        }

        if (video.isChecked()) {
            foldername.clear();
            for (int j = 0; j < videoList.size(); j++) {
                String s = videoList.get(j).toString();
                String[] parts = s.split("0/");
                String[] parts1 = parts[1].split("/");
                foldername.add(j, parts1[0]);
            }
            HashSet hs = new HashSet();
            hs.addAll(foldername);
            foldername.clear();
            foldername.addAll(hs);
            for (int i = 0; i < foldername.size(); i++) {
                System.out.println("Folder:" + foldername.get(i));
            }
            for (int j = 0; j < foldername.size(); j++) {
                myDirectory = new File(root + "/Videos", foldername.get(j));
                if (!myDirectory.exists()) {
                    myDirectory.mkdirs();
                } else {
                    System.out.println("Exist");
                }
            }
        }
    }
    /*end check and create folder before moving*/

    /*moving files*/
    public void move()
    {
        int i;

        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDoalog.setMessage("moving files...");
        progressDoalog.show();

        if(doc.isChecked())
        {

            for(i=0;i<docList.size();i++) {
                try {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (progressDoalog.getProgress() <= progressDoalog.getMax()) {
                                    Thread.sleep(1500);
                                    handle.sendMessage(handle.obtainMessage());
                                    if (progressDoalog.getProgress() == progressDoalog
                                            .getMax()) {
                                        progressDoalog.dismiss();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                handle = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        progressDoalog.incrementProgressBy(1);
                    }
                };

                    FileUtils.moveFileToDirectory(sourceFile=new File(docList.get(i).toString()), destinationFile=new File(root+"/Documents"),true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(music.isChecked())
        {
            for(i=0;i<musicList.size();i++) {
                try {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (progressDoalog.getProgress() <= progressDoalog
                                        .getMax()) {
                                    Thread.sleep(2000);
                                    handle.sendMessage(handle.obtainMessage());
                                    if (progressDoalog.getProgress() == progressDoalog
                                            .getMax()) {
                                        progressDoalog.dismiss();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                    handle = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            progressDoalog.incrementProgressBy(1);
                        }
                    };

                    FileUtils.moveFileToDirectory(sourceFile=new File(musicList.get(i).toString()), destinationFile=new File(root+"/Music"),true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(image.isChecked())
        {
            for(int j=0;j<imageList.size();j++)
            {
                String s=imageList.get(j).toString();
                for(int k=0;k<foldername.size();k++)
                {
                    if(s.contains(foldername.get(k)))
                    {
                        try {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        while (progressDoalog.getProgress() <= progressDoalog
                                                .getMax()) {
                                            Thread.sleep(2000);
                                            handle.sendMessage(handle.obtainMessage());
                                            if (progressDoalog.getProgress() == progressDoalog
                                                    .getMax()) {
                                                progressDoalog.dismiss();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                            handle = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    progressDoalog.incrementProgressBy(1);
                                }
                            };

                            FileUtils.moveFileToDirectory(sourceFile=new File(imageList.get(j).toString()), destinationFile=new File(root+"/Images/"+foldername.get(k)),true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if(video.isChecked())
        {
            for(int j=0;j<videoList.size();j++)
            {
                String s=videoList.get(j).toString();
                for(int k=0;k<foldername.size();k++)
                {
                    if(s.contains(foldername.get(k)))
                    {
                        try {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        while (progressDoalog.getProgress() <= progressDoalog
                                                .getMax()) {
                                            Thread.sleep(2000);
                                            handle.sendMessage(handle.obtainMessage());
                                            if (progressDoalog.getProgress() == progressDoalog
                                                    .getMax()) {
                                                progressDoalog.dismiss();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                            handle = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    progressDoalog.incrementProgressBy(1);
                                }
                            };

                            FileUtils.moveFileToDirectory(sourceFile=new File(videoList.get(j).toString()), destinationFile=new File(root+"/Videos/"+foldername.get(k)),true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        imageList.clear();
        docList.clear();
        musicList.clear();
        videoList.clear();
    }
    /*end moving files*/


    /*Marshmallow permissions*/
    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMS_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case PERMS_REQUEST_CODE:

                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed){
            //user granted all permissions we can perform our task.
            checkfolder();
        }
        else {
            hasPermissions();
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                }
            }
        }

    }
    /*end Marshmallow permissions*/
}
