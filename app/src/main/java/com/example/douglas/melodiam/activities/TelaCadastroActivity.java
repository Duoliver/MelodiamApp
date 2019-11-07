package com.example.douglas.melodiam.activities;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.douglas.melodiam.activities.NavMenuActivity;
import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.model.Lista;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.services.ListaService;
import com.example.douglas.melodiam.services.UsuarioService;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN;


public class TelaCadastroActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    private EditText etLoginCadastro;

    @Password(min = 6, scheme = Password.Scheme.ANY)
    private EditText etSenhaCadastro;

    @ConfirmPassword
    private EditText etConfirmaSenhaCadastro;

    private Button btCadastrar;
    private Usuario usuario;
    private UsuarioService usuarioService;
    private ListaService listaService;
    private Lista lista;
    private com.mobsandgeeks.saripaar.Validator validator;

    private boolean validated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        this.inicializaComponentes();

        validator = new com.mobsandgeeks.saripaar.Validator(this);
        validator.setValidationListener(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        usuarioService = retrofit.create(UsuarioService.class);
        listaService = retrofit.create(ListaService.class);


        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
                if(etConfirmaSenhaCadastro.getText().toString().equals(etSenhaCadastro.getText().toString())) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(1);
                    usuario.setLogin(etLoginCadastro.getText().toString());
                    usuario.setSenha(etSenhaCadastro.getText().toString());
                    usuarioService.buscarPorLogin(usuario.getLogin()).enqueue(new Callback<Usuario>() {
                        @Override
                        public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                            if(response.body() == null && validated == true){
                                usuarioService.cadastrarUsuario(usuario).enqueue(new Callback<Usuario>() {
                                    @Override
                                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                        usuario = response.body();
                                        Intent itPerfil = new Intent(TelaCadastroActivity.this,
                                                SpotifyActivity.class);
                                        itPerfil.putExtra("usuario", usuario);
                                        criarListas(usuario);
                                        startActivity(itPerfil);
                                    }

                                    @Override
                                    public void onFailure(Call<Usuario> call, Throwable t) {
                                        Toast.makeText(TelaCadastroActivity.this, "Servidor indiponível :(",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(TelaCadastroActivity.this, "Login em uso e/ou dados incorretos", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {
                            Toast.makeText(TelaCadastroActivity.this, "Servidor indisponível :(", Toast.LENGTH_SHORT).show();
                        }
                    });


                }else{
                    Toast.makeText(TelaCadastroActivity.this, "As senhas não conferem", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void criarListas(Usuario usuario) {
        Lista[] listas = new Lista[3];
        listas[0] = new Lista
                (1, usuario, "Quero escutar", "Álbuns que "+usuario.getLogin()+" quer escutar");
        listas[1] = new Lista
                (2, usuario, "Já escutei", "Álbuns que "+usuario.getLogin()+" já escutou");
        listas[2] = new Lista
                (3, usuario, "Abandonei", "Álbuns que "+usuario.getLogin()+" não terminou de escutar");
        for(Lista listaAtual:listas){
            listaService.cadastrarLista(listaAtual).enqueue(new Callback<Lista>() {
                @Override
                public void onResponse(Call<Lista> call, Response<Lista> response) {

                }

                @Override
                public void onFailure(Call<Lista> call, Throwable t) {

                }
            });
        }
    }

    public void inicializaComponentes() {
        this.etLoginCadastro = (EditText) findViewById(R.id.et_login_cadastro);
        this.etSenhaCadastro = (EditText) findViewById(R.id.et_senha_cadastro);
        this.etConfirmaSenhaCadastro = (EditText) findViewById(R.id.et_confirma_senha);
        this.btCadastrar = (Button) findViewById(R.id.bt_cadastrar_cadastro);
    }

    @Override
    public void onValidationSucceeded() {
        validated = true;
        Toast.makeText(TelaCadastroActivity.this, "Validação efetuada com sucesso", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        validated = false;
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
        }

    }
}