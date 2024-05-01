package com.example.proyectotfg.recyclerView;

public class cardPlace {

    String imgurl, name;

    public cardPlace(String imgurl, String name) {
        this.imgurl = imgurl;
        this.name = name;
    }

    public cardPlace() {
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
