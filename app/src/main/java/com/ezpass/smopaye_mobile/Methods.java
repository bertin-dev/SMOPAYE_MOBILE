package com.ezpass.smopaye_mobile;

/**
 * Created by delaroy on 3/21/17.
 */
public class Methods {

    public void setColorTheme(){

        switch (Constant.color){
            case 0xff039BE5:
                Constant.theme = R.style.AppTheme;
                break;
            default:
                Constant.theme = R.style.AppTheme_red;
                break;
        }
    }
}
