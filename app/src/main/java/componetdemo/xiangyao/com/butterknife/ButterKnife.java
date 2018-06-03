package componetdemo.xiangyao.com.butterknife;

import android.app.Activity;

/**
 * Created by xiangyao on 2018/6/2.
 */

public class ButterKnife {

    public static void bind(Activity activity) {

        Class<? extends Activity> aClass = activity.getClass();

        String viewBiderName = aClass.getName() + "$ViewBinder";

        try {
            Class<?> viewBidnerClass = Class.forName(viewBiderName);

            ViewBinder viewBinder = (ViewBinder) viewBidnerClass.newInstance();

            viewBinder.bind(activity);

        } catch (Exception e) {


        }


    }

}
