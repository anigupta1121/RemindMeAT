package com.placepickexample.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.placepickexample.MainActivity;
import com.placepickexample.R;
import com.placepickexample.Services.GPSTracker;
import com.placepickexample.Services.LocationService;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;


public class PlacePickFrag extends Fragment implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    FirebaseDatabase mDataBase;
    static DatabaseReference mref;
    private static final int PLACE_PICKER_REQUEST = 1;

    static  DatePickerDialog dpd;
   static TimePickerDialog tpd;
    private GoogleApiClient mClient;
    Button btnPlace,btnDate,btnTime;
   static Button btnConfirm;
    CheckBox chkAlarm,chkReminder;
   static TextView tvPlace,tvDate,tvTime;
    static String x,y,p,d,t;
    Switch gpsEnable;
    LocationManager locationManager;
    EditText etNote;
    String unique_id;

    public PlacePickFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_place_pick, container, false);
        MainActivity.toolbar.setTitle("Pick Place");
        mClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mDataBase=FirebaseDatabase.getInstance();

        unique_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        mref=mDataBase.getReferenceFromUrl("https://remindmeat-5e359.firebaseio.com/").child(unique_id).push();
        btnPlace=(Button)v.findViewById(R.id.pick_place);
        btnDate=(Button)v.findViewById(R.id.pick_date);
        btnTime=(Button)v.findViewById(R.id.pick_time);
        btnConfirm=(Button)v.findViewById(R.id.confirm);
        tvDate=(TextView)v.findViewById(R.id.tv_date);
        tvPlace=(TextView)v.findViewById(R.id.tv_place);
        tvTime=(TextView)v.findViewById(R.id.tv_time);


        chkAlarm=(CheckBox)v.findViewById(R.id.chkAlarm);
        chkReminder=(CheckBox)v.findViewById(R.id.chkReminder);
        gpsEnable=(Switch)v.findViewById(R.id.switch1);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        etNote=(EditText)v.findViewById(R.id.etNote);

        chkReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkReminder.isChecked())
                    etNote.setVisibility(EditText.VISIBLE);
                else
                    etNote.setVisibility(EditText.GONE);
            }

        });
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            gpsEnable.setChecked(false);
        }
        else
        {
            gpsEnable.setChecked(true);
        }



        gpsEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gpsEnable.isChecked()){
                   turnOnGps();
                }
                else
                    turnOfGps();

            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        new PlacePickFrag(),
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(false);
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(true);
                dpd.setAccentColor(Color.parseColor("#9C27B0"));
                dpd.setTitle("DatePicker");

                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                tpd = TimePickerDialog.newInstance(
                         new PlacePickFrag(),
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.setThemeDark(false);
                tpd.vibrate(true);
                tpd.setAccentColor(Color.parseColor("#9C27B0"));
                tpd.setTitle("TimePicker");

                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");

            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNote.getVisibility()==EditText.VISIBLE&&etNote.getText().toString().equals(""))
                {
                    Toast.makeText(getContext(),"Please add a note!",Toast.LENGTH_SHORT).show();
                }
                else if(!chkAlarm.isChecked() && !chkReminder.isChecked())
                {
                    Toast.makeText(getContext(),"Tick atleast one option!",Toast.LENGTH_SHORT).show();

                    return;
                }
                else {
                    mref.child("date").setValue(d);
                    mref.child("place").setValue(p);
                    mref.child("time").setValue(t);
                    mref.child("latlang").child("latitude").setValue(x);
                    mref.child("latlang").child("longitude").setValue(y);
                    mref.child("timestamp").setValue(System.currentTimeMillis()/1000);
                    mref.child("note").setValue(etNote.getText().toString().trim());

                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container,new HomeFrag())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();
                }
            }
        });
        return v;
    }

    private void turnOfGps() {
        Toast.makeText(getContext(),"Turning on GPS",Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure to turn Off Gps")
                .setMessage("Open location settings")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();



    }

    private void turnOnGps() {
        Toast.makeText(getContext(),"Turning off GPS",Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Turn on Gps")
                .setMessage("Open location settings")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                gpsEnable.setChecked(false);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        //mref=FirebaseDatabase.getInstance().getReferenceFromUrl("https://remindmeat-5e359.firebaseio.com/");
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST&&data!=null) {

                Place place = PlacePicker.getPlace(data, getContext());
            x=place.getLatLng().latitude+"";
            y=place.getLatLng().longitude+"";
                String toastMsg = String.format("Place: %s", place.getName());
            p=place.getName()+"";
            tvPlace.setText(toastMsg);
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();

        }
        if(tvDate.getText()!=""&&tvPlace.getText()!=""&&tvTime.getText()!="") {
            btnConfirm.setVisibility(Button.VISIBLE);




        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        d=dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        tvDate.setText(date);
        if(tvDate.getText()!=""&&tvPlace.getText()!=""&&tvTime.getText()!="") {
            btnConfirm.setVisibility(Button.VISIBLE);


        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        t=hourString+":"+minuteString+":"+secondString;
        String time = "You picked the following time: "+hourString+":"+minuteString+":"+secondString;

       tvTime.setText(time);
        if(tvDate.getText()!=""&&tvPlace.getText()!=""&&tvTime.getText()!="") {
            btnConfirm.setVisibility(Button.VISIBLE);


        }
    }
}
