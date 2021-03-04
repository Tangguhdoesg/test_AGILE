package com.example.flourish;

public class User {
    private String name, profilePic, ID;

    public User(String name, String profilePic, String ID) {
        this.name = name;
        this.profilePic = profilePic;
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
