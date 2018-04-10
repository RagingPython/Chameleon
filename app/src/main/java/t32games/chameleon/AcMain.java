package t32games.chameleon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import t32games.chameleon.model.TestModel;
import t32games.chameleon.presenter.TestPresenter;
import t32games.chameleon.view.FragmentControl;

public class AcMain extends AppCompatActivity {
    TestModel m;
    TestPresenter p;
    FragmentControl fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        m = new TestModel();
        p = new TestPresenter();
        fc = new FragmentControl(getFragmentManager(),R.id.AcMainContainer);

        //fc.setPresenter(p);
        //p.initialize(fc,m,null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fc.setPresenter(p);
        p.initialize(fc,m,null);
    }
}
