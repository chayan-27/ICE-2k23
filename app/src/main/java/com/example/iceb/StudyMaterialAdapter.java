package com.example.iceb;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.iceb.server.Controller;
import com.example.iceb.server.Studymaterial;
import com.example.iceb.server2.FetchInfo2;
import com.example.iceb.server2.SubjectResponse;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class StudyMaterialAdapter extends RecyclerView.Adapter<StudyMaterialAdapter.StudyMaterialHolder> {
    List<SubjectResponse> components;
    Context context;
    String section;
    String subject;
    ProgressBar progressBar;
    String courseplan;


    public StudyMaterialAdapter(List<SubjectResponse> components, Context context, String section, String subject, ProgressBar progressBar, String courseplan) {
        this.components = components;
        this.context = context;
        this.section = section;
        this.subject = subject;
        this.progressBar = progressBar;
        this.courseplan = courseplan;
    }

    @NonNull
    @Override
    public StudyMaterialHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.studymaterialcard, viewGroup, false);

        return new StudyMaterialHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull StudyMaterialHolder studyMaterialHolder, int i) {
        String title = components.get(i).getTopic();;
        if (!(courseplan.equals("yes"))) {


            //  String subject = components.get(i).getSubject();
             title = components.get(i).getTopic();
        } else {
            if (components.get(i).getTopic().equalsIgnoreCase("Courseplan")) {
               // studyMaterialHolder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                studyMaterialHolder.cardView.setVisibility(View.VISIBLE);
            }else{
                //studyMaterialHolder.cardView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                studyMaterialHolder.cardView.setVisibility(View.GONE);

            }
        }
        String update = components.get(i).getUploadDate();
        String extension = components.get(i).getFile().substring(components.get(i).getFile().lastIndexOf("."));
        studyMaterialHolder.textView.setVisibility(View.GONE);
        studyMaterialHolder.textView1.setText(title);
        studyMaterialHolder.textView3.setText(getDate(update));
        String path = "StudyMaterials/" + subject;
        String name = "/" + title + extension;

        File file = new File(Objects.requireNonNull(context.getExternalFilesDir(path)).getAbsolutePath() + name);
        new TestBack(studyMaterialHolder.imgpdf, extension, title).execute(file);


       /* if (file.exists()) {
            check[0] = true;
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
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
            studyMaterialHolder.imgpdf.setImageBitmap(bitmap);

           *//* AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PDFViewfrag(file)).addToBackStack(null).commit();*//*

        } else {
            if (extension.equals("") || extension.equals("pdf") || extension.equals(title)) {

            } else {
                if (extension.equals("pptx") || extension.equals("ppt")) {
                    studyMaterialHolder.imgpdf.setImageResource(R.drawable.ic_icons8_microsoft_powerpoint_2019);
                } else if (extension.equals("doc")) {
                    studyMaterialHolder.imgpdf.setImageResource(R.drawable.ic_icons8_microsoft_word_2019);
                } else if (extension.equals("jpg") || extension.equals("png") || extension.equalsIgnoreCase("jpeg")) {
                    studyMaterialHolder.imgpdf.setImageResource(R.drawable.ic_iconfinder_image_272704);
                } else {
                    studyMaterialHolder.imgpdf.setImageResource(R.drawable.ic_noun_file_);
                }
            }

        }*/
        studyMaterialHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyMaterialHolder.progressBar.setVisibility(View.VISIBLE);
                studyMaterialHolder.progressBar.setMax(100);
                // progressBar=studyMaterialHolder.progressBar;
                animation(0, 50, 10000, studyMaterialHolder.progressBar);
               /* try {
                    if (extension.equals("") || extension.equals("pdf") || extension.equals(title)) {
                        downloadstudymaterial(title, section, subject, studyMaterialHolder.imgpdf, "pdf", studyMaterialHolder.progressBar);
                    } else {
                        downloadstudymaterial(title, section, subject, studyMaterialHolder.imgpdf, extension, studyMaterialHolder.progressBar);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                downloadstudymaterial(file, extension, components.get(i).getFile(), studyMaterialHolder.imgpdf, studyMaterialHolder.progressBar);
            }
        });


    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    public class StudyMaterialHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView1;
        TextView textView3;
        CardView cardView;
        ImageView imgpdf;
        ProgressBar progressBar;

        public StudyMaterialHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.subject);
            textView1 = (TextView) itemView.findViewById(R.id.title);

            textView3 = (TextView) itemView.findViewById(R.id.sdate);
            cardView = (CardView) itemView.findViewById(R.id.cards);
            imgpdf = (ImageView) itemView.findViewById(R.id.imgpdf);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pres);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void downloadstudymaterial(String title, String section, String subject, ImageView imgpdf, String extension, ProgressBar progressBar) throws IOException {
        String path = "StudyMaterials/" + subject;
        String name;
        if (extension.equals("pdf")) {
            name = "/" + title + ".pdf";
        } else {
            name = "/" + title + "." + extension;
        }

        File file = new File(Objects.requireNonNull(context.getExternalFilesDir(path)).getAbsolutePath() + name);
        if (file.exists()) {
            progressBar.setVisibility(View.GONE);
            if (extension.equals(".pdf")) {
                PdfViewAct.file1 = file;
                Intent intent = new Intent(context, PdfViewAct.class);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri apkURI = FileProvider.getUriForFile(
                        context, context.getApplicationContext()

                                .getPackageName() + ".provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(apkURI);
                context.startActivity(intent);
            }
           /* AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PDFViewfrag(file)).addToBackStack(null).commit();*/

        } else {
            Toast.makeText(context, "Processing Please Wait....", Toast.LENGTH_LONG).show();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ice.com.144-208-108-137.ph103.peopleshostshared.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            FetchInfo fetchInfo = retrofit.create(FetchInfo.class);
            Call<Controller> call = fetchInfo.downloadStudyMaterial(title, section);
            //    Toast.makeText(context, ""+ call.request().body().contentLength(), Toast.LENGTH_LONG).show();


            call.enqueue(new Callback<Controller>() {
                @Override
                public void onResponse(Call<Controller> call, Response<Controller> response) {
                    if (!(response.isSuccessful())) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "No Response From The Server", Toast.LENGTH_LONG).show();
                        return;
                    }


                    //  Toast.makeText(context, ""+response.headers().size()+"+++"+response.raw().headers().size(), Toast.LENGTH_LONG).show();


                    List<Studymaterial> list = response.body().getStudymaterial();
                    String he = (String) list.get(0).getStuContent();
                    String ext = list.get(0).getExt();
                    byte[] decodedString = Base64.decode(he.getBytes(), Base64.DEFAULT);
                    String path = "StudyMaterials/" + subject;
                    String name = "";


                    name = "/" + title + "." + ext;


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
                    if (ext.equals("pdf") || ext.equals(null) || ext.equals("")) {
                        PdfViewAct.file1 = file;
                        Intent intent = new Intent(context, PdfViewAct.class);
                        context.startActivity(intent);

                        ParcelFileDescriptor parcelFileDescriptor = null;
                        try {
                            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
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
                    } else {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri apkURI = FileProvider.getUriForFile(
                                context, context.getApplicationContext()

                                        .getPackageName() + ".provider", root);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setData(apkURI);
                        context.startActivity(intent);

                    }


                    //  appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PDFViewfrag(file)).addToBackStack(null).commit();


                }

                @Override
                public void onFailure(Call<Controller> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Error Occured!!Please Try Again Later", Toast.LENGTH_LONG).show();

                }


            });
        }
    }

    public void downloadstudymaterial(File file, String extension, String url, ImageView imageView, ProgressBar progressBar) {
        if (file.exists()) {
            progressBar.setVisibility(View.GONE);
            if (extension.equals(".pdf")) {
                PdfViewAct.file1 = file;
                Intent intent = new Intent(context, PdfViewAct.class);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri apkURI = FileProvider.getUriForFile(
                        context, context.getApplicationContext()

                                .getPackageName() + ".provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(apkURI);
                context.startActivity(intent);
            }
        } else {
           String base="https://academic-manager-nitt.el.r.appspot.com/";
        
       // String base="https://academic-manager-nitt.el.r.appspot.com/";
        
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            FetchInfo2 fetchInfo = retrofit.create(FetchInfo2.class);
            String fileurl = base + url.substring(1);
            Call<ResponseBody> call1 = fetchInfo.downloadFileWithDynamicUrlSync(url);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "No Response From The Server", Toast.LENGTH_LONG).show();
                        return;
                    }
                    boolean b = writeResponseBodyToDisk(response.body(), file);
                    progressBar.setVisibility(View.GONE);
                    if (b) {
                        if (extension.equals(".pdf")) {
                            PdfViewAct.file1 = file;
                            Intent intent = new Intent(context, PdfViewAct.class);
                            context.startActivity(intent);
                            new TestBack(imageView, extension, "").execute(file);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri apkURI = FileProvider.getUriForFile(
                                    context, context.getApplicationContext()

                                            .getPackageName() + ".provider", file);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setData(apkURI);
                            context.startActivity(intent);
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Some Error Occured!Please Try Again", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        }

    }

    public void animation(int a, int b, int time, ProgressBar progressBar) {
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
                    animation(50, 75, 20000, progressBar);
                } else if (b == 75) {
                    animation(75, 88, 40000, progressBar);
                } else if (b == 88) {
                    animation(88, 94, 80000, progressBar);
                } else if (b == 94) {
                    animation(94, 97, 160000, progressBar);
                } else if (b == 97) {
                    animation(97, 99, 320000, progressBar);
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

    public class TestBack extends AsyncTask<File, Void, String> {


        ImageView imageView;
        String extension;
        String title;
        Bitmap bitmap1;

        public TestBack(ImageView imageView, String extension, String title) {
            this.imageView = imageView;
            this.extension = extension;
            this.title = title;
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This will normally run on a background thread. But to better
         * support testing frameworks, it is recommended that this also tolerates
         * direct execution on the foreground thread, as part of the {@link #execute} call.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param files The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(File... files) {
            if (files[0].exists() && extension.equals(".pdf")) {

                ParcelFileDescriptor parcelFileDescriptor = null;
                try {
                    parcelFileDescriptor = ParcelFileDescriptor.open(files[0], ParcelFileDescriptor.MODE_READ_ONLY);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                PdfRenderer renderer = null;
                try {
                    renderer = new PdfRenderer(parcelFileDescriptor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap1 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
                PdfRenderer.Page page = renderer.openPage(0);
                page.render(bitmap1, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                return "exists";

           /* AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PDFViewfrag(file)).addToBackStack(null).commit();*/

            } else {
                return "no";

            }

        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.
         * To better support testing frameworks, it is recommended that this be
         * written to tolerate direct execution as part of the execute() call.
         * The default version does nothing.</p>
         *
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param s The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         */
        @Override
        protected void onPostExecute(String s) {
            if (s.equals("exists")) {
                imageView.setImageBitmap(bitmap1);

            } else {
                if (extension.equals("") || extension.equals(title)) {

                } else {
                    if (extension.equals(".pptx") || extension.equals(".ppt")) {
                        imageView.setImageResource(R.drawable.ic_icons8_microsoft_powerpoint_2019);
                    } else if (extension.equals(".doc")) {
                        imageView.setImageResource(R.drawable.ic_icons8_microsoft_word_2019);
                    } else if (extension.equals(".jpg") || extension.equals(".png") || extension.equalsIgnoreCase(".jpeg")) {
                        imageView.setImageResource(R.drawable.ic_iconfinder_image_272704);
                    } else if (extension.equals(".pdf")) {
                        imageView.setImageResource(R.drawable.pdfic);
                    } else {
                        imageView.setImageResource(R.drawable.ic_noun_file_);
                    }
                }
            }
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, File file) {
        try {

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();


                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public String getDate(String date) {
        String actual_date = date.substring(0, date.indexOf("T"));
        String actual_time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
        String now = actual_date + " " + actual_time;
        String[] dead = now.split(" ");
        String[] dead1 = dead[0].split("-");
        String dead2 = dead1[2] + "-" + dead1[1] + "-" + dead1[0];
        String[] dead3 = dead[1].split(":");
        Integer h1 = Integer.parseInt(dead3[0]);
        String period = "";
        if (h1 > 12) {
            h1 = h1 - 12;
            period = "PM";
        } else {
            if (h1 == 0) {
                h1 = 12;
            }
            period = "AM";
        }
        String findead = dead2 + " " + h1 + ":" + dead3[1] + " " + period;

        return findead;
    }
}
