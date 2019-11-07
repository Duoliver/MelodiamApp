package com.example.douglas.melodiam.activities;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Amizade;
import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.model.Lista;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.services.AmizadeService;
import com.example.douglas.melodiam.services.ListaService;
import com.example.douglas.melodiam.services.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsuarioFragment extends Fragment {

    private View view;
    private TextView tvNomeUsuario, tvNumeroListasUsuario;
    private Button btEnviarSolicitacao;
    private ImageButton ibMostrarListas, ibMostrarAmigosUsuario;
    private ListView lvListas, lvAmigos;

    private List<Lista> listaListasUsuario = new ArrayList<>();
    private List<Amizade> listaAmizadesUsuario = new ArrayList<>();
    private List<Usuario> listaAmigosUsuario = new ArrayList<>();
    private byte statusBotao;
    private AmizadeService amizadeService;
    private Amizade amizade = new Amizade();

    private ListaFragment listaFragment;

    private Long idUsuario2;
    private long numeroListas;

    private UsuarioService usuarioService;
    private Usuario usuario2;
    private ListaService listaService;

    private UsuarioFragment usuarioFragment;

    private Usuario usuario1;
    private PerfilFragment perfilFragment = new PerfilFragment();

    private CustomListViewUsuario customListViewUsuario;

    private Lista lista;

    private boolean statusAmizade;


    public UsuarioFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_usuario, container, false);

        this.tvNomeUsuario = (TextView) view.findViewById(R.id.tv_login_usuario_usuario);
        this.tvNumeroListasUsuario = (TextView) view.findViewById(R.id.tv_numero_listas_usuario);
        this.btEnviarSolicitacao = (Button) view.findViewById(R.id.bt_opcoes_usuario);
        this.ibMostrarListas = (ImageButton) view.findViewById(R.id.ib_ver_listas_usuario);
        this.ibMostrarAmigosUsuario = (ImageButton) view.findViewById(R.id.ib_ver_amigos_usuario);
        this.lvListas = (ListView) view.findViewById(R.id.lv_listas_usuario);
        this.lvAmigos = (ListView) view.findViewById(R.id.lv_amigos_usuario);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuarioService = retrofit.create(UsuarioService.class);
        listaService = retrofit.create(ListaService.class);
        amizadeService = retrofit.create(AmizadeService.class);

        usuario1 = perfilFragment.getUsuario();


        ibMostrarListas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvListas.getVisibility() == View.VISIBLE) {
                    lvListas.setVisibility(View.GONE);
                    ibMostrarListas.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvListas.setVisibility(View.VISIBLE);
                }


            }
        });

        ibMostrarAmigosUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvAmigos.getVisibility() == View.VISIBLE) {
                    lvAmigos.setVisibility(View.GONE);
                    ibMostrarAmigosUsuario.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvAmigos.setVisibility(View.VISIBLE);
                }


            }
        });

        Bundle bundle = this.getArguments();
        Intent itToken = getActivity().getIntent();


        String token = itToken.getStringExtra("token");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        SpotifyService spotify = api.getService();

        if(bundle != null) {
            idUsuario2 = bundle.getLong("id usuario2");
        }


        usuarioService.buscarPorId(idUsuario2).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                usuario2 = response.body();
                tvNomeUsuario.setText(usuario2.getLogin());
                retornarNumeroListas();
                Log.i("USUARIO2", usuario2.toString());
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível resgatar o usuário", Toast.LENGTH_SHORT).show();
            }
        });


        listaService.buscarPorAutor(idUsuario2).enqueue(new Callback<List<Lista>>() {
            @Override
            public void onResponse(Call<List<Lista>> call, Response<List<Lista>> response) {
                listaListasUsuario = response.body();
                Log.d("LISTAS DO USUARIO ", listaListasUsuario.toString());
                ArrayAdapter<Lista> adapterListaListasUsuario = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaListasUsuario);
                lvListas.setAdapter(adapterListaListasUsuario);

            }

            @Override
            public void onFailure(Call<List<Lista>> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível retornar a lista de listas desse usuário", Toast.LENGTH_SHORT).show();
            }
        });


        Log.i("LISTAs ", listaListasUsuario.toString());



        amizadeService.buscarAmigosPorUsuario(idUsuario2).enqueue(new Callback<List<Amizade>>() {
            @Override
            public void onResponse(Call<List<Amizade>> call, Response<List<Amizade>> response) {
                listaAmizadesUsuario = response.body();
                listaAmigosUsuario = new ArrayList<>();
                for(int i = 0; i < listaAmizadesUsuario.size(); i ++) {
                    if(usuario1.getIdUsuario() == listaAmizadesUsuario.get(i).getUsuario1().getIdUsuario()) {
                        listaAmigosUsuario.add(listaAmizadesUsuario.get(i).getUsuario1());
                    }else{
                        listaAmigosUsuario.add(listaAmizadesUsuario.get(i).getUsuario2());
                    }

                }
                Log.d("AMIGOS DO USUARIO ", listaAmigosUsuario.toString());
                customListViewUsuario = new CustomListViewUsuario(getActivity(), listaAmigosUsuario);
                lvAmigos.setAdapter(customListViewUsuario);

            }

            @Override
            public void onFailure(Call<List<Amizade>> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível retornar a lista de amigos desse usuário", Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("id u1 " , String.valueOf(usuario1.getIdUsuario()));
        Log.i("id u2 " , String.valueOf(idUsuario2));


        amizadeService.buscarStatusEntreUsuarios(usuario1.getIdUsuario(), idUsuario2).enqueue(new Callback<Amizade>() {
            @Override
            public void onResponse(Call<Amizade> call, final Response<Amizade> response) {

                if(response.body() == null) {
                    btEnviarSolicitacao.setText("Adicionar aos amigos");
                    statusBotao = 1;
                }else{
                    amizade = response.body();
                    if(amizade.isStatus() == true){
                        statusBotao = 3;
                        btEnviarSolicitacao.setText("Desfazer amizade");
                    }else{
                        btEnviarSolicitacao.setText("Solicitação enviada");
                        statusBotao = 2;
                    }

                }

            }

            @Override
            public void onFailure(Call<Amizade> call, Throwable t) {
                Toast.makeText(getActivity(), "Erro!", Toast.LENGTH_SHORT).show();
            }
        });


        btEnviarSolicitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (statusBotao){
                    case 1:
                        amizade.setStatus(false);
                        amizade.setUsuario1(usuario1);
                        amizade.setUsuario2(usuario2);
                        amizadeService.solicitarAmizade(amizade).enqueue(new Callback<Amizade>() {
                            @Override
                            public void onResponse(Call<Amizade> call, Response<Amizade> response) {
                                if(response.isSuccessful()){
                                    amizade = response.body();
                                    btEnviarSolicitacao.setText("Solicitação Enviada");
                                    Toast.makeText(getActivity(), "Solicitação enviada!", Toast.LENGTH_SHORT).show();
                                    Log.i("AMIZADE" ,response.body().toString());
                                    statusBotao = 2;
                                }
                            }

                            @Override
                            public void onFailure(Call<Amizade> call, Throwable t) {
                                Toast.makeText(getActivity(), "Erro de envio!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 2:
                        amizadeService.deletarAmizade(amizade.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    btEnviarSolicitacao.setText("Adicionar aos amigos");
                                    Toast.makeText(getActivity(), "Solicitação desfeita!", Toast.LENGTH_SHORT).show();
                                    statusBotao = 1;
                                }

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getActivity(), "Erro ao desfazer solicitação!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    case 3:
                        amizadeService.deletarAmizade(amizade.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(getActivity(), "Amizade desfeita!", Toast.LENGTH_SHORT).show();
                                    btEnviarSolicitacao.setText("Adicionar aos amigos");
                                    statusBotao = 1;
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getActivity(), "Erro ao excluir amigo!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    default:
                        break;

                }


            }
        });


        lvListas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lista = listaListasUsuario.get(i);
                listaFragment = new ListaFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("lista", lista);
                listaFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, listaFragment);
                fragmentTransaction.addToBackStack(null);
                lvListas.getSelectedItemPosition();
                fragmentTransaction.commit();

            }
        });

        lvAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                usuarioFragment = new UsuarioFragment();
                Long idUsuario2 = listaAmigosUsuario.get(i).getIdUsuario();
                Bundle bundle = new Bundle();
                bundle.putLong("id usuario2", idUsuario2);
                usuarioFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, usuarioFragment);
                fragmentTransaction.addToBackStack(null);
                lvAmigos.getSelectedItemPosition();
                fragmentTransaction.commit();

            }
        });




        return view;

    }

    private void retornarNumeroListas() {
        listaService.retornarNumeroListas(idUsuario2).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                numeroListas = response.body();
                tvNumeroListasUsuario.setText(String.valueOf(numeroListas));
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível resgatar o número de listas desse usuário", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static UsuarioFragment newInstance(){
        return new UsuarioFragment();
    }

}