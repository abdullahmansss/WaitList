package mans.abdullah.abdullah_mansour.waitlist;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.cardview.*;


import mans.abdullah.abdullah_mansour.waitlist.data.WaitlistContract;
import mans.abdullah.abdullah_mansour.waitlist.data.WaitlistDBhelper;

public class MainActivity extends AppCompatActivity
{
    FloatingActionButton newuser;
    ImageView list;
    GuestAdapter guestAdapter;
    SQLiteDatabase sqLiteDatabase;
    WaitlistDBhelper waitlistDBhelper;
    Cursor cursor;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newuser = (FloatingActionButton) findViewById(R.id.newuser);
        recyclerView = (RecyclerView) findViewById(R.id.waitlist_recycler);
        list = (ImageView) findViewById(R.id.waitlist_image);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        waitlistDBhelper = new WaitlistDBhelper(getApplicationContext());

        sqLiteDatabase = waitlistDBhelper.getWritableDatabase();

        cursor = getAllGuests();

        guestAdapter = new GuestAdapter(getApplicationContext(), cursor);

        recyclerView.setAdapter(guestAdapter);

        if (cursor.getCount() != 0)
        {
            list.setVisibility(View.GONE);
        }
        else
            {
                list.setVisibility(View.VISIBLE);
            }

        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showCustomDialog();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                //remove from DB
                removeGuest(id);
                //update the list
                guestAdapter.swapCursor(getAllGuests());

                if (getAllGuests().getCount() == 0)
                {
                    list.setVisibility(View.VISIBLE);
                }
            }

            //COMPLETED (11) attach the ItemTouchHelper to the waitlistRecyclerView
        }).attachToRecyclerView(recyclerView);
    }

    private long addNewGuest(String name, String partySize)
    {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return sqLiteDatabase.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }

    private Cursor getAllGuests()
    {
        return sqLiteDatabase.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

    private boolean removeGuest(long id)
    {
        return sqLiteDatabase.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.newguest_dialoge);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final Button add_guest = (Button) dialog.findViewById(R.id.add_guest_btn);
        final Button back = (Button) dialog.findViewById(R.id.back_btn);

        final EditText guest_name = (EditText) dialog.findViewById(R.id.guest_name);
        final EditText guest_number = (EditText) dialog.findViewById(R.id.guest_number);

        final View layout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setTextColor(Color.WHITE);
        text.setText("Please enter valid data");
        CardView lyt_card = (CardView) layout.findViewById(R.id.lyt_card);
        lyt_card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final Toast toast = new Toast(getApplicationContext());

        add_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name = guest_name.getText().toString();
                String number = guest_number.getText().toString();

                if (name.length() == 0 || number.length() == 0)
                {
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
                else
                    {
                        addNewGuest(name,number);
                        guestAdapter.swapCursor(getAllGuests());

                        list.setVisibility(View.GONE);

                        dialog.dismiss();
                    }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toast.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
