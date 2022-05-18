package com.example.yahavproject;

import android.widget.Button;

public class Day {
    private String name;
    private Button arrow;

    public Day() {
    }

    public Day (String name, Button arrow) {
        this.name = name;
        this.arrow = arrow;
    }
    public Day (String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Button getArrow() {
        return arrow;
    }

    public void setArrow(Button arrow) {
        this.arrow = arrow;
    }
}
