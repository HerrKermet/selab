package com.example.a22b11.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a22b11.MyApplication;
import com.example.a22b11.R;
import com.example.a22b11.Sportactivity_Edit;
import com.example.a22b11.Sportactivity_Edit_Selection;
import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class itemAdapter extends RecyclerView.Adapter<itemViewHolder>{

    List<Activity> items;
    ActivityDao activityDao;
    android.app.Activity callingActivity;
    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");


    public itemAdapter(List<Activity> items, ActivityDao activityDao, android.app.Activity callingActivity) {
        this.items = items;
        this.activityDao = activityDao;
        this.callingActivity = callingActivity;

    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

        return new itemViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        holder.name.setText(String.valueOf(items.get(position).type));
        holder.duration.setText(String.valueOf(items.get(position).duration) + "s");



        holder.date.setText(String.valueOf(formatter.format(Date.from(items.get(position).start))));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

class itemViewHolder extends RecyclerView.ViewHolder {



    TextView name;
    TextView duration;
    TextView date;
    ActivityDao activityDao;
    CardView cardItem;
    Toast toast;

    android.app.Activity callingActivity;

    private itemAdapter adapter;

    public itemViewHolder(@NonNull View itemView) {
        super(itemView);




        name = itemView.findViewById(R.id.itemName);
        duration = itemView.findViewById(R.id.itemDuration);
        date = itemView.findViewById(R.id.itemDate);
        cardItem = itemView.findViewById(R.id.cardItem);

        //on click for CardItem
        cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(callingActivity, Sportactivity_Edit.class);
                intent.putExtra("databaseActivityEdit",adapter.items.get(getAdapterPosition()));
                callingActivity.startActivity(intent);
            }
        });

        //on click for Delete button
        itemView.findViewById(R.id.itemDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toast.setText(R.string.holdForDeletion);
                toast.cancel(); // cancel previous toast if there are any
                toast.show();

            }
        } );

        //on long click for Delete button
        itemView.findViewById(R.id.itemDelete).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //TODO DELETE ACTIVITY FROM DATABASE


                //start delete selected activity from database
                activityDao.delete(adapter.items.get(getAdapterPosition()));
                //end
                adapter.items.remove(getAdapterPosition());

                toast.setText(R.string.deletedSuccessful);
                toast.cancel();
                toast.show();




                adapter.notifyItemRemoved(getAdapterPosition());



                return true;
            }
            });

    }

    public itemViewHolder linkAdapter(itemAdapter adapter) {
        this.adapter = adapter;
        this.activityDao = adapter.activityDao;
        this.callingActivity = adapter.callingActivity;
        this.toast = Toast.makeText(adapter.callingActivity,"",Toast.LENGTH_SHORT);
        return this;
    }
}