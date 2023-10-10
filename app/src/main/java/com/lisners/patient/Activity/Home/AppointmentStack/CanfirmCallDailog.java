package com.lisners.patient.Activity.Home.AppointmentStack;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.lisners.patient.R;

public class CanfirmCallDailog {
  final  Dialog dialog ;
  Button btn_cancel , btn_callnow ;
    OnClickLister lister ;

  public interface OnClickLister{
      void onClick();
  }

  public CanfirmCallDailog(Context context,OnClickLister onClickLister){
         this.lister=onClickLister;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_show_call_charges);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_callnow = dialog.findViewById(R.id.btn_call_now);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_callnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lister!=null)
                lister.onClick();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}
