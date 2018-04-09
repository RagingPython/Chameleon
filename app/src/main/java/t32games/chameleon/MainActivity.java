package t32games.chameleon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.reactivex.Observable;
import t32games.chameleon.model.ModelFacade;
import t32games.chameleon.model.TestModel;
import t32games.chameleon.presenter.PresenterFacade;
import t32games.chameleon.presenter.TestPresenter;
import t32games.chameleon.view.FragmentControl;

public class MainActivity extends AppCompatActivity {
    TestModel m;
    TestPresenter p;
    FragmentControl fc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m = new TestModel();
        p = new TestPresenter();
        fc = new FragmentControl(getFragmentManager(),);



    }
}
