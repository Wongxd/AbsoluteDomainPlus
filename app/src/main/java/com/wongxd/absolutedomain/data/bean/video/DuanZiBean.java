package com.wongxd.absolutedomain.data.bean.video;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wongxd on 2018/1/23.
 */

public class DuanZiBean {



    private DataBeanX data;
    private String message;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBeanX {

        private boolean has_more;
        private boolean has_new_message;
        private String max_time;
        private String min_time;
        private String tip;
        private List<DataBean> data;

        public boolean isHas_more() {
            return has_more;
        }

        public void setHas_more(boolean has_more) {
            this.has_more = has_more;
        }

        public boolean isHas_new_message() {
            return has_new_message;
        }

        public void setHas_new_message(boolean has_new_message) {
            this.has_new_message = has_new_message;
        }

        public String getMax_time() {
            return max_time;
        }

        public void setMax_time(String max_time) {
            this.max_time = max_time;
        }

        public String getMin_time() {
            return min_time;
        }

        public void setMin_time(String min_time) {
            this.min_time = min_time;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {

            private GroupBean group;
            private int type;


            public GroupBean getGroup() {
                return group;
            }

            public void setGroup(GroupBean group) {
                this.group = group;
            }


            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public static class GroupBean {

                @SerializedName("360p_video")
                private _$360pVideoBean _$360p_video;
                @SerializedName("480p_video")
                private _$480pVideoBean _$480p_video;
                @SerializedName("720p_video")
                private _$480pVideoBean _$720p_video;
                private double duration;
                private long id;
                private LargeCoverBean large_cover;
                private MediumCoverBean medium_cover;
                private String title;
                private String content;

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public _$360pVideoBean get_$360p_video() {
                    return _$360p_video;
                }

                public void set_$360p_video(_$360pVideoBean _$360p_video) {
                    this._$360p_video = _$360p_video;
                }

                public _$480pVideoBean get_$480p_video() {
                    return _$480p_video;
                }

                public void set_$480p_video(_$480pVideoBean _$480p_video) {
                    this._$480p_video = _$480p_video;
                }


                public _$480pVideoBean get_$720p_video() {
                    return _$720p_video;
                }

                public void set_$720p_video(_$480pVideoBean _$720p_video) {
                    this._$720p_video = _$720p_video;
                }

                public double getDuration() {
                    return duration;
                }

                public void setDuration(double duration) {
                    this.duration = duration;
                }


                public long getId() {
                    return id;
                }

                public void setId(long id) {
                    this.id = id;
                }


                public LargeCoverBean getLarge_cover() {
                    return large_cover;
                }

                public void setLarge_cover(LargeCoverBean large_cover) {
                    this.large_cover = large_cover;
                }

                public MediumCoverBean getMedium_cover() {
                    return medium_cover;
                }

                public void setMedium_cover(MediumCoverBean medium_cover) {
                    this.medium_cover = medium_cover;
                }

                public static class _$360pVideoBean {
                    /**
                     * height : 608
                     * uri : 360p/00dee26efc55430db1e470a2e194b39e
                     * url_list : [{"url":"http://ic.snssdk.com/neihan/video/playback/?video_id=00dee26efc55430db1e470a2e194b39e&quality=360p&line=0&is_gif=0&device_platform="},{"url":"http://ic.snssdk.com/neihan/video/playback/?video_id=00dee26efc55430db1e470a2e194b39e&quality=360p&line=1&is_gif=0&device_platform="}]
                     * width : 480
                     */

                    private int height;
                    private String uri;
                    private int width;
                    private List<UrlListBean> url_list;

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }

                    public String getUri() {
                        return uri;
                    }

                    public void setUri(String uri) {
                        this.uri = uri;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public List<UrlListBean> getUrl_list() {
                        return url_list;
                    }

                    public void setUrl_list(List<UrlListBean> url_list) {
                        this.url_list = url_list;
                    }

                    public static class UrlListBean {
                        /**
                         * url : http://ic.snssdk.com/neihan/video/playback/?video_id=00dee26efc55430db1e470a2e194b39e&quality=360p&line=0&is_gif=0&device_platform=
                         */

                        private String url;

                        public String getUrl() {
                            return url;
                        }

                        public void setUrl(String url) {
                            this.url = url;
                        }
                    }
                }

                public static class _$480pVideoBean {
                    /**
                     * height : 608
                     * uri : 480p/00dee26efc55430db1e470a2e194b39e
                     * url_list : [{"url":"http://ic.snssdk.com/neihan/video/playback/?video_id=00dee26efc55430db1e470a2e194b39e&quality=480p&line=0&is_gif=0&device_platform="},{"url":"http://ic.snssdk.com/neihan/video/playback/?video_id=00dee26efc55430db1e470a2e194b39e&quality=480p&line=1&is_gif=0&device_platform="}]
                     * width : 480
                     */

                    private int height;
                    private String uri;
                    private int width;
                    private List<UrlListBeanX> url_list;

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }

                    public String getUri() {
                        return uri;
                    }

                    public void setUri(String uri) {
                        this.uri = uri;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public List<UrlListBeanX> getUrl_list() {
                        return url_list;
                    }

                    public void setUrl_list(List<UrlListBeanX> url_list) {
                        this.url_list = url_list;
                    }

                    public static class UrlListBeanX {
                        /**
                         * url : http://ic.snssdk.com/neihan/video/playback/?video_id=00dee26efc55430db1e470a2e194b39e&quality=480p&line=0&is_gif=0&device_platform=
                         */

                        private String url;

                        public String getUrl() {
                            return url;
                        }

                        public void setUrl(String url) {
                            this.url = url;
                        }
                    }
                }


                public static class LargeCoverBean {
                    /**
                     * uri : large/577e0010be7f358df23a
                     * url_list : [{"url":"http://p9.pstatp.com/large/577e0010be7f358df23a"},{"url":"http://pb1.pstatp.com/large/577e0010be7f358df23a"},{"url":"http://pb3.pstatp.com/large/577e0010be7f358df23a"}]
                     */

                    private String uri;
                    private List<UrlListBeanXXX> url_list;

                    public String getUri() {
                        return uri;
                    }

                    public void setUri(String uri) {
                        this.uri = uri;
                    }

                    public List<UrlListBeanXXX> getUrl_list() {
                        return url_list;
                    }

                    public void setUrl_list(List<UrlListBeanXXX> url_list) {
                        this.url_list = url_list;
                    }

                    public static class UrlListBeanXXX {
                        /**
                         * url : http://p9.pstatp.com/large/577e0010be7f358df23a
                         */

                        private String url;

                        public String getUrl() {
                            return url;
                        }

                        public void setUrl(String url) {
                            this.url = url;
                        }
                    }
                }


                public static class MediumCoverBean {
                    /**
                     * uri : large/577e0010be7f358df23a
                     * url_list : [{"url":"http://p9.pstatp.com/large/577e0010be7f358df23a"},{"url":"http://pb1.pstatp.com/large/577e0010be7f358df23a"},{"url":"http://pb3.pstatp.com/large/577e0010be7f358df23a"}]
                     */

                    private String uri;
                    private List<UrlListBeanXXX> url_list;

                    public String getUri() {
                        return uri;
                    }

                    public void setUri(String uri) {
                        this.uri = uri;
                    }

                    public List<UrlListBeanXXX> getUrl_list() {
                        return url_list;
                    }

                    public void setUrl_list(List<UrlListBeanXXX> url_list) {
                        this.url_list = url_list;
                    }

                    public static class UrlListBeanXXX {
                        /**
                         * url : http://p9.pstatp.com/large/577e0010be7f358df23a
                         */

                        private String url;

                        public String getUrl() {
                            return url;
                        }

                        public void setUrl(String url) {
                            this.url = url;
                        }
                    }
                }
            }
        }
    }
}
