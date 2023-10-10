package com.lisners.patient.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lisners.patient.ApiModal.CalendarModel;
import com.lisners.patient.ApiModal.SpacializationMedel;
import com.lisners.patient.R;
import com.lisners.patient.zWork.restApi.pojo.bookAppointments.Specialization;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class UtilsFunctions {


    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isValidPass(String email) {
        String emailRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static String errorShow(JSONArray array) {
        try {
            if (array != null) {
                for (int i = 0; i < array.length(); i++)
                    return array.getString(i) + "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return "";
    }

    public static void SetLOGO(Context context, String URL, ImageView imageView) {
        try {
            Glide
                    .with(context)
                    .load(URL)
                    .centerCrop()
                    .placeholder(R.drawable.button_light_primary)
                    .into(imageView);
        } catch (RuntimeException e) {
            e.getStackTrace();
        } catch (Exception e) {
        }
    }

    public static void SetLOGO(Context context, int URL, ImageView imageView) {
        try {
            Glide
                    .with(context)
                    .load(URL)
                    .centerCrop()
                    .placeholder(R.drawable.button_light_primary)
                    .into(imageView);
        } catch (RuntimeException e) {
            e.getStackTrace();
        } catch (Exception e) {
        }
    }

    public static void SetLOGOWithRoundedCorners(Context context, String URL, ImageView imageView,int radius) {
        try {

            Picasso.get()
                    .load(URL)
                    .resize(50,50)
                    .transform(new RoundedCornersTransformation(radius, 0,
                            RoundedCornersTransformation.CornerType.ALL))
                    .placeholder(R.drawable.button_light_primary)
                    .into(imageView);
        } catch (RuntimeException e) {
            e.getStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static String getMonths(int id) {

        if (id == 0) return "Jan";
        else if (id == 1) return "Feb";
        else if (id == 2) return "March";
        else if (id == 3) return "April";
        else if (id == 4) return "May";
        else if (id == 5) return "June";
        else if (id == 6) return "July";
        else if (id == 7) return "Aug";
        else if (id == 8) return "Sep";
        else if (id == 9) return "Oct";
        else if (id == 10) return "Nov";
        else return "Dec";

    }

    public static String getDay(int id) {
        if (id == 0) return "Sun";
        else if (id == 1) return "Mon";
        else if (id == 2) return "Tue";
        else if (id == 3) return "Wed";
        else if (id == 4) return "Thu";
        else if (id == 5) return "Fri";
        else return "Sat";
    }

    public static String convertTime(String time) {
        String format;
        String[] arr = time.split(":");

        int hh = Integer.parseInt(arr[0]);

        if (hh > 12) {
            hh = hh - 12;
            format = "PM";
        } else if (hh == 00) {
            hh = 12;
            format = "AM";
        } else if (hh == 12) {
            hh = 12;
            format = "PM";
        } else {
            format = "AM";
        }

        String hour = String.format("%02d", hh);

        String minute = arr[1];
        String second = arr[2];


        return hour + ":" + minute + " " + format;
    }

    public static String getSpecializeString(ArrayList<SpacializationMedel> dateSt) {
        try {
            String st = "";
            int size = dateSt.size();
            if (size > 2) {
                st = dateSt.get(0).getTitle() + ", " + dateSt.get(1).getTitle() + "... +" + (dateSt.size() - 2);
            } else {
                for (SpacializationMedel medel : dateSt) {
                    if (st.isEmpty())
                        st = medel.getTitle();
                    else
                        st = st + ", " + medel.getTitle();

                }
            }
            return st + " ";
        } catch (Exception e) {
            return "";
        }
    }

    public static String getSpecializeString(List<Specialization> dateSt) {
        try {
            String st = "";
            int size = dateSt.size();
            if (size > 2) {
                st = dateSt.get(0).getTitle() + ", " + dateSt.get(1).getTitle() + "... +" + (dateSt.size() - 2);
            } else {
                for (Specialization medel : dateSt) {
                    if (st.isEmpty())
                        st = medel.getTitle();
                    else
                        st = st + ", " + medel.getTitle();

                }
            }
            return st + " ";
        } catch (Exception e) {
            return "";
        }
    }


    public static String fullDateFormat(String dateSt) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy | HH:mm:aa");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date = format.parse(dateSt);
            return formatter.format(date);
        } catch (Exception e) {
            return dateSt;
        }
    }

    public static String getFullName(String name) {
        try {
            return name.charAt(0) + "".toUpperCase() + name.substring(1, name.length());
        } catch (Exception e) {
            return name;
        }
    }


    public static String dateFormat(String dateSt) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = format.parse(dateSt);
            return formatter.format(date);
        } catch (Exception e) {
            return dateSt;
        }
    }

    public static String timeFormat(String dateSt) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:aa");
            DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            Date date = format.parse(dateSt);
            return formatter.format(date);
        } catch (Exception e) {
            return dateSt;
        }
    }


    public static String getSeconds(String start, String end) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:aa");
            Date stdate = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(start);
            Date stend = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(end);
            return (stend.getHours() - stdate.getHours()) + ":" + (stend.getMinutes() - stdate.getMinutes());
        } catch (Exception e) {

            return "0";
        }
    }

    public static long getDateSeconds(String start) {
        try {
            Log.e("start", start);
            Date stdate = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH).parse(start);
            return stdate.getTime();
        } catch (Exception e) {

            return 0;
        }
    }

    public static long getMillisecondsRAnge(String start, String end) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:aa");
            Date stdate = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(start);
            Date stend = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(end);
            return stend.getTime() - stdate.getTime();
        } catch (Exception e) {

            return 10 * 60 * 1000;
        }
    }

    public static String getFistLastChar(String name) {
        String s = "";
        try {
            String[] strings = name.split(" ");
            for (String st : strings) {
                if (s.isEmpty())
                    s = st.charAt(0) + "";
                else {
                    s = s + st.charAt(0);
                    break;
                }
            }

        } catch (Exception e) {
            return "";
        } finally {
            if (s.isEmpty())
                if (name != null) {
                    return name.charAt(0) + "";
                } else {
                    return "";
                }
            else
                return s.toUpperCase();
        }

    }

    public static String splitCamelCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder converted = new StringBuilder();
        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }

    public static void hideKeyboard(Activity activity) {
        try {
            View v = activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    public static String calculateTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        if (day <= 0)
            return "  H:" + hours + " M:" + minute + " S:" + second + "  ";
        else return "  D:" + day + " H:" + hours + " M:" + minute + " S:" + second + "  ";

    }

    public static String showRatting(String rate) {
        if (rate != null && rate.length() > 3)
            return rate.substring(0, 3);
        return "0";
    }

    public static String showFirstName(String name) {
        if (name != null && name.contains(" "))
            return name.substring(0, name.indexOf(" "));
        return name;
    }

}
