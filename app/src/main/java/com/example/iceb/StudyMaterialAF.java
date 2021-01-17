package com.example.iceb;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.iceb.AdminUsage.AssignmentUP;
import com.example.iceb.AdminUsage.Studymf;
import com.example.iceb.AdminUsage.Studymf2;
import com.example.iceb.server.Controller;
import com.example.iceb.server.Studymaterial;
import com.example.iceb.server2.FetchInfo2;
import com.example.iceb.server2.SubjectResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class StudyMaterialAF extends Fragment {
    Integer semester;
    String subject;
    String section;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    String subject_id;
    String courseplan;
    boolean admin;
    String assignment;
    String batch;

    @SuppressLint("ValidFragment")
    public StudyMaterialAF(Integer semester, String subject, String section, String subject_id, String courseplan, boolean admin, String assignment,String batch) {
        // Required empty public constructor
        this.section = section;
        this.semester = semester;
        this.subject = subject;
        this.subject_id = subject_id;
        this.courseplan = courseplan;
        this.admin = admin;
        this.assignment = assignment;
        this.batch=batch;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_material_a, container, false);
        recyclerView = view.findViewById(R.id.recycle);
        progressBar = view.findViewById(R.id.pres);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        animation();

        materials(subject_id);
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ice.com.144-208-108-137.ph103.peopleshostshared.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FetchInfo fetchInfo = retrofit.create(FetchInfo.class);
        Call<Controller> call=fetchInfo.getstudymaterial(semester,section,subject);
        call.enqueue(new Callback<Controller>() {
            @Override
            public void onResponse(Call<Controller> call, Response<Controller> response) {
                if (!(response.isSuccessful())) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No Response From The Server", Toast.LENGTH_LONG).show();
                    return;
                }
                List<Studymaterial> list = response.body().getStudymaterial();
                recyclerView.setAdapter(new StudyMaterialAdapter(list, getContext(), section,subject,progressBar));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Controller> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error Occured!!Please Try Again Later", Toast.LENGTH_LONG).show();


            }
        });*/

        if (admin) {
            final boolean[] check = {false};
            FloatingActionButton fab = view.findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Studymf(section, "", subject_ids, subject_name, batch)).addToBackStack(null).commit();
                    if (assignment.equals("yes")) {
                      //  getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AssignmentUP(section, "", null, null, batch,subject,subject_id)).addToBackStack(null).commit();

                    } else {
                       //  fab.setVisibility(View.GONE);
                        if(!check[0]){
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Studymf2(subject, subject_id, section, courseplan)).addToBackStack(null).commit();
                            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_delete));
                            check[0] =true;



                        }else {
                            getActivity().getSupportFragmentManager().popBackStack();
                            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
                            check[0] =false;


                            //fab.setVisibility(View.GONE);
                        }

                    }

                }
            });
        } else {
            FloatingActionButton fab = view.findViewById(R.id.fab);
            fab.setVisibility(View.GONE);
        }


        return view;
    }

    public void animation() {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 180);
        animation.setDuration(20000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //do something when the countdown is complete
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animation.start();
    }

    public void materials(String subject_id) {
        String base = "https://academic-manager-nitt.el.r.appspot.com/";

        // String base="https://academic-manager-nitt.el.r.appspot.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FetchInfo2 fetchInfo = retrofit.create(FetchInfo2.class);
        Call<List<SubjectResponse>> call = fetchInfo.loadsubjectmaterial(subject_id);
        call.enqueue(new Callback<List<SubjectResponse>>() {
            @Override
            public void onResponse(Call<List<SubjectResponse>> call, Response<List<SubjectResponse>> response) {
                if (!(response.isSuccessful())) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No Response From The Server", Toast.LENGTH_LONG).show();
                    return;
                }

                recyclerView.setAdapter(new StudyMaterialAdapter(response.body(), getContext(), section, subject, progressBar, courseplan));
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<List<SubjectResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error Occured!!Please Try Again Later", Toast.LENGTH_LONG).show();


            }
        });
    }

}
