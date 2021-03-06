package com.example.iceb;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iceb.server.Assignment;
import com.example.iceb.server.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentHolder> {
    List<Assignment> components;
    Context context;
    String section;
    ProgressBar progressBar;
    int roll;
    int semester;
    String l;
    String subh;

    public AssignmentAdapter(List<Assignment> components, Context context, String section, ProgressBar progressBar, int roll, int semester) {
        this.components = components;
        this.context = context;
        this.section = section;
        this.progressBar = progressBar;
        this.roll = roll;
        this.semester = semester;
    }

    @NonNull
    @Override
    public AssignmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.assigncard, viewGroup, false);
        return new AssignmentHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AssignmentHolder assignmentHolder, int i) {
        String subject = components.get(i).getSubject();
        String title = components.get(i).getTitle();
        String update = components.get(i).getUploadDate();
        String[] up1 = new String[2];
        try {


            up1 = update.split("&");
            if (up1.length == 0) {
                up1 = new String[2];
                up1[1] = update;
                l = "";
                up1[0] = "";
            } else {
                l = up1[0];
            }
        } catch (Exception e) {
        }
        String subdate = components.get(i).getSubbmissionDate();
        subh=subdate;
        assignmentHolder.textView.setText(subject);
        assignmentHolder.textView1.setText(title);
        try {


            assignmentHolder.textView2.setText("Sent : " + up1[1]);
            assignmentHolder.textView3.setText("DeadLine : " + subdate + "\n" + up1[0]);
        } catch (Exception e) {
            assignmentHolder.textView2.setText("Sent : " + update);
            assignmentHolder.textView3.setText("DeadLine : " + subdate);

        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse(subdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (System.currentTimeMillis() <= strDate.getTime()) {
            assignmentHolder.cardView.setCardBackgroundColor(Color.parseColor("#88F39E"));
        } else {
            assignmentHolder.cardView.setCardBackgroundColor(Color.parseColor("#ff726f"));
            if (subdate.equals(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()))) {
                assignmentHolder.textView2.setTextColor(Color.BLACK);
                try {
                    if (up1[0].charAt(up1[0].length() - 1) == '0') {
                        assignmentHolder.textView3.setText("DeadLine : Today");
                    }else{
                        assignmentHolder.textView3.setText("DeadLine : Today\n" + up1[0]);

                    }

                } catch (Exception e) {
                    assignmentHolder.textView3.setText("DeadLine : Today");
                }
                assignmentHolder.textView3.setTextColor(Color.BLACK);
            }
            //Toast.makeText(context,new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()),Toast.LENGTH_LONG).show();


        }
        String path = "Assignments/" + subject;
        String name = "/" + title + ".pdf";
        File file = new File(Objects.requireNonNull(context.getExternalFilesDir(path)).getAbsolutePath() + name);
        if (file.exists()) {
            ParcelFileDescriptor parcelFileDescriptor= null;
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            PdfRenderer renderer = null;
            try {
                renderer = new PdfRenderer(parcelFileDescriptor);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
            PdfRenderer.Page page = renderer.openPage(0);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            assignmentHolder.imgpdf.setImageBitmap(bitmap);

        }


        assignmentHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               assignmentHolder. progressBar.setVisibility(View.VISIBLE);
               animation(0,50,10000,assignmentHolder.progressBar);
                downloadassign(title, section, subject,assignmentHolder.imgpdf,assignmentHolder.progressBar);
            }
        });

    }


    @Override
    public int getItemCount() {
        return components.size();
    }

    public class AssignmentHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView1;
        TextView textView2;
        TextView textView3;
        CardView cardView;
        ImageView imgpdf;
        ProgressBar progressBar;

        public AssignmentHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.subject);
            textView1 = (TextView) itemView.findViewById(R.id.title);
            textView2 = (TextView) itemView.findViewById(R.id.udate);
            textView3 = (TextView) itemView.findViewById(R.id.sdate);
            cardView = (CardView) itemView.findViewById(R.id.cards);
            imgpdf=(ImageView)itemView.findViewById(R.id.imgpdf);
            progressBar=(ProgressBar)itemView.findViewById(R.id.pres);
        }
    }

    public void downloadassign(String title, String section, String subject,ImageView imgpdf,ProgressBar progressBar) {
        String path = "Assignments/" + subject;
        String name = "/" + title + ".pdf";
        File file = new File(Objects.requireNonNull(context.getExternalFilesDir(path)).getAbsolutePath() + name);
        if (file.exists()) {
            progressBar.setVisibility(View.GONE);
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;

            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UploadAssignF(file, section, roll, subject, title, semester,subh)).addToBackStack(null).commit();

        } else {
            Toast.makeText(context, "Processing Please Wait....", Toast.LENGTH_LONG).show();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ice.com.144-208-108-137.ph103.peopleshostshared.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            FetchInfo fetchInfo = retrofit.create(FetchInfo.class);
            Call<Controller> call = fetchInfo.downloadAssignment(title, section);
            call.enqueue(new Callback<Controller>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onResponse(Call<Controller> call, Response<Controller> response) {
                    if (!(response.isSuccessful())) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "No Response From The Server", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<Assignment> list = response.body().getAssignments();
                    String he = (String) list.get(0).getAssContent();
                    byte[] decodedString = Base64.decode(he.getBytes(), Base64.DEFAULT);
                    File root = new File(Objects.requireNonNull(context.getExternalFilesDir(path)).getAbsolutePath() + name);
                    try {
                        OutputStream fileOutputStream = new FileOutputStream(root);
                        fileOutputStream.write(decodedString);
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Your File is Downloaded in your Internal storage/Android/data/com.example.iceb/files", Toast.LENGTH_LONG).show();
                    AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                    progressBar.setVisibility(View.GONE);
                    // Toast.makeText(context, "File found", Toast.LENGTH_LONG).show();

                    appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UploadAssignF(file, section, roll, subject, title, semester,subh)).addToBackStack(null).commit();

                    ParcelFileDescriptor parcelFileDescriptor= null;
                    try {
                        parcelFileDescriptor = ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    PdfRenderer renderer = null;
                    try {
                        renderer = new PdfRenderer(parcelFileDescriptor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
                    PdfRenderer.Page page = renderer.openPage(0);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    imgpdf.setImageBitmap(bitmap);


                }

                @Override
                public void onFailure(Call<Controller> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Error Occured!!Please Try Again Later", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    public void animation(int a, int b, int time,ProgressBar progressBar) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", a, b);
        animation.setDuration(time);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //do something when the countdown is complete
                if (b == 50) {
                    animation(50, 75, 20000,progressBar);
                } else if (b == 75) {
                    animation(75, 88, 40000,progressBar);
                } else if (b == 88) {
                    animation(88, 94, 80000,progressBar);
                } else if (b == 94) {
                    animation(94, 97, 160000,progressBar);
                } else if (b == 97) {
                    animation(97, 99, 320000,progressBar);
                }
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
}
