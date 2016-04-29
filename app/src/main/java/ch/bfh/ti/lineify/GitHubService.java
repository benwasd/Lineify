package ch.bfh.ti.lineify;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubService {
    @GET("users/{user}")
    Call<User> users(@Path("user") String user);
}