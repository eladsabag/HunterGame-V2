package com.example.huntergame_v2.objects;

public class Player {
    private int x,y;
    private final String name;

    public Player(int x,int y,String name) { this.x=x; this.y=y; this.name=name; }

    public void setX(int x) { this.x=x; }

    public int getX() { return x; }

    public void setY(int y) { this.y=y; }

    public int getY() { return y; }

    public String getName() { return name; }
}