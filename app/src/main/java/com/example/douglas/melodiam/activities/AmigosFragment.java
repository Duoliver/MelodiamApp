package com.example.douglas.melodiam.activities;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Amizade;
import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.model.Lista;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.services.AmizadeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmigosFragment extends Fragment {

    private View view;
    private ImageButton ibMostrarPendentes, ibMostrarAmigos;
    private ListView lvPendentes, lvAmigos;
    private UsuarioFragment usuarioFragment;

    private List<Amizade> listaAmizadesPendentes = new ArrayList<>();
    private List<Amizade> listaAmizadesAmigos = new ArrayList<>();

    private List<Usuario> listaPendentes = new ArrayList<>();
    private List<Usuario> listaAmigos = new ArrayList<>();

    private AmizadeService amizadeService;
    private PerfilFragment perfilFragment = new PerfilFragment();
    private Usuario usuario;
    private Amizade amizade;
    private CustomListViewUsuario customListViewUsuario;


    public AmigosFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_amigos, container, false);

        this.ibMostrarPendentes = (ImageButton) view.findViewById(R.id.ib_ver_solicitacoes);
        this.ibMostrarAmigos = (ImageButton) view.findViewById(R.id.ib_ver_amigos);
        this.lvPendentes = (ListView) view.findViewById(R.id.lv_solicitacoes);
        this.lvAmigos = (ListView) view.findViewById(R.id.lv_amigos);

        lvPendentes.setVisibility(View.VISIBLE);
        lvAmigos.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        amizadeService = retrofit.create(AmizadeService.class);
        usuario = perfilFragment.getUsuario();
        Log.i("USER", usuario.toString());

        amizadeService.buscarAmigosPorUsuario(usuario.getIdUsuario()).enqueue(new Callback<List<Amizade>>() {
            @Override
            public void onResponse(Call<List<Amizade>> call, Response<List<Amizade>> response) {
                listaAmizadesAmigos = response.body();
                listaAmigos = new ArrayList<>();
                for (int i = 0; i < listaAmizadesAmigos.size(); i++) {
                    if (usuario.getIdUsuario() == listaAmizadesAmigos.get(i).getUsuario1().getIdUsuario()) {
                        listaAmigos.add(listaAmizadesAmigos.get(i).getUsuario2());
                    } else {
                        listaAmigos.add(listaAmizadesAmigos.get(i).getUsuario1());
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Amizade>> call, Throwable t) {
                Toast.makeText(getActivity(),"Não foi possível pegar a lista de amigos", Toast.LENGTH_SHORT).show();
            }
        });



        customListViewUsuario = new CustomListViewUsuario(getActivity(), listaAmigos);
        lvAmigos.setAdapter(customListViewUsuario);


        amizadeService.buscarPendentesPorUsuario(usuario.getIdUsuario()).enqueue(new Callback<List<Amizade>>() {
            @Override
            public void onResponse(Call<List<Amizade>> call, Response<List<Amizade>> response) {
                listaAmizadesPendentes = response.body();
                listaPendentes = new ArrayList<>();
                for(int i = 0; i < listaAmizadesPendentes.size(); i ++) {
                    listaPendentes.add(listaAmizadesPendentes.get(i).getUsuario1());

                }
            }

            @Override
            public void onFailure(Call<List<Amizade>> call, Throwable t) {
                Toast.makeText(getActivity(),"Não foi possível pegar a lista de pendentes", Toast.LENGTH_SHORT).show();
            }
        });


        customListViewUsuario = new CustomListViewUsuario(getActivity(), listaPendentes);
        lvPendentes.setAdapter(customListViewUsuario);




        this.ibMostrarPendentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvPendentes.getVisibility() == View.VISIBLE) {
                    lvPendentes.setVisibility(View.GONE);
                    ibMostrarPendentes.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvPendentes.setVisibility(View.VISIBLE);
                }


            }
        });

        this.ibMostrarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvAmigos.getVisibility() == View.VISIBLE) {
                    lvAmigos.setVisibility(View.GONE);
                    ibMostrarAmigos.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvAmigos.setVisibility(View.VISIBLE);
                }


            }
        });


        this.lvPendentes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final Usuario usuario2 = listaPendentes.get(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                builder.setTitle("Solicitação de amizade:");
                builder.setMessage("Você deseja aceitar este usuário como amigo?");

                amizadeService.buscarStatusEntreUsuarios(usuario2.getIdUsuario(), usuario.getIdUsuario()).enqueue(new Callback<Amizade>() {
                    @Override
                    public void onResponse(Call<Amizade> call, Response<Amizade> response) {
                        amizade = response.body();
                    }

                    @Override
                    public void onFailure(Call<Amizade> call, Throwable t) {
                        Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        amizade.setStatus(true);
                        amizadeService.aceitarUsuario(amizade.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    listaAmigos.add(amizade.getUsuario1());
                                    listaPendentes.remove(amizade.getUsuario1());
                                    Toast.makeText(getActivity(), "Este usuário agora é seu amigo! :)", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getActivity(), "Não foi possível confirmar a amizade", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                builder.setNegativeButton("Rejeitar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        amizadeService.deletarAmizade(amizade.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                listaPendentes.remove(amizade.getUsuario1());
                                Toast.makeText(getActivity(), "Usuario excluído", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getActivity(), "Falha ao rejeitar usuário", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });



        this.lvAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usuarioFragment = new UsuarioFragment();

                Long idUsuario2 = listaAmigos.get(i).getIdUsuario();
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

    public static AmigosFragment newInstance(){
        return new AmigosFragment();
    }

}