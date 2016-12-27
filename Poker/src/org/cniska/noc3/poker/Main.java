package org.cniska.noc3.poker;

public class Main {

    public static void main(String[] args) {
        Config config = new Config();
        App app = new App(config);

        app.run();
    }
}
