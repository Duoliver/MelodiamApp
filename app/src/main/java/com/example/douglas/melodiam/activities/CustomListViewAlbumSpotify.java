package com.example.douglas.melodiam.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.douglas.melodiam.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;

public class CustomListViewAlbumSpotify extends ArrayAdapter<Album> {

    private Activity context;
    private List<Album> listaAlbuns;

    @SuppressLint("ResourceType")
    public CustomListViewAlbumSpotify(Activity context, List<Album> listaAlbuns) {
        super(context, 0, listaAlbuns);
        this.context = context;
        this.listaAlbuns = listaAlbuns;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;

        if (r == null) {
            r = LayoutInflater.from(this.context).inflate(R.layout.lv_estilo_album, parent, false);
        }

        Album album = (Album) listaAlbuns.get(position);

        Log.d("ALBUM", album.name + "");
        TextView nome = (TextView) r.findViewById(R.id.tv_nome_album);
        nome.setText(album.name);

        TextView autor = (TextView) r.findViewById(R.id.tv_tipo_album);
        autor.setText(album.artists.get(0).name);

        ImageView capa = (ImageView) r.findViewById(R.id.iv_capa);
        Picasso.get().load(album.images.get(2).url).into(capa);
        return r;

    }
}
