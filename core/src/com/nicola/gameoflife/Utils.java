package com.nicola.gameoflife;

public class Utils {
    public static float getYFromXOfLine(int lineFromX, int lineFromY, int lineToX, int lineToY, int x){
        int diffX =  lineFromX - lineToX;
        int diffY = lineFromY - lineToY;

        float m = Float.MAX_VALUE;
        if (diffX != 0){
            m = (diffY * 1.0f)/diffX;
        }

        return Math.round(m * (x - lineFromX) + lineFromY);
    }

    public static float getXFromYOfLine(int lineFromX, int lineFromY, int lineToX, int lineToY, int y){
        int diffX =  lineFromX - lineToX;
        int diffY = lineFromY - lineToY;

        float m = 0;
        if (diffY != 0){
            m = (diffY * 1.0f)/diffX;
        }

        if (diffX == 0) {
            return lineFromX;
        }
        return Math.round((y - lineFromY + (m * lineFromX))/m);
    }
}
