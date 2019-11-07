package com.example.douglas.melodiam.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Comentario;

import java.util.List;

public class CustomListViewComentario extends ArrayAdapter<Comentario> {

    private Activity context;
    private List<Comentario> comentarios;

    public CustomListViewComentario(Activity context, List<Comentario> comentarios){
        super(context, 0, comentarios);
        this.context = context;
        this.comentarios = comentarios;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        View r = convertView;

        if(r == null){
            r = LayoutInflater.from(this.context).inflate(R.layout.lv_estilo_comentario, parent, false);
        }

        Comentario comentario = comentarios.get(position);

        TextView autor = r.findViewById(R.id.tv_autor_comentario);
        autor.setText(comentario.getAutor().getLogin());

        TextView data = r.findViewById(R.id.tv_datahora_comentario);
        data.setText(comentario.getData());

        TextView texto = r.findViewById(R.id.tv_texto_comentario);
        texto.setText(comentario.getTexto());

        return r;
    }
}
