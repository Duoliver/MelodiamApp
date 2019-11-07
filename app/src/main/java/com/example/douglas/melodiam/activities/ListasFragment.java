package com.example.douglas.melodiam.activities;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Amizade;
import com.example.douglas.melodiam.model.AmizadeLista;
import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.model.Lista;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.services.AmizadeListaService;
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
public class ListasFragment extends Fragment {

    private View view;
    private ImageButton ibMostraListasCriadas, ibMostraListasRecebidas;
    private ListView lvListasCriadas, lvListasRecebidas;
    private FloatingActionButton fbCriaLista;
    private EditText input;
    private EditText inputDescricao;

    private ArrayList<Lista> listasCriadas = new ArrayList<>();
    private ArrayList<Lista> listasRecebidas = new ArrayList<>();
    private ArrayAdapter<Lista> adapterListasCriadas;
    private ArrayAdapter<Lista> adapterListasRecebidas;
    private AmizadeLista amizadeLista = new AmizadeLista();
    private AmizadeListaService amizadeListaService;
    private ListaFragment listaFragment = new ListaFragment();;
    private Usuario usuario;
    private PerfilFragment perfilFragment = new PerfilFragment();
    private UsuarioService usuarioService;
    private ListaService listaService;
    private Lista lista;



    public ListasFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_listas, container, false);

        this.ibMostraListasCriadas = (ImageButton) view.findViewById(R.id.ib_ver_listas_suas);
        this.ibMostraListasRecebidas = (ImageButton) view.findViewById(R.id.ib_ver_listas_recebidas);
        this.fbCriaLista = (FloatingActionButton) view.findViewById(R.id.fab_criar_lista);
        this.lvListasCriadas = (ListView) view.findViewById(R.id.lv_suas_listas);
        this.lvListasRecebidas = (ListView) view.findViewById(R.id.lv_listas_recebidas);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.usuarioService = retrofit.create(UsuarioService.class);
        this.listaService = retrofit.create(ListaService.class);
        this.amizadeListaService = retrofit.create(AmizadeListaService.class);


        this.usuario = perfilFragment.getUsuario();
        Log.i("USUARIO: ", usuario.getLogin() + " " + usuario.getSenha() + " " + usuario.getIdUsuario());

        this.ibMostraListasCriadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvListasCriadas.getVisibility() == View.VISIBLE) {
                    lvListasCriadas.setVisibility(View.GONE);
                    ibMostraListasCriadas.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvListasCriadas.setVisibility(View.VISIBLE);
                    ibMostraListasCriadas.setImageResource(R.drawable.ic_esconder);
                }
            }
        });

        this.ibMostraListasRecebidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvListasRecebidas.getVisibility() == View.VISIBLE) {
                    lvListasRecebidas.setVisibility(View.GONE);
                    ibMostraListasRecebidas.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvListasRecebidas.setVisibility(View.VISIBLE);
                    ibMostraListasRecebidas.setImageResource(R.drawable.ic_esconder);
                }


            }
        });


        listaService.buscarPorAutor(usuario.getIdUsuario()).enqueue(new Callback<List<Lista>>() {
            @Override
            public void onResponse(Call<List<Lista>> call, Response<List<Lista>> response) {
                listasCriadas = (ArrayList<Lista>) response.body();
                adapterListasCriadas = new ArrayAdapter<>
                        (getActivity(), android.R.layout.simple_list_item_1, listasCriadas);
                lvListasCriadas.setAdapter(adapterListasCriadas);
            }

            @Override
            public void onFailure(Call<List<Lista>> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível carregar seus álbuns!", Toast.LENGTH_SHORT).show();
            }
        });

        amizadeListaService.buscarRecebidosPorUsuario(usuario.getIdUsuario()).enqueue(new Callback<List<AmizadeLista>>() {
            @Override
            public void onResponse(Call<List<AmizadeLista>> call, Response<List<AmizadeLista>> response) {
                for(int i = 0; i < response.body().size(); i++) {
                    listasRecebidas.add(response.body().get(i).getLista());
                }
                Log.i("LISTA ", String.valueOf(listasRecebidas.size()));

                adapterListasRecebidas = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listasRecebidas);
                lvListasRecebidas.setAdapter(adapterListasRecebidas);
            }

            @Override
            public void onFailure(Call<List<AmizadeLista>> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível retornar as listas", Toast.LENGTH_SHORT).show();
            }
        });


        this.fbCriaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                builder.setTitle("Criar lista:");
                builder.setMessage("Digite o nome da lista que será criada: ");
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                input = new EditText(getActivity());
                inputDescricao = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Nome da lista");
                inputDescricao.setInputType(InputType.TYPE_CLASS_TEXT);
                inputDescricao.setHint("Descrição");
                layout.addView(input);
                layout.addView(inputDescricao);
                builder.setView(layout);

                builder.setPositiveButton("Criar lista", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        lista = new Lista();
                        lista.setIdLista(0);
                        lista.setNome(input.getText().toString());
                        lista.setDescricao(inputDescricao.getText().toString());
                        lista.setAutor(usuario);
                        listaService.cadastrarLista(lista).enqueue(new Callback<Lista>() {
                            @Override
                            public void onResponse(Call<Lista> call, Response<Lista> response) {
                                if(response.isSuccessful()){
                                    listasCriadas.add(lista);
                                    adapterListasCriadas.notifyDataSetChanged();
                                    Toast.makeText(getActivity(), "Lista criada", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Lista> call, Throwable t) {
                                Toast.makeText(getActivity(), "Erro ao criar a lista!", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getActivity(), "Operação cancelada", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        lvListasCriadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lista = listasCriadas.get(i);
                ListaFragment listaFragment = new ListaFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("lista", lista);
                listaFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, listaFragment);
                fragmentTransaction.addToBackStack(null);
                lvListasRecebidas.getSelectedItemPosition();
                fragmentTransaction.commit();
            }
        });

        lvListasRecebidas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lista = listasRecebidas.get(i);
                ListaFragment listaFragment = new ListaFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("lista", lista);
                listaFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, listaFragment);
                fragmentTransaction.addToBackStack(null);
                lvListasRecebidas.getSelectedItemPosition();
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public static ListasFragment newInstance(){
        return new ListasFragment();
    }

}