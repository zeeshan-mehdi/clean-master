package com.alim.cleanmaster;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;


import java.util.List;

import io.paperdb.Paper;

public class SetPattern extends AppCompatActivity {
    String save_pattern_key = "pattern";
    PatternLockView patternLockView;
    String final_pattern;
    String confirm_pattern1;
    String confirm_pattern2;
    Button createPattern;
    static  boolean confirmFlag=false;
    static  boolean finishFlag=false;
    TextView txtSatus;

    boolean changePattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        changePattern = getIntent().getBooleanExtra("change",false);

        if(changePattern){
            Paper.book().write(save_pattern_key,"null");
        }

        final String save_pattern = Paper.book().read(save_pattern_key);

        if (save_pattern != null && !save_pattern.equals("null")) {
          //  setContentView(R.layout.activity_set_pattern);
          //  showLockScreen(SetPattern.this,R.layout.activity_set_pattern);
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_set_pattern);
            dialog .setCancelable(false);
            dialog.show();
            patternLockView = dialog.findViewById(R.id.pattern);
            patternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(patternLockView, pattern);
                    if (final_pattern.equals(save_pattern)) {
                        boolean flag = getIntent().getBooleanExtra("flag", false);
                        if (flag) {
                            Intent intent = new Intent(getApplicationContext(), AppLockActivity.class);
                            startActivity(intent);
                        }
                        patternLockView.setCorrectStateColor(getResources().getColor(R.color.defaultGreen2));
                        dialog.dismiss();
                        finish();
                        overridePendingTransition(R.anim.bottom_in, R.anim.top_out);

                    } else {
                        patternLockView.setCorrectStateColor(getResources().getColor(R.color.red));
                        Messages.sendMessage(getApplicationContext(), "wrong pattern Try Again");
                    }
                }

                @Override
                public void onCleared() {

                }
            });
        } else {
           // setContentView(R.layout.new_pattern);
           // showLockScreen(SetPattern.this,R.layout.new_pattern);
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.new_pattern);
            dialog .setCancelable(false);
            dialog.show();


            patternLockView =dialog.findViewById(R.id.new_pattern);

            txtSatus = dialog.findViewById(R.id.txtStatus);

            txtSatus.setText("Draw an unlock Pattern");

            patternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    if(!confirmFlag) {
                        final_pattern = PatternLockUtils.patternToString(patternLockView, pattern);
                        patternLockView.clearPattern();
                        txtSatus.setText("Draw again to Confirm Pattern");
                        confirmFlag = true;
                    }else{
                        confirm_pattern1 = PatternLockUtils.patternToString(patternLockView, pattern);
                        if(confirm_pattern1.equals(final_pattern)){
                            Paper.book().write(save_pattern_key, final_pattern);
                            txtSatus.setText("Pattern recorded");
                            Messages.sendMessage(getApplicationContext(), "Pattern Created Successfully");
                            Intent intent = new Intent(getApplicationContext(), AppLockActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            confirmFlag=false;
                            patternLockView.clearPattern();
                            txtSatus.setText("Try Again");
                            Messages.sendMessage(getApplicationContext(), "Pattern not Matched Try Again");
                        }
                    }
                }

                @Override
                public void onCleared() {

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        return;
    }
    public void showLockScreen(Context context, int id){
        Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(id);
        dialog.show();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }
}

