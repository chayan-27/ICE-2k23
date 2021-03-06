package com.example.iceb;

import com.example.iceb.server.APKcl;
import com.example.iceb.server.Controller;
import com.example.iceb.server.Poll;
import com.example.iceb.server.UserFile;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface FetchInfo {
    @GET("Service1.svc/DownloadTimeTable")
    Call<Controller> getTimetest(@Query("sem") Integer sem, @Query("section") String section);

    @GET("Service1.svc/GetAssignment")
    Call<Controller> getAssignment(@Query("sem") Integer sem, @Query("section") String section);

    @GET("Service1.svc/DownloadAssignment")
    Call<Controller> downloadAssignment(@Query("Title") String title, @Query("section") String section);

    @GET("Service1.svc/GetStudyMaterial")
    Call<Controller> getstudymaterial(@Query("sem") Integer sem, @Query("section") String section, @Query("subject") String subject);

    @GET("Service1.svc/GetStudyMaterialSubject")
    Call<Controller> getstudymaterialsubject(@Query("sem") Integer sem, @Query("section") String section);


    @GET("Service1.svc/DownloadStudyMaterial")
    Call<Controller> downloadStudyMaterial(@Query("Title") String title, @Query("section") String section);

    @GET("Service1.svc/GetCoursePlan")
    Call<Controller> getcourseplan(@Query("sem") Integer sem, @Query("section") String section);

    @GET("Service1.svc/DownloadCoursePlan")
    Call<Controller> downloadCoursePlan(@Query("Subject") String subject, @Query("section") String section);

    @POST("Service1.svc/UploadUserFile")
    Call<Controller> sendassign(@Body UserFile userFile);

    @POST("Service1.svc/UnsubmitAssignment")
    Call<Controller> unsubassign(@Body UserFile userFile);

    @GET("Service1.svc/getpolllist")
    Call<Controller> getpolllist(@Query("sem") Integer sem, @Query("section") String section);

    @POST("Service1.svc/PollVote")
    Call<Controller> pollvote(@Body Poll poll);

    @POST("Service1.svc/downloadapk")
    Call<Controller> downloadapk(@Body APKcl apKcl);

    @GET("Service1.svc/viewpoll")
    Call<Controller> viewpoll(@Query("pid") Integer pid);
}
