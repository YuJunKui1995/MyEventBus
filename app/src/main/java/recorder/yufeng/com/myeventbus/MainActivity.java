package recorder.yufeng.com.myeventbus;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import recorder.yufeng.com.myeventbus.eventbus.MyEventBus;
import recorder.yufeng.com.myeventbus.eventbus.Subscribe;
import recorder.yufeng.com.myeventbus.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                MyEventBus.getDefault().post(new Event(" ui 我是2秒后的消息啊啊啊啊啊啊啊啊啊啊啊啊啊"));
            }
        }, 2000);//延迟10秒执行

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                    //工作线程
                    MyEventBus.getDefault().post(new Event(" work 我是2秒后的消息啊啊啊啊啊啊啊啊啊啊啊啊啊"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }


    @Subscribe(threadMode = ThreadMode.ASYNC) //都是工作线程
    public void test(Event event) {
//        Toast.makeText(this, event.s, Toast.LENGTH_LONG).show();
        Log.i(TAG,"event.s="+event.s+"Looper.myLooper()==Looper.getMainLooper()="+ (Looper.myLooper()==Looper.getMainLooper()));
    }

    class Event {
        String s;

        public Event(String s) {
            this.s = s;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyEventBus.getDefault().register(this);//注册

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        MyEventBus.getDefault().unregister(this);//注销
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
