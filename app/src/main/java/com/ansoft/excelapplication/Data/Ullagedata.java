package com.ansoft.excelapplication.Data;

import android.widget.EditText;

/**
 * Created by Bibek on 4/8/2017.
 */

public class Ullagedata {

    int feet;
    int inch;
    int numerator;
    int denominator;
    double temperature;

    public Ullagedata(EditText feet, EditText inch, EditText fraction, EditText denomi, EditText temperature) {
        if(!feet.getText().toString().isEmpty()){
            this.feet=Integer.parseInt(feet.getText().toString());
        }else{
            this.feet=0;
        }
        if(!inch.getText().toString().isEmpty()){
            this.inch=Integer.parseInt(inch.getText().toString());
        }else{
            this.inch=0;
        }
        if(!fraction.getText().toString().isEmpty()){
            this.numerator =Integer.parseInt(fraction.getText().toString());
        }else{
            this.numerator =0;
        }

        if(!denomi.getText().toString().isEmpty()){
            this.denominator =Integer.parseInt(denomi.getText().toString());
        }else{
            this.denominator =0;
        }
        if(!temperature.getText().toString().isEmpty()){
            this.temperature=Double.parseDouble(temperature.getText().toString());
        }else{
            this.temperature=0.0;
        }
    }

    public String getUllage(){
        if(denominator==0 || numerator==0){
            return feet+"' "+inch+"\"";
        }
        return feet+"' "+inch+" "+numerator+"/"+denominator+"\"";
    }

    public String getTemperature(){
        return temperature+"";
    }

}
