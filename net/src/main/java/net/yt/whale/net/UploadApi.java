//package net.yt.whale.net;
//
//import java.util.Map;
//
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.Multipart;
//import retrofit2.http.POST;
//import retrofit2.http.Part;
//import retrofit2.http.PartMap;
//import retrofit2.http.Path;
//
///**
// * Auth : xiao.yunfei
// * Date : 2020/7/29 11:18
// * Package name : net.yt.whale.net
// * Des :
// */
//public interface UploadApi {
//
//
//    /**
//     * 单个文件上传
//     *
//     * @param file 文件
//     * @return Call<ResponseBody>
//     */
//    @Multipart
//    @POST("{url}")
//    Call<BaseResult<?>> upLoadSingleFile(@Path("url") String url,
//                                         @Part MultipartBody.Part file);
//
//
//    /**
//     * 多个文件上传
//     *
//     * @param partParams 文件 map
//     * @return Call<ResponseBody>
//     */
//    @Multipart
//    @POST("{url}")
//    Call<BaseResult<?>> upLoadFiles(@Path("url") String url,
//                                    @PartMap Map<String, RequestBody> partParams);
//
//    /**
//     * 带参数的单文件上传
//     *
//     * @param body 参数
//     * @param file 文件
//     * @return Call<ResponseBody>
//     */
//    @Multipart
//    @POST("{url}")
//    Call<BaseResult<?>> upLoadSingleFile(@Path("url") String url,
//                                         @Body RequestBody body,
//                                         @Part MultipartBody.Part file);
//
//    /**
//     * 多个文件上传
//     *
//     * @param body       参数
//     * @param partParams 文件
//     * @return Call<ResponseBody>
//     */
//    @Multipart
//    @POST("{url}")
//    Call<BaseResult<?>> upLoadFiles(@Path("url") String url,
//                                    @Body RequestBody body,
//                                    @PartMap Map<String, RequestBody> partParams);
//}