package com.placepickexample;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by anirudh on 25/10/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {

    Context context;
    ArrayList<Vars> reminderList;
    String unique_id;
    DatabaseReference mDatabase;
    public RecyclerAdapter(Context context, ArrayList<Vars> reminderList)
    {
        this.reminderList=reminderList;
        this.context=context;
        unique_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        mDatabase= FirebaseDatabase.getInstance().getReferenceFromUrl("https://remindmeat-5e359.firebaseio.com/").child(unique_id);
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        holder.setScaleAnimation(holder.v);
        try {
            holder.letter.setText(reminderList.get(position).note.trim().substring(0, 1).toUpperCase());
        }
        catch (StringIndexOutOfBoundsException e)
        {
            holder.letter.setText("⏰");
        }
        if(reminderList.get(position).note.trim().equals(""))
        {
            holder.tvName.setText("ALARM");
        }
        else
        holder.tvName.setText(reminderList.get(position).note);

        holder.tvplace.setText(reminderList.get(position).place);
        holder.tvDistance.setText("");

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

   public void removeFromFireBase(int pos){
       String key=reminderList.get(pos).pushId;
       System.out.println("xyz: "+key);
       mDatabase.child(key).removeValue();

    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        View v;
        TextView tvName,letter,tvplace,tvDistance;
        public MyHolder(View v) {
            super(v);
            this.v=v;
            tvplace=(TextView) v.findViewById(R.id.tvPlace);
            tvName=(TextView)v.findViewById(R.id.tvName);
            tvDistance=(TextView)v.findViewById(R.id.tvDistance);
            letter=(TextView)v.findViewById(R.id.letter);
        }

        private void setScaleAnimation(View view) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(500);
            view.startAnimation(anim);
        }
    }
}
