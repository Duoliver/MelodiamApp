package com.example.douglas.melodiam.activities;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.douglas.melodiam.R;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.TrackSimple;

public class CustomListViewMusica extends ArrayAdapter<TrackSimple> {

    private Context context;
    private List<TrackSimple> musicas;

    public CustomListViewMusica(@NonNull Context context, @LayoutRes int res, List<TrackSimple> musicas){
        super(context, 0, musicas);
        this.context = context;
        this.musicas = musicas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(this.context).inflate(R.layout.lv_estilo_musica, parent, false);

        TrackSimple musicaAtual = musicas.get(position);

        TextView nome = (TextView) listItem.findViewById(R.id.tv_nome_musica);
        nome.setText(musicaAtual.name);

        TextView tempo = (TextView) listItem.findViewById(R.id.tv_tempo_musica);
        long tempoReal = musicaAtual.duration_ms;
        long tempoMinutos = (int) tempoReal/60000;

        String minutos = String.valueOf(tempoMinutos);
        String segundos = String.valueOf((tempoReal%60000)/1000);

        switch(segundos.length()){
            case 0:
                segundos = "00";
                break;
            case 1:
                segundos = "0"+segundos;
                break;
            default:
                break;
        }

        tempo.setText(minutos+":"+segundos);

        return listItem;
    }
}
