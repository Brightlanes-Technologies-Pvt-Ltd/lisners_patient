package com.lisners.patient.Activity.Home.AppointmentStack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.Adaptors.AvailabilityAdaptor;
import com.lisners.patient.Adaptors.CalendarAdaptor;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.CalendarModel;
import com.lisners.patient.ApiModal.CounselorProfile;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.Functions.MySpannable;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.databinding.FragmentAppointmentProfileBinding;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.call.AgoraVideoCallActivity;
import com.lisners.patient.zWork.restApi.pojo.timeSlot.TimeSlot;
import com.lisners.patient.zWork.restApi.viewmodel.ContentsViewModel;
import com.lisners.patient.zWork.utils.DialogUtil;
import com.lisners.patient.zWork.utils.TimeUtils;
import com.lisners.patient.zWork.utils.ViewModelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;


public class AppointmentProfileFragment extends Fragment implements View.OnClickListener {
    /*RelativeLayout layout_total_amount;*/
    /*TextView tv_total_amount;*/
    RecyclerView rv_time, rv_dates;

    TextView tvHeader, tv_address, tv_availability, tv_short_name, tv_user_name, tv_specialize, tv_ratting, tv_about, tv_phone_rate, tv_video_rate;
    ImageButton btn_header_left, ib_favourite;
    Button btn_call, btn_book_new, btn_live_call;
    ArrayList<CalendarModel> calendarModels;
    ImageView iv_left_side, iv_right_side, iv_user_profile;
    View v_live_status;
    EditText edit_notes;
    RadioGroup rgGender;
    RadioButton rb_phone, rb_video;
    DProgressbar dProgressbar;

    CalendarAdaptor calendarAdaptor;
    List<Date> dates;
    Date cdate, newDate, stdate;
    TimeSlot selectedSlot;
    String call_type, call_value;

    CounselorProfile counselorProfile;

    ContentsViewModel contentsVM;

    DialogUtil dialogUtil;
    AlertDialog successDialog;

    SpacializationMedel category;

    IntentFilter filter = new IntentFilter("online_event");
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("smsBroadcastReceiver", "onReceive");
            if (intent != null) {
                Log.e("rec===?>", "" + intent.getStringExtra("data"));

                JsonArray array = new Gson().fromJson(intent.getStringExtra("data"), JsonArray.class);
                JsonObject jsonObject = array.get(0).getAsJsonObject().get("nameValuePairs").getAsJsonObject();
                Log.e("rec===?>", "" + jsonObject.has("data"));
                if (jsonObject.has("data")) {
                    JsonObject object = jsonObject.getAsJsonObject("data").getAsJsonObject("nameValuePairs");
                    int onlineStatus = object.get("status").getAsInt();
                    int userId = object.get("user_id").getAsInt();
                    if (userId == GlobalData.bookAppointmentTherapist.getId()) {
                        if (onlineStatus == 1) {
                            v_live_status.setVisibility(View.VISIBLE);
                        } else {
                            v_live_status.setVisibility(View.GONE);
                        }

                        if (v_live_status.getVisibility() == View.VISIBLE) {
                            if (rb_phone.isChecked() || rb_video.isChecked())
                                btn_live_call.setVisibility(View.VISIBLE);
                            else
                                btn_live_call.setVisibility(View.GONE);
                        } else {
                            btn_live_call.setVisibility(View.GONE);
                        }
                    }

                }

            }
        }
    };

    FragmentAppointmentProfileBinding binding;
    List<SpacializationMedel> spCat_List = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_appointment_profile, container, false);
        binding = (FragmentAppointmentProfileBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_appointment_profile, container, false);

        init(binding.getRoot());
        getDateRange();
        fatchProfile();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentsVM = ViewModelUtils.getViewModel(ContentsViewModel.class, this);
        dialogUtil = new DialogUtil(getActivity());


        Bundle bundle = getArguments();

        SpacializationMedel previous_category = null;
        try {
            previous_category = bundle.getParcelable("category");
        } catch (
                Exception e) {
            e.printStackTrace();
        }


        binding.laySpecializationCategory.setVisibility(View.VISIBLE);
        binding.spnCategory.setSpinnerData(new ArrayList<>());

        binding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Timber.e("Class : %d", position);
                binding.tiCategory.setError(null);
                category = spCat_List.get(binding.spnCategory.getBinding().spnMain.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spCat_List.clear();
        spCat_List.addAll(GlobalData.bookAppointmentTherapist.getSpecialization());
        SpacializationMedel spacializationMedel = new SpacializationMedel(0, "--select option--", "");
        spCat_List.add(0, spacializationMedel);

        List<String> spinnerList = new ArrayList<>();
        for (int i = 0; i < spCat_List.size(); i++) {
            spinnerList.add(spCat_List.get(i).getTitle());
        }
        binding.spnCategory.setSpinnerData(spinnerList);


        if (!(previous_category == null) && !previous_category.getTitle().equalsIgnoreCase("")) {
            int spinnerPosition = spinnerList.indexOf(previous_category.getTitle());
            binding.spnCategory.getBinding().spnMain.setSelection(spinnerPosition);
        }


    }

    public void init(View view) {
        /*layout_total_amount = view.findViewById(R.id.layout_total_amount);
        tv_total_amount = view.findViewById(R.id.tv_total_amount);*/


        tvHeader = view.findViewById(R.id.tvHeader);
        btn_header_left = view.findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_arrow_right);
        iv_left_side = view.findViewById(R.id.iv_left_side);
        iv_right_side = view.findViewById(R.id.iv_right_side);
        rv_dates = view.findViewById(R.id.rv_dates);
        rb_phone = view.findViewById(R.id.rb_phone);
        rb_video = view.findViewById(R.id.rb_video);
        edit_notes = view.findViewById(R.id.edit_notes);
        iv_user_profile = view.findViewById(R.id.iv_profile);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        tv_specialize = view.findViewById(R.id.tv_specialize);
        tv_ratting = view.findViewById(R.id.tv_ratting);
        rgGender = view.findViewById(R.id.rgGender);
        tv_about = view.findViewById(R.id.tv_about);
        tv_phone_rate = view.findViewById(R.id.tv_phone_rate);
        tv_video_rate = view.findViewById(R.id.tv_video_rate);
        v_live_status = view.findViewById(R.id.v_live_status);
        btn_live_call = view.findViewById(R.id.btn_live_call);
        tv_availability = view.findViewById(R.id.tv_availability);
        tv_address = view.findViewById(R.id.tv_address);
        ib_favourite = view.findViewById(R.id.ib_favourite);
        tv_short_name = view.findViewById(R.id.tv_short_name);

        btn_live_call.setOnClickListener(this);
        counselorProfile = new CounselorProfile();
        iv_left_side.setOnClickListener(this);
        iv_right_side.setOnClickListener(this);
        btn_header_left.setOnClickListener(this);
        ib_favourite.setOnClickListener(this);
        tvHeader.setText("Appointment details");
        dProgressbar = new DProgressbar(getContext());

        btn_book_new = view.findViewById(R.id.btn_book_new);
        btn_book_new.setOnClickListener(this);
        calendarModels = new ArrayList<>();

        Calendar starttime = Calendar.getInstance();
        starttime.add(Calendar.MONTH, -6);

        Calendar endtime = Calendar.getInstance();
        endtime.add(Calendar.MONTH, 6);

        rv_time = view.findViewById(R.id.rv_time);
        /*GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);*/

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);

        rv_time.setLayoutManager(layoutManager);
        cdate = new Date();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);

        dates = new ArrayList<>();
        calendarAdaptor = new CalendarAdaptor(getContext(), new CalendarAdaptor.OnClickListener() {
            @Override
            public void onClick(Date date, int pos) {
                stdate = date;
                if (GlobalData.bookAppointmentTherapist != null) {
                    getTimeSlots(date);
                }
            }
        });

        rb_phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (selectedSlot != null) {
                        /*tv_total_amount.setText("₹ " + (counselorProfile.getVoice_call() != null ? Double.parseDouble(counselorProfile.getVoice_call()) * TimeUtils.getTimeDifferenceInMinutes("HH:mm:ss", selectedSlot.getStartTime(), selectedSlot.getEndTime()) + "/ session" : ""));
                        layout_total_amount.setVisibility(View.VISIBLE);*/
                    }
                    call_type = "voice";
                    handlerRadioClick(isChecked);
                }
            }
        });

        rb_video.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (selectedSlot != null) {
                        /*tv_total_amount.setText("₹ " + (counselorProfile.getVideo_call() != null ? Double.parseDouble(counselorProfile.getVideo_call()) * TimeUtils.getTimeDifferenceInMinutes("HH:mm:ss", selectedSlot.getStartTime(), selectedSlot.getEndTime()) + "/ session" : ""));
                        layout_total_amount.setVisibility(View.VISIBLE);*/
                    }

                    call_type = "video";
                    handlerRadioClick(isChecked);
                }
            }
        });

        rv_dates.setLayoutManager(linearLayoutManager);
        rv_dates.setAdapter(calendarAdaptor);

    }

    public void getTimeSlots(Date date) {
        String day = String.valueOf(date.getDay());
        String counselor_id = GlobalData.bookAppointmentTherapist.getId() + "";


        hideTimeSlotMessage();
        dProgressbar.show();
        contentsVM
                .getTimeSlots(counselor_id, day)
                .observe(getViewLifecycleOwner(), response -> {
                    dProgressbar.dismiss();

                    if (response.getStatus() && response.getData() != null && response.getData().getTimeSlot() != null && response.getData().getTimeSlot().size() > 0) {

                        AvailabilityAdaptor availabilityAdaptor = new AvailabilityAdaptor(getContext(), date, response.getData().getTimeSlot(), new AvailabilityAdaptor.OnClickListener() {
                            @Override
                            public void click(TimeSlot timeSlot, int poss) {
                                selectedSlot = timeSlot;
                                /*if (rb_phone.isChecked()) {
                                    if (selectedSlot != null) {
                                        tv_total_amount.setText("₹ " + (counselorProfile.getVoice_call() != null ? Double.parseDouble(counselorProfile.getVoice_call()) * TimeUtils.getTimeDifferenceInMinutes("HH:mm:ss", selectedSlot.getStartTime(), selectedSlot.getEndTime()) + "/ session" : ""));
                                        layout_total_amount.setVisibility(View.VISIBLE);
                                    }
                                } else if (rb_video.isChecked()) {
                                    if (selectedSlot != null) {
                                        tv_total_amount.setText("₹ " + (counselorProfile.getVideo_call() != null ? Double.parseDouble(counselorProfile.getVideo_call()) * TimeUtils.getTimeDifferenceInMinutes("HH:mm:ss", selectedSlot.getStartTime(), selectedSlot.getEndTime()) + "/ session" : ""));
                                        layout_total_amount.setVisibility(View.VISIBLE);
                                    }
                                }*/
                            }
                        });
                        rv_time.setAdapter(availabilityAdaptor);

                    } else {
                        if (response.getErrorStatus() > 0)
                            showTimeSlotMessage(response.getMessage());
                        else
                            showTimeSlotMessage("No slots available");
                    }

                });
    }

    private void hideTimeSlotMessage() {
        tv_availability.setVisibility(View.GONE);
        tv_availability.setText("");
        tv_availability.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
    }

    private void showTimeSlotMessage(String message) {
        selectedSlot = null;
        if ((rv_time.getAdapter() != null)) {
            ((AvailabilityAdaptor) rv_time.getAdapter()).getList().clear();
            ((AvailabilityAdaptor) rv_time.getAdapter()).notifyDataSetChanged();
        }

        tv_availability.setVisibility(View.VISIBLE);
        tv_availability.setText(message);
        tv_availability.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
    }

    public void handlerRadioClick(boolean isChecked) {
        if (call_type.equals("voice")) {
            rb_phone.setChecked(isChecked);
            rb_video.setChecked(!isChecked);

        } else if (call_type.equals("video")) {
            rb_phone.setChecked(!isChecked);
            rb_video.setChecked(isChecked);
        }
        if (v_live_status.getVisibility() == View.VISIBLE) {
            btn_live_call.setVisibility(View.VISIBLE);
        } else {
            btn_live_call.setVisibility(View.GONE);
        }
    }

    public void fatchProfile() {

        if (GlobalData.bookAppointmentTherapist != null) {

            ArrayList<SpacializationMedel> list = GlobalData.bookAppointmentTherapist.getSpecialization();
            counselorProfile = GlobalData.bookAppointmentTherapist.getCounselor_profile();
            tv_user_name.setText(UtilsFunctions.splitCamelCase(GlobalData.bookAppointmentTherapist.getName()));
            if (GlobalData.bookAppointmentTherapist.getIs_online() == 1)
                v_live_status.setVisibility(View.VISIBLE);
            else
                v_live_status.setVisibility(View.GONE);

            if (GlobalData.bookAppointmentTherapist.getProfile_image() != null ){
                UtilsFunctions.SetLOGO(getContext(), GlobalData.bookAppointmentTherapist.getProfile_image(), iv_user_profile);
                tv_short_name.setVisibility(View.GONE);
            } else {
                tv_short_name.setText(UtilsFunctions.getFistLastChar(GlobalData.bookAppointmentTherapist.getName()));
                tv_short_name.setVisibility(View.VISIBLE);
            }

            tv_ratting.setText(UtilsFunctions.showRatting(GlobalData.bookAppointmentTherapist.getAvg_rating()));
            if (GlobalData.bookAppointmentTherapist.getIs_saved() == 1)
                ib_favourite.setImageResource(R.drawable.ic_svg_heart_red);
            else
                ib_favourite.setImageResource(R.drawable.ic_svg_heart);


            if (list != null) {
                for (SpacializationMedel s : list) {
                    if (tv_specialize.getText().toString().isEmpty())
                        tv_specialize.setText(s.getTitle());
                    else
                        tv_specialize.setText(tv_specialize.getText() + ", " + s.getTitle());
                }
                tv_ratting.setText(UtilsFunctions.showRatting(GlobalData.bookAppointmentTherapist.getAvg_rating()));
            }

            if (counselorProfile.getProfession() != null) {
                binding.tvProfession.setText(counselorProfile.getProfession().getTitle());
            }

            if (counselorProfile != null) {
                tv_about.setText(counselorProfile.getDescription());
                /*tv_video_rate.setText("₹ " + (counselorProfile.getVideo_call() != null ? Double.parseDouble(counselorProfile.getVideo_call()) * counselorProfile.getDefault_duration() + "/ session" : ""));
                tv_phone_rate.setText("₹ " + (counselorProfile.getVoice_call() != null ? Double.parseDouble(counselorProfile.getVoice_call()) * counselorProfile.getDefault_duration() + "/ session" : ""));*/
                tv_video_rate.setText("₹ " + (counselorProfile.getVideo_call() != null ? Double.parseDouble(counselorProfile.getVideo_call()) + "/ session" : ""));
                tv_phone_rate.setText("₹ " + (counselorProfile.getVoice_call() != null ? Double.parseDouble(counselorProfile.getVoice_call()) + "/ session" : ""));

                tv_address.setText(GlobalData.bookAppointmentTherapist.getCity());
                makeTextViewResizable(tv_about, 3, "More >>", true);
            } else
                rgGender.setVisibility(View.GONE);

        }
    }


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (tv.getLineCount() <= maxLine) {

                } else if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "<< See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
//                        tv.setTextColor(Color.parseColor("#FF0000"));
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "More >>", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }


    public void getDateRange() {
        newDate = new Date(cdate.getTime());
        newDate.setDate(newDate.getDate() + 30);
        Log.e("newDate", newDate + " " + cdate);
        dates.addAll(UtilsFunctions.getDaysBetweenDates(cdate, newDate));
        calendarAdaptor.updateList(dates);
    }

    /*private void getAvailabilityTimes(Date date) {
        int day = date.getDay();
        if (GlobalData.bookAppointmentTherapist != null) {
            Map<String, String> params = new HashMap<>();
            params.put("counselor_id", GlobalData.bookAppointmentTherapist.getId() + "");
            params.put("week_day_id", day + "");

            dProgressbar.show();

            PostApiHandler postApiHandler = new PostApiHandler(getContext(), URLs.GET_APPOINTMENT_DATE, params, new PostApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    dProgressbar.dismiss();
                    Log.e("sds", params.get("counselor_id") + " " + new Gson().toJson(jsonObject));
                    if (jsonObject.has("status") && jsonObject.has("data")) {
                        TherapistDayTimeSlot dayTimeSlot = new Gson().fromJson(jsonObject.getString("data"), TherapistDayTimeSlot.class);
                        AvailabilityAdaptor availabilityAdaptor = new AvailabilityAdaptor(getContext(), dayTimeSlot.getTime_slot(), new AvailabilityAdaptor.OnClickListener() {
                            @Override
                            public void click(TimeSlot timeSlot, int poss) {
                                selectedSlot = timeSlot;
                            }
                        });
                        rv_time.setAdapter(availabilityAdaptor);
                        if (dayTimeSlot.getDay() != null && dayTimeSlot.getDay().length() > 0) {
                            tv_availability.setVisibility(View.GONE);
                            rv_time.setVisibility(View.VISIBLE);
                        } else {
                            tv_availability.setVisibility(View.VISIBLE);
                            rv_time.setVisibility(View.GONE);
                        }
                    } else {
                        tv_availability.setVisibility(View.VISIBLE);
                        rv_time.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(ANError anError) {
                    dProgressbar.dismiss();
                    tv_availability.setVisibility(View.VISIBLE);
                    rv_time.setVisibility(View.GONE);
                }
            });
            postApiHandler.execute();
        }
    }*/

    private void call() {
        String uuid = UUID.randomUUID().toString();
        String dateString = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (stdate == null)
            Toast.makeText(getContext(), "Select Day", Toast.LENGTH_SHORT).show();
        else if (selectedSlot == null)
            Toast.makeText(getContext(), "Select Time Slot", Toast.LENGTH_SHORT).show();
        else if (call_type == null)
            Toast.makeText(getContext(), "Select Call Type", Toast.LENGTH_SHORT).show();


        else {

            if (TimeUtils.isCurrentDateAndGivenDateSame(stdate) && !TimeUtils.isCurrentTimeBeforeGivenTime(selectedSlot.getStartTime(), "HH:mm:ss")) {
                Toast.makeText(getContext(), "This slot is not available. Please choose another one", Toast.LENGTH_SHORT).show();

            } else {

                String callvalue = call_type == "voice" ? counselorProfile.getVoice_call() : counselorProfile.getVideo_call();
                dateString = dateFormat.format(stdate);
                String notes = edit_notes.getText().toString();
                Map<String, String> params = new HashMap<>();
                params.put("date", dateString);
                params.put("appointment_detail_id", selectedSlot.getId() + "");
                params.put("call_type", call_type);
                params.put("call_rate", callvalue);
                params.put("add_notes", notes);
                params.put("chenal_code", uuid);
                params.put("counselor_id", GlobalData.bookAppointmentTherapist.getId() + "");
                dProgressbar.show();

                PostApiHandler postApiHandler = new PostApiHandler(getContext(), URLs.GET_APPOINTMENT_SAVE, params, new PostApiHandler.OnClickListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) throws JSONException {
                        Log.e("jsonObject", new Gson().toJson(jsonObject));
                        dProgressbar.dismiss();
                        if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new BookedAppointmentFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                        if (jsonObject.has("message"))
                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        // getFragmentManager().popBackStack();
                        if (jsonObject.has("errors")) {
                            JSONObject errObj = jsonObject.getJSONObject("errors");
                            for (Iterator<String> it = errObj.keys(); it.hasNext(); ) {
                                String key = it.next();
                                if (key != null)
                                    Toast.makeText(getContext(), UtilsFunctions.errorShow(errObj.getJSONArray(key)), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        dProgressbar.dismiss();
                        if (error.getErrorBody() != null) {
                            APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                            if (apiErrorModel.getMessage() != null)
                                Toast.makeText(getContext(), apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                postApiHandler.execute();
            }
        }
    }


    void bookAppointment() {

        String uuid = UUID.randomUUID().toString();
        String dateString = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (stdate == null)
            Toast.makeText(getContext(), "Select Day", Toast.LENGTH_SHORT).show();
        else if (selectedSlot == null)
            Toast.makeText(getContext(), "Select Time Slot", Toast.LENGTH_SHORT).show();
        else if (call_type == null)
            Toast.makeText(getContext(), "Select Call Type", Toast.LENGTH_SHORT).show();

        else if (edit_notes.getText().toString().trim().isEmpty())
            Toast.makeText(getContext(), "The Note field is required.", Toast.LENGTH_SHORT).show();
        else if (category == null) {
            Toast.makeText(getContext(), "Please select a specialization", Toast.LENGTH_SHORT).show();
            binding.tiCategory.setError("Please select a specialization");
        } else if (category.getId() == 0) {
            Toast.makeText(getContext(), "Please select a specialization", Toast.LENGTH_SHORT).show();
            binding.tiCategory.setError("Please select a specialization");
        } else {

            if (TimeUtils.isCurrentDateAndGivenDateSame(stdate) && !TimeUtils.isCurrentTimeBeforeGivenTime(selectedSlot.getStartTime(), "HH:mm:ss")) {
                Toast.makeText(getContext(), "This slot timing is expired", Toast.LENGTH_SHORT).show();

            } else {

                String callvalue = call_type == "voice" ? counselorProfile.getVoice_call() : counselorProfile.getVideo_call();
                dateString = dateFormat.format(stdate);
                String notes = edit_notes.getText().toString();
                Map<String, String> params = new HashMap<>();
                params.put("date", dateString);
                params.put("appointment_detail_id", selectedSlot.getId() + "");
                params.put("call_type", call_type);
                params.put("call_rate", callvalue);
                params.put("add_notes", notes);
                params.put("specialization_id", "" + category.getId());
                params.put("chenal_code", uuid);
                params.put("counselor_id", GlobalData.bookAppointmentTherapist.getId() + "");


                dProgressbar.show();

                contentsVM
                        .bookAppointments(params)
                        .observe(getViewLifecycleOwner(), response -> {
                            dProgressbar.dismiss();

                            if (response.getStatus() && response.getData() != null) {


                                /*FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, new AppointmentFragment());
                                transaction.addToBackStack(null);
                                transaction.commit();*/


                                successDialog = dialogUtil.createBookAppointmentSuccessDialog("Appointment booked", response.getMessage(), response.getData(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        successDialog.cancel();

                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.fragment_container, new BookedAppointmentFragment());
                                        transaction.addToBackStack(null);
                                        transaction.commit();


                                    }
                                });
                                successDialog.show();

                            } else {
                                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });

            }
        }
    }

    private void onCallBoxShow() {
        String uuid = UUID.randomUUID().toString();
        String callvalue = (call_type.equals("voice")) ? counselorProfile.getVoice_call() : counselorProfile.getVideo_call();
        String notes = edit_notes.getText().toString();
        dProgressbar.show();
        Map<String, String> params = new HashMap<>();
        params.put("call_type", call_type);
        params.put("call_rate", callvalue);
        params.put("add_notes", notes);
        params.put("chenal_code", uuid);
        params.put("specialization_id", "" + category.getId());
        params.put("counselor_id", GlobalData.bookAppointmentTherapist.getId() + "");
        Log.e("params", new Gson().toJson(params));


        contentsVM
                .callNow(params)
                .observe(getViewLifecycleOwner(), response -> {
                    dProgressbar.dismiss();

                    if (response.getStatus() && response.getData() != null) {

                        ((HomeActivity) getContext()).getSetting();
                        GlobalData.call_appointment = response.getData();
                        //Intent intent = new Intent(getContext(), VideoCallScreen.class);
                        Intent intent = AgoraVideoCallActivity.makeIntent(getContext(), GlobalData.call_appointment);
                        startActivityForResult(intent, ConstantValues.REQUEST_CALL);

                    } else {
                        Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                });


        /*PostApiHandler apiHandler = new PostApiHandler(getContext(), URLs.STORE_CALL_NOW, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                dProgressbar.dismiss();
                if (jsonObject.has("status") && jsonObject.has("data")) {
                    ((HomeActivity) getContext()).getSetting();
                    GlobalData.call_appointment = new Gson().fromJson(jsonObject.getString("data"), BookedAppointment.class);
                    //Intent intent = new Intent(getContext(), VideoCallScreen.class);
                    Intent intent = PlaceVideoCallActivity.makeIntent(getContext(),GlobalData.call_appointment);
                    startActivityForResult(intent, ConstantValues.REQUEST_CALL);
                } else if (jsonObject.has("errors")) {
                    JSONObject errObj = jsonObject.getJSONObject("errors");
                    for (Iterator<String> it = errObj.keys(); it.hasNext(); ) {
                        String key = it.next();
                        if (key != null)
                            Toast.makeText(getContext(), UtilsFunctions.errorShow(errObj.getJSONArray(key)), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(ANError error) {
                dProgressbar.dismiss();
                if (error.getErrorBody() != null) {
                    APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                    if (apiErrorModel.getMessage() != null)
                        Toast.makeText(getContext(), apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
        apiHandler.execute();*/
    }


    public void saveFav() {
        Map<String, String> params = new HashMap<>();
        params.put("counselor_id", GlobalData.bookAppointmentTherapist.getId() + "");

        PostApiHandler postApiHandler = new PostApiHandler(getContext(), URLs.GET__FAV_SAVE, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                favApiCall = true;
                if (jsonObject.has("status")) {
                    {

                        GlobalData.bookAppointmentTherapist.set_Save("1");
                        ib_favourite.setImageResource(R.drawable.ic_svg_heart_red);
                    }

                }
                if (jsonObject.has("message"))
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(ANError error) {
                favApiCall = true;
                if (error.getErrorBody() != null) {
                    APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                    if (apiErrorModel.getMessage() != null)
                        Toast.makeText(getContext(), apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
        postApiHandler.execute();
    }


    public void deleteFav() {
        Map<String, String> params = new HashMap<>();
        params.put("counselor_id", GlobalData.bookAppointmentTherapist.getId() + "");
        PostApiHandler postApiHandler = new PostApiHandler(getContext(), URLs.GET_DELETE_FAV, params, new PostApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                favApiCall = true;
                if (jsonObject.has("status")) {
                    GlobalData.bookAppointmentTherapist.set_Save("0");
                    ib_favourite.setImageResource(R.drawable.ic_svg_heart);
                }
                if (jsonObject.has("message"))
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(ANError error) {
                favApiCall = true;
                if (error.getErrorBody() != null) {
                    APIErrorModel apiErrorModel = new Gson().fromJson(error.getErrorBody(), APIErrorModel.class);
                    if (apiErrorModel.getMessage() != null)
                        Toast.makeText(getContext(), apiErrorModel.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
        postApiHandler.execute();
    }

    boolean favApiCall = true;

    private void isSaveItem() {
        if (favApiCall) {
            favApiCall = false;
            if (GlobalData.bookAppointmentTherapist.getIs_saved() == 1)
                deleteFav();
            else
                saveFav();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_live_call:
                if (category == null) {
                    Toast.makeText(getContext(), "Please select a specialization", Toast.LENGTH_SHORT).show();
                    binding.tiCategory.setError("Please select a specialization");
                } else if (category.getId() == 0) {
                    Toast.makeText(getContext(), "Please select a specialization", Toast.LENGTH_SHORT).show();
                    binding.tiCategory.setError("Please select a specialization");
                } else {

                    CanfirmCallDailog canfirmCallDailog = new CanfirmCallDailog(getContext(), new CanfirmCallDailog.OnClickLister() {
                        @Override
                        public void onClick() {
                            if (call_type == null)
                                Toast.makeText(getContext(), "Select Call Type", Toast.LENGTH_SHORT).show();
                            else {
                                onCallBoxShow();
                            }
                        }
                    });
                    canfirmCallDailog.show();
                }
                break;
            case R.id.btn_header_left:
                getFragmentManager().popBackStack();
                break;
            case R.id.btn_book_new:
                bookAppointment();
                break;
            case R.id.ib_favourite:
                isSaveItem();
                break;
            case R.id.iv_left_side:
                if (0 < calendarAdaptor.index) {
                    calendarAdaptor.index = calendarAdaptor.index - 1;
                    calendarAdaptor.notifyDataSetChanged();
                    rv_dates.post(new Runnable() {
                        @Override
                        public void run() {
                            rv_dates.scrollToPosition(calendarAdaptor.index);
                            getTimeSlots(dates.get(calendarAdaptor.index));
                        }
                    });

                }


                break;
            case R.id.iv_right_side:
                if (calendarAdaptor.index < calendarAdaptor.getItemCount() - 1) {
                    calendarAdaptor.index = calendarAdaptor.index + 1;
                    calendarAdaptor.notifyDataSetChanged();
                    rv_dates.post(new Runnable() {
                        @Override
                        public void run() {
                            rv_dates.scrollToPosition(calendarAdaptor.index);
                            getTimeSlots(dates.get(calendarAdaptor.index));
                        }
                    });
                }

                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }


}