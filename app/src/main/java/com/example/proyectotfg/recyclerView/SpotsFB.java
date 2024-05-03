package com.example.proyectotfg.recyclerView;

public class SpotsFB {

    String title, img;
    int id;

    public SpotsFB(int id, String title, String img) {
        this.id = id;
        this.title = title;
        this.img = img;
    }

    public SpotsFB() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
