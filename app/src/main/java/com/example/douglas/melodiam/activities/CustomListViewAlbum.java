package com.example.douglas.melodiam.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.LayoutRes;
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

import kaaes.spotify.webapi.android.models.AlbumSimple;

public class CustomListViewAlbum extends ArrayAdapter<AlbumSimple> {


    private Activity context;
    private List<AlbumSimple> listaAlbuns;

    @SuppressLint("ResourceType")
    public CustomListViewAlbum(Activity context, List<AlbumSimple> listaAlbuns) {
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

        AlbumSimple album = (AlbumSimple) listaAlbuns.get(position);

        Log.d("ALBUM", album.name + "");
        TextView nome = (TextView) r.findViewById(R.id.tv_nome_album);
        nome.setText(album.name);

        TextView tipo = (TextView) r.findViewById(R.id.tv_tipo_album);
        tipo.setText(album.type.toUpperCase());

        ImageView capa = (ImageView) r.findViewById(R.id.iv_capa);
        Picasso.get().load(album.images.get(2).url).into(capa);
        return r;

    }
}