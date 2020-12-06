package com.ticandroid.baley_labeye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    private Button register=null;
    private Button login=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        register=(Button) findViewById(R.id.register);
        login=(Button) findViewById(R.id.login);
        register.setOnClickListener(this);
        login.setOnClickListener(this);

    }
    public void onClick(View v){
        if(v==register){
            Intent inscription = new Intent(this,InscriptionActivity.class);
            startActivity(inscription);
            finish();
        }
        else if(v==login){
            Intent connexion = new Intent(this, ConnexionActivity.class);
            startActivity(connexion);
            finish();
        }


    }

}

