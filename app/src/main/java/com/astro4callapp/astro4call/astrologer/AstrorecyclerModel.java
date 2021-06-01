package com.astro4callapp.astro4call.astrologer;

public class AstrorecyclerModel {

    String name, exp, charge, image, uesrId, rating, status;

    public AstrorecyclerModel() {
    }

    public AstrorecyclerModel(String name, String exp, String charge, String image, String uesrId) {
        this.name = name;
        this.exp = exp;
        this.charge = charge;
        this.image = image;
        this.uesrId = uesrId;
    }

    public AstrorecyclerModel(String uesrId) {
        this.uesrId = uesrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUesrId() {
        return uesrId;
    }

    public void setUesrId(String uesrId) {
        this.uesrId = uesrId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
