package com.example.douglas.melodiam.activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.AlbumLista;
import com.example.douglas.melodiam.model.Amizade;
import com.example.douglas.melodiam.model.AmizadeLista;
import com.example.douglas.melodiam.model.AvaliacaoAlbum;
import com.example.douglas.melodiam.model.AvaliacaoLista;
import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.model.Lista;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.services.AlbumListaService;
import com.example.douglas.melodiam.services.AmizadeListaService;
import com.example.douglas.melodiam.services.AmizadeService;
import com.example.douglas.melodiam.services.AvaliacaoListaService;
import com.example.douglas.melodiam.services.ListaService;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.RetrofitError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaFragment extends Fragment {

    private View view;
    private TextView tvNomeLista, tvNomeAutor, tvNumeroAlbuns, tvAvaliacaoUsuario, tvAvaliacaoMedia,
            tvDescricaoLista, tvSuaAvaliacao;
    private ImageButton ibMostraAlbuns, ibEditarLista, ibCompartilharLista, ibExcluirLista;
    private EditText inputNome, inputDescricao;
    private ListView lvAlbuns;
    private AlbumFragment albumFragment;
    private RatingBar rbAvaliacaoLista;

    private LinearLayout llOpcoesLista, llOpcoesDescricao;

    private ListaService listaService;
    private long listaId;
    private Lista lista;

    private AlbumListaService albumListaService;
    private List<AlbumLista> listAlbumLista;
    private List<String> listaIdAlbums;
    private List<Album> listAlbumSpotify;
    private List<Usuario> listaAmigos;
    private AvaliacaoListaService avaliacaoListaService;

    private AmizadeLista amizadeLista;
    private AmizadeListaService amizadeListaService;

    private AvaliacaoLista avaliacaoLista;
    private Usuario usuario1;

    private AmizadeService amizadeService;

    private List<Amizade> listaAmizades;

    private Usuario usuario2;

    private Amizade amizade;


    public ListaFragment() {
    }


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lista, container, false);

        inicializaComponentes();

        PerfilFragment perfilFragment = new PerfilFragment();
        usuario1 = perfilFragment.getUsuario();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.listaService = retrofit.create(ListaService.class);
        this.albumListaService = retrofit.create(AlbumListaService.class);
        this.avaliacaoListaService = retrofit.create(AvaliacaoListaService.class);
        this.amizadeListaService = retrofit.create(AmizadeListaService.class);
        this.amizadeService = retrofit.create(AmizadeService.class);


        Bundle bundle = this.getArguments();
        Intent itToken = getActivity().getIntent();


        String token = itToken.getStringExtra("token");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        final SpotifyService spotify = api.getService();

        if(bundle != null) {
            lista = (Lista) bundle.getSerializable("lista");
            tvDescricaoLista.setText(lista.getDescricao());
            tvNomeLista.setText(lista.getNome());
            tvNomeAutor.setText(lista.getAutor().getLogin());
            if(lista.getAutor().getIdUsuario() == usuario1.getIdUsuario()){
                rbAvaliacaoLista.setVisibility(View.GONE);
                tvSuaAvaliacao.setVisibility(View.GONE);
            }else{
                llOpcoesDescricao.setVisibility(View.GONE);
                llOpcoesLista.setVisibility(View.GONE);
            }
            buscarAvaliacao();

        }

        this.ibMostraAlbuns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvAlbuns.getVisibility() == View.VISIBLE) {
                    lvAlbuns.setVisibility(View.GONE);
                    ibMostraAlbuns.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvAlbuns.setVisibility(View.VISIBLE);
                    ibMostraAlbuns.setImageResource(R.drawable.ic_esconder);
                }
            }
        });

        albumListaService.buscarPorLista(lista.getIdLista()).enqueue(new Callback<List<AlbumLista>>() {
            @Override
            public void onResponse(Call<List<AlbumLista>> call, Response<List<AlbumLista>> response) {
                if(response.body() != null){
                    listAlbumLista = response.body();
                    listAlbumSpotify = new ArrayList<>();
                    Toast.makeText(getActivity(), listAlbumLista.size()+" álbuns encontrados", Toast.LENGTH_SHORT).show();
                    for(int y = 0; y < listAlbumLista.size(); y ++) {
                        spotify.getAlbum(listAlbumLista.get(y).getAlbum().getIdSpotify(), new retrofit.Callback<Album>() {
                            @Override
                            public void success(Album album, retrofit.client.Response response) {
                                listAlbumSpotify.add(album);
                                Log.d("ALBUM", album.name);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getActivity(), "Não foi possível retornar os álbuns", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                    CustomListViewAlbumSpotify adapter = new CustomListViewAlbumSpotify(getActivity(), listAlbumSpotify);
                    lvAlbuns.setAdapter(adapter);
                    lvAlbuns.setVisibility(View.GONE);
                    lvAlbuns.setVisibility(View.VISIBLE);
                    tvNumeroAlbuns.setText(String.valueOf(listAlbumSpotify.size())+" álbuns");

                }else{
                    Toast.makeText(getActivity(), "Cadê os álbuns?", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<AlbumLista>> call, Throwable t) {
                Toast.makeText(getActivity(),"Não foi possível carregar os álbuns!", Toast.LENGTH_SHORT).show();
            }
        });




        this.lvAlbuns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(listAlbumSpotify.get(i).name);
                builder.setMessage("O que fazer?");
                builder.setNeutralButton("Excluir da lista", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        albumListaService.excluirDaLista(listAlbumLista.get(i).getIdAlbumLista()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText
                                            (getActivity(), "Álbum excluído da lista com sucesso", Toast.LENGTH_SHORT).show();
                                    listAlbumSpotify.remove(i);
                                    listAlbumLista.remove(i);
                                }else{
                                    Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText
                                        (getActivity(), "Servidor tenporariamente indisponível", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setPositiveButton("Visitar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        albumFragment = new AlbumFragment();
                        String id = listAlbumSpotify.get(i).id;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("id", id);
                        albumFragment.setArguments(bundle);

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_frame, albumFragment);
                        fragmentTransaction.addToBackStack(null);
                        lvAlbuns.getSelectedItemPosition();
                        fragmentTransaction.commit();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        this.rbAvaliacaoLista.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(avaliacaoLista == null) {
                    avaliacaoLista = new AvaliacaoLista();
                    avaliacaoLista.setAutor(usuario1);
                    avaliacaoLista.setAvaliacao(v);
                    avaliacaoLista.setLista(lista);
                    avaliacaoLista.setIdAvaliacao(1);
                    avaliacaoListaService.cadastrarAvaliacao(avaliacaoLista).enqueue(new Callback<AvaliacaoLista>() {
                        @Override
                        public void onResponse(Call<AvaliacaoLista> call, Response<AvaliacaoLista> response) {
                            if(response.isSuccessful()){
                                tvSuaAvaliacao.setText("Sua avaliação:" + avaliacaoLista.getAvaliacao());
                                Toast.makeText(getActivity(), "Avaliação efetuada com sucesso", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<AvaliacaoLista> call, Throwable t) {
                            Toast.makeText(getActivity(), "Avaliação não efetuada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    avaliacaoLista = new AvaliacaoLista
                            (avaliacaoLista.getIdAvaliacao(), ratingBar.getRating(), usuario1, lista);
                    avaliacaoListaService.editarAvaliacao(avaliacaoLista).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(getActivity(),"Avaliação atualizada com sucesso", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getActivity(), "Servidor temporariamente indisponível", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        amizadeService.buscarTodasAmizades().enqueue(new Callback<List<Amizade>>() {
            @Override
            public void onResponse(Call<List<Amizade>> call, Response<List<Amizade>> response) {
                if(response.body() != null){
                    listaAmizades = response.body();
                    listaAmigos = listarAmigos(listaAmizades);
                }
            }

            @Override
            public void onFailure(Call<List<Amizade>> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível resgatar as amizades", Toast.LENGTH_SHORT).show();
            }
        });

        this.tvSuaAvaliacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avaliacaoLista != null){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Excluir avaliação");
                    builder.setMessage("Você quer excluir sua avaliação dessa lista?");
                    builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            avaliacaoListaService.excluirAvaliacao(lista.getIdLista()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.isSuccessful()){
                                        Toast.makeText(getActivity(), "Sua avaliação foi deletada", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Servidor temporariamente indisponível", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        this.ibEditarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LISTA:", lista.getNome()+" "+lista.getAutor()+
                " "+lista.getDescricao()+
                " "+lista.getIdLista());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                builder.setTitle("Editar lista:");
                builder.setMessage("Edite sua lista:");
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                inputNome = new EditText(getActivity());
                inputDescricao = new EditText(getActivity());
                inputNome.setInputType(InputType.TYPE_CLASS_TEXT);
                inputNome.setHint("Nome da lista");
                inputNome.setText(lista.getNome());
                inputDescricao.setInputType(InputType.TYPE_CLASS_TEXT);
                inputDescricao.setHint("Descrição");
                inputDescricao.setText(lista.getDescricao());
                layout.addView(inputNome);
                layout.addView(inputDescricao);
                builder.setView(layout);

                builder.setPositiveButton("Editar lista", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        lista.setNome(inputNome.getText().toString());
                        lista.setDescricao(inputDescricao.getText().toString());
                        lista.setAutor(usuario1);
                        listaService.editarLista(lista).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(getActivity(), "Lista editada com sucesso", Toast.LENGTH_SHORT).show();
                                    tvNomeLista.setText(lista.getNome());
                                    tvDescricaoLista.setText(lista.getDescricao());
                                }else{
                                    Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getActivity(), "Servidor indisponível", Toast.LENGTH_SHORT).show();
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

        this.ibCompartilharLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                builder.setTitle("Compartilhar lista");
                builder.setMessage("Selecione com quem deseja compartilhar essa lista: ");

                ListView lvAmigos = new ListView(getActivity());
                CustomListViewUsuario adapter = new CustomListViewUsuario(getActivity(), listaAmigos);
                Log.d("NUMERO DE AMIGOS", listaAmigos.size() + "");
                lvAmigos.setAdapter(adapter);
                builder.setView(lvAmigos);
                lvAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        usuario2 = listaAmigos.get(i);
                        Toast.makeText(getActivity(), "Amigo selecionada", Toast.LENGTH_SHORT).show();
                    }
                });


                builder.setPositiveButton("Compartilhar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        amizadeService.buscarStatusEntreUsuarios
                                (usuario1.getIdUsuario(), usuario2.getIdUsuario()).enqueue(new Callback<Amizade>() {
                            @Override
                            public void onResponse(Call<Amizade> call, Response<Amizade> response) {
                                if(response.isSuccessful()){
                                    amizade = response.body();
                                }else{
                                    Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Amizade> call, Throwable t) {
                                Toast.makeText(getActivity(), "Servidor indisponível", Toast.LENGTH_SHORT).show();
                            }
                        });

                        amizadeLista.setAmizade(amizade);
                        amizadeLista.setLista(lista);

                        amizadeListaService.compartilhar(amizadeLista).enqueue(new Callback<AmizadeLista>() {
                            @Override
                            public void onResponse(Call<AmizadeLista> call, Response<AmizadeLista> response) {
                                Toast.makeText(getActivity(), "Lista compartilhada!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<AmizadeLista> call, Throwable t) {
                                Toast.makeText(getActivity(), "Não foi possível compartilhar", Toast.LENGTH_SHORT).show();
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

        this.ibExcluirLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Excluir lista");
                builder.setMessage("Tens certeza de que quer excluir esta lista?");
                builder.setPositiveButton("Excluir Lista", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listaService.excluir(lista.getIdLista()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(getActivity(), "Lista excluída", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getActivity(), "Servidor indisponível", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Operação cancelada", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = builder.create();
                builder.show();
            }
        });

        avaliacaoListaService.calcularMediaLista(listaId).enqueue(new Callback<Float>() {
            @Override
            public void onResponse(Call<Float> call, Response<Float> response) {
                if(response.body() != 0 && response.isSuccessful()){
                    tvAvaliacaoMedia.setText(String.format("%.1f", response.body()));
                }

            }

            @Override
            public void onFailure(Call<Float> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível retornar a média da lista", Toast.LENGTH_SHORT).show();
            }
        });

        if(lista == null) {
            Log.i("LISTA", "null");
        }



        return view;
    }

    private void buscarAvaliacao() {
        Log.d("BUSCAR AVALIAÇÃO", lista.getIdLista()+" "+usuario1.getIdUsuario());
        avaliacaoListaService.buscarPorListaEUsuario
                (lista.getIdLista(), usuario1.getIdUsuario()).enqueue(new Callback<AvaliacaoLista>() {
            @Override
            public void onResponse(Call<AvaliacaoLista> call, Response<AvaliacaoLista> response) {
                if(response.body() != null){
                    avaliacaoLista = response.body();
                    rbAvaliacaoLista.setRating(avaliacaoLista.getAvaliacao());
                    tvSuaAvaliacao.setText("Sua avaliação: "+avaliacaoLista.getAvaliacao());
                }else{
                    Toast.makeText(getActivity(), "Algo deu errado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AvaliacaoLista> call, Throwable t) {
                Toast.makeText(getActivity(), "Servidor Temporariamente Indisponível", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<Usuario> listarAmigos(List<Amizade> listaAmizades) {
        List<Usuario> listaAmigos = new ArrayList<>();
        for(Amizade amizade : listaAmizades){
            if(usuario1.getIdUsuario() == amizade.getUsuario1().getIdUsuario()){
                listaAmigos.add(amizade.getUsuario2());
            }else{
                listaAmigos.add(amizade.getUsuario1());
            }
        }
        return listaAmigos;
    }

    private void inicializaComponentes() {
        this.ibMostraAlbuns = (ImageButton) view.findViewById(R.id.ib_ver_albuns_lista);
        this.lvAlbuns = (ListView) view.findViewById(R.id.lv_albuns_lista);
        this.tvSuaAvaliacao = (TextView) view.findViewById(R.id.tv_sua_avaliacao);
        this.tvNomeLista = (TextView) view.findViewById(R.id.tv_nome_lista_lista);
        this.tvNomeAutor = (TextView) view.findViewById(R.id.tv_lista_autor);
        this.tvNumeroAlbuns = (TextView) view.findViewById(R.id.tv_numero_albuns);
        this.tvAvaliacaoMedia = (TextView) view.findViewById(R.id.tv_avaliacao_media_lista);
        this.tvDescricaoLista = (TextView) view.findViewById(R.id.tv_descricao_lista);
        this.rbAvaliacaoLista = (RatingBar) view.findViewById(R.id.rb_avaliacao_lista);
        this.ibCompartilharLista = (ImageButton) view.findViewById(R.id.ib_compartilhar_lista);
        this.ibEditarLista = (ImageButton) view.findViewById(R.id.ib_editar_lista);
        this.ibExcluirLista = (ImageButton) view.findViewById(R.id.ib_excluir_lista);
        this.llOpcoesLista = (LinearLayout) view.findViewById(R.id.ll_opcoes_lista);
        this.llOpcoesDescricao = (LinearLayout) view.findViewById(R.id.ll_opcoes_descricao_lista);
    }

    public static ListaFragment newInstance(){
        return new ListaFragment();
    }

}