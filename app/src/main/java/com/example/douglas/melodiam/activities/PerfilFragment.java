package com.example.douglas.melodiam.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.services.ListaService;
import com.example.douglas.melodiam.services.UsuarioService;
import com.example.douglas.melodiam.activities.MainActivity;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private TextView tvLogin;
    private TextView tvNumeroListas;
    private Button btMudarLogin;
    private Button btMudarSenha;
    private Button btExcluirConta;
    private Button btLogout;
    private static Usuario usuario;
    private float numero;
    private EditText input;
    private View view;
    private LinearLayout layout;
    private ListaService listaService;
    private UsuarioService usuarioService;
    private boolean welcome = false;

    public PerfilFragment() {

    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.usuarioService = retrofit.create(UsuarioService.class);
        this.listaService = retrofit.create(ListaService.class);

        view = inflater.inflate(R.layout.fragment_perfil, container, false);

        inicializaComponentes();



        if(!welcome){
            Toast.makeText(getActivity(), "Bem-vindo ao seu perfil Melodiam!", Toast.LENGTH_LONG).show();
            welcome = true;
        }


        Intent intent = getActivity().getIntent();
        usuario = (Usuario) intent.getSerializableExtra("usuario");
        Log.i("LOGIN", usuario.getIdUsuario() + " " + usuario.getLogin() + " " + usuario.getSenha());

        tvLogin.setText(usuario.getLogin());
        listaService.retornarNumeroListas(usuario.getIdUsuario()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                tvNumeroListas.setText(response.body()+" listas criadas");
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível retornar o número de listas", Toast.LENGTH_SHORT).show();
            }
        });
        this.tvNumeroListas.setText(numero + " listas criadas");

        this.btMudarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final String loginAntigo = usuario.getLogin();
                input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(loginAntigo);
                builder.setView(input);
                builder.setTitle("Atualizar login:");
                builder.setMessage("Digite um novo login:");

                builder.setPositiveButton("Atualizar login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        usuario.setLogin(input.getText().toString());
                        usuarioService.editarUsuario(usuario).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    tvLogin.setText(input.getText().toString());
                                    Toast.makeText(getActivity(), "Login alterado com sucesso.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                usuario.setLogin(loginAntigo);
                                Toast.makeText(getActivity(), "Não foi possível alterar seu login.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getActivity(), "Operação cancelada!", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        this.btMudarSenha.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override

            public void onClick(View v) {
                layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Atualizar senha:");
                builder.setMessage("Digite uma nova senha:");
                final EditText senha = new EditText(getActivity());
                senha.setHint("Senha atual");
                senha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(senha);
                final String senhaAntiga = usuario.getSenha();

                final EditText novaSenha = new EditText(getActivity());
                novaSenha.setHint("Nova senha");
                novaSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(novaSenha);

                final EditText confirmarSenha = new EditText(getActivity());
                confirmarSenha.setHint("Confirmar nova senha");
                confirmarSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(confirmarSenha);
                builder.setView(layout);


                builder.setPositiveButton("Atualizar senha", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Log.d("SENHAS", senhaAntiga+" "+senha.getText().toString()+"\n" +
                                novaSenha.getText().toString()+" "+confirmarSenha.getText().toString());
                        usuario.setSenha(novaSenha.getText().toString());
                        String senhaTexto = senha.getText().toString();
                        String novaSenhaTexto = novaSenha.getText().toString();
                        String confirmarNovaSenhaTexto = confirmarSenha.getText().toString();

                        if(senhaAntiga.equals(senhaTexto) && novaSenhaTexto.equals(confirmarNovaSenhaTexto)){
                            Toast.makeText(getActivity(), "Senha alterada com sucesso", Toast.LENGTH_LONG).show();
                            usuario.setSenha(novaSenha.getText().toString());
                            usuarioService.editarUsuario(usuario).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Toast.makeText(getActivity(), "Senha alterada com sucesso.", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    usuario.setSenha(senhaAntiga);
                                    Toast.makeText(getActivity(), "Falha na mudança de senha.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if(!(novaSenha.getText().toString().equals(confirmarSenha.getText().toString()))){
                            Toast.makeText(getActivity(), "As senhas não conferem", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), "Senha de usuário inválida", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getActivity(), "Operação cancelada!", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        this.btExcluirConta.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Excluir a conta do Melodiam:");
                builder.setMessage("Você deseja mesmo excluir sua conta?");
                final EditText senha = new EditText(getActivity());
                senha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                senha.setHint("Senha");
                builder.setView(senha);
                builder.setPositiveButton("Excluir conta", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(senha.getText().toString().equals(usuario.getSenha())){
                            usuarioService.excluirUsuario(usuario.getIdUsuario()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Toast.makeText(getActivity(), "Obrigado por utilizar o Melodiam ;-)", Toast.LENGTH_LONG).show();
                                    Intent itTelaMain = new Intent(getActivity(), MainActivity.class);
                                    startActivity(itTelaMain);
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Não foi possível excluir sua conta", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            Toast.makeText(getActivity(), "Senha de usuário inválida", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getActivity(), "Operação cancelada!", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });



        this.btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Fazer logout:");
                builder.setMessage("Você deseja mesmo sair da sua conta?");

                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent itTelaMain = new Intent(getActivity(), MainActivity.class);
                        startActivity(itTelaMain);
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getActivity(), "Operação cancelada!", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });



        return view;
    }

    private void inicializaComponentes(){
        tvLogin = (TextView) view.findViewById(R.id.tv_login_usuario_perfil);
        tvNumeroListas = (TextView) view.findViewById(R.id.tv_numero_listas);
        btMudarLogin = (Button) view.findViewById(R.id.bt_mudar_login);
        btMudarSenha = (Button) view.findViewById(R.id.bt_mudar_senha);
        btExcluirConta = (Button) view.findViewById(R.id.bt_excluir_conta);
        btLogout = (Button) view.findViewById(R.id.bt_logout);
    }

    public static PerfilFragment newInstance(){
        return new PerfilFragment();
    }


}