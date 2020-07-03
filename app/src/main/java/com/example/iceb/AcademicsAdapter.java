package com.example.iceb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AcademicsAdapter extends RecyclerView.Adapter<AcademicsAdapter.AcadHolder> {
    List<String> components;
    Context context;
    int ar[];
    String section;
    String url="";

    public AcademicsAdapter(List<String> components, Context context, int[] ar,String section) {
        this.components = components;
        this.context = context;
        this.ar = ar;
        this.section=section;
    }

    @NonNull
    @Override
    public AcadHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.compo, viewGroup, false);
        return new AcadHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AcadHolder acadHolder, int i) {
        acadHolder.textView.setText(components.get(i).toUpperCase());
        acadHolder.imageView.setImageResource(ar[i]);
        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        FragmentManager fragment = appCompatActivity.getSupportFragmentManager();
        acadHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (i) {
                    case 0:
                        fragment.beginTransaction().replace(R.id.fragment_container, new Timet(section)).addToBackStack(null).commit();
                        break;
                    case 1:
                        fragment.beginTransaction().replace(R.id.fragment_container, new StudyMaterialF(section)).addToBackStack(null).commit();
                        break;
                    case 2:
                        fragment.beginTransaction().replace(R.id.fragment_container, new CoursePLanF(section)).addToBackStack(null).commit();
                        break;
                    case 3:
                        fragment.beginTransaction().replace(R.id.fragment_container, new Attendance_Test()).addToBackStack(null).commit();
                        break;
                    case 4:
                        if(section.equals("ICEA")){
                            url="https://docs.google.com/forms/d/e/1FAIpQLSel5KahoMyq6gfUXQolISYV5iyPum1BU4Kwxw-DLvyyGzOQiw/viewform";
                        }else{
                            url="https://docs.google.com/forms/d/e/1FAIpQLScd26k0BVjrqC6LQ0UoTpg7a-BOSGI6de3J1cvu5JfPfEqVEg/viewform";
                        }
                        fragment.beginTransaction().replace(R.id.fragment_container, new Studymf(url)).addToBackStack(null).commit();
                        break;
                    case 5:
                        if(section.equals("ICEA")){
                            url="https://drive.google.com/drive/folders/1VOA7oh1lgWQq5TNyWMv6EAIHz_BEgny8?usp=sharing";
                        }else{
                            url="https://drive.google.com/drive/u/2/folders/1Em0c6h8scxmh3ZInGDCI3FFrdWtNnYtq";
                        }
                        fragment.beginTransaction().replace(R.id.fragment_container, new Studymf(url)).addToBackStack(null).commit();
                        break;
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    public class AcadHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        CardView cardView;

        public AcadHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            cardView = (CardView) itemView.findViewById(R.id.cards);
            imageView = (ImageView) itemView.findViewById(R.id.pokemi);

        }
    }
}