package com.example.douglas.melodiam.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Usuario;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN;

public class SpotifyActivity extends AppCompatActivity{

    private static String token;
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost/";
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        Intent login = getIntent();
        user = (Usuario) login.getSerializableExtra("usuario");

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder("b4028e5617a34a73a20705f6fc8ab1fb",
                        TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{""});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    token = response.getAccessToken();
                    Intent perfil = new Intent(SpotifyActivity.this, NavMenuActivity.class);
                    perfil.putExtra("usuario", user);
                    perfil.putExtra("token", token);
                    startActivity(perfil);
                    break;

                case ERROR:
                    Toast.makeText(this, "Não foi possível obter o token de autorização :(", Toast.LENGTH_LONG).show();
                    Log.i("ERRO:", response.getError());
                    break;

                default:
            }
        }
    }

    public String getToken(){
        return this.token;
    }
}