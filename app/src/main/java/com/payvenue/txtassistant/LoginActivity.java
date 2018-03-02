package com.payvenue.txtassistant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.payvenue.txtassistant.Helper.JSONApi;
import com.payvenue.txtassistant.Model.Constants;

public class LoginActivity extends AppCompatActivity {

    EditText txtUser, txtPass, txtSpin;
    Button btnLogin;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        
        txtUser = (EditText) findViewById(R.id.txtUsername);
        txtPass = (EditText) findViewById(R.id.txtPassword);
        txtSpin = (EditText) findViewById(R.id.txtSpin);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        txtUser.setFilters(new InputFilter[]{Constants.filter});
        txtPass.setFilters(new InputFilter[]{Constants.filter});
        txtSpin.setFilters(new InputFilter[]{Constants.filter});

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = txtUser.getText().toString().trim();
                String pass = txtPass.getText().toString().trim();
                String spin = txtSpin.getText().toString().trim();

                if(user.isEmpty()){
                    txtUser.requestFocus();
                    txtUser.setError("Required");
                }else if(pass.isEmpty()){
                    txtPass.requestFocus();
                    txtPass.setError("Required");
                }else if(spin.isEmpty()){
                    txtSpin.requestFocus();
                    txtSpin.setError("Required");
                }else{
                    String url = Constants.url + "cmd=Login&userid=" + user + "&password=" +
                            pass + "&spin=" + spin;

                    JSONApi.getInstance(LoginActivity.this).login(url, pass, spin);
                }
            }
        });
    }
}
