package org.cniska.noc3.poker;

/**
 * User interface logic for the application.
 */
class UI {

    static void outputHeading(String text) {
        outputDivider();
        outputLine(text);
        outputDivider();
    }

    static void outputLine(String text) {
        System.out.println(text);
    }

    static void outputDivider() {
        outputLine("------------------------------------------------------------");
    }

    static void outputError(String text) {
        outputLine("FEL: " + text);
    }
}
