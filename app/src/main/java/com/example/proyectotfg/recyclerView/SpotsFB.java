package com.example.proyectotfg.recyclerView;

public class SpotsFB {

    String title, image1;
    int id;

    public SpotsFB(int id, String title, String image1) {
        this.id = id;
        this.title = title;
        this.image1 = image1;
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

    public String getImage1() {
        return image1;
    }

    public void setImg(String img) {
        this.image1 = image1;
    }

}
