package com.veggies.android.backup;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.veggies.android.model.DBHelper;
import com.veggies.android.model.DBManager;
import com.veggies.android.todoList.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Xiaobin Lin on 4/13/2016.
 */
public class GoogleAPIHandler{
    private static GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions mGoogleSigninOption;
    private static DriveId mGoogleDriveDBID;
    private static Context mContext;
    private static GoogleSignInAccount mGoogleAccount;
    private static Runnable mRunAfterRestore;
    private static Runnable mRunAfterFailure;
    private static boolean mStartActivity = false;
    private static final String DB_DIR = "/data/data/" + "com.veggies.android.todoList" + "/databases";
    private static final String DB_PATH = DB_DIR + "/" + DBHelper.DB_NAME;
    public static final String BACKUP_NAME = "Taski.backup.db";

    //this method should be called inside mGoogleApiClient's onConnected callback
    public static void onConnected(Runnable onConnectedRunnable){
        if(onConnectedRunnable != null){
            onConnectedRunnable.run();
        }
    }

    public static void setGoogleSigninOption(GoogleSignInOptions signin){
        mGoogleSigninOption = signin;
    }

    public static GoogleSignInOptions getGoogleSigninOption(){
        return mGoogleSigninOption;
    }

    public static void setGoogleApiClient(GoogleApiClient client){
        mGoogleApiClient = client;
    }

    public static GoogleApiClient getGoogleApiClient(){
        return  mGoogleApiClient;
    }

    public static void setContext(Context context){
        mContext = context;
    }

    public static void setGoogleAccount(GoogleSignInAccount account){
        mGoogleAccount = account;
    }

    public static void revokeAccess(){
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            //Signs out the current signed-in user if any.
            //It also clears the account previously selected by the user and a future sign in attempt will require the user pick an account again.
            Auth.GoogleSignInApi.revokeAccess(GoogleAPIHandler.getGoogleApiClient());
        }
    }

    public static void backupDB(){
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, BACKUP_NAME))
                    .build();

            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                    .queryChildren(mGoogleApiClient, query)
                    .setResultCallback(onBackupRetrieved);
        }
    }

    final private static ResultCallback<DriveApi.MetadataBufferResult> onBackupRetrieved = new ResultCallback<DriveApi.MetadataBufferResult>() {
        @Override
        public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
            if(result != null){
                MetadataBuffer buffer = result.getMetadataBuffer();
                if(buffer == null){
                    return;
                }

                if(buffer.getCount() > 0){
                    Metadata metadata = buffer.get(0);

                    Log.d("handleSignInResult",
                            "remote DB file found, will modify the current file, name: " + metadata.getTitle() +
                            "\nlink:" + metadata.getWebContentLink());

                    mGoogleDriveDBID = metadata.getDriveId();
                    DriveFile file = mGoogleDriveDBID.asDriveFile();

                    file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).setResultCallback(modifyBackupDBCallback);
                    //mGoogleDriveDBID = null;
                    buffer.release();
                }
                else{
                    //can't find the db file
                    Drive.DriveApi.newDriveContents(getGoogleApiClient())
                            .setResultCallback(createBackupDBCallback);

                    Log.d("handleSignInResult", "remote DB file not found, will create a new one");
                }
            }
        }
    };

    private static final ResultCallback<DriveApi.DriveContentsResult> modifyBackupDBCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                // display an error saying file can't be opened
                Log.d("handleSignInResult", "Backup: failed to open remote DB backup file");
                return;
            }

            final DriveContents driveContents = result.getDriveContents();

            // Perform I/O off the UI thread.
            new Thread() {
                @Override
                public void run() {
                    // write content to DriveContents
                    OutputStream outputStream = driveContents.getOutputStream();
                    File data = Environment.getDataDirectory();
                    File db = new File(DB_PATH);
                    byte[] buffer = new byte[512];
                    int length;

                    try {
                        FileInputStream dbStream = new FileInputStream(db);
                        while ((length = dbStream.read(buffer)) > 0){
                            outputStream.write(buffer, 0, length);
                        }

                        //finish writing
                        dbStream.close();
                        outputStream.close();

                        //commit the change
                        driveContents.commit(mGoogleApiClient, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("handleSignInResult", "backed up by modifying existing DB file");
                }
            }.start();
        }
    };

    //modified code from Google Drive API Demo
    //callback that writes the local DB file to Google Drive
    final private static ResultCallback<DriveApi.DriveContentsResult> createBackupDBCallback
            = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                return;
            }
            final DriveContents driveContents = result.getDriveContents();

            // Perform I/O off the UI thread.
            new Thread() {
                @Override
                public void run() {
                    // write content to DriveContents
                    OutputStream outputStream = driveContents.getOutputStream();
                    File db = new File(DB_PATH);
                    byte[] buffer = new byte[512];
                    int length;

                    if(!db.exists()){
                        Log.d("handleSignInResult", "can't find local db, won't backup to drive");
                        return;
                    }

                    try {
                        FileInputStream dbStream = new FileInputStream(db);
                        while ((length = dbStream.read(buffer)) > 0){
                            outputStream.write(buffer, 0, length);
                        }

                        //finish writing
                        dbStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(BACKUP_NAME)
                            .setMimeType("text/plain")
                            .setStarred(true).build();

                    // create a file on root folder
                    Drive.DriveApi.getRootFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, driveContents);

                    Log.d("handleSignInResult", "backed up by creating a new DB file");
                }
            }.start();
        }
    };


    /**
     * restore database from Drive
     * If startActivity is true, then start the main activity
     * runnable will be run once DB is restored
     */
    public static void restoreDB(boolean startActivity, Runnable success, Runnable failed){
        mStartActivity = startActivity;
        mRunAfterRestore = success;
        mRunAfterFailure = failed;

        if(mGoogleApiClient.isConnected()) {
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, BACKUP_NAME))
                    .build();

            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                    .queryChildren(mGoogleApiClient, query)
                    .setResultCallback(onRestoreRetrieved);

            //Drive.DriveApi.query(mGoogleApiClient, query);
        }
        else{
            Log.d("handleSignInResult", "not connected to Google");
            if(mStartActivity) {
                startMainActivity(mGoogleAccount.getEmail());
            }

            if(mRunAfterFailure != null){
                mRunAfterFailure.run();
            }
        }
    }

    //query callback when doing DB restore
    private static final ResultCallback<DriveApi.MetadataBufferResult> onRestoreRetrieved = new ResultCallback<DriveApi.MetadataBufferResult>() {
        @Override
        public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
            Log.d("handleSignInResult", "onRestoreRetrieved");

            if(result != null){
                MetadataBuffer buffer = result.getMetadataBuffer();
                if(buffer == null){
                    if(mStartActivity) {
                        startMainActivity(mGoogleAccount.getEmail());
                    }

                    if(mRunAfterFailure != null){
                        mRunAfterFailure.run();
                    }
                    return;
                }//end if buffer null

                if(buffer.getCount() > 0){
                    mGoogleDriveDBID = buffer.get(0).getDriveId();
                    DriveFile file = mGoogleDriveDBID.asDriveFile();

                    //buffer.get(0).getModifiedDate().getTime();

                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(contentsOpenedCallback);
                    mGoogleDriveDBID = null;
                    buffer.release();
                }
                else{
                    //can't find the db file
                    Log.d("handleSignInResult", "Remote DB file not found, will not restore DB");
                    if(mStartActivity) {
                        startMainActivity(mGoogleAccount.getEmail());
                    }

                    if(mRunAfterFailure != null){
                        mRunAfterFailure.run();
                    }
                }
            }
            else{
                if(mStartActivity) {
                    startMainActivity(mGoogleAccount.getEmail());
                }

                if(mRunAfterFailure != null){
                    mRunAfterFailure.run();
                }
            }
        }
    };


    //callback when remote DB backup file is opened, will copy the remote DB file into local file
    private static final ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
            Log.d("handleSignInResult", "contentsOpenedCallback");

            if (!result.getStatus().isSuccess()) {
                // display an error saying file can't be opened
                Log.d("handleSignInResult", "Restore: fail to open remote DB backup file");
                if(mStartActivity) {
                    startMainActivity(mGoogleAccount.getEmail());
                }

                if(mRunAfterFailure != null){
                    mRunAfterFailure.run();
                }
                return;
            }

            // DriveContents object contains pointers
            // to the actual byte stream
            DriveContents contents = result.getDriveContents();

            InputStream inputStream = contents.getInputStream();
            File data = Environment.getDataDirectory();
            File db = new File(DB_PATH);
            File dbdir = new File(DB_DIR);
            FileOutputStream fileOutputStream;
            byte[] buffer = new byte[512];
            int length;

            Log.d("handleSignInResult", "db file path:\n" + db.getAbsolutePath().toString());
            try{
                //create the DB directory if needed
                if(!dbdir.exists()){
                    dbdir.mkdir();
                }

                //this method will create an empty file if the file doesn't exist
                db.createNewFile();

                fileOutputStream = new FileOutputStream(db);
                while ((length = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }

                fileOutputStream.close();
                inputStream.close();
                Log.d("handleSignInResult", "Restored");

                if(mStartActivity) {
                    startMainActivity(mGoogleAccount.getEmail());
                }

                if(mRunAfterRestore != null){
                    mRunAfterRestore.run();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    private static void startMainActivity(String email){
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(MainActivity.EMAIL, email);
        mContext.startActivity(intent);
    }
}
