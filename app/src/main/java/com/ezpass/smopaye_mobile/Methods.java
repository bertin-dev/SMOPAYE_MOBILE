package com.ezpass.smopaye_mobile;


/**
 * @see Methods
 *
 * class permettant de définir la couleur du thème
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
