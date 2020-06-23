package com.huanyuenwei.Entuty;

import lombok.Data;

@Data
public class DeveloperApiInfo {

    private String name;

    private String email;

    private String role;

    public DeveloperApiInfo() {
    }

    public DeveloperApiInfo(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

}
