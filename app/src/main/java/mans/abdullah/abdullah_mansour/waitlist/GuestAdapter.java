package mans.abdullah.abdullah_mansour.waitlist;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import mans.abdullah.abdullah_mansour.waitlist.GuestAdapter.Guestviewholder;
import mans.abdullah.abdullah_mansour.waitlist.data.WaitlistContract.WaitlistEntry;

public class GuestAdapter extends RecyclerView.Adapter<Guestviewholder>
{
    Context context;
    Cursor cursor;

    public GuestAdapter(Context context,Cursor cursor)
    {
        this.context = context;
        this.cursor = cursor;
    }


    @NonNull
    @Override
    public Guestviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item , parent , false);
        return new  Guestviewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Guestviewholder holder, int position)
    {
        if (!cursor.moveToPosition(position))
        {
            return;
        }

        String name = cursor.getString(cursor.getColumnIndex(WaitlistEntry.COLUMN_GUEST_NAME));
        String size = cursor.getString(cursor.getColumnIndex(WaitlistEntry.COLUMN_PARTY_SIZE));

        long id = cursor.getLong(cursor.getColumnIndex(WaitlistEntry._ID));

        holder.name.setText(name);
        holder.size.setText(size);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount()
    {
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (cursor != null)
        {
            cursor.close();
        }

        cursor = newCursor;

        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    public class Guestviewholder extends RecyclerView.ViewHolder
    {
        TextView name,size;

        public Guestviewholder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name_result);
            size = (TextView) itemView.findViewById(R.id.size_result);
        }
    }
}
