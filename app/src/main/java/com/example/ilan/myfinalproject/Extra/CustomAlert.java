package com.example.ilan.myfinalproject.Extra;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
// custom alertbox
public final class CustomAlert
{

    private CustomAlert() {}


    public static void createAlertDialog(Context context, String title, String msg, String positiveText, String negativeText, final AlertDialogListener alertDialogListener)
    {
    DialogInterface.OnClickListener clickListener=null;
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

    if(title!=null)
        {
        dialogBuilder.setTitle(title);
        }

    if(msg!=null)
        {
        dialogBuilder.setMessage(msg);
        }

    if(alertDialogListener!=null)
        {
        clickListener=new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch(which)
                    {
                    case DialogInterface.BUTTON_POSITIVE:
                        alertDialogListener.onPositive(dialog);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        alertDialogListener.onNegative(dialog);
                        break;

                    }
                }
            };
        }

    dialogBuilder.setCancelable(true);

    dialogBuilder.setPositiveButton(positiveText, clickListener);
    dialogBuilder.setNegativeButton(negativeText, clickListener);

    dialogBuilder.create().show();
    }



    public interface AlertDialogListener
    {
        void onPositive(DialogInterface dialog);
        void onNegative(DialogInterface dialog);
    }
}
