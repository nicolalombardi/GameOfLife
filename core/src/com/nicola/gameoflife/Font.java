package com.nicola.gameoflife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;

/**
 * Created by Nicola Lombardi on 25/10/2016.
 */
public class Font {
    FreeTypeFontGenerator fontGenerator;
    FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    HashMap<Integer, BitmapFont> fonts;

    public Font(){
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("GreenFlame.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fonts = new HashMap<Integer, BitmapFont>();
    }

    public BitmapFont getFont(int size, Color color){
        if(fonts.get(size) == null){
            fontParameter.size = size;
            fonts.put(size,fontGenerator.generateFont(fontParameter));
        }
        BitmapFont temp = fonts.get(size);
        temp.setColor(color);
        return temp;
    }

}
