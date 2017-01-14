package com.placepickexample.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Fade;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.placepickexample.MainActivity;
import com.placepickexample.R;
import com.placepickexample.RecyclerAdapter;
import com.placepickexample.Vars;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFrag extends Fragment implements pass_data {

    Button create;
    public HomeFrag() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    RecyclerAdapter adapter;
     ArrayList<Vars> reminderList=new ArrayList<>();
     DatabaseReference mDatabase;
    SwipeRefreshLayout refreshLayout;
    TextView tvNoReminders;
    String unique_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        unique_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        mDatabase= FirebaseDatabase.getInstance().getReferenceFromUrl("https://remindmeat-5e359.firebaseio.com/").child(unique_id);



        View v= inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity.toolbar.setTitle("Reminders");
        refreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeLayout);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView=(RecyclerView)v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(layoutManager);
        tvNoReminders=(TextView)v.findViewById(R.id.tvNoReminders);



        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean hideToolBar = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (hideToolBar) {

                    MainActivity.toolbar.animate().translationY(-MainActivity.toolbar.getHeight())
                            .setInterpolator(new AccelerateInterpolator(2))
                            .start();

                   ((MainActivity)getActivity()).getSupportActionBar().hide();
                } else {
                    MainActivity.toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                    ((MainActivity)getActivity()).getSupportActionBar().show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20) {
                    hideToolBar = true;

                } else if (dy < -5) {
                    hideToolBar = false;
                }
            }
        });

        getData();
        refreshLayout.setRefreshing(true);
        adapter=new RecyclerAdapter(getContext(),reminderList);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();


            }
        });
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                adapter.removeFromFireBase(viewHolder.getAdapterPosition());
                reminderList.remove(viewHolder.getAdapterPosition());
                // this line animates what happens after delete
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                Toast.makeText(getActivity(), "Delete successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);



        create=(Button)v.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                ((MainActivity)getActivity()).getSupportActionBar().show();
                ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,new PlacePickFrag())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null).commit();
            }
        });
        return v;
    }



    private void getData() {

        refreshLayout.setRefreshing(true);
        reminderList.removeAll(reminderList);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot k:dataSnapshot.getChildren()){

                    reminderList.add(new Vars(k.child("place").getValue().toString(),k.child("note").getValue().toString()
                            ,Double.parseDouble( k.child("latlang").child("latitude").getValue().toString()),
                            Double.parseDouble( k.child("latlang"
                            ).child("longitude").getValue().toString()),k.getKey()));
                }
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
                if (reminderList.size()==0)
                {
                    tvNoReminders.setVisibility(TextView.VISIBLE);
                    tvNoReminders.setText("OOPS!! \n NO REMINDERS SET \n Tap + to set one..");
                }
                else {
                    tvNoReminders.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

   /* private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }*/
    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
      //reminderList.removeAll(reminderList);
    }


    @Override
    public ArrayList<Vars> passData() {
        return reminderList;
    }
}
interface pass_data{
    ArrayList<Vars> passData();

}