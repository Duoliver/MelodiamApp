package com.example.douglas.melodiam.activities;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Album;
import com.example.douglas.melodiam.model.AlbumLista;
import com.example.douglas.melodiam.model.AlbumServer;
import com.example.douglas.melodiam.model.AvaliacaoAlbum;
import com.example.douglas.melodiam.model.Comentario;
import com.example.douglas.melodiam.model.Ip;
import com.example.douglas.melodiam.model.Lista;
import com.example.douglas.melodiam.model.Usuario;
import com.example.douglas.melodiam.services.AlbumListaService;
import com.example.douglas.melodiam.services.AlbumService;
import com.example.douglas.melodiam.services.AvaliacaoAlbumService;
import com.example.douglas.melodiam.services.ComentarioService;
import com.example.douglas.melodiam.services.ListaService;
import com.example.douglas.melodiam.services.UsuarioService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Albums;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {

    private View view;
    private ImageView ivCapa;
    private TextView tvNomeAlbum, tvArtistaAlbum, tvAnoAlbum, tvMediaAlbum, tvCopy;
    private ImageButton ibAddAlbum, ibComentarAlbum, ibAvaliarAlbum, ibMostraMusicas, ibMostraComentarios;
    private ListView lvMusicas, lvComentarios, lvDialogAdd;
    private LinearLayout layout;

    private String albumId;
    private kaaes.spotify.webapi.android.models.Album albumSpotify;
    private CustomListViewComentario adapterComentarios;
    private ListaService listaService;
    private AlbumServer albumServer;
    private AlbumLista albumLista;
    private AlbumListaService albumListaService;
    private AlbumService albumService;
    private Comentario comentario;
    private ComentarioService comentarioService;
    private Usuario usuario;
    private UsuarioService usuarioService;
    private AvaliacaoAlbum avaliacaoAlbum;
    private AvaliacaoAlbumService avaliacaoAlbumService;
    private Lista itemDialog;
    private CustomListViewMusica customListViewMusica;
    private List<TrackSimple> musicas;
    private Comentario comment;

    private List<Comentario> listaComentarios;
    List<Lista> listasDialog;
    private EditText etInput;
    private List<Comentario> comentarios = new ArrayList<>();

    public AlbumFragment() {

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album, container, false);

        this.inicializaComponentes();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip.getIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        listaService = retrofit.create(ListaService.class);
        comentarioService = retrofit.create(ComentarioService.class);
        usuarioService = retrofit.create(UsuarioService.class);
        avaliacaoAlbumService = retrofit.create(AvaliacaoAlbumService.class);
        albumService = retrofit.create(AlbumService.class);
        albumListaService = retrofit.create(AlbumListaService.class);


        PerfilFragment perfilFragment = new PerfilFragment();
        usuario = perfilFragment.getUsuario();

        listaService.buscarPorAutor(usuario.getIdUsuario()).enqueue(new retrofit2.Callback<List<Lista>>() {
            @Override
            public void onResponse(Call<List<Lista>> call, retrofit2.Response<List<Lista>> response) {
                listasDialog = response.body();
                if(listasDialog != null){
                    Log.d("LISTAS",
                            "listas de "+usuario.getLogin()+"("+usuario.getIdUsuario()+") baixadas com sucesso\n"
                            +listasDialog.size()+" listas encontradas");
                }else{
                    Log.d("LISTAS", "null");
                }

            }

            @Override
            public void onFailure(Call<List<Lista>> call, Throwable t) {
                Log.d("LISTAS", "não foi possivel baixar as listas de "+usuario.getLogin());
            }
        });

        Bundle bundle = this.getArguments();
        Intent itToken = getActivity().getIntent();


        String token = itToken.getStringExtra("token");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        SpotifyService spotify = api.getService();


        if(bundle != null) {
            albumId = bundle.getString("id");
            Log.d("ALBUM ID", albumId);
        }

        albumService.buscarIdSpotify(albumId).enqueue(new retrofit2.Callback<AlbumServer>() {
            @Override
            public void onResponse(Call<AlbumServer> call, retrofit2.Response<AlbumServer> response) {
                if(response.isSuccessful()){
                    albumServer = response.body();
                    calcularMedia();
                    buscarComentarios();
                    buscarAvaliacao();
                    Log.d("ALBUMSERVER", albumServer.getIdAlbum()+", "+albumServer.getIdSpotify());
                }else{
                    albumServer = new AlbumServer(1, albumId);
                    albumService.cadastrarAlbum(albumServer).enqueue(new retrofit2.Callback<AlbumServer>() {
                        @Override
                        public void onResponse(Call<AlbumServer> call, retrofit2.Response<AlbumServer> response) {
                            if(response.isSuccessful()){
                                albumServer = response.body();
                                Log.d("ALBUMSERVER", albumServer.getIdAlbum()+", "+albumServer.getIdSpotify());
                                lvComentarios.setVisibility(View.VISIBLE);
                            }else{
                                Log.d("ALBUMSERVER", "null");
                            }

                        }

                        @Override
                        public void onFailure(Call<AlbumServer> call, Throwable t) {
                            Toast.makeText(getActivity(),"Não foi possível cadastrar o álbum", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<AlbumServer> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível recuperar o álbum", Toast.LENGTH_SHORT).show();
            }
        });

        spotify.getAlbum(albumId, new Callback<kaaes.spotify.webapi.android.models.Album>() {
            @Override
            public void success(kaaes.spotify.webapi.android.models.Album album, Response response) {
                albumSpotify = album;
                tvNomeAlbum.setText(album.name);
                tvArtistaAlbum.setText(album.artists.get(0).name);
                tvAnoAlbum.setText(album.release_date);
                tvAnoAlbum.setText(album.release_date.substring(0, 4));
                if(album.copyrights.size() > 0) tvCopy.setText(album.copyrights.get(0).text);
                Picasso.get().load(album.images.get(1).url).into(ivCapa);
                musicas = album.tracks.items;
                customListViewMusica = new CustomListViewMusica(getActivity(), 0, musicas);
                lvMusicas.setAdapter((ListAdapter) customListViewMusica);
                lvMusicas.setVisibility(View.VISIBLE);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Album failure", error.toString());
            }
        });




        this.ibAddAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                builder.setTitle("Adicionar álbum à lista:");
                builder.setMessage("Selecione a lista: ");

                if(listasDialog != null){
                    lvDialogAdd = new ListView(getActivity());
                    ArrayAdapter<Lista> adapter = new ArrayAdapter<>
                            (getActivity(), android.R.layout.simple_list_item_1, listasDialog);
                    Log.d("NUMERO DE LISTAS", listasDialog.size()+"");
                    lvDialogAdd.setAdapter(adapter);

                    builder.setView(lvDialogAdd);
                    lvDialogAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            itemDialog = listasDialog.get(i);
                            Toast.makeText(getActivity(), "Lista selecionada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    TextView tvAvisoListas = new TextView(getActivity());
                    tvAvisoListas.setText("Nenhuma lista encontrada");
                    builder.setView(tvAvisoListas);
                }


                builder.setPositiveButton("Adicionar álbum", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        albumLista = new AlbumLista(1, albumServer, itemDialog);
                        albumListaService.inserirEmLista(albumLista).enqueue(new retrofit2.Callback<AlbumLista>() {
                            @Override
                            public void onResponse(Call<AlbumLista> call, retrofit2.Response<AlbumLista> response) {
                                Toast.makeText(getActivity(), "Álbum adicionado à lista", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<AlbumLista> call, Throwable t) {
                                Toast.makeText(getActivity(), "Erro de operação!", Toast.LENGTH_SHORT).show();
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

        this.ibComentarAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(inflater.inflate(R.layout.alert_comentario, null));

                final EditText etComentario = new EditText(getActivity());
                etComentario.setInputType(InputType.TYPE_CLASS_TEXT);
                etComentario.setHint("Comentário");
                etComentario.setSingleLine(false);
                etComentario.setLines(1);
                etComentario.setMaxLines(20);
                etComentario.setGravity(Gravity.LEFT | Gravity.TOP);
                layout.addView(etComentario);

                builder.setTitle("Comentário:");
                builder.setMessage("Faça seu comentário: ");


                builder.setView(layout);

                builder.setPositiveButton("Comentar álbum", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        Log.i("DATA/HORA ", dateFormat.format(date));
                        comment = new Comentario();
                        comment.setAlbum(albumServer);
                        comment.setTexto(etComentario.getText().toString());
                        comment.setData(dateFormat.format(date));
                        comment.setAutor(usuario);


                        comentarioService.publicar(comment).enqueue(new retrofit2.Callback<Comentario>() {
                            @Override
                            public void onResponse(Call<Comentario> call, retrofit2.Response<Comentario> response) {
                                listaComentarios.add(comment);
                                adapterComentarios = new CustomListViewComentario(getActivity(), listaComentarios);
                                lvComentarios.setAdapter(adapterComentarios);
                                Toast.makeText(getActivity(), "Comentário efetuado", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(Call<Comentario> call, Throwable t) {
                                Toast.makeText(getActivity(), "Erro de operação!", Toast.LENGTH_SHORT).show();
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

        this.ibAvaliarAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RelativeLayout layout = new RelativeLayout(getActivity());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Avaliação:");
                builder.setMessage("Faça sua avaliação: ");
                final RatingBar ratingBar = new RatingBar(getActivity());
                ratingBar.setNumStars(5);
                ratingBar.setStepSize((float) 0.5);
                if(avaliacaoAlbum != null) ratingBar.setRating(avaliacaoAlbum.getAvaliacao());
                layout.addView(ratingBar);
                builder.setView(layout);

                builder.setPositiveButton("Avaliar álbum", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(avaliacaoAlbum == null){
                            Log.d("AVALIAÇÃO", "null");
                            avaliacaoAlbum = new AvaliacaoAlbum
                                    (1, ratingBar.getRating(), usuario, albumServer);

                            avaliacaoAlbumService.cadastrarAvaliacao(avaliacaoAlbum).enqueue
                                    (new retrofit2.Callback<AvaliacaoAlbum>() {
                                @Override
                                public void onResponse(Call<AvaliacaoAlbum> call, retrofit2.Response<AvaliacaoAlbum> response) {
                                    if(response.isSuccessful()){
                                        Toast.makeText(getActivity(), "Avaliação efetuada", Toast.LENGTH_SHORT).show();
                                        avaliacaoAlbum = response.body();
                                    }

                                }

                                @Override
                                public void onFailure(Call<AvaliacaoAlbum> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Erro de operação!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            avaliacaoAlbum.setAvaliacao(ratingBar.getRating());
                            Log.d("AvaliacaoAlbum", avaliacaoAlbum.getAvaliacao()+"");
                            avaliacaoAlbumService.editarAvaliacao(avaliacaoAlbum).enqueue(new retrofit2.Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                                    Toast.makeText
                                            (getActivity(),"Avaliação atualizada com sucesso", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                        }



                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getActivity(), "Operação cancelada", Toast.LENGTH_SHORT).show();
                    }
                });

                if(avaliacaoAlbum != null){
                    builder.setNeutralButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            avaliacaoAlbumService.excluirAvaliacao(avaliacaoAlbum.getIdAvaliacao())
                                    .enqueue(new retrofit2.Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                                    if(response.isSuccessful()){
                                        Toast.makeText(getActivity(), "Avaliação excluida", Toast.LENGTH_SHORT).show();
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
                }

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        this.ibMostraMusicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvMusicas.getVisibility() == View.VISIBLE) {
                    lvMusicas.setVisibility(View.GONE);
                    ibMostraMusicas.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvMusicas.setVisibility(View.VISIBLE);
                    ibMostraMusicas.setImageResource(R.drawable.ic_esconder);
                }


            }
        });

        this.ibMostraComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lvComentarios.getVisibility() == View.VISIBLE) {
                    lvComentarios.setVisibility(View.GONE);
                    ibMostraComentarios.setImageResource(R.drawable.ic_mostrar);
                }else{
                    lvComentarios.setVisibility(View.VISIBLE);
                    ibMostraComentarios.setImageResource(R.drawable.ic_esconder);
                }


            }
        });


        this.lvComentarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(usuario.getIdUsuario() == listaComentarios.get(i).getAutor().getIdUsuario()){
                    comentario = listaComentarios.get(i);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    builder.setTitle("Editar álbum");
                    builder.setMessage("O que você deseja fazer?");


                    builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final AlertDialog.Builder builderComentario = new AlertDialog.Builder(getActivity());
                            builderComentario.setTitle("Editar Comentário");
                            final EditText etEditarComentario = new EditText(getActivity());
                            etEditarComentario.setInputType(InputType.TYPE_CLASS_TEXT);
                            etEditarComentario.setHint("Comentário");
                            etEditarComentario.setSingleLine(false);
                            etEditarComentario.setLines(1);
                            etEditarComentario.setMaxLines(20);
                            etEditarComentario.setGravity(Gravity.LEFT | Gravity.TOP);
                            comentario.setTexto(comentario.getTexto());
                            builderComentario.setView(etEditarComentario);


                            builderComentario.setPositiveButton("Editar comentário", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    comentario.setTexto(etEditarComentario.getText().toString());
                                    comentarioService.editarComentario(comentario).enqueue(new retrofit2.Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                                            Toast.makeText
                                                    (getActivity(), "Comentário atualizado!", Toast.LENGTH_SHORT).show();
                                            listaComentarios.set(i, comentario);
                                            adapterComentarios = new CustomListViewComentario
                                                    (getActivity(), listaComentarios);
                                            lvComentarios.setAdapter(adapterComentarios);
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText
                                                    (getActivity(), "Erro em atualizar comentário!", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                }
                            });
                            builderComentario.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), "Operação cancelada", Toast.LENGTH_SHORT).show();
                                }
                            });

                            AlertDialog alertComentario = builderComentario.create();
                            alertComentario.show();
                        }
                    });


                    builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Toast.makeText(getActivity(), "Operação cancelada", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            comentarioService.excluirComentario(comentario.getIdComentario()).enqueue(new retrofit2.Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                                    Toast.makeText(getActivity(), "Comentário deletado!", Toast.LENGTH_SHORT).show();
                                    listaComentarios.remove(comentario);
                                    adapterComentarios = new CustomListViewComentario(getActivity(), listaComentarios);
                                    lvComentarios.setAdapter(adapterComentarios);
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText
                                            (getActivity(), "Erro em deletar comentário!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });
        return view;
    }


    private void inicializaComponentes() {
        this.ivCapa = (ImageView) view.findViewById(R.id.iv_album);
        this.tvNomeAlbum = (TextView) view.findViewById(R.id.tv_nome_album_album);
        this.tvArtistaAlbum = (TextView) view.findViewById(R.id.tv_artista_album);
        this.tvAnoAlbum = (TextView) view.findViewById(R.id.tv_ano_album);
        this.tvMediaAlbum = (TextView) view.findViewById(R.id.tv_avaliacao_media_album);
        this.tvCopy = (TextView) view.findViewById(R.id.tv_copyright_album);
        this.ibAddAlbum = (ImageButton) view.findViewById(R.id.bt_adicionar_album_album);
        this.ibComentarAlbum = (ImageButton) view.findViewById(R.id.bt_comentar_album);
        this.ibAvaliarAlbum = (ImageButton) view.findViewById(R.id.bt_avaliar_album);
        this.ibMostraMusicas = (ImageButton) view.findViewById(R.id.ib_ver_musicas_album);
        this.ibMostraComentarios = (ImageButton) view.findViewById(R.id.ib_ver_comentarios_album);
        this.lvMusicas = (ListView) view.findViewById(R.id.lv_musicas_album);
        this.lvComentarios = (ListView) view.findViewById(R.id.lv_comentarios_album);
        this.listaComentarios = new ArrayList<>();
        this.adapterComentarios = new CustomListViewComentario(getActivity(), listaComentarios);
        lvComentarios.setAdapter(adapterComentarios);
        lvMusicas.setVisibility(View.GONE);
        lvComentarios.setVisibility(View.GONE);
    }

    private void calcularMedia(){
        avaliacaoAlbumService.calcularMediaAlbum(albumServer.getIdAlbum()).enqueue(new retrofit2.Callback<Float>() {
            @Override
            public void onResponse(Call<Float> call, retrofit2.Response<Float> response) {
                if(response.body() != 0){
                    tvMediaAlbum.setText(String.format("%.1f", response.body()));
                }
            }

            @Override
            public void onFailure(Call<Float> call, Throwable t) {

            }
        });
    }

    private void buscarComentarios(){
        comentarioService.buscarPorAlbum(albumServer.getIdAlbum()).enqueue(new retrofit2.Callback<List<Comentario>>() {
            @Override
            public void onResponse(Call<List<Comentario>> call, retrofit2.Response<List<Comentario>> response) {
                if(response.body() != null){
                    listaComentarios = response.body();
                    adapterComentarios = new CustomListViewComentario(getActivity(), listaComentarios);
                    lvComentarios.setAdapter(adapterComentarios);
                    lvComentarios.setVisibility(View.VISIBLE);
                    Log.d("COMENTARIOS", listaComentarios.size()+" comentários");
                }
            }

            @Override
            public void onFailure(Call<List<Comentario>> call, Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível carregar os comentários", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void buscarAvaliacao() {
        avaliacaoAlbumService.buscarPorAlbumEUsuario
                (albumServer.getIdAlbum(), usuario.getIdUsuario()).enqueue(new retrofit2.Callback<AvaliacaoAlbum>() {
            @Override
            public void onResponse(Call<AvaliacaoAlbum> call, retrofit2.Response<AvaliacaoAlbum> response) {
                if(response.body() != null && response.body().getAvaliacao() != 0){
                    avaliacaoAlbum = response.body();
                    avaliacaoAlbum.setAlbum(albumServer);
                    Log.d("AVALIAÇÃO ALBUM", avaliacaoAlbum.getAlbum().getIdAlbum()+" "+avaliacaoAlbum.getAvaliacao());
                }
            }

            @Override
            public void onFailure(Call<AvaliacaoAlbum> call, Throwable t) {

            }
        });
    }


    public static AlbumFragment newInstance(){
        return new AlbumFragment();
    }


}