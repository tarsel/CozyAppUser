package com.fandataxiuser.ui.fragment.deliveryitem;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.fandataxiuser.R;
import com.fandataxiuser.base.BaseFragment;
import com.fandataxiuser.data.network.model.DeliveryData;
import com.fandataxiuser.data.network.model.EstimateFare;
import com.fandataxiuser.data.network.model.Service;
import com.fandataxiuser.ui.activity.main.MainActivity;
import com.fandataxiuser.ui.adapter.ItemAdapter;
import com.fandataxiuser.ui.fragment.book_ride.BookRideFragment;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class ItemDetailsFragment extends BaseFragment{

    //
//    @BindView(R.id.edt_delivery_address)
//    TextInputEditText  edt_address;
//    @BindView(R.id.edt_delivery_insturction)
//    TextInputEditText  edt_instruction;
//    @BindView(R.id.edt_receivername)
//    TextInputEditText  edt_Rname;
//    @BindView(R.id.edt_receiverphone)
//    TextInputEditText  edt_Rphone;
    @BindView(R.id.rv_delivery)
    RecyclerView  recyclerView;

    @BindView(R.id.tv_no_item)
    TextView tv_empty_text;

    ItemAdapter adapter;

    Unbinder unbinder;
    String serviceName;
    Service service;
    EstimateFare estimateFare;
    ArrayList<DeliveryData> deliveryList= new ArrayList<>();
    public ItemDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_delivery;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        Bundle args = getArguments();
        if (args != null) {
            serviceName = args.getString("service_name");
            service = (Service) args.getSerializable("mService");
            estimateFare = (EstimateFare) args.getSerializable("estimate_fare");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter= new ItemAdapter(getContext(),deliveryList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @OnClick({R.id.btn_next, R.id.btn_reset,R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_next:

                moveNext();
                break;
            case R.id.iv_add:
                AddDialog();
                break;
            case R.id.btn_reset:
                deliveryList.clear();
                adapter.notifyDataSetChanged();
                tv_empty_text.setVisibility(View.VISIBLE);
//
//                        edt_address.setText("");
//                        edt_procuct_name.setText("");
//                        edt_instruction.setText("");
//                        edt_Rname.setText("");
//                        edt_Rphone.setText("");

                break;
            default:
                break;
        }
    }



    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    private void moveNext() {
        if (service != null) {
            hideKeyboard();
            String json= new Gson().toJson(deliveryList);
            Log.d("JSON", "moveNext: "+json);
            Bundle bundle = new Bundle();
            bundle.putString("service_name", service.getName());
            bundle.putSerializable("mService", service);
            bundle.putSerializable("estimate_fare", estimateFare);
            bundle.putSerializable("delivery",json);
            BookRideFragment bookRideFragment = new BookRideFragment();
            bookRideFragment.setArguments(bundle);
            ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);
        }

    }


    protected void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    private void AddDialog() {
        BottomSheetDialog couponDialog = new BottomSheetDialog(Objects.requireNonNull(getContext()), R.style.SheetDialog);
        couponDialog.setCanceledOnTouchOutside(true);
        couponDialog.setCancelable(true);
        couponDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        couponDialog.setContentView(R.layout.dialog_add_item);
        TextInputEditText  edt_address= couponDialog.findViewById(R.id.edt_delivery_address);
        TextInputEditText  edt_instruction=couponDialog.findViewById(R.id.edt_delivery_insturction);
        TextInputEditText  edt_Rname=couponDialog.findViewById(R.id.edt_receivername);
        TextInputEditText  edt_Rphone=couponDialog.findViewById(R.id.edt_receiverphone);
        TextInputEditText  edt_product_name=couponDialog.findViewById(R.id.edt_item_name);
        Button button= couponDialog.findViewById(R.id.btn_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edt_Rname.getText().toString())){
                    Toasty.error(getContext(),"Give Receiver Name");
                    return;
                }
                if(TextUtils.isEmpty(edt_Rphone.getText().toString())){
                    Toasty.error(getContext(),"Give Receiver Phone ");

                    return;
                }
                if(TextUtils.isEmpty(edt_instruction.getText().toString())){
                    Toasty.error(getContext(),"Give Delivery Instruction");

                    return;
                }
                if(TextUtils.isEmpty(edt_product_name.getText().toString())){
                    Toasty.error(getContext(),"Give product Name");

                    return;
                }
                if(TextUtils.isEmpty(edt_address.getText().toString())){
                    Toasty.error(getContext(),"Give Delivery Address");

                    return;
                }
                DeliveryData data= new DeliveryData();
                data.setItemToDeliver(edt_product_name.getText().toString());
                data.setDeliveryAddress(edt_address.getText().toString());
                data.setAnyInstructions(edt_instruction.getText().toString());
                data.setReceiverName(edt_Rname.getText().toString());
                data.setReceiverMobile(edt_Rphone.getText().toString());
                deliveryList.add(data);
                if(deliveryList.size()>0){
                    tv_empty_text.setVisibility(View.GONE);
                }else{
                    tv_empty_text.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                couponDialog.dismiss();

            }
        });




        couponDialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                new BottomSheetDialog(getContext()).dismiss();
                Log.d("TAG", "--------- Do Something -----------");
                return true;
            }
            return false;
        });
        Window window = couponDialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(param);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        couponDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        couponDialog.show();
    }

}
