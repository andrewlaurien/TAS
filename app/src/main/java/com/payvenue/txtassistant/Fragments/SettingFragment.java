package com.payvenue.txtassistant.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.MainActivity;
import com.payvenue.txtassistant.Model.Constants;
import com.payvenue.txtassistant.R;

public class SettingFragment extends Fragment {

    TextView pass, spin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        pass = (TextView) view.findViewById(R.id.tv_changePass);
        spin = (TextView) view.findViewById(R.id.tv_changeSpin);

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.change_pass_dialog, null, false);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view);

                final EditText oldPassTxt, newPassTxt, confirmPassTxt;

                oldPassTxt = (EditText) view.findViewById(R.id.old_passTxt);
                newPassTxt = (EditText) view.findViewById(R.id.new_passTxt);
                confirmPassTxt = (EditText) view.findViewById(R.id.confirm_passTxt);
                Button changeBtn = (Button) view.findViewById(R.id.changeBtn);

                oldPassTxt.setFilters(new InputFilter[]{Constants.filter});
                newPassTxt.setFilters(new InputFilter[]{Constants.filter});
                confirmPassTxt.setFilters(new InputFilter[]{Constants.filter});

                final AlertDialog dialog = builder.create();
                dialog.show();

                changeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldPass = oldPassTxt.getText().toString().trim();
                        String newPass = newPassTxt.getText().toString().trim();
                        String confirmPass = confirmPassTxt.getText().toString().trim();

                        if(oldPass.isEmpty()){
                            oldPassTxt.setError("Required.");
                            oldPassTxt.requestFocus();
                        }else if(!oldPass.equals(MainActivity.pass)){
                            oldPassTxt.setError("Incorrect password.");
                            oldPassTxt.requestFocus();
                        }else if(newPass.isEmpty()){
                            newPassTxt.setError("Required.");
                            newPassTxt.requestFocus();
                        }else if(confirmPass.isEmpty()){
                            confirmPassTxt.setError("Required.");
                            confirmPassTxt.requestFocus();
                        }else if(!newPass.equals(confirmPass)){
                            confirmPassTxt.setError("New password does not match.");
                            confirmPassTxt.requestFocus();
                        }else{
                            showDialog(oldPass, newPass, "Password", dialog);
                        }
                    }
                });
            }
        });

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.change_spin_dialog, null, false);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view);

                final EditText oldSpinTxt, newSpinTxt, confirmSpinTxt;

                oldSpinTxt = (EditText) view.findViewById(R.id.old_spinTxt);
                newSpinTxt = (EditText) view.findViewById(R.id.new_spinTxt);
                confirmSpinTxt = (EditText) view.findViewById(R.id.confirm_spinTxt);
                Button changeBtn = (Button) view.findViewById(R.id.changeBtn);

                InputFilter lengthFilter = new InputFilter.LengthFilter(4);

                oldSpinTxt.setFilters(new InputFilter[]{Constants.filter, lengthFilter});
                newSpinTxt.setFilters(new InputFilter[]{Constants.filter, lengthFilter});
                confirmSpinTxt.setFilters(new InputFilter[]{Constants.filter, lengthFilter});

                final AlertDialog dialog = builder.create();
                dialog.show();

                changeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldSpin = oldSpinTxt.getText().toString().trim();
                        String newSpin = newSpinTxt.getText().toString().trim();
                        String confirmSpin = confirmSpinTxt.getText().toString().trim();

                        if(oldSpin.isEmpty()){
                            oldSpinTxt.setError("Required.");
                            oldSpinTxt.requestFocus();
                        }else if(!oldSpin.equals(MainActivity.spin)){
                            oldSpinTxt.setError("Incorrect SPIN.");
                            oldSpinTxt.requestFocus();
                        }else if(newSpin.isEmpty()){
                            newSpinTxt.setError("Required.");
                            newSpinTxt.requestFocus();
                        }else if(confirmSpin.isEmpty()){
                            confirmSpinTxt.setError("Required.");
                            confirmSpinTxt.requestFocus();
                        }else if(!newSpin.equals(confirmSpin)){
                            confirmSpinTxt.setError("New SPIN does not match.");
                            confirmSpinTxt.requestFocus();
                        }else{
                            showDialog(oldSpin, newSpin, "SPIN", dialog);
                        }
                    }
                });
            }
        });

        return view;
    }

    public void showDialog(final String oldVar, final String newVar, final String message, final AlertDialog alertDialog){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to change "+ message +"?");
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "";
                if (message.equals("Password")){
                    url = Constants.url + "cmd=changePassword&userID=" + MainActivity.userId +
                            "&accountID=" + MainActivity.accId + "&oldPassword=" + oldVar +
                            "&newPassword=" + newVar;
                    MainActivity.pass = newVar;

                }else{
                    url = Constants.url + "cmd=changeSPIN&userID=" + MainActivity.userId +
                            "&accountID=" + MainActivity.accId + "&oldSPIN=" + oldVar +
                            "&newSPIN=" + newVar;
                    MainActivity.spin = newVar;
                }

                JSONApi.getInstance(getActivity()).changeDetails(url);
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
