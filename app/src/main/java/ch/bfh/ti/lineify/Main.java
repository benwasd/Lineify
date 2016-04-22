package ch.bfh.ti.lineify;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import rx.Observable;

public class Main extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        findViewById(R.id.fullscreen_content).setSystemUiVisibility(flags);

        Observable<String> helloMessagesObservable = Observable.create(subscriber -> {
            subscriber.onNext("HI");
            subscriber.onNext("HALLO");
            subscriber.onNext("GRüEZI");
            subscriber.onCompleted();
        });

        helloMessagesObservable.subscribe(s -> Log.i("Main", s));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}