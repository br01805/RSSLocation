package com.example.brianr.rsslocation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by jaylagreely on 4/18/17.
 */

public class LoginPage extends AppCompatActivity {

    Button login;
    Button register;

    ConnectionClass mConnectionClass;
    EditText edtUserID, edtPass;
    ProgressBar pbbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mConnectionClass = new ConnectionClass();
        edtUserID = (EditText) findViewById(R.id.user);
        edtPass = (EditText) findViewById(R.id.pass);
        login = (Button) findViewById(R.id.login);
        pbbar = (ProgressBar) findViewById(R.id.progressBar);
        pbbar.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin doLogin = new DoLogin();
                doLogin.execute("");
            }
        });

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterPage.class);
                startActivity(i);
            }
        });
    }

    public class DoLogin extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        String userid = edtUserID.getText().toString();
        String password = edtPass.getText().toString();

        protected void onPreExecute(){
            pbbar.setVisibility(View.VISIBLE);
        }


        protected void onPostExecute(String r){
        pbbar.setVisibility(View.GONE);
        Toast.makeText(LoginPage.this,r,Toast.LENGTH_SHORT).show();

        if(isSuccess){
            Intent i = new Intent(LoginPage.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

        protected String doInBackground(String... params) {
            if (userid.trim().equals("") || password.trim().equals(""))
                z = "Please enter User Id and Password";
            else {
                try {
                    Connection con = mConnectionClass.CONN();
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "select * from Logins where username='" + userid + "' and password='" + password + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if (rs.next()) {
                            z = "Login Successful";
                            isSuccess = true;
                        } else {
                            z = "invalid Credentials";
                            isSuccess = false;
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }
            return z;

        }
    }
}
