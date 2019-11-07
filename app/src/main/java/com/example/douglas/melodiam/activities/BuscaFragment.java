package com.example.douglas.melodiam.activities;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.services.UsuarioService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuscaFragment extends Fragment {

    private View view;
    private ListView lvAlbunsBusca, lvUsuariosBusca;
    private AlbumFragment albumFragment;
    private UsuarioFragment usuarioFragment;
    private kaaes.spotify.webapi.android.models.AlbumSimple album;
    private UsuarioService usuarioService;
    private List<AlbumSimple> listaAlbuns;
    private List<Usuario> listaUsuario;
    private CustomListViewUsuario customListViewUsuario;
    private ImageButton ibMostrarAlbuns, ibMostrarUsuarios;

    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private SpotifyService spotify;
    private String token;


    public BuscaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buscar, container, false);
        this.lvAlbunsBusca = view.findViewById(R.id.lv_albuns_busca);
        this.lvUsuariosBusca = view.findViewById(R.id.lv_usuarios_busca);
        this.ibMostrarAlbuns = view.findViewById(R.id.ib_mostrar_albuns_busca);
        this.ibMostrarUsuarios = view.findViewById(R.id.ib_mostrar_usuarios_busca);

        Intent itToken = getActivity().getIntent();
        token = itToken.getStringExtra("token");
        SpotifyApi api = new SpotifyApi();
        spotify = api.getService();
        api.setAccessToken(token);
        Log.i("TOKEN", token);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuarioService = retrofit.create(UsuarioService.class);

        setHasOptionsMenu(true);
        customListViewUsuario = new CustomListViewUsuario(getActivity(), usuarios);
        lvAlbunsBusca.setTextFilterEnabled(true);

        this.ibMostrarUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvUsuariosBusca.getVisibility() == View.VISIBLE) {
                    lvUsuariosBusca.setVisibility(View.GONE);
                    ibMostrarUsuarios.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvUsuariosBusca.setVisibility(View.VISIBLE);
                    ibMostrarUsuarios.setImageResource(R.drawable.ic_esconder);
                }
            }
        });

        this.ibMostrarAlbuns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lvAlbunsBusca.getVisibility() == View.VISIBLE) {
                    lvAlbunsBusca.setVisibility(View.GONE);
                    ibMostrarAlbuns.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvAlbunsBusca.setVisibility(View.VISIBLE);
                    ibMostrarAlbuns.setImageResource(R.drawable.ic_esconder);
                }
            }
        });



        lvAlbunsBusca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                albumFragment = new AlbumFragment();
                String id = getAlbum(i).id;
                Bundle bundle = new Bundle();
                bundle.putSerializable("id", id);
                albumFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, albumFragment);
                fragmentTransaction.addToBackStack(null);
                lvAlbunsBusca.getSelectedItemPosition();
                fragmentTransaction.commit();
            }
        });

        lvUsuariosBusca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usuarioFragment = new UsuarioFragment();
                Long idUsuario2 = listaUsuario.get(i).getIdUsuario();
                Bundle bundle = new Bundle();
                bundle.putLong("id usuario2", idUsuario2);
                usuarioFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, usuarioFragment);
                fragmentTransaction.addToBackStack(null);
                lvUsuariosBusca.getSelectedItemPosition();
                fragmentTransaction.commit();
            }
        });


        return view;
    }

    public kaaes.spotify.webapi.android.models.AlbumSimple getAlbum(int index) {
        return listaAlbuns.get(index);
    }



    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.m_search);


        final SearchView searchView = (SearchView) item.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                usuarioService.buscarPorLogin(s).enqueue(new retrofit2.Callback<Usuario>() {
                    @Override

                    public void onResponse(Call<Usuario> call, retrofit2.Response<Usuario> response) {

                        listaUsuario = new ArrayList<>();
                        CustomListViewUsuario adapter = new CustomListViewUsuario(getActivity(), listaUsuario);
                        Usuario user = response.body();
                        lvUsuariosBusca.setAdapter(adapter);
                        if(user != null){
                            listaUsuario.add(user);

                        }else{
                            Toast.makeText(getActivity(), "Nenhum usuário encontrado", Toast.LENGTH_SHORT);
                            listaUsuario.clear();
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {

                    }
                });

                spotify.searchAlbums(s, new Callback<AlbumsPager>() {
                    @Override
                    public void success(AlbumsPager albumsPager, Response response) {
                        listaAlbuns = albumsPager.albums.items;
                        if(listaAlbuns != null){
                            CustomListViewAlbum adapter = new CustomListViewAlbum(getActivity(), listaAlbuns);
                            lvAlbunsBusca.setAdapter(adapter);
                            Toast.makeText(getActivity(), listaAlbuns.size()+"", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), "Nenhum álbum encontrado", Toast.LENGTH_SHORT);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });


                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public static BuscaFragment newInstance() {
        return new BuscaFragment();
    }
}
