package com.altomedia.altoindoapp.models;

public class User {
    public String uid, member_id, upline_id, full_name, email, role, tier, security_pin;
    public boolean is_active;
    public long balance_wallet, points_personal, points_group;

    public User() {} // Required for Firebase

    public User(String uid, String member_id, String email, String full_name, String upline_id) {
        this.uid = uid;
        this.member_id = member_id;
        this.email = email;
        this.full_name = full_name;
        this.upline_id = upline_id;
        this.role = "member";
        this.tier = "bronze";
        this.is_active = false;
        this.balance_wallet = 0;
        this.points_personal = 0;
        this.points_group = 0;
        this.security_pin = "123456";
    }
}
