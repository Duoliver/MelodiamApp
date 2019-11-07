package com.example.douglas.melodiam.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglas.melodiam.activities.NavMenuActivity;
import com.example.douglas.melodiam.activities.TelaCadastroActivity;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.services.UsuarioService;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.models.Album;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN;

public class MainActivity extends AppCompatActivity {

    private Button btLogarMain, btCadastroMain;
    private EditText etLoginMain, etSenhaMain;
    private EditText etLoginLogin;
    private EditText etSenhaLogin;
    private Usuario usuario;
    private UsuarioService usuarioService;
    private static String token;

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.inicializaComponentes();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.usuarioService = retrofit.create(UsuarioService.class);


        btLogarMain.setOnClickListener(new View.OnClickListener() {
            //TelaCadastroActivity telaCadastro = new TelaCadastroActivity();
            @Override
            public void onClick(View view) {
                usuario = new Usuario(1, etLoginLogin.getText().toString(), etSenhaLogin.getText().toString());
                usuarioService.buscarPorLoginESenha(etLoginLogin.getText().toString(), etSenhaLogin.getText().toString())
                        .enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                        if(response.isSuccessful()) {
                            usuario = response.body();
                            Intent itPerfil = new Intent(MainActivity.this, SpotifyActivity.class);
                            itPerfil.putExtra("usuario", usuario);
                            startActivity(itPerfil);
                        }else{
                            Toast.makeText
                                    (MainActivity.this, "Login ou senha incorretos!", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Erro de autentificação", Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

        btCadastroMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCadastro = new Intent(MainActivity.this, TelaCadastroActivity.class);
                startActivity(intentCadastro);
            }
        });

    }

    public void inicializaComponentes() {
        this.btLogarMain = findViewById(R.id.bt_logar_main);
        this.btCadastroMain = findViewById(R.id.bt_cadastro_main);
        this.etLoginMain = findViewById(R.id.et_login_main);
        this.etSenhaMain = findViewById(R.id.et_senha_main);
        this.etLoginLogin = (EditText) findViewById(R.id.et_login_main);
        this.etSenhaLogin = (EditText) findViewById(R.id.et_senha_main);
    }
}
