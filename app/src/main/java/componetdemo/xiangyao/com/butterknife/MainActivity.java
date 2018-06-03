package componetdemo.xiangyao.com.butterknife;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import componetdemo.xiangyao.com.butterknife_annotion.BindView;

/**
 * @author xiangyao
 */
public class MainActivity extends Activity {
    @BindView(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        textView.setText("ahhahahah");

    }
}
