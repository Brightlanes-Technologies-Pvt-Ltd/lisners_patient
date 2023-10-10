package com.lisners.patient.Activity.Home.HomeStack;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.AppointmentModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.CounselorProfile;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.Functions.MySpannable;
import com.lisners.patient.GlobalData;
import com.lisners.patient.R;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.PutApiHandler;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ConsultationDetailsFragment extends Fragment implements View.OnClickListener {
    TextView tvHeader, tv_review_comment_patient, tv_counselor_name,tv_profession, tv_history_date_time, tv_call_amount, tv_counselor_specialize, tv_call_sec, tv_time_schedule, tv_prescription, tv_review, profile_char_name;
    ImageButton btn_header_left;
    EditText edit_text_review_comment;
    ImageView iv_profile;
    Button btn_review;
    DProgressbar dProgressbar;
    RatingBar ratingBar;
    BookedAppointment appointment;
    LinearLayout lay_notes_review;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation_details, container, false);
        init(view);
        return view;
    }

    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

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
                try {
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
                } catch (Exception e) {
                }
            }
        });

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                     final int maxLine, final String spanableText, final boolean viewMore) {
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


    private void init(View view) {
        lay_notes_review = view.findViewById(R.id.lay_notes_review);
        btn_header_left = view.findViewById(R.id.btn_header_left);
        btn_header_left.setImageResource(R.drawable.ic_svg_arrow_right);
        tvHeader = view.findViewById(R.id.tvHeader);
        iv_profile = view.findViewById(R.id.iv_profile);
        tv_counselor_name = view.findViewById(R.id.tv_counselor_name);
        tv_profession = view.findViewById(R.id.tv_profession);
        tv_counselor_specialize = view.findViewById(R.id.tv_counselor_specialize);
        tv_time_schedule = view.findViewById(R.id.tv_time_schedule);
        tv_prescription = view.findViewById(R.id.tv_prescription);
        btn_review = view.findViewById(R.id.btn_review);
        tv_review_comment_patient = view.findViewById(R.id.tv_review_comment_patient);
        edit_text_review_comment = view.findViewById(R.id.edit_text_review_comment);
        ratingBar = view.findViewById(R.id.ratingBar);
        tv_review = view.findViewById(R.id.tv_review);
        profile_char_name = view.findViewById(R.id.profile_char_name);
        tv_history_date_time = view.findViewById(R.id.tv_history_date_time);
        tv_call_sec = view.findViewById(R.id.tv_call_sec);
        tv_call_amount = view.findViewById(R.id.tv_call_amount);

        btn_review.setOnClickListener(this);
        btn_header_left.setOnClickListener(this);
        tvHeader.setText("Consultation Details");
        dProgressbar = new DProgressbar(getContext());
        if (GlobalData.call_appointment != null) {
            fetchProfile();
        } else
            btn_review.setVisibility(View.GONE);


    }

    public void fetchProfile() {
        dProgressbar.show();
        GetApiHandler apiHandler = new GetApiHandler(getContext(), URLs.GET_BOOK_APPOINTMENT_DETAILS + "/" + GlobalData.call_appointment.getId(), new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                Log.e("jsonObject", new Gson().toJson(jsonObject));
                dProgressbar.dismiss();
                if (jsonObject.has("status")) {
                    appointment = new Gson().fromJson(jsonObject.getString("data"), BookedAppointment.class);
                    setDetails();

                } else
                    setDetails();
            }

            @Override
            public void onError() {
                dProgressbar.dismiss();
                setDetails();
            }
        });
        apiHandler.execute();
    }

    public void setDetails() {
        if (appointment == null)
            appointment = GlobalData.call_appointment;
        if (appointment.getAppointment_detail() == null || (appointment.getRating() != null && !appointment.getRating().equals("0.0") && !appointment.getRating().equals("0"))) {
            tv_review.setText("( " + appointment.getRating() + " )");
            //btn_review.setVisibility(View.GONE);
            ratingBar.setRating(Float.valueOf(appointment.getRating()));
            tv_review_comment_patient.setText((appointment.getComment() == null ? "" : appointment.getComment()) + "     ");
            edit_text_review_comment.setText((appointment.getComment() == null ? "" : appointment.getComment()) + "     ");

            if (appointment.getComment() == null || appointment.getComment().trim().isEmpty()) {
                lay_notes_review.setVisibility(View.GONE);
            } else {
                lay_notes_review.setVisibility(View.VISIBLE);
            }
            edit_text_review_comment.setVisibility(View.GONE);
            makeTextViewResizable(tv_review_comment_patient, 3, "More >>", true);
            edit_text_review_comment.setEnabled(false);
            //ratingBar.setEnabled(false);

        } else {
            //btn_review.setVisibility(View.VISIBLE);
            tv_review_comment_patient.setVisibility(View.GONE);
            ratingBar.setRating(1);
            tv_review.setText("( " + appointment.getRating() + " )");
            ratingBar.setRating(Float.valueOf(appointment.getRating()));


        }


        tv_history_date_time.setText(DateUtil.dateFormatter(appointment.getDate(), "dd-MM-yyyy", "dd MMM yyyy"));
        User counselors = appointment.getCounselor();
        ArrayList<SpacializationMedel> list = appointment.getSpecialization();
        AppointmentModel appointmentModel = appointment.getAppointment_detail();

        if (appointmentModel != null) {
            tv_time_schedule.setText(String.format("%s - %s", DateUtil.dateFormatter(appointmentModel.getStart_time(), "HH:mm:ss", "hh:mm a"), DateUtil.dateFormatter(appointmentModel.getEnd_time(), "HH:mm:ss", "hh:mm a")));
        } else {
            tv_time_schedule.setText("");
        }


        if (appointment.getIs_promotional() == 0) {
            tv_call_amount.setText("₹ " + (appointment.getTotal_amount() == null ? "" : appointment.getTotal_amount()));
        } else {
            tv_call_amount.setText("Free appointment");
        }

        if (counselors != null) {
            tv_counselor_name.setText(UtilsFunctions.getFullName(counselors.getName()));

            if (counselors.getCounselor_profile().getProfession() != null) {
                tv_profession.setText(counselors.getCounselor_profile().getProfession().getTitle());
            }

            CounselorProfile counselorProfile = counselors.getCounselor_profile();
            if (counselorProfile != null) {
                tv_prescription.setText(appointment.getPrescriprion() == null ? "" : appointment.getPrescriprion());
                makeTextViewResizable(tv_prescription, 3, "More >>", true);
            }
            if (counselors.getProfile_image() != null ) {
                UtilsFunctions.SetLOGO(getContext(), counselors.getProfile_image(), iv_profile);
                profile_char_name.setVisibility(View.GONE);
            } else
                profile_char_name.setText(UtilsFunctions.getFistLastChar(counselors.getName()));

        }
        if (appointment.getCall_time() != null)
            tv_call_sec.setText(appointment.getCall_time() + "sec");

        tv_counselor_specialize.setText(appointment.getSpecialization_name());
        /*if (list != null) {
            for (SpacializationMedel s : list) {
                if (tv_counselor_specialize.getText().toString().isEmpty())
                    tv_counselor_specialize.setText(s.getTitle());
                else
                    tv_counselor_specialize.setText(tv_counselor_specialize.getText() + ", " + s.getTitle());
            }
        }*/
    }

    public void updateReview() {
        if (ratingBar.getRating() <= 0)
            Toast.makeText(getContext(), "Add your Ratting", Toast.LENGTH_SHORT).show();
        else {
            String review_comment = edit_text_review_comment.getText().toString();
            String rating = String.valueOf(ratingBar.getRating());
            Map<String, String> params = new HashMap<>();
            params.put("rating", rating);
            params.put("comment", review_comment);
            params.put("_method", "put");
            dProgressbar.show();

            PutApiHandler putApiHandler = new PutApiHandler(getContext(), URLs.GET_APPOINTMENT_REVIEW + "/" + GlobalData.call_appointment.getId(), params, new PutApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    dProgressbar.dismiss();
                    if (jsonObject.has("message"))
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    if (jsonObject.has("status"))
                        getFragmentManager().popBackStack();

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

            putApiHandler.execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_header_left:
                getFragmentManager().popBackStack();
                break;
            case R.id.btn_review:
                updateReview();
        }
    }
}