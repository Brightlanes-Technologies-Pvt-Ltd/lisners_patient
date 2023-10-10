package com.lisners.patient.Activity.Home.ProfileStack;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lisners.patient.Activity.Auth.LoginActivity;
import com.lisners.patient.Activity.Auth.SignUpFormActivity;
import com.lisners.patient.Activity.Home.HomeActivity;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.R;
import com.lisners.patient.utils.ActivityResultBus;
import com.lisners.patient.utils.ActivityResultEvent;
import com.lisners.patient.utils.ConstantValues;
import com.lisners.patient.utils.DProgressbar;
import com.lisners.patient.utils.GetApiHandler;
import com.lisners.patient.utils.PostApiHandler;
import com.lisners.patient.utils.StoreData;
import com.lisners.patient.utils.URLs;
import com.lisners.patient.utils.UtilsFunctions;
import com.lisners.patient.zWork.restApi.viewmodel.LoginViewModel;
import com.lisners.patient.zWork.utils.ViewModelUtils;
import com.lisners.patient.zWork.utils.config.AppConfig;
import com.lisners.patient.zWork.utils.helperClasses.ImageSelectorHelper;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;


public class EditProfileFragment extends Fragment implements View.OnClickListener {
    TextView tvHeader, tv_profile_name, tv_short_name,version;
    ImageButton btn_header_left;
    RecyclerView rv_spacilization;
    String[] strings = new String[]{"Health", "Relationship"};
    RadioButton rdGenderMale, rdGenderFemale, rdGenderOther;
    EditText edit_name, edit_address, ed_gender;
    TextInputLayout np_name, np_address;
    Button btn_update_profile;

    String CP = Manifest.permission.CAMERA;
    String SP = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    List<String> PERMISSIONS = new ArrayList<>();
    File FileImagePath;
    ImageView iv_profile, iv_edit_icon;
    String gender = "";
    DProgressbar dProgressbar;
    StoreData storeData;


    ImageSelectorHelper userImageSelectorHelper;
    LoginViewModel loginVM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        storeData = new StoreData(getContext());
        init(view);
        return view;
    }

    private void init(View view) {
        loginVM = ViewModelUtils.getViewModel(LoginViewModel.class, this);

        userImageSelectorHelper = new ImageSelectorHelper(this, getContext(), getActivity(), new ImageSelectorHelper.ImageSelectorListener() {
            @Override
            public void onImageGet(Bitmap imageBitmap, File imageFile) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        //Glide.with(getActivity()).load(imageBitmap).into(iv_profile);
                        updateUserImage(imageFile);

                    }
                });

            }
        });
        version = view.findViewById(R.id.version);
        ed_gender = view.findViewById(R.id.ed_gender);
        tvHeader = view.findViewById(R.id.tvHeader);
        rdGenderMale = view.findViewById(R.id.rdGenderMale);
        rdGenderFemale = view.findViewById(R.id.rdGenderFemale);
        rdGenderOther = view.findViewById(R.id.rdGenderOther);
        tv_profile_name = view.findViewById(R.id.tv_profile_name);
        btn_header_left = view.findViewById(R.id.btn_header_left);
        iv_edit_icon = view.findViewById(R.id.iv_edit_icon);
        iv_profile = view.findViewById(R.id.iv_profile);
        btn_header_left.setImageResource(R.drawable.ic_svg_header_menu);
        edit_name = view.findViewById(R.id.edit_full_name);
        np_name = view.findViewById(R.id.np_full_name);
        np_address = view.findViewById(R.id.np_email);
        edit_address = view.findViewById(R.id.edit_email);
        btn_update_profile = view.findViewById(R.id.btn_update_profile);
        tv_short_name = view.findViewById(R.id.tv_short_name);
        dProgressbar = new DProgressbar(getContext());
        btn_update_profile.setOnClickListener(this);
        btn_header_left.setOnClickListener(this);
        iv_edit_icon.setOnClickListener(this);

        tvHeader.setText("Edit Profile");
        rdGenderMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    gender = "male";
                Log.e("gender", gender + " " + isChecked);
            }
        });
        rdGenderFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    gender = "female";
                Log.e("gender", gender + " " + isChecked);
            }
        });
        rdGenderOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    gender = "other";
                Log.e("gender", gender + " " + isChecked);
            }

        });
        getProfile();

        version.setText(String.valueOf(AppConfig.VERSION_NAME));
    }

    public void getProfile() {
        dProgressbar.show();
        GetApiHandler apiHandler = new GetApiHandler(getContext(), URLs.GET_PROFILE, new GetApiHandler.OnClickListener() {
            @Override
            public void onResponse(JSONObject jsonObject) throws JSONException {
                dProgressbar.dismiss();
                if (jsonObject.has("status") && jsonObject.has("data")) {
                    User user = new Gson().fromJson(jsonObject.getString("data"), User.class);
                    edit_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                    tv_profile_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                    tv_short_name.setText(UtilsFunctions.getFistLastChar(user.getName()));
                    edit_address.setText(user.getEmail());
                    if (user.getProfile_image() != null)
                        UtilsFunctions.SetLOGO(getContext(), user.getProfile_image(), iv_profile);
                    if (user.getGender() != null) {
                        if (user.getGender().equals("male")) {
                            rdGenderMale.setChecked(true);
                            gender = "male";
                        } else if (user.getGender().equals("female")) {
                            rdGenderFemale.setChecked(true);
                            gender = "female";
                        } else if (user.getGender().equals("other")) {
                            rdGenderOther.setChecked(true);
                            gender = "other";
                        }
                    }
                }
            }

            @Override
            public void onError() {
                dProgressbar.dismiss();
            }
        });
        apiHandler.execute();
    }

    public void setProfile() {
        np_name.setError("");
        np_address.setError("");

        String name = edit_name.getText().toString();
        String email = edit_address.getText().toString();
        if (name.isEmpty())
            np_name.setError("Enter full name");
        else if (email.isEmpty())
            np_address.setError("Enter Email");
        else if (!UtilsFunctions.isValidEmail(email))
            np_address.setError("Invalid Email");
        else {
            dProgressbar.show();
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("email", email);
            params.put("gender", "" + gender);
            PostApiHandler postApiHandler = new PostApiHandler(getContext(), URLs.GET_UPDATE, params, new PostApiHandler.OnClickListener() {
                @Override
                public void onResponse(JSONObject jsonObject) throws JSONException {
                    Log.e("jsonObject", new Gson().toJson(jsonObject));

                    if (jsonObject.has("status") && jsonObject.has("data")) {
                        storeData.setData(ConstantValues.USER_DATA, jsonObject.getString("data"), new StoreData.SetListener() {
                            @Override
                            public void setOK() {
                                ((HomeActivity) getContext()).setProfile();

                                try {


                                    User user = new Gson().fromJson(jsonObject.getString("data"), User.class);
                                    edit_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                                    tv_profile_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                                    tv_short_name.setText(UtilsFunctions.getFistLastChar(user.getName()));
                                    edit_address.setText(user.getEmail());
                                    if (user.getProfile_image() != null )
                                        UtilsFunctions.SetLOGO(getContext(), user.getProfile_image(), iv_profile);
                                    if (user.getGender() != null) {
                                        if (user.getGender().equals("male")) {
                                            rdGenderMale.setChecked(true);
                                            gender = "male";
                                        } else if (user.getGender().equals("female")) {
                                            rdGenderFemale.setChecked(true);
                                            gender = "female";
                                        } else if (user.getGender().equals("other")) {
                                            rdGenderOther.setChecked(true);
                                            gender = "other";
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Toast.makeText(getActivity(),
                                jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        if (jsonObject.has("type") && jsonObject.getString("type").equalsIgnoreCase("validation")) {
                            if (jsonObject.has("errors")) {
                                JSONObject errObj = jsonObject.getJSONObject("errors");
                                if (errObj.has("name")) {
                                    edit_name.setError(UtilsFunctions.errorShow(errObj.getJSONArray("name")));
                                    edit_name.requestFocus();
                                } else if (errObj.has("email")) {
                                    edit_address.setError(UtilsFunctions.errorShow(errObj.getJSONArray("email")));
                                    edit_address.requestFocus();
                                } else if (errObj.has("gender")) {
                                    ed_gender.setError(UtilsFunctions.errorShow(errObj.getJSONArray("gender")));
                                    ed_gender.requestFocus();
                                }

                            }

                        } else {
                            Toast.makeText(getActivity(),
                                    jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }


                    dProgressbar.dismiss();
                    /*if (jsonObject.has("message"))
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    if (jsonObject.has("errors")) {
                        JSONObject errObj = jsonObject.getJSONObject("errors");
                        for (Iterator<String> it = errObj.keys(); it.hasNext(); ) {
                            String key = it.next();
                            if (key != null)
                                Toast.makeText(getContext(), UtilsFunctions.errorShow(errObj.getJSONArray(key)), Toast.LENGTH_SHORT).show();
                        }
                    }*/
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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getPermission() {
        boolean storagePermission = hasPermissions(getContext(), SP);
        boolean cameraPermission = hasPermissions(getContext(), CP);

        Log.v("Permission", String.valueOf(storagePermission));
        Log.v("Permission", String.valueOf(cameraPermission));
        PERMISSIONS.add(SP);
        PERMISSIONS.add(CP);

        if (!storagePermission || !cameraPermission) {
            requestPermissions(PERMISSIONS.toArray(new String[PERMISSIONS.size()]), 2);
        } else {
            selectImage();
        }
    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    // getActivity() use so onActivityResult get right requestCode not create by it self
                    getActivity().startActivityForResult(takePicture, ConstantValues.RESULT_CAMERA_OK);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // getActivity() use so onActivityResult get right requestCode not create by it self
                    getActivity().startActivityForResult(pickPhoto, ConstantValues.RESULT_GALLERY_OK);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0 || grantResults == null) {
            *//*If result is null*//*
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            *//*If We accept permission*//*
            selectImage();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            *//*If We Decline permission*//*
        }
    }*/

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getActivity().getContentResolver() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onStart() {
        super.onStart();
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    public void onStop() {
        super.onStop();
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
    }

    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };


    private void updateUserImage(File imageFile) {

        dProgressbar.show();

        loginVM
                .imageUpdate(
                        imageFile
                )
                .observe(this, response -> {
                    dProgressbar.dismiss();

                    if (response.getStatus() && response.getData() != null) {

                        storeData.setData(ConstantValues.USER_DATA, new Gson().toJson(response.getData()), new StoreData.SetListener() {
                            @Override
                            public void setOK() {
                                ((HomeActivity) getContext()).setProfile();
                                try {


                                    User user = response.getData();
                                    edit_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                                    tv_profile_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                                    tv_short_name.setText(UtilsFunctions.getFistLastChar(user.getName()));
                                    edit_address.setText(user.getEmail());
                                    if (user.getProfile_image() != null)
                                        UtilsFunctions.SetLOGO(getContext(), user.getProfile_image(), iv_profile);
                                    if (user.getGender() != null) {
                                        if (user.getGender().equals("male")) {
                                            rdGenderMale.setChecked(true);
                                            gender = "male";
                                        } else if (user.getGender().equals("female")) {
                                            rdGenderFemale.setChecked(true);
                                            gender = "female";
                                        } else if (user.getGender().equals("other")) {
                                            rdGenderOther.setChecked(true);
                                            gender = "other";
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        try {
                            Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });


    }


    private void uploadPicture(File image) {

        DProgressbar dProgressbar = new DProgressbar(getContext());
        dProgressbar.show();
        AndroidNetworking.upload(URLs.SET_PROFILE)
                .addMultipartFile("image", image)
                .addHeaders("Authorization", "Bearer " + storeData.getToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dProgressbar.dismiss();

                        try {
                            if (response.has("status") && response.has("data")) {
                                storeData.setData(ConstantValues.USER_DATA, response.getString("data"), new StoreData.SetListener() {
                                    @Override
                                    public void setOK() {
                                        ((HomeActivity) getContext()).setProfile();
                                        try {


                                            User user = new Gson().fromJson(response.getString("data"), User.class);
                                            edit_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                                            tv_profile_name.setText(UtilsFunctions.splitCamelCase(user.getName()));
                                            tv_short_name.setText(UtilsFunctions.getFistLastChar(user.getName()));
                                            edit_address.setText(user.getEmail());
                                            if (user.getProfile_image() != null)
                                                UtilsFunctions.SetLOGO(getContext(), user.getProfile_image(), iv_profile);
                                            if (user.getGender() != null) {
                                                if (user.getGender().equals("male")) {
                                                    rdGenderMale.setChecked(true);
                                                    gender = "male";
                                                } else if (user.getGender().equals("female")) {
                                                    rdGenderFemale.setChecked(true);
                                                    gender = "female";
                                                } else if (user.getGender().equals("other")) {
                                                    rdGenderOther.setChecked(true);
                                                    gender = "other";
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                            if (response.has("message"))
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {

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
    }


   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (requestCode == ConstantValues.RESULT_CAMERA_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        iv_profile.setImageBitmap(selectedImage);
                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        Uri tempUri = getImageUri(getContext(), selectedImage);
                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        FileImagePath = new File(getRealPathFromURI(tempUri));
                        uploadPicture();
                    }
                    break;

                case 1:
                    if (requestCode == ConstantValues.RESULT_GALLERY_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
//                                profile_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                // use glide libery because when image size is bigger image not show
                                Glide.with(this).load(picturePath).into(iv_profile);
                                FileImagePath = new File(picturePath);
//                                onSaveProfileImage(new File(picturePath));
                                uploadPicture();
//                                uploadBitmap( BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            userImageSelectorHelper.onActivityResult(requestCode, resultCode, data);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                userImageSelectorHelper.onClickPickImage();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_header_left:
                ((HomeActivity) getActivity()).openDrawer();
                break;
            case R.id.iv_edit_icon:

                userImageSelectorHelper.onClickPickImage();
                //getPermission();
                break;
            case R.id.btn_update_profile:
                setProfile();
                break;
        }
    }
}