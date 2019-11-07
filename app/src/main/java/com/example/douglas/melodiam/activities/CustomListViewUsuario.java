package com.example.douglas.melodiam.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.model.Amizade;
import com.example.douglas.melodiam.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class CustomListViewUsuario extends ArrayAdapter<Usuario> {
    private Usuario usuario;


    private List<Usuario> usuarios;
    private Activity context;

    @SuppressLint("ResourceType")
    public CustomListViewUsuario(Activity context,  List<Usuario> usuarios) {
        super(context, 0, usuarios);

        this.context = context;
        this.usuarios = usuarios;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;

        if(r == null) {
            r = LayoutInflater.from(this.context).inflate(R.layout.lv_estilo_usuario, parent, false);
        }

        Usuario usuario = usuarios.get(position);
        TextView login = r.findViewById(R.id.tv_login_usuario_busca);
        login.setText(usuario.getLogin());

        return r;
    }



}