package com.arpaul.locationfinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;

import com.arpaul.customalertlibrary.dialogs.CustomDialog;
import com.arpaul.customalertlibrary.popups.statingDialog.CustomPopupType;
import com.arpaul.customalertlibrary.popups.statingDialog.PopupListener;
import com.arpaul.utilitieslib.UnCaughtException;

/**
 * Created by Aritra on 15-08-2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements PopupListener {

    public LayoutInflater baseInflater;
    public LinearLayout llBody;
    private CustomDialog cDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(BaseActivity.this,"aritra1704@gmail.com",getString(R.string.app_name)));
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_base);

        initialiseBaseControls();

        bindBaseControls();

        initialize(savedInstanceState);
    }

    public abstract void initialize(Bundle savedInstanceState);

    private void bindBaseControls(){

    }

    /**
     * Shows Dialog with user defined buttons.
     * @param title
     * @param message
     * @param okButton
     * @param noButton
     * @param from
     * @param isCancelable
     */
    public void showCustomDialog(final String title, final String message, final String okButton, final String noButton, final String from, boolean isCancelable){
        runOnUiThread(new RunShowDialog(title,message,okButton,noButton,from, isCancelable));
    }

    public void showCustomDialog(final String title, final String message, final String okButton, final String noButton, final String from, CustomPopupType dislogType, boolean isCancelable){
        runOnUiThread(new RunShowDialog(title,message,okButton,noButton,from, dislogType, isCancelable));
    }

    public void hideCustomDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cDialog != null && cDialog.isShowing())
                    cDialog.dismiss();
            }
        });
    }

    class RunShowDialog implements Runnable {
        private String strTitle;// FarmName of the materialDialog
        private String strMessage;// Message to be shown in materialDialog
        private String firstBtnName;
        private String secondBtnName;
        private String from;
        private String params;
        private boolean isCancelable=false;
        private CustomPopupType dislogType = CustomPopupType.DIALOG_NORMAL;
        public RunShowDialog(String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable)
        {
            this.strTitle 		= strTitle;
            this.strMessage 	= strMessage;
            this.firstBtnName 	= firstBtnName;
            this.secondBtnName	= secondBtnName;
            this.isCancelable 	= isCancelable;
            if (from != null)
                this.from = from;
            else
                this.from = "";
        }

        public RunShowDialog(String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, CustomPopupType dislogType, boolean isCancelable)
        {
            this.strTitle 		= strTitle;
            this.strMessage 	= strMessage;
            this.firstBtnName 	= firstBtnName;
            this.secondBtnName	= secondBtnName;
            this.dislogType     = dislogType;
            this.isCancelable 	= isCancelable;
            if (from != null)
                this.from = from;
            else
                this.from = "";
        }

        @Override
        public void run() {
                showNotNormal();
        }

        private void showNotNormal(){
            try{
                if (cDialog != null && cDialog.isShowing())
                    cDialog.dismiss();

                cDialog = new CustomDialog(BaseActivity.this, BaseActivity.this,strTitle,strMessage,
                        firstBtnName, secondBtnName, from, dislogType);

                cDialog.show();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnButtonYesClick(String from) {
        dialogYesClick(from);
    }

    @Override
    public void OnButtonNoClick(String from) {
        dialogNoClick(from);
    }

    public void dialogYesClick(String from) {

    }

    public void dialogNoClick(String from) {
        if(from.equalsIgnoreCase("")){

        }
    }

    private void initialiseBaseControls(){
        baseInflater            = 	this.getLayoutInflater();
        llBody                  =   (LinearLayout) findViewById(R.id.llBody);
    }
}
