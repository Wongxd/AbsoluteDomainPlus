package com.github.wongxd.core_lib.data.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by wongxd on 2017/12/19.
 */

public class UserBean extends BmobUser {

    private Boolean isShowAd = true;

    private Boolean isShowActivity = true;

    private String nickName;

    private String qqHeader;

    //图集收藏
    private BmobFile imgFavorite;

    private BmobFile videoFavorite;

    private BmobFile textFavorite;

    public String getQqHeader() {
        return qqHeader;
    }

    public void setQqHeader(String qqHeader) {
        this.qqHeader = qqHeader;
    }

    public Boolean getShowAd() {
        return isShowAd;
    }

    public void setShowAd(Boolean showAd) {
        isShowAd = showAd;
    }

    public Boolean getShowActivity() {
        return isShowActivity;
    }

    public void setShowActivity(Boolean showActivity) {
        isShowActivity = showActivity;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public BmobFile getImgFavorite() {
        return imgFavorite;
    }

    public void setImgFavorite(BmobFile imgFavorite) {
        this.imgFavorite = imgFavorite;
    }

    public BmobFile getVideoFavorite() {
        return videoFavorite;
    }

    public void setVideoFavorite(BmobFile videoFavorite) {
        this.videoFavorite = videoFavorite;
    }

    public BmobFile getTextFavorite() {
        return textFavorite;
    }

    public void setTextFavorite(BmobFile textFavorite) {
        this.textFavorite = textFavorite;
    }
}
