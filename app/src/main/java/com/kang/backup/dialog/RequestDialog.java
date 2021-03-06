package com.kang.backup.dialog;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kang.backup.R;
import com.kang.backup.SignupActivity;
import com.kang.backup.fragment.RequestDetailFragment;
import com.kang.backup.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RequestDialog extends Dialog {
    private Context context;
    private String request_publisher;
    private String uid;
    private Animation tranlateToLeftAnim, tranlateFromLeftAnim, tranlateToRightAnim, tranlateFromRightAnim;
    SharedPreferences.Editor editor;

    String request_data="", request_stime="", request_etime="", request_txt="";

    String cnt;

    LinearLayout calendar_layout, stime_layout, etime_layout, request_layout, result_layout;

    CalendarView calendar_view;
    TimePicker stime_view,etime_view;
    EditText request_view;
    Button calendar_btn, calendar_back_btn, stime_btn, stime_back_btn, etime_btn, etime_back_btn, request_btn, request_back_btn;
    Button result_btn, result_back_btn;

    TextView result_calendar_view, result_stime_view,result_etime_view, result_request_view;

    Date currenttimeformat;
    String currenttime;
    HashMap<String,Object> hashMap = new HashMap<>();

    int stime, etime;

    public RequestDialog(Context context, String request_publisher) {
        super(context);
        this.context = context;
        this.request_publisher = request_publisher;
        editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
    }

    // ????????? ??????????????? ????????? ????????????.
    public void callFunction() {

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        uid = prefs.getString("uid", "none");

        // ????????? ?????????????????? ?????????????????? Dialog???????????? ????????????.
        final Dialog dlg = new Dialog(context);

        // ??????????????? ??????????????? ?????????.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ????????? ?????????????????? ??????????????? ????????????.
        dlg.setContentView(R.layout.request_dialog);

        // ????????? ?????????????????? ????????????.
        dlg.show();

        // ????????????
        calendar_layout = dlg.findViewById(R.id.calendar_layout);
        stime_layout = dlg.findViewById(R.id.stime_layout);
        etime_layout = dlg.findViewById(R.id.etime_layout);
        request_layout = dlg.findViewById(R.id.request_layout);
        result_layout = dlg.findViewById(R.id.result_layout);

        // ???????????? ???????????? ???
        calendar_view = dlg.findViewById(R.id.calendar_view);
        stime_view = dlg.findViewById(R.id.stime_view);
        etime_view = dlg.findViewById(R.id.etime_view);
        request_view = dlg.findViewById(R.id.request_view);

        // ?????? ????????? ?????? ??????
        calendar_btn = dlg.findViewById(R.id.calendar_btn);
        calendar_back_btn = dlg.findViewById(R.id.calendar_back_btn);
        stime_btn = dlg.findViewById(R.id.stime_btn);
        stime_back_btn = dlg.findViewById(R.id.stime_back_btn);
        etime_btn = dlg.findViewById(R.id.etime_btn);
        etime_back_btn = dlg.findViewById(R.id.etime_back_btn);
        request_btn = dlg.findViewById(R.id.request_btn);
        request_back_btn = dlg.findViewById(R.id.request_back_btn);
        result_btn = dlg.findViewById(R.id.result_btn);
        result_back_btn = dlg.findViewById(R.id.result_back_btn);

        // ?????? ?????? ??????
        result_calendar_view = dlg.findViewById(R.id.result_calendar_view);
        result_stime_view = dlg.findViewById(R.id.result_stime_view);
        result_etime_view = dlg.findViewById(R.id.result_etime_view);
        result_request_view = dlg.findViewById(R.id.result_request_view);

        //anim ????????? ?????????????????? ???????????? ??????
        tranlateToLeftAnim = AnimationUtils.loadAnimation(context,R.anim.toleft);
        tranlateFromLeftAnim = AnimationUtils.loadAnimation(context,R.anim.fromleft);
        tranlateToRightAnim = AnimationUtils.loadAnimation(context,R.anim.toright);
        tranlateFromRightAnim = AnimationUtils.loadAnimation(context,R.anim.fromright);

        //????????? ???????????? ???????????? ??????????????? ?????????????????? ?????? ????????? ?????? ????????? ????????? ??? ??????.
        RequestDialog.SlidingPageAnimationListener animListener = new RequestDialog.SlidingPageAnimationListener();

        // ?????? ??????
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReceiveRequest").child(request_publisher).child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cnt = Long.toString(snapshot.getChildrenCount());
                System.out.println("ReceiveRequestCount : " + cnt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        calendar_view.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int j, int k) {
                request_data = i + " / " + j + " / " + k;
                hashMap.put("requestData", request_data);
                result_calendar_view.setText(request_data);
                stime_layout.setVisibility(View.VISIBLE);
                stime_layout.startAnimation(tranlateToLeftAnim);
                calendar_layout.setVisibility(View.GONE);
                calendar_layout.startAnimation(tranlateFromLeftAnim);
            }
        });

        stime_view.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int j) {
                request_stime = i + " : " + j;
                stime = (i*60)+j;
            }
        });

        etime_view.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int j) {
                request_etime = i + " : " + j;
                etime = (i*60)+j;
            }
        });

        currenttimeformat = new Date();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        currenttime = transFormat.format(currenttimeformat);
        System.out.println(currenttime);

        // ???????????? ?????? ??????
        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!request_data.equals("")) {

                }
            }
        });

        // ?????? (??????????????? ??????)
        calendar_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // ???????????? ?????? ??????
        stime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!request_stime.equals("")) {
                    hashMap.put("requestStime", request_stime);
                    result_stime_view.setText(request_stime);
                    etime_layout.setVisibility(View.VISIBLE);
                    etime_layout.startAnimation(tranlateToLeftAnim);
                    stime_layout.setVisibility(View.GONE);
                    stime_layout.startAnimation(tranlateFromLeftAnim);
                }
            }
        });

        // ?????? ?????? (???????????? ?????? ??????)
        stime_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar_layout.setVisibility(View.VISIBLE);
                calendar_layout.startAnimation(tranlateFromRightAnim);
                stime_layout.setVisibility(View.GONE);
                stime_layout.startAnimation(tranlateToRightAnim);
            }
        });

        // ???????????? ?????? ??????
        etime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!request_etime.equals("")) {
                    if(stime >= etime) {
                        Toast.makeText(context, "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    } else {
                        hashMap.put("requestEtime", request_etime);
                        result_etime_view.setText(request_etime);
                        request_layout.setVisibility(View.VISIBLE);
                        request_layout.startAnimation(tranlateToLeftAnim);
                        etime_layout.setVisibility(View.GONE);
                        etime_layout.startAnimation(tranlateFromLeftAnim);
                    }
                }
            }
        });

        // ?????? ?????? (???????????? ?????? ??????)
        etime_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stime_layout.setVisibility(View.VISIBLE);
                stime_layout.startAnimation(tranlateFromRightAnim);
                etime_layout.setVisibility(View.GONE);
                etime_layout.startAnimation(tranlateToRightAnim);
            }
        });

        // ?????? ?????? ??????
        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request_txt = request_view.getText().toString();

                // ???????????? ????????? ??????
                hashMap.put("requestTxt", request_txt);
                // ???????????? ????????? ??????
                result_request_view.setText(request_txt);
                // ?????? ??????
                result_layout.setVisibility(View.VISIBLE);
                result_layout.startAnimation(tranlateToLeftAnim);
                request_layout.setVisibility(View.GONE);
                request_layout.startAnimation(tranlateFromLeftAnim);
            }
        });

        // ?????? ?????? (???????????? ?????? ??????)
        request_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etime_layout.setVisibility(View.VISIBLE);
                etime_layout.startAnimation(tranlateFromRightAnim);
                request_layout.setVisibility(View.GONE);
                request_layout.startAnimation(tranlateToRightAnim);
            }
        });

        // ????????? ????????? ??????
        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashMap.put("currentTime", currenttime);
                hashMap.put("sendUser", uid);
                hashMap.put("receiveUser", request_publisher);
                // ?????? 0 - ?????????, 1 - ??????, 2 - ??????
                hashMap.put("state", "0");

                // ?????????, ?????? ?????? ????????????
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(request_publisher);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (getContext() == null) {
                            return;
                        }

                        UserModel user = dataSnapshot.getValue(UserModel.class);

                        hashMap.put("major", user.getMajor());
                        hashMap.put("area", user.getArea());
                        hashMap.put("requestCnt", cnt);

                        editor.putString("request_cnt", cnt);
                        editor.apply();

                        sendRequest();
                        receiveRequest();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dlg.dismiss();
            }
        });

        // ??????(????????? ?????? ??????)
        result_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });

    }

    public void sendRequest() {
        // receive request(ReceiveRequest->????????????id->??????id->??????) ??????
        // ?????? ??????
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReceiveRequest").child(request_publisher).child(uid);
        reference.child(cnt).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    System.out.println("???????????? ????????? ????????? ?????? ??????");
                }
            }
        });
    }

    public void receiveRequest() {
        // send request(SendRequest->??????id->????????????id->??????) ??????
        // ??????
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SendRequest").child(uid).child(request_publisher);
        reference.child(cnt).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    System.out.println("?????? ????????? ????????? ?????? ??????");

                    editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("request_publisher", request_publisher);
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,
                            new RequestDetailFragment()).commit();
                }
            }
        });
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
