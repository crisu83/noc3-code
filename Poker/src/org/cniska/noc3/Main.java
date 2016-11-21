package org.cniska.noc3;

public class Main {

    public static void main(String[] args) {
        Config config = new Config();
        App app = new App(config);

        app.run();
    }
}
