package t32games.chameleon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import t32games.chameleon.model.Model;
import t32games.chameleon.presenter.Presenter;
import t32games.chameleon.view.FragmentControl;

public class AcMain extends AppCompatActivity {
    Model m;
    Presenter p;
    FragmentControl fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        m = new Model();
        p = new Presenter();
        fc = new FragmentControl(getFragmentManager(),R.id.AcMainContainer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fc.setPresenter(p);
        p.initialize(fc,m,null);
    }
}
