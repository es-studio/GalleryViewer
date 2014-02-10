package com.esstudio.gallery;

import java.util.HashMap;

import com.esstudio.gallery.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class DrawerLongClickListener implements OnItemLongClickListener {

    private HashMap<String, String> mItems;
    private TextView tv1;
    private TextView tv2;
    private DrawerAdaptor mAdt;

    public DrawerLongClickListener(HashMap<String, String> mItems,
            DrawerAdaptor adt) {
        this.mItems = mItems;
        this.mAdt = adt;

    }

    private void deleteItem() {

        String key = tv2.getText().toString();
        mItems.remove(key);
        mAdt.notifyDataSetChanged();
        
        Settings.setProperties(mItems);

        //        MainActivity.getInstance().showToast(key + "-" +mItems.containsKey(key));

    }

    public void showDialog() {
        Context c = MainActivity.getInstance();
        // Context context = getApplicationContext();
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        //        alert.setTitle("Delete List"); // Set Alert dialog title
        alert.setTitle(null); // Set Alert dialog title
        alert.setItems(new String[] {
            "Delete"
        },
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                    case 0:
                        deleteItem();
                        break;

                    }

                }
            });

        // View l = findViewById(R.layout.dialog);
        // alert.setView(l);
        alert.setPositiveButton("Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                } // End of onClick(DialogInterface dialog, int whichButton)
            }); // End of alert.setPositiveButton

        AlertDialog alertDialog = alert.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View v, int arg2,
                                   long arg3) {
        // TODO Auto-generated method stub

        tv1 = (TextView) v.findViewById(R.id.textView1);
        tv2 = (TextView) v.findViewById(R.id.textView2);

        showDialog();

        return false;
    }
}
