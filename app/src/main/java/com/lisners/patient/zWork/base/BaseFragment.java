package com.lisners.patient.zWork.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.lisners.patient.R;
import com.lisners.patient.databinding.FragmentBaseBinding;
import com.lisners.patient.zWork.utils.customClasses.MyProgressDialog;


public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {
    //private FragmentToActivityComm mActivityCallback;
    private FragmentBaseBinding mFragmentBaseBinding;
    private T mFrgmentBinding;
    //private FragmentToTabFragmentComm mParentFragmentCallback;

    private MyProgressDialog progressDialog;

    public abstract int getLayoutResID();





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mFragmentBaseBinding = (FragmentBaseBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_base, container, false);
        this.mFrgmentBinding = DataBindingUtil.inflate(inflater, getLayoutResID(), this.mFragmentBaseBinding.layoutContainer, true);

       /* this.mFragmentBaseBinding.includedErrorLayout.tvErrorRefresh.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                BaseFragment.this.lambda$onCreateView$0$BaseFragment(view);
            }
        });*/

        return this.mFragmentBaseBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new MyProgressDialog(getActivity());
    }

    public static <T extends Fragment> T newInstance(Class<T> clazz, Bundle args) {
        T fragment = null;
        try {
            fragment = (T) clazz.newInstance();
            if (args != null) {
                fragment.setArguments(args);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return fragment;
    }


    public void askPermission(String[] permissionList, int requestCode) {
        requestPermissions(permissionList, requestCode);
    }


    public void showProgress() {

        FragmentBaseBinding fragmentBaseBinding = this.mFragmentBaseBinding;
        if (fragmentBaseBinding != null) {
            fragmentBaseBinding.progress.setVisibility(View.VISIBLE);
        }

    }

    public void hideProgress() {
        FragmentBaseBinding fragmentBaseBinding = this.mFragmentBaseBinding;

        if (fragmentBaseBinding != null) {
            fragmentBaseBinding.progress.setVisibility(View.GONE);
        }
    }

    public void showMainLayout() {
        FragmentBaseBinding fragmentBaseBinding = this.mFragmentBaseBinding;
        if (fragmentBaseBinding != null) {
            fragmentBaseBinding.layoutContainer.setVisibility(View.VISIBLE);
        }
    }

    public void hideMainLayout() {
        FragmentBaseBinding fragmentBaseBinding = this.mFragmentBaseBinding;
        if (fragmentBaseBinding != null) {
            fragmentBaseBinding.layoutContainer.setVisibility(View.GONE);
        }
    }


    public void showError(String msg) {

        this.mFragmentBaseBinding.layoutError.setVisibility(View.VISIBLE);
        this.mFragmentBaseBinding.includedErrorLayout.errorText.setText(msg);
    }


    public void hideError() {
        this.mFragmentBaseBinding.layoutError.setVisibility(View.GONE);
        this.mFragmentBaseBinding.includedErrorLayout.errorText.setText("");
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    public T getFragmentBinding() {
        return this.mFrgmentBinding;
    }

  /*  public FragmentToActivityComm getActivityCallback() {
        return this.mActivityCallback;
    }

    public FragmentToTabFragmentComm getParentFragmentCallback() {
        return this.mParentFragmentCallback;
    }*/


    /* public void onAttach(Context context) {
         super.onAttach(context);
         if (context instanceof BaseFragmentActivity) {
             this.mActivityCallback = (FragmentToActivityComm) context;
         }
         if (getParentFragment() instanceof FragmentToTabFragmentComm) {
             this.mParentFragmentCallback = (FragmentToTabFragmentComm) getParentFragment();
         }
     }
 */
    public void onDestroy() {
        super.onDestroy();
    }

    public void refreshClicked() {
    }


    public void showProgressDialog() {
        progressDialog.show();
    }

    public void closeProgressDialog() {
        progressDialog.dismiss();
    }
}

